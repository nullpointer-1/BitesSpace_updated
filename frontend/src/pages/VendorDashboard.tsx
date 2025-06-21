// src/pages/vendor/dashboard.tsx
import { useState, useEffect } from "react";
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
import { toast } from "@/hooks/use-toast"; // Assuming this is your Shadcn UI toast hook
import AddItemModal from "@/components/vendor/AddItemModal";
import OrderQueue from "@/components/vendor/OrderQueue";
import VendorStats from "@/components/vendor/VendorStats";
import QRCodeScanner from "@/components/vendor/QRCodeScanner";
import { Client } from "@stomp/stompjs";
import SockJS from 'sockjs-client';
import axiosInstance from "@/api/axiosInstance"; // Use your configured axiosInstance
import { useAuth, User } from '@/context/AuthContext'; // Import useAuth and the User type from AuthContext

// Interface for MenuItem (Product from backend)
interface MenuItem {
  id: number; // Product ID (Long) from backend
  name: string;
  price: number;
  imageUrl: string;
  description: string;
  isVeg: boolean;
  category: string;
  isAvailable: boolean; // Field to control availability
  preparationTime: string;
  shopId: number; // Crucial for backend association
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
  // Get user info and authentication state from AuthContext
  const { user, isAuthenticated, isLoading: authLoading, logout } = useAuth();
  
  // Use local state for UI-specific things not directly part of the AuthContext User,
  // like the store's online status (which should probably be managed on the Shop entity backend).
  const [isStoreOnline, setIsStoreOnline] = useState(false); // Initial mock state for store status

  const [items, setItems] = useState<MenuItem[]>([]);
  const [isAddItemModalOpen, setIsAddItemModalOpen] = useState(false);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [menuLoading, setMenuLoading] = useState(true);
  const [menuError, setMenuError] = useState<string | null>(null);

  const [stompClient, setStompClient] = useState<Client | null>(null);
  const navigate = useNavigate();

  // Effect to handle initial authentication check and redirect
  useEffect(() => {
    if (!authLoading) { // Once authentication state has loaded
      if (!isAuthenticated || !user || user.role !== 'Vendor') {
        // If not authenticated, or not a vendor, redirect to vendor login
        toast({ title: "Authentication Required", description: "Please log in as a vendor to access the dashboard.", variant: "destructive" });
        navigate("/vendor/login");
      } else {
        // Optionally fetch the actual store online status from backend here
        // For now, it's just a mock initial state.
        setIsStoreOnline(true); // Default to online for demo purposes
      }
    }
  }, [isAuthenticated, user, authLoading, navigate]);


  // Effect to fetch menu items when user (from AuthContext) is available
  useEffect(() => {
    const fetchMenuItems = async () => {
      // Use user.shopId from AuthContext as the primary ID for fetching products
      const currentShopId = user?.shopId ?? user?.id; // Fallback to user.id if shopId isn't set (though it should be for a vendor)

      if (!currentShopId) {
        setMenuLoading(false);
        console.log("Shop ID not available for menu item fetch. User:", user);
        setMenuError("Could not determine shop ID to load menu items.");
        return;
      }

      setMenuLoading(true);
      setMenuError(null);
      try {
        // Use axiosInstance for authenticated requests
        // Endpoint: /api/vendors/{shopId}/products (as per your VendorController)
        const response = await axiosInstance.get<MenuItem[]>(`/vendors/${currentShopId}/products`);
        setItems(response.data);
      } catch (err: any) {
        console.error("Error fetching menu items:", err);
        setMenuError(err.response?.data?.message || "Failed to load menu items. Please try again.");
        setItems([]); // Clear items on error
      } finally {
        setMenuLoading(false);
      }
    };

    // Only fetch if user is authenticated and is a vendor
    if (isAuthenticated && user?.role === 'Vendor') {
      fetchMenuItems();
    } else {
      setMenuLoading(false); // If not authenticated or not a vendor, stop loading
      setItems([]);
    }
    
  }, [isAuthenticated, user?.shopId, user?.id, user?.role]); // Re-fetch when auth status or relevant user IDs change

