// src/pages/StallPage.tsx
import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import StallHeader from "@/components/stall/StallHeader";
import StallHero from "@/components/stall/StallHero";
import MenuSection from "@/components/stall/MenuSection";
import { useCart } from '@/context/CartContext';

// Import UI components for dialog (assuming you have them, e.g., Shadcn UI)
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";


// --- Define Interfaces for your data structure ---
interface Shop {
  id: number;
  name: string;
  address: string;
  contactNumber: string;
  cuisine: string;
  active: boolean;
  imageUrl: string;
  rating: number;
  deliveryTime: string;
  distance: string;
  speciality: string;
  isVeg: boolean;
  featured: boolean;
}

// NOTE: Product must now include shopId for cart logic
interface Product {
  id: number;
  name: string;
  price: number;
  imageUrl: string;
  description: string;
  isVeg: boolean;
  category: string;
  rating: number;
  preparationTime: string;
  shopId: number; // <--- IMPORTANT: Ensure your backend Product model/DTO returns this!
}

interface MenuItem extends Product {
  image: string;
}

interface StallHeroDisplayData { /* ... as before ... */ }
// --- End of Interfaces ---


const StallPage = () => {
  const { stallId } = useParams<{ stallId: string }>(); 
  const { cart, currentShopId, currentShopName, addToCart, removeFromCart, clearCartAndAddProduct, getCartItemQuantity, totalItemsInCart } = useCart(); // Use totalItemsInCart from context

  const [stall, setStall] = useState<StallHeroDisplayData | null>(null);
  const [items, setItems] = useState<MenuItem[]>([]);
  const [filter, setFilter] = useState<string>("all");
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // State for shop mismatch dialog
  const [showMismatchDialog, setShowMismatchDialog] = useState(false);
  const [productToClearAndAdd, setProductToClearAndAdd] = useState<MenuItem | null>(null);

  useEffect(() => {
    const fetchStallData = async () => {
      if (!stallId) {
        setError("Stall ID is missing.");
        setLoading(false);
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const stallResponse = await axios.get<Shop>(`http://localhost:8989/api/shops/${stallId}`);
        const fetchedStall = stallResponse.data;

        const mappedStallForHero: StallHeroDisplayData = {
          name: fetchedStall.name,
          image: fetchedStall.imageUrl,
          rating: fetchedStall.rating,
          time: fetchedStall.deliveryTime,
          distance: fetchedStall.distance,
          cuisine: fetchedStall.cuisine,
          speciality: fetchedStall.speciality,
          isVeg: fetchedStall.isVeg,
        };
        setStall(mappedStallForHero);

        const itemsResponse = await axios.get<Product[]>(`http://localhost:8989/api/products/shop/${stallId}`);
        
        if (Array.isArray(itemsResponse.data)) {
          const fetchedProducts: Product[] = itemsResponse.data;
          const mappedItems: MenuItem[] = fetchedProducts.map(item => ({
            ...item,
            image: item.imageUrl
            // Make sure product.shopId is available from backend if you need it.
            // If not directly available, you'll need to fetch the shop details here
            // or modify your Product DTO to include shopId.
            // For now, let's assume `product.shopId` exists.
          }));
          setItems(mappedItems);
        } else {
          console.error("API did not return an array for products:", itemsResponse.data);
          setItems([]);
        }

      } catch (err: any) {
        console.error("Error fetching stall data:", err);
        if (axios.isAxiosError(err) && err.response) {
            if (err.response.status === 404) {
                setError("Stall not found.");
            } else {
                setError(`Failed to load stall details: ${err.response.statusText || err.message}`);
            }
        } else {
            setError("An unexpected error occurred while loading stall details.");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchStallData();
  }, [stallId]);

  const handleAddToCart = (item: MenuItem) => {
    // Pass the full product object to addToCart in context
    const result = addToCart(item);
    if (result === 'mismatch') {
      setProductToClearAndAdd(item);
      setShowMismatchDialog(true);
    }
  };

  const handleRemoveFromCart = (itemId: number) => {
    removeFromCart(itemId);
  };

  const handleClearCartAndAdd = () => {
    if (productToClearAndAdd) {
      clearCartAndAddProduct(productToClearAndAdd);
      setProductToClearAndAdd(null);
      setShowMismatchDialog(false);
    }
  };

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center text-lg text-gray-700">Loading stall details...</div>;
  }

  if (error) {
    return <div className="min-h-screen flex items-center justify-center text-lg text-red-700 font-semibold">Error: {error}</div>;
  }

  if (!stall) {
    return <div className="min-h-screen flex items-center justify-center text-lg text-gray-700">Stall not found.</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <StallHeader stallName={stall.name} totalItems={totalItemsInCart} /> {/* Use totalItemsInCart from context */}
      <StallHero stall={stall} />
      <MenuSection
        items={items}
        filter={filter}
        onFilterChange={setFilter}
        cart={cart}
        onAddToCart={handleAddToCart}
        onRemoveFromCart={handleRemoveFromCart}
        getCartItemQuantity={getCartItemQuantity}
      />

      {/* Shop Mismatch Alert Dialog */}
      <AlertDialog open={showMismatchDialog} onOpenChange={setShowMismatchDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Items from another shop detected!</AlertDialogTitle>
            <AlertDialogDescription>
              Your cart currently contains items from **{currentShopName || 'another shop'}**.
              To add items from **{stall.name}**, you need to clear your current cart.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleClearCartAndAdd}>
              Clear Cart & Add
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
};

export default StallPage;