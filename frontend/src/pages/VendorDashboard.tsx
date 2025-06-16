import { useState, useEffect } from "react"; // Removed useRef
import { Link, useNavigate } from "react-router-dom";
import {
  Plus,
  Bell,
  Package,
  TrendingUp,
  Users,
  DollarSign,
  Clock,
  CheckCircle,
  XCircle,
  Edit,
  Trash2,
  Eye,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { toast } from "@/hooks/use-toast";
import AddItemModal from "@/components/vendor/AddItemModal";
import OrderQueue from "@/components/vendor/OrderQueue";
import VendorStats from "@/components/vendor/VendorStats";
import QRCodeScanner from "@/components/vendor/QRCodeScanner";
import { Client } from "@stomp/stompjs";
import SockJS from 'sockjs-client';

// ====================================================================
// MOCK DATA AND INTERFACES (UNCHANGED - Ensure these are consistent with your backend)
// ====================================================================

const mockVendor = {
  id: 1, // Assuming Long for vendorId in backend, ensure consistency
  name: "Spice Paradise",
  email: "vendor@spiceparadise.com",
  username: "spiceparadise",
  phone: "+91 98765 43210",
  address: "Food Court, Mall Road, City Center",
  isOnline: true,
  rating: 4.5,
  totalOrders: 1247,
  totalRevenue: 89450,
};

const mockItems = [
  {
    id: 101,
    name: "Chicken Biryani",
    price: 299,
    image:
      "https://images.unsplash.com/photo-1563379091339-03246963d96c?w=300&h=200&fit=crop",
    description: "Aromatic basmati rice with tender chicken pieces",
    isVeg: false,
    category: "Main Course",
    isAvailable: true,
    preparationTime: "25-30 min",
  },
  {
    id: 102,
    name: "Paneer Butter Masala",
    price: 249,
    image:
      "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=300&h=200&fit=crop",
    description: "Creamy tomato curry with soft paneer cubes",
    isVeg: true,
    category: "Main Course",
    isAvailable: true,
    preparationTime: "15-20 min",
  },
  {
    id: 103,
    name: "Vegetable Noodles",
    price: 180,
    image: "https://images.unsplash.com/photo-1606859183610-d007c6f0c793?w=300&h=200&fit=crop",
    description: "Stir-fried noodles with fresh vegetables",
    isVeg: true,
    category: "Noodles",
    isAvailable: true,
    preparationTime: "20-25 min"
  },
  {
    id: 104,
    name: "Mutton Rogan Josh",
    price: 450,
    image: "https://images.unsplash.com/photo-1582298687367-3d3f2d2e1a3d?w=300&h=200&fit=crop",
    description: "Rich and flavorful mutton curry from Kashmir",
    isVeg: false,
    category: "Main Course",
    isAvailable: false, // Example: temporarily unavailable
    preparationTime: "40-45 min"
  }
];

interface VendorDetails {
  id: number; // Keep as number if your backend Long maps to JS number
  name: string;
  email: string;
  username: string;
  phone?: string;
  address?: string;
  isOnline?: boolean;
  rating?: number;
  totalOrders?: number;
  totalRevenue?: number;
}

interface MenuItem {
  id: number;
  name: string;
  price: number;
  image: string;
  description: string;
  isVeg: boolean;
  category: string;
  isAvailable: boolean;
  preparationTime: string;
}

interface Notification {
  id: number;
  message: string;
  time: string;
  isRead: boolean;
}

// ====================================================================
// VendorDashboard Component
// ====================================================================

const VendorDashboard = () => {
  const [vendor, setVendor] = useState<VendorDetails | null>(null);
  const [items, setItems] = useState<MenuItem[]>([]);
  const [isAddItemModalOpen, setIsAddItemModalOpen] = useState(false);
  const [notifications, setNotifications] = useState<Notification[]>([]);

  // KEY CHANGE: Use useState for stompClient instead of useRef
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const navigate = useNavigate();

  // Effect to load vendor details from localStorage
  useEffect(() => {
    const storedVendorDetails = localStorage.getItem("vendorDetails");
    if (storedVendorDetails) {
      try {
        const parsedVendor: VendorDetails = JSON.parse(storedVendorDetails);
        if (parsedVendor && parsedVendor.username) {
          // Provide sensible defaults if some fields are missing from storage
          setVendor({
            ...parsedVendor,
            id: parsedVendor.id || mockVendor.id,
            name: parsedVendor.name || mockVendor.name,
            email: parsedVendor.email || mockVendor.email,
            phone: parsedVendor.phone || mockVendor.phone,
            address: parsedVendor.address || mockVendor.address,
            isOnline: parsedVendor.isOnline ?? mockVendor.isOnline, // Use nullish coalescing for boolean
            rating: parsedVendor.rating || mockVendor.rating,
            totalOrders: parsedVendor.totalOrders || mockVendor.totalOrders,
            totalRevenue: parsedVendor.totalRevenue || mockVendor.totalRevenue,
          });
        } else {
          toast({ title: "Authentication Error", description: "Vendor details incomplete. Please log in again.", variant: "destructive" });
          localStorage.removeItem("vendorDetails");
          navigate("/login/vendor");
        }
      } catch (error) {
        console.error("Failed to parse vendor details from localStorage:", error);
        toast({ title: "Session Error", description: "Corrupted vendor session. Please log in again.", variant: "destructive" });
        localStorage.removeItem("vendorDetails");
        navigate("/vendor/login");
      }
    } else {
      toast({ title: "Authentication Required", description: "Please log in to access the dashboard.", variant: "destructive" });
      navigate("/vendor/login");
    }

    setItems(mockItems); // Still using mock data, replace with actual API call if ready
    setNotifications([
      { id: 1, message: "New order received - #ORD001", time: "2 min ago", isRead: false },
      { id: 2, message: "Order #ORD002 completed", time: "15 min ago", isRead: true },
      { id: 3, message: "Low stock alert for Chicken Biryani", time: "1 hour ago", isRead: false },
    ]);
  }, [navigate]);

  // Effect to manage WebSocket connection lifecycle
  useEffect(() => {
    // Only attempt to connect if vendor details are loaded and stompClient isn't already active
    if (!vendor?.username || !vendor?.id) {
      console.log("Vendor username or ID not available for WebSocket connection yet. Waiting for state update...");
      return;
    }

    // Prevent creating multiple clients if one already exists and is active
    if (stompClient && stompClient.connected) {
      console.log("STOMP client already connected and active. Skipping new connection.");
      return;
    }

    console.log(`Attempting WebSocket connection for vendor: ${vendor.username} (ID: ${vendor.id})`);

    const client = new Client({
      brokerURL: "ws://localhost:8989/ws",
      webSocketFactory: () => new SockJS("http://localhost:8989/ws"),
      debug: function (str) {
        // console.log("STOMP DEBUG:", str); // Uncomment for detailed STOMP logging
      },
      reconnectDelay: 5000, // Attempt to reconnect every 5 seconds
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      console.log("Connected to WebSocket for vendor:", vendor.username);
      // Set the connected client to state, which will trigger a re-render
      setStompClient(client);

      // Subscribe to general notifications for this vendor
      client.subscribe(`/topic/notifications/${vendor.username}`, (message) => {
        const newNotification = JSON.parse(message.body);
        console.log("New notification received via WebSocket:", newNotification);
        toast({ title: "New Notification!", description: newNotification.message, variant: "info" });
        setNotifications((prev) => [
          { id: Date.now(), message: newNotification.message, time: "Just now", isRead: false },
          ...prev,
        ]);
      });
      // OrderQueue component will handle its own subscription for /topic/orders/${vendorUsername}
    };

    client.onStompError = (frame) => {
      console.error("Broker reported STOMP error:", frame.headers["message"]);
      console.error("Additional STOMP details:", frame.body);
      toast({ title: "WebSocket Error", description: "Could not connect to real-time updates. Please refresh.", variant: "destructive" });
      setStompClient(null); // Reset client state on STOMP error
    };

    client.onWebSocketClose = (event) => {
      console.log("WebSocket closed:", event);
      setStompClient(null); // Reset client state on WebSocket close
      toast({ title: "WebSocket Disconnected", description: "Real-time updates interrupted. Attempting to reconnect...", variant: "warning" });
    };

    client.onWebSocketError = (event) => {
      console.error("WebSocket error:", event);
      setStompClient(null); // Reset client state on WebSocket error
      toast({ title: "WebSocket Error", description: "Real-time connection error. Please check network.", variant: "destructive" });
    };

    client.activate(); // Start the connection process

    // Cleanup function: deactivate the client when component unmounts or dependencies change
    return () => {
      if (client.connected) {
        client.deactivate(); // Deactivate the local 'client' instance created in this effect run
        console.log("Deactivating STOMP client for vendor:", vendor.username);
      }
      // Ensure the state is reset even if deactivate fails or doesn't complete immediately
      setStompClient(null);
    };
  }, [vendor?.username, vendor?.id]); // Re-run effect if vendor username or ID changes

  // ... (rest of your component functions remain unchanged) ...

  const toggleStoreStatus = () => {
    if (!vendor) return; // Prevent action if vendor is null
    setVendor((prev) => (prev ? { ...prev, isOnline: !prev.isOnline } : null));
    toast({
      title: vendor.isOnline ? "Store Closed" : "Store Opened",
      description: vendor.isOnline
        ? "Your store is now offline and won't receive new orders"
        : "Your store is now online and ready to receive orders",
    });
    // In a real app, you'd send an API call to update the backend here
  };

  const toggleItemAvailability = (itemId: number) => {
    setItems((prev) =>
      prev.map((item) =>
        item.id === itemId ? { ...item, isAvailable: !item.isAvailable } : item
      )
    );
    toast({
      title: "Item Updated",
      description: "Item availability has been updated",
    });
    // In a real app, you'd send an API call to update the backend here
  };

  const handleAddItem = (newItem: any) => {
    const item = {
      ...newItem,
      id: Date.now(), // Generate a unique ID for mock data
      isAvailable: true,
      image: newItem.image || "https://via.placeholder.com/300x200?text=Food+Item", // Fallback image
    };
    setItems((prev) => [...prev, item]);
    setIsAddItemModalOpen(false);
    toast({
      title: "Item Added",
      description: "New item has been added to your menu",
    });
    // In a real app, you'd send an API call to the backend (e.g., POST /api/vendors/{vendorId}/products)
  };

  const deleteItem = (itemId: number) => {
    setItems((prev) => prev.filter((item) => item.id !== itemId));
    toast({
      title: "Item Deleted",
      description: "Item has been removed from your menu",
    });
    // In a real app, you'd send an API call to the backend (e.g., DELETE /api/vendors/{vendorId}/products/{productId})
  };

  const handleQRScanSuccess = (orderId: string) => {
    console.log("Scanned order ID:", orderId);
    toast({
      title: "Order Verified",
      description: `Order ${orderId} has been verified via QR code`,
    });
    // In a real app, you'd send an API call to your backend to mark the order as delivered/completed
  };

  const unreadNotifications = notifications.filter((n) => !n.isRead).length;

  // Render a loading state if vendor details aren't loaded yet
  if (!vendor) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        Loading vendor dashboard...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center gap-4">
              <h1 className="text-2xl font-bold text-gray-900">
                Vendor Dashboard for{" "}
                <span className="text-orange-600">
                  {vendor.username}
                </span>{" "}
              </h1>
              <Badge variant={vendor.isOnline ? "default" : "secondary"}>
                {vendor.isOnline ? "Online" : "Offline"}
              </Badge>
            </div>

            <div className="flex items-center gap-4">
              <Button
                variant={vendor.isOnline ? "destructive" : "default"}
                onClick={toggleStoreStatus}
              >
                {vendor.isOnline ? "Close Store" : "Open Store"}
              </Button>

              <div className="relative">
                <Button variant="outline" size="sm">
                  <Bell className="h-4 w-4" />
                  {unreadNotifications > 0 && (
                    <Badge className="absolute -top-2 -right-2 h-5 w-5 rounded-full p-0 flex items-center justify-center text-xs">
                      {unreadNotifications}
                    </Badge>
                  )}
                </Button>
              </div>

              <Link to="/login/vendor">
                <Button variant="outline" size="sm">
                  <Eye className="h-4 w-4 mr-2" />
                  Log out
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <VendorStats vendor={vendor} />

        <Tabs defaultValue="orders" className="mt-8">
          <TabsList className="grid w-full grid-cols-5">
            <TabsTrigger value="orders">Order Queue</TabsTrigger>
            <TabsTrigger value="scanner">QR Scanner</TabsTrigger>
            <TabsTrigger value="menu">Menu Management</TabsTrigger>
            <TabsTrigger value="analytics">Analytics</TabsTrigger>
            <TabsTrigger value="notifications">Notifications</TabsTrigger>
          </TabsList>

          <TabsContent value="orders" className="mt-6">
            {/* Pass stompClient (now from state), vendorUsername, and vendorId to OrderQueue */}
            {vendor.username && vendor.id ? (
              <OrderQueue
                stompClient={stompClient} // THIS IS THE UPDATED PROP!
                vendorUsername={vendor.username}
                vendorId={vendor.id}
              />
            ) : (
              // This message will show briefly while vendor data loads or if it fails
              <p className="text-center text-gray-500">Loading order queue...</p>
            )}
          </TabsContent>

          <TabsContent value="scanner" className="mt-6">
            <div className="max-w-md mx-auto">
              <QRCodeScanner onScanSuccess={handleQRScanSuccess} />
            </div>
          </TabsContent>

          <TabsContent value="menu" className="mt-6">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-semibold">Menu Items</h3>
              <Button onClick={() => setIsAddItemModalOpen(true)}>
                <Plus className="h-4 w-4 mr-2" />
                Add New Item
              </Button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {items.map((item) => (
                <Card key={item.id} className="overflow-hidden">
                  <div className="relative">
                    <img
                      src={item.image}
                      alt={item.name}
                      className="w-full h-48 object-cover"
                    />
                    <Badge
                      className={`absolute top-3 left-3 ${
                        item.isVeg ? "bg-green-600" : "bg-red-600"
                      }`}
                    >
                      {item.isVeg ? "Veg" : "Non-Veg"}
                    </Badge>
                    <Badge
                      className={`absolute top-3 right-3 ${
                        item.isAvailable ? "bg-green-600" : "bg-gray-500"
                      }`}
                    >
                      {item.isAvailable ? "Available" : "Unavailable"}
                    </Badge>
                  </div>
                  <CardContent className="p-4">
                    <h4 className="text-lg font-semibold mb-2">
                      {item.name}
                    </h4>
                    <p className="text-gray-600 text-sm mb-3">
                      {item.description}
                    </p>
                    <div className="flex items-center justify-between mb-4">
                      <span className="text-xl font-bold text-orange-600">
                        ₹{item.price}
                      </span>
                      <span className="text-sm text-gray-500">
                        {item.preparationTime}
                      </span>
                    </div>
                    <div className="flex gap-2">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => toggleItemAvailability(item.id)}
                        className="flex-1"
                      >
                        {item.isAvailable ? (
                          <XCircle className="h-4 w-4 mr-1" />
                        ) : (
                          <CheckCircle className="h-4 w-4 mr-1" />
                        )}
                        {item.isAvailable ? "Disable" : "Enable"}
                      </Button>
                      <Button size="sm" variant="outline">
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => deleteItem(item.id)}
                        className="text-red-600 hover:text-red-700"
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </TabsContent>

          <TabsContent value="analytics" className="mt-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>Daily Sales</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl font-bold text-green-600">
                    ₹2,450
                  </div>
                  <p className="text-gray-600">+12% from yesterday</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Orders Today</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl font-bold text-blue-600">23</div>
                  <p className="text-gray-600">+5 from yesterday</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Average Order Value</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl font-bold text-purple-600">
                    ₹387
                  </div>
                  <p className="text-gray-600">-2% from yesterday</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Customer Rating</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl font-bold text-yellow-600">
                    4.5
                  </div>
                  <p className="text-gray-600">Based on 156 reviews</p>
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          <TabsContent value="notifications" className="mt-6">
            <Card>
              <CardHeader>
                <CardTitle>Recent Notifications</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {notifications.map((notification) => (
                    <div
                      key={notification.id}
                      className={`p-4 rounded-lg border ${
                        notification.isRead
                          ? "bg-gray-50"
                          : "bg-blue-50 border-blue-200"
                      }`}
                    >
                      <div className="flex justify-between items-start">
                        <p className="font-medium">{notification.message}</p>
                        <span className="text-sm text-gray-500">
                          {notification.time}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      <AddItemModal
        isOpen={isAddItemModalOpen}
        onClose={() => setIsAddItemModalOpen(false)}
        onAddItem={handleAddItem}
      />
    </div>
  );
};

export default VendorDashboard;