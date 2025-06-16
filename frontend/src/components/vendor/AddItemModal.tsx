
import { useState } from "react";
import { X, Upload } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";

interface AddItemModalProps {
  isOpen: boolean;
  onClose: () => void;
  onAddItem: (item: any) => void;
}

const AddItemModal = ({ isOpen, onClose, onAddItem }: AddItemModalProps) => {
  const [formData, setFormData] = useState({
    name: "",
    price: "",
    description: "",
    category: "Main Course",
    isVeg: true,
    preparationTime: "",
    image: "https://images.unsplash.com/photo-1563379091339-03246963d96c?w=300&h=200&fit=crop"
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onAddItem({
      ...formData,
      price: parseInt(formData.price)
    });
    setFormData({
      name: "",
      price: "",
      description: "",
      category: "Main Course",
      isVeg: true,
      preparationTime: "",
      image: "https://images.unsplash.com/photo-1563379091339-03246963d96c?w=300&h=200&fit=crop"
    });
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <Card className="w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <CardContent className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold">Add New Item</h2>
            <Button variant="ghost" size="sm" onClick={onClose}>
              <X className="h-4 w-4" />
            </Button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Image Preview */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Item Image</label>
              <div className="border-2 border-dashed border-gray-300 rounded-lg p-4">
                <img
                  src={formData.image}
                  alt="Item preview"
                  className="w-full h-48 object-cover rounded-lg mb-4"
                />
                <Button type="button" variant="outline" className="w-full">
                  <Upload className="h-4 w-4 mr-2" />
                  Upload Image
                </Button>
              </div>
            </div>

            {/* Item Name */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Item Name *</label>
              <input
                type="text"
                required
                value={formData.name}
                onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter item name"
              />
            </div>

            {/* Price */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Price (₹) *</label>
              <input
                type="number"
                required
                value={formData.price}
                onChange={(e) => setFormData(prev => ({ ...prev, price: e.target.value }))}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter price"
              />
            </div>

            {/* Description */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Description</label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 h-20"
                placeholder="Enter item description"
              />
            </div>

            {/* Category */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Category</label>
              <select
                value={formData.category}
                onChange={(e) => setFormData(prev => ({ ...prev, category: e.target.value }))}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="Main Course">Main Course</option>
                <option value="Appetizer">Appetizer</option>
                <option value="Dessert">Dessert</option>
                <option value="Beverages">Beverages</option>
                <option value="Snacks">Snacks</option>
              </select>
            </div>

            {/* Veg/Non-Veg */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Food Type</label>
              <div className="flex gap-4">
                <label className="flex items-center gap-2">
                  <input
                    type="radio"
                    name="foodType"
                    checked={formData.isVeg}
                    onChange={() => setFormData(prev => ({ ...prev, isVeg: true }))}
                    className="text-green-600"
                  />
                  <Badge className="bg-green-600">Vegetarian</Badge>
                </label>
                <label className="flex items-center gap-2">
                  <input
                    type="radio"
                    name="foodType"
                    checked={!formData.isVeg}
                    onChange={() => setFormData(prev => ({ ...prev, isVeg: false }))}
                    className="text-red-600"
                  />
                  <Badge className="bg-red-600">Non-Vegetarian</Badge>
                </label>
              </div>
            </div>

            {/* Preparation Time */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Preparation Time</label>
              <input
                type="text"
                value={formData.preparationTime}
                onChange={(e) => setFormData(prev => ({ ...prev, preparationTime: e.target.value }))}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="e.g., 15-20 min"
              />
            </div>

            {/* Submit Buttons */}
            <div className="flex gap-4 pt-4">
              <Button type="button" variant="outline" onClick={onClose} className="flex-1">
                Cancel
              </Button>
              <Button type="submit" className="flex-1">
                Add Item
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default AddItemModal;
