import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowLeft, User as UserIcon, History, Heart, MapPin, CreditCard, Settings,Search, Bell, HelpCircle, Star, Clock, Package, Mail } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useUser } from "@/context/UserContext"; // Import useUser hook
import axios from "axios"; // Import axios for API calls

// --- Interfaces (match your backend DTOs/Entities) ---

interface OrderItemDocument {
  productId: string; // Assuming product ID is string from backend
  productName: string;
  priceAtOrder: number;
  quantity: number;
  imageUrl: string;
  isVeg: boolean;
}

interface OrderDocument {
  id: string; // MongoDB _id
  orderId: string; // Your custom UUID
  customerEmail: string;
  userId: number; // Assuming userId is a number (Long) from backend
  shopId: number;
  shopName: string;
  shopAddress: string;
  vendorId: number;
  vendorName: string;
  customerName: string;
  customerPhone: string;
  totalAmount: number;
  status: string;
  orderDate: string; // ISO 8601 string from Instant
  estimatedPickupTime: string; // ISO 8601 string from Instant
  items: OrderItemDocument[];
}

interface User {
  id: string | null; // Frontend might handle it as string, convert to number for backend calls
  name: string | null;
  mobileNumber: string | null;
  email?: string | null; // Now expecting email from UserContext
}

const UserDashboard = () => {
  const [activeTab, setActiveTab] = useState("overview");
  const { user, logout } = useUser(); // Get user data and logout function from context
  const [userOrders, setUserOrders] = useState<OrderDocument[]>([]); // State for dynamic orders
  const [orderLoading, setOrderLoading] = useState(true); // Loading state for orders
  const [orderError, setOrderError] = useState<string | null>(null); // Error state for orders
  const navigate = useNavigate(); // For redirection

  // Redirect to login if user is not logged in (user.id is null or undefined)
  useEffect(() => {
    if (!user || !user.id) {
      navigate("/login/user"); // Redirect to your user login page
    }
  }, [user, navigate]); // Depend on user and navigate

  // --- Fetch User-Specific Order History ---
  useEffect(() => {
    const fetchUserOrders = async () => {
      if (!user || !user.id) {
        setOrderLoading(false);
        return;
      }
      setOrderLoading(true);
      setOrderError(null);
      try {
        // Convert user.id from string to number if your backend expects a Long
        const userIdAsNumber = Number(user.id); 
        // Assuming a new API endpoint for fetching orders by user ID
        const response = await axios.get<OrderDocument[]>(`http://localhost:8989/api/orders/user/${userIdAsNumber}`);
        setUserOrders(response.data);
      } catch (err: any) {
        console.error("Error fetching user orders:", err);
        setOrderError("Failed to load your order history. Please try again.");
      } finally {
        setOrderLoading(false);
      }
    };

    fetchUserOrders();
    // Re-fetch orders if user changes (e.g., after login/logout context update)
  }, [user]); 

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) { // Ensure case-insensitive comparison
      case "completed": 
      case "delivered": return "default"; // Assuming 'delivered' maps to 'completed' status
      case "preparing": 
      case "placed":
      case "ready_for_pickup": return "secondary"; // Adjusted for backend statuses
      case "cancelled": 
      case "rejected": return "destructive"; // Adjusted for backend statuses
      default: return "secondary";
    }
  };

  // Helper to get initials for avatar from user's name
  const getUserInitials = (name: string | null) => {
    if (!name || name.trim() === "") return "U"; // Default to 'U' for unknown user
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  };

  // Show a loading or redirecting message if user is not yet loaded or being redirected
  if (!user || !user.id) {
    return (
      <div className="min-h-screen flex items-center justify-center text-lg text-gray-700">
        Loading user data or redirecting...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-4">
              <Link to="/">
                <Button variant="ghost" size="sm">
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Home
                </Button>
              </Link>
              <h1 className="text-xl font-semibold">My Dashboard</h1>
            </div>
            <div className="flex items-center gap-4">
            <Link to="/user/browse/stalls">
                <Button className="bg-orange-600 hover:bg-orange-700">
                  <Search className="h-4 w-4 mr-2" />
                  Order Now
                </Button>
              </Link>
              <Button variant="outline" size="sm">
                <Bell className="h-4 w-4 mr-2" />
                Notifications
              </Button>
              {/* Logout Button */}
              <Button variant="outline" size="sm" onClick={logout}>
                Log Out
              </Button>
              <Avatar>
                <AvatarFallback>
                  {getUserInitials(user.name)} {/* Use dynamic user name for initials */}
                </AvatarFallback>
              </Avatar>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Section */}
        <div className="mb-8">
          <Card className="bg-gradient-to-r from-orange-500 to-red-500 text-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-2xl font-bold mb-2">Welcome back, {user.name || "User"}!</h2>
                  <p className="opacity-90">Member since January 2024</p> {/* This date is still static */}
                </div>
                <div className="text-right">
                  <div className="text-3xl font-bold">{userOrders.length}</div> {/* Dynamic total orders */}
                  <p className="text-sm opacity-90">Total Orders</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Quick Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Total Orders</CardTitle>
              <Package className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{userOrders.length}</div> {/* Dynamic total orders */}
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Favorite Stalls</CardTitle>
              <Heart className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              {/* This is still mock data; requires backend for dynamic favorites */}
              <div className="text-2xl font-bold">2</div> 
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">This Month</CardTitle>
              <Clock className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              {/* This is still mock data; requires backend filtering */}
              <div className="text-2xl font-bold">12</div>
              <p className="text-xs text-muted-foreground">Orders placed</p>
            </CardContent>
          </Card>
        </div>

        {/* Main Content Tabs */}
        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="overview">Overview</TabsTrigger>
            <TabsTrigger value="orders">Orders</TabsTrigger>
            <TabsTrigger value="favorites">Favorites</TabsTrigger>
            <TabsTrigger value="profile">Profile</TabsTrigger>
          </TabsList>

          <TabsContent value="overview" className="space-y-6">
            {/* Recent Orders (Now uses dynamic data) */}
            <Card>
              <CardHeader>
                <CardTitle>Recent Orders</CardTitle>
              </CardHeader>
              <CardContent>
                {orderLoading ? (
                  <div className="text-center text-gray-600">Loading recent orders...</div>
                ) : orderError ? (
                  <div className="text-center text-red-600">{orderError}</div>
                ) : userOrders.length === 0 ? (
                  <div className="text-center text-gray-600">No recent orders found.</div>
                ) : (
                  <div className="space-y-4">
                    {userOrders.slice(0, 3).map((order) => ( // Display top 3 recent orders
                      <div key={order.id} className="flex items-center justify-between p-4 border rounded-lg">
                        <div className="flex-1">
                          <h4 className="font-medium">{order.shopName}</h4>
                          <p className="text-sm text-gray-600">
                            {order.items.map(item => `${item.productName} x${item.quantity}`).join(", ")}
                          </p>
                          <p className="text-xs text-gray-500">
                            {new Date(order.orderDate).toLocaleDateString()}
                          </p>
                        </div>
                        <div className="flex items-center gap-4">
                          <Badge variant={getStatusColor(order.status)} className="capitalize">
                            {order.status.replace(/_/g, ' ')} {/* Format status for display */}
                          </Badge>
                          <span className="font-medium">₹{order.totalAmount.toFixed(2)}</span>
                          <Link to={`/order-tracking/${order.orderId}`}> {/* Use orderId for tracking */}
                            <Button size="sm" variant="outline">View</Button>
                          </Link>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
                <div className="mt-4">
                  <Button variant="outline" className="w-full" onClick={() => setActiveTab("orders")}>
                    View All Orders
                  </Button>
                </div>
              </CardContent>
            </Card>

            {/* Quick Actions (Remains the same) */}
            <Card>
              <CardHeader>
                <CardTitle>Quick Actions</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <Link to="/"> {/* Link to main browse stalls page */}
                    <Button variant="outline" className="w-full flex flex-col h-20">
                      <MapPin className="h-6 w-6 mb-2" />
                      <span className="text-sm">Browse Stalls</span>
                    </Button>
                  </Link>
                  <Link to="/cart">
                    <Button variant="outline" className="w-full flex flex-col h-20">
                      <Package className="h-6 w-6 mb-2" />
                      <span className="text-sm">View Cart</span>
                    </Button>
                  </Link>
                  <Link to="/chat-support"> {/* Placeholder for chat support */}
                    <Button variant="outline" className="w-full flex flex-col h-20">
                      <HelpCircle className="h-6 w-6 mb-2" />
                      <span className="text-sm">Get Help</span>
                    </Button>
                  </Link>
                  <Button variant="outline" className="w-full flex flex-col h-20" onClick={() => setActiveTab("favorites")}>
                    <Heart className="h-6 w-6 mb-2" />
                    <span className="text-sm">Favorites</span>
                  </Button>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="orders" className="space-y-6">
            {/* Full Order History (Now uses dynamic data) */}
            <Card>
              <CardHeader>
                <CardTitle>Order History</CardTitle>
              </CardHeader>
              <CardContent>
                {orderLoading ? (
                  <div className="text-center text-gray-600">Loading full order history...</div>
                ) : orderError ? (
                  <div className="text-center text-red-600">{orderError}</div>
                ) : userOrders.length === 0 ? (
                  <div className="text-center text-gray-600">You haven't placed any orders yet.</div>
                ) : (
                  <div className="space-y-4">
                    {userOrders.map((order) => (
                      <div key={order.id} className="flex items-center justify-between p-4 border rounded-lg">
                        <div className="flex-1">
                          <div className="flex items-center gap-2 mb-1">
                            <h4 className="font-medium">{order.shopName}</h4>
                            <Badge variant={getStatusColor(order.status)} className="capitalize">
                              {order.status.replace(/_/g, ' ')} {/* Format status for display */}
                            </Badge>
                          </div>
                          <p className="text-sm text-gray-600">
                            {order.items.map(item => `${item.productName} x${item.quantity}`).join(", ")}
                          </p>
                          <p className="text-xs text-gray-500">
                            {new Date(order.orderDate).toLocaleDateString()}
                          </p>
                          {/* Removed rating display as it's not directly in OrderDocument, 
                              can be added if you implement a separate review system */}
                        </div>
                        <div className="flex items-center gap-4">
                          <span className="font-medium">₹{order.totalAmount.toFixed(2)}</span>
                          <Link to={`/order-tracking/${order.orderId}`}> {/* Use orderId for tracking */}
                            <Button size="sm" variant="outline">View Details</Button>
                          </Link>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="favorites" className="space-y-6">
            {/* Favorite Stalls (Still uses mock data) */}
            <Card>
              <CardHeader>
                <CardTitle>Favorite Stalls</CardTitle>
              </CardHeader>
              <CardContent>
                {/* This section still uses mockFavoriteStalls. You'll need backend API to make this dynamic. */}
                <div className="grid gap-4">
                  {/*
                  {mockFavoriteStalls.map((stall) => (
                    <div key={stall.id} className="flex items-center justify-between p-4 border rounded-lg">
                      <div className="flex-1">
                        <h4 className="font-medium">{stall.name}</h4>
                        <p className="text-sm text-gray-600">{stall.cuisine} Cuisine</p>
                        <div className="flex items-center gap-2 mt-1">
                          <div className="flex items-center gap-1">
                            <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                            <span className="text-sm">{stall.rating}</span>
                          </div>
                          <span className="text-xs text-gray-500">Last ordered: {stall.lastOrdered}</span>
                        </div>
                      </div>
                      <div className="flex gap-2">
                        <Link to={`/stall/${stall.id}`}>
                          <Button size="sm">Order Again</Button>
                        </Link>
                        <Button size="sm" variant="outline">
                          <Heart className="h-4 w-4" /> 
                        </Button>
                      </div>
                    </div>
                  ))}
                  */}
                  <div className="text-center text-gray-600 p-4">
                    Your favorite stalls will appear here! (Currently using mock data)
                  </div>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="profile" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Profile Information</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid gap-4">
                  <div>
                    <label className="text-sm font-medium">Name</label>
                    <p className="text-sm text-gray-600">{user.name || "N/A"}</p>
                  </div>
                  <div>
                    <label className="text-sm font-medium">Email</label>
                    <p className="text-sm text-gray-600">{user.email || "N/A"}</p> {/* Use dynamic user.email */}
                  </div>
                  <div>
                    <label className="text-sm font-medium">Phone</label>
                    <p className="text-sm text-gray-600">{user.mobileNumber ? `+91 ${user.mobileNumber}` : "N/A"}</p>
                  </div>
                </div>
                <Button className="w-full">
                  <Settings className="h-4 w-4 mr-2" />
                  Edit Profile {/* This button will need implementation for editing profile */}
                </Button>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Preferences</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* These are static preferences for now */}
                <div className="flex items-center justify-between">
                  <span>Email Notifications</span>
                  <Button variant="outline" size="sm">Enabled</Button>
                </div>
                <div className="flex items-center justify-between">
                  <span>SMS Notifications</span>
                  <Button variant="outline" size="sm">Disabled</Button>
                </div>
                <div className="flex items-center justify-between">
                  <span>Order Updates</span>
                  <Button variant="outline" size="sm">Enabled</Button>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default UserDashboard;