  // Effect to manage WebSocket connection lifecycle
  useEffect(() => {
    // Only connect if user is authenticated, is a vendor, and we have the necessary IDs
    if (!isAuthenticated || !user || user.role !== 'Vendor' || !user.username || !user.id) {
      console.log("User not authenticated, not a vendor, or missing details for WebSocket. Skipping connection.");
      if (stompClient && stompClient.connected) {
        stompClient.deactivate(); // Ensure old client is deactivated if user logs out
        setStompClient(null);
      }
      return;
    }

    // Prevent reconnecting if already connected for the same user
    if (stompClient && stompClient.connected) {
      console.log("STOMP client already connected and active for current user. Skipping new connection.");
      return;
    }

    console.log(`Attempting WebSocket connection for vendor: ${user.username} (ID: ${user.id})`);

    const client = new Client({
      brokerURL: "ws://localhost:8989/ws", // Ensure this matches your backend WebSocket endpoint
      webSocketFactory: () => new SockJS("http://localhost:8989/ws"),
      debug: function (str) {
        // console.log("STOMP DEBUG:", str); // Uncomment for detailed STOMP logging
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      console.log("Connected to WebSocket for vendor:", user.username);
      setStompClient(client);

      // Subscribe to vendor-specific notifications using username
      client.subscribe(`/topic/notifications/${user.username}`, (message) => {
        const newNotification = JSON.parse(message.body);
        console.log("New notification received via WebSocket:", newNotification);
        toast({ title: "New Notification!", description: newNotification.message, variant: "info" });
        setNotifications((prev) => [
          { id: Date.now(), message: newNotification.message, time: "Just now", isRead: false },
          ...prev,
        ]);
      });
      // Mock initial notifications (replace with actual fetch if backend provides them)
      setNotifications([
        { id: 1, message: "New order received - #ORD001", time: "2 min ago", isRead: false },
        { id: 2, message: "Order #ORD002 completed", time: "15 min ago", isRead: true },
        { id: 3, message: "Low stock alert for Chicken Biryani", time: "1 hour ago", isRead: false },
      ]);
    };

    client.onStompError = (frame) => {
      console.error("Broker reported STOMP error:", frame.headers["message"]);
      console.error("Additional STOMP details:", frame.body);
      toast({ title: "WebSocket Error", description: "Could not connect to real-time updates. Please refresh.", variant: "destructive" });
      setStompClient(null);
    };

    client.onWebSocketClose = (event) => {
      console.log("WebSocket closed:", event);
      setStompClient(null);
      toast({ title: "WebSocket Disconnected", description: "Real-time updates interrupted. Attempting to reconnect...", variant: "warning" });
    };

    client.onWebSocketError = (event) => {
      console.error("WebSocket error:", event);
      setStompClient(null);
      toast({ title: "WebSocket Error", description: "Real-time connection error. Please check network.", variant: "destructive" });
    };

    client.activate();

    return () => {
      // Cleanup: deactivate STOMP client when component unmounts or dependencies change
      if (client.connected) {
        client.deactivate();
        console.log("Deactivating STOMP client for vendor:", user.username);
      }
      setStompClient(null);
    };
  }, [isAuthenticated, user?.username, user?.id, user?.role]); // Re-connect WebSocket if auth status or relevant user IDs change

  const toggleStoreStatus = () => {
    // This is a mock UI toggle. For persistence, you need a backend API endpoint
    // to update the shop's online status.
    setIsStoreOnline((prev) => !prev);
    toast({
      title: isStoreOnline ? "Store Closed" : "Store Opened",
      description: isStoreOnline
        ? "Your store is now offline and won't receive new orders"
        : "Your store is now online and ready to receive orders",
    });
  };

  const toggleItemAvailability = async (itemId: number, currentAvailability: boolean) => {
    // Ensure the current user is a vendor and has an ID
    if (!user || user.role !== 'Vendor' || !user.id) {
        toast({ title: "Error", description: "Authentication error: User is not a valid vendor.", variant: "destructive" });
        return;
    }
    try {
        // Assuming your backend has a PUT endpoint like /api/products/{productId}/availability
        await axiosInstance.put(`/products/${itemId}/availability`, { isAvailable: !currentAvailability });
        setItems((prev) =>
            prev.map((item) =>
                item.id === itemId ? { ...item, isAvailable: !currentAvailability } : item
            )
        );
        toast({
            title: "Item Updated",
            description: `Item availability changed to ${!currentAvailability ? "Available" : "Unavailable"}.`,
            variant: "success",
        });
    } catch (error: any) {
        console.error("Error toggling item availability:", error);
        toast({
            title: "Update Failed",
            description: error.response?.data?.message || "Failed to update item availability.",
            variant: "destructive",
        });
    }
  };

  const handleAddItem = async (newItemData: Omit<MenuItem, 'id' | 'isAvailable' | 'shopId' | 'imageUrl'> & { image: string }) => {
    // Use user.shopId from AuthContext as the primary ID for associating the product
    const currentShopId = user?.shopId ?? user?.id; 

    if (!currentShopId) {
        toast({ title: "Error", description: "Shop ID not found for adding item. Please ensure vendor is associated with a shop.", variant: "destructive" });
        return;
    }
    try {
        const itemToCreate = {
            ...newItemData,
            imageUrl: newItemData.image, // Map 'image' from modal to 'imageUrl' for backend
            shopId: currentShopId, // Associate with the current vendor's shop
            isAvailable: true, // New items are available by default
        };
        // Assuming your backend has a POST endpoint like /api/products
        const response = await axiosInstance.post<MenuItem>("/products", itemToCreate);
        setItems((prev) => [...prev, response.data]); // Add the item returned by the backend (with actual ID)
        setIsAddItemModalOpen(false);
        toast({
            title: "Item Added",
            description: `${response.data.name} has been added to your menu.`,
            variant: "success",
        });
    } catch (error: any) {
        console.error("Error adding item:", error);
        toast({
            title: "Add Item Failed",
            description: error.response?.data?.message || "Failed to add new item.",
            variant: "destructive",
        });
    }
  };

  const deleteItem = async (itemId: number) => {
    // Ensure the current user is a vendor and has an ID
    if (!user || user.role !== 'Vendor' || !user.id) {
        toast({ title: "Error", description: "Authentication error: User is not a valid vendor.", variant: "destructive" });
        return;
    }
    try {
        // Assuming your backend has a DELETE endpoint like /api/products/{productId}
        await axiosInstance.delete(`/products/${itemId}`);
        setItems((prev) => prev.filter((item) => item.id !== itemId));
        toast({
            title: "Item Deleted",
            description: "Item has been removed from your menu.",
            variant: "success",
        });
    } catch (error: any) {
        console.error("Error deleting item:", error);
        toast({
            title: "Delete Failed",
            description: error.response?.data?.message || "Failed to delete item.",
            variant: "destructive",
        });
    }
  };

  const handleQRScanSuccess = (orderId: string) => {
    console.log("Scanned order ID:", orderId);
    toast({
      title: "Order Verified",
      description: `Order ${orderId} has been verified via QR code`,
    });
    // In a real app, you'd send an API call to your backend to mark the order as delivered/completed
    // Example: axiosInstance.put(`/orders/${orderId}/status`, { newStatus: "COMPLETED", vendorId: user.id });
  };

  const unreadNotifications = notifications.filter((n) => !n.isRead).length;

  // Handle global loading state from AuthContext (e.g., initial auth check)
  if (authLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-4"></div>
            <p>Loading dashboard...</p>
        </div>
      </div>
    );
  }

  // If not authenticated or not a vendor, show access denied.
  // The useEffect above will also handle the redirect.
  if (!isAuthenticated || !user || user.role !== 'Vendor') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 text-red-500">
        Access Denied. Please log in as a vendor.
      </div>
    );
  }

  // Render the dashboard only if authenticated as a vendor
  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center gap-4">
              <h1 className="text-2xl font-bold text-gray-900">
                Vendor Dashboard for{" "}
                <span className="text-orange-600">
                  {user.username} {/* Use user.username from AuthContext */}
                </span>{" "}
              </h1>
              {/* `isStoreOnline` is now local state in VendorDashboard */}
              <Badge variant={isStoreOnline ? "default" : "secondary"} className={isStoreOnline ? "bg-green-600" : ""}>
                {isStoreOnline ? "Online" : "Offline"}
              </Badge>
            </div>

            <div className="flex items-center gap-4">
              <Button
                variant={isStoreOnline ? "destructive" : "default"}
                onClick={toggleStoreStatus}
              >
                {isStoreOnline ? "Close Store" : "Open Store"}
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

              {/* Use the logout function from AuthContext */}
              <Button variant="outline" size="sm" onClick={logout}>
                <Eye className="h-4 w-4 mr-2" />
                Log out
              </Button>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Pass AuthContext's user to VendorStats */}
        {user && <VendorStats vendor={user} />}

        <Tabs defaultValue="orders" className="mt-8">
          <TabsList className="grid w-full grid-cols-5">
            <TabsTrigger value="orders">Order Queue</TabsTrigger>
            <TabsTrigger value="scanner">QR Scanner</TabsTrigger>
            <TabsTrigger value="menu">Menu Management</TabsTrigger>
            <TabsTrigger value="analytics">Analytics</TabsTrigger>
            <TabsTrigger value="notifications">Notifications</TabsTrigger>
          </TabsList>

          <TabsContent value="orders" className="mt-6">
            {user?.username && user?.id ? (
              <OrderQueue
                stompClient={stompClient}
                vendorUsername={user.username}
                vendorId={user.id}
                shopId={user.shopId} // Pass shopId if relevant for orders
              />
            ) : (
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

            {menuLoading ? (
                <div className="text-center text-gray-600 py-10">Loading menu items...</div>
            ) : menuError ? (
                <div className="text-center text-red-600 py-10">{menuError}</div>
            ) : items.length === 0 ? (
                <div className="text-center text-gray-600 py-10">No menu items found. Add your first item!</div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {items.map((item) => (
                    <Card key={item.id} className="overflow-hidden">
                      <div className="relative">
                        <img
                          src={item.imageUrl}
                          alt={item.name}
                          className="w-full h-48 object-cover"
                          onError={(e) => { e.currentTarget.src = "https://placehold.co/300x200/cccccc/333333?text=No+Image"; }}
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
                            onClick={() => toggleItemAvailability(item.id, item.isAvailable)}
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
                            <Edit className="h-4 w-4" /> {/* Edit Item functionality to be implemented */}
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
            )}
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
