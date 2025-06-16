
import { Plus, Minus, Star } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";

interface FoodItemProps {
  item: {
    id: number;
    name: string;
    price: number;
    image: string;
    description: string;
    isVeg: boolean;
    category: string;
    rating: number;
    preparationTime: string;
  };
  cartQuantity: number;
  onAddToCart: () => void;
  onRemoveFromCart: () => void;
}

const FoodItem = ({ item, cartQuantity, onAddToCart, onRemoveFromCart }: FoodItemProps) => {
  return (
    <Card className="overflow-hidden hover:shadow-lg transition-shadow">
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
      </div>
      <CardContent className="p-4">
        <div className="flex items-center gap-1 mb-2">
          <Star className="h-4 w-4 text-yellow-400 fill-current" />
          <span className="text-sm font-medium">{item.rating}</span>
          <span className="text-gray-400">•</span>
          <span className="text-sm text-gray-600">{item.preparationTime}</span>
        </div>
        <h4 className="text-lg font-semibold text-gray-900 mb-2">{item.name}</h4>
        <p className="text-gray-600 text-sm mb-3">{item.description}</p>
        <div className="flex items-center justify-between">
          <span className="text-xl font-bold text-orange-600">₹{item.price}</span>
          <div className="flex items-center gap-2">
            {cartQuantity ? (
              <div className="flex items-center gap-2">
                <Button
                  size="sm"
                  variant="outline"
                  onClick={onRemoveFromCart}
                >
                  <Minus className="h-4 w-4" />
                </Button>
                <span className="font-medium">{cartQuantity}</span>
                <Button
                  size="sm"
                  onClick={onAddToCart}
                >
                  <Plus className="h-4 w-4" />
                </Button>
              </div>
            ) : (
              <Button
                size="sm"
                onClick={onAddToCart}
                className="flex items-center gap-1"
              >
                <Plus className="h-4 w-4" />
                Add
              </Button>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default FoodItem;
