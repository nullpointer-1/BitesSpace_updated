import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios"; // Import axios
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Store, Eye, EyeOff } from "lucide-react";
import { toast } from "@/hooks/use-toast";

const VendorLoginPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showForgotPassword, setShowForgotPassword] = useState(false);
  const [forgotEmail, setForgotEmail] = useState(""); // This is not used in your backend for login, but kept for consistency with frontend mock
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await axios.post("http://localhost:8989/api/vendors/login", {
        username,
        password,
      });

      // Assuming your backend returns { token: "...", vendor: { ... } } on success
      const { token, vendor } = response.data;

      if (token) {
        // Store the token (e.g., in localStorage)
        localStorage.setItem("vendorToken", token);
        // Optionally store vendor details if needed in other parts of the app
        localStorage.setItem("vendorDetails", JSON.stringify(vendor));

        toast({
          title: "Login Successful",
          description: "Welcome back to your vendor dashboard!",
        });
        navigate("/vendor/dashboard"); // Navigate to the dashboard after successful login
      } else {
        toast({
          title: "Login Failed",
          description: "No token received. Please check credentials.",
          variant: "destructive",
        });
      }
    } catch (error: any) {
      console.error("Login error:", error);
      const errorMessage = error.response?.data?.message || "An unexpected error occurred during login.";
      toast({
        title: "Login Failed",
        description: errorMessage,
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleForgotPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    // This part still uses a setTimeout as there's no backend endpoint for forgot password yet.
    // If you implement a backend endpoint, replace this with an axios.post call.
    setTimeout(() => {
      if (forgotEmail) {
        toast({
          title: "Password Reset Email Sent",
          description: "Check your email for password reset instructions.",
        });
        setShowForgotPassword(false);
        setForgotEmail("");
      } else {
        toast({
          title: "Error",
          description: "Please enter your email.",
          variant: "destructive",
        });
      }
      setIsLoading(false);
    }, 1000);
  };

  if (showForgotPassword) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <div className="flex justify-center mb-4">
              <Store className="h-12 w-12 text-primary" />
            </div>
            <CardTitle className="text-2xl font-bold">Reset Password</CardTitle>
            <p className="text-muted-foreground">
              Enter your email to receive reset instructions
            </p>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleForgotPassword} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">Email Address</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="vendor@example.com"
                  value={forgotEmail}
                  onChange={(e) => setForgotEmail(e.target.value)}
                  required
                />
              </div>

              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "Sending..." : "Send Reset Link"}
              </Button>

              <Button
                type="button"
                variant="ghost"
                className="w-full"
                onClick={() => setShowForgotPassword(false)}
              >
                Back to Login
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className="flex justify-center mb-4">
            <Store className="h-12 w-12 text-primary" />
          </div>
          <CardTitle className="text-2xl font-bold">Vendor Login</CardTitle>
          <p className="text-muted-foreground">
            Access your vendor dashboard
          </p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleLogin} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                type="text"
                placeholder="Enter your username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                </Button>
              </div>
            </div>

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? "Signing In..." : "Sign In"}
            </Button>

            <div className="text-center">
              <Button
                type="button"
                variant="link"
                className="text-sm"
                onClick={() => setShowForgotPassword(true)}
              >
                Forgot Password?
              </Button>
            </div>
          </form>

          <Separator className="my-6" />

          <div className="text-center">
            <p className="text-sm text-muted-foreground">
              Don't have an account?{" "}
              <Button variant="link" className="p-0 text-sm">
                Contact Support
              </Button>
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default VendorLoginPage;