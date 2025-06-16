// src/App.tsx
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"; // Import Navigate
// import Index from "./pages/Index"; // <--- Remove this import (or rename it if it's your new UserBrowseStallsPage)
import UserBrowseStallsPage from "./pages/UserBrowseStallsPage"; // <--- Import your new Browse Stalls Page
import StallPage from "./pages/StallPage";
import CartPage from "./pages/CartPage";
import CheckoutPage from "./pages/CheckoutPage";
import OrderTrackingPage from "./pages/OrderTrackingPage";
import VendorDashboard from "./pages/VendorDashboard";
import VendorLoginPage from "./pages/VendorLoginPage";
import UserLoginPage from "./pages/UserLoginPage";
import UserDashboardPage from "./pages/UserDashboard";
import NotFound from "./pages/NotFound";
import { CartProvider } from './context/CartContext';
import ChatSupportPage from "./pages/ChatSupportPage";
import SupportStaffLoginPage from "./pages/SupportStaffLoginPage";
import SupportDashboard from "./pages/SupportDashboard";
import DeliveryLoginPage from "./pages/DeliveryLoginPage";
import { UserProvider, useUser } from './context/UserContext';
import DeliveryDashboard from "./pages/DeliveryDashboard";
import Index from "./pages/Index";

const queryClient = new QueryClient();

// A Protected Route Wrapper
const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
  const { isAuthenticated } = useUser();
  if (!isAuthenticated) {
    // Redirect to login page if not authenticated
    return <Navigate to="/login/user" replace />;
  }
  return children;
};

// A component to decide where to go from root (/)
const HomeOrDashboard = () => {
  const { isAuthenticated } = useUser();
  // If authenticated, go to dashboard.
  // Otherwise, go to the UserBrowseStallsPage.
  return isAuthenticated ? <Navigate to="/dashboard" replace /> : <Navigate to="/user/browse/stalls" replace />;
};


const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <UserProvider>
          <CartProvider>
            <Routes>
              {/* Public routes */}
              <Route path="/" element={<HomeOrDashboard />} /> {/* Decides between Dashboard and Browse Stalls */}
              <Route path="/login/user" element={<UserLoginPage />} />
              <Route path="/login/vendor" element={<VendorLoginPage />} />
              <Route path="/login/support" element={<SupportStaffLoginPage />} />
              <Route path="/login/delivery" element={<DeliveryLoginPage />} />
              
              {/* New route for browsing stalls */}
              <Route path="/user/browse/stalls" element={<Index />} /> {/* <--- New Route */}

              {/* Protected User Dashboard Route */}
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute>
                    <UserDashboardPage />
                  </ProtectedRoute>
                }
              />

              {/* Other application routes that might need protection */}
              {/* Consider wrapping these with <ProtectedRoute> if they should only be accessible when logged in */}
              <Route path="/stall/:stallId" element={<StallPage />} />
              <Route path="/cart" element={<CartPage />} />
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/order-tracking/:orderId" element={<OrderTrackingPage />} />
              
              {/* Vendor, Support, Delivery dashboards */}
              <Route path="/vendor/dashboard" element={<VendorDashboard />} />
              <Route path="/chat-support" element={<ChatSupportPage />} />
              <Route path="/support/dashboard" element={<SupportDashboard />} />
              <Route path="/delivery/dashboard" element={<DeliveryDashboard />} />
              
              {/* Catch-all for 404 */}
              <Route path="*" element={<NotFound />} />
            </Routes>
          </CartProvider>
        </UserProvider>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
