// src/components/vendor/EditItemModal.tsx
import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";
import { toast } from "@/hooks/use-toast";

// Ensure this matches your Item interface in VendorDashboard.tsx
interface Item {
  id: number;
  name: string;
  price: number;
  image: string;
  description: string;
  isVeg: boolean;
  category: string;
  isAvailable: boolean;
  preparationTime: string;
  isKetoFriendly: boolean;
  isLowCarb: boolean;
  isHighProtein: boolean;
}

interface EditItemModalProps {
  isOpen: boolean;
  onClose: () => void;
  itemData: Item | null; // The item object to pre-fill the form
  // onUpdateItem now accepts an updatedData object and optionally a file to upload
  onUpdateItem: (id: number, updatedData: Omit<Item, 'id' | 'isAvailable'>, imageFile?: File | null) => void;
}

const categories = ["Vegetarian", "Non-Vegetarian", "Beverages", "Desserts", "Snacks"];

const EditItemModal: React.FC<EditItemModalProps> = ({
  isOpen,
  onClose,
  itemData,
  onUpdateItem,
}) => {
  const [formData, setFormData] = useState({
    name: '',
    price: 0,
    description: '',
    category: '',
    isVeg: false,
    image: '', // This will hold the URL
    preparationTime: '',
    isKetoFriendly: false,
    isHighProtein: false,
    isLowCarb: false,
  });

  // State to hold the selected file for upload
  const [imageFile, setImageFile] = useState<File | null>(null);

  // Effect to pre-fill form data when itemData changes
  useEffect(() => {
    if (itemData) {
      setFormData({
        name: itemData.name,
        price: itemData.price,
        description: itemData.description,
        category: itemData.category,
        isVeg: itemData.isVeg,
        image: itemData.image || '',
        preparationTime: itemData.preparationTime,
        isKetoFriendly: itemData.isKetoFriendly,
        isHighProtein: itemData.isHighProtein,
        isLowCarb: itemData.isLowCarb,
      });
      setImageFile(null); // Clear any previously selected file when opening for a new item
    }
  }, [itemData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { id, value } = e.target;
    setFormData((prev) => ({ ...prev, [id]: value }));
  };

  const handlePriceChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setFormData((prev) => ({ ...prev, price: parseFloat(value) || 0 }));
  };

  const handleSelectChange = (id: string, value: string) => {
    setFormData((prev) => ({ ...prev, [id]: value }));
  };

  const handleBooleanSwitchChange = (field: keyof typeof formData) => (checked: boolean) => {
    setFormData((prev) => ({ ...prev, [field]: checked }));
  };

  // New handler for file input
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setImageFile(e.target.files[0]);
      // Optional: Clear the image URL input if a file is selected
      setFormData((prev) => ({ ...prev, image: '' }));
    } else {
      setImageFile(null);
    }
  };

  const handleSubmit = async () => { // Made handleSubmit async
    if (!itemData) return;

    // Basic validation
    if (!formData.name || !formData.description || formData.price <= 0 || !formData.category || !formData.preparationTime) {
      toast({
        title: "Validation Error",
        description: "Please fill in all required fields correctly.",
        variant: "destructive",
      });
      return;
    }

    // Pass the imageFile to onUpdateItem. The parent component will handle the upload.
    // The image URL in formData will be used if no new file is selected.
    onUpdateItem(itemData.id, formData, imageFile);
    onClose();
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Edit Menu Item</DialogTitle>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="name" className="text-right">
              Name *
            </Label>
            <Input
              id="name"
              value={formData.name}
              onChange={handleChange}
              className="col-span-3"
            />
          </div>
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="price" className="text-right">
              Price *
            </Label>
            <Input
              id="price"
              type="number"
              value={formData.price}
              onChange={handlePriceChange}
              className="col-span-3"
            />
          </div>
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="description" className="text-right">
              Description *
            </Label>
            <Textarea
              id="description"
              value={formData.description}
              onChange={handleChange}
              className="col-span-3"
            />
          </div>
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="category" className="text-right">
              Category *
            </Label>
            <Select onValueChange={(value) => handleSelectChange('category', value)} value={formData.category}>
              <SelectTrigger className="col-span-3">
                <SelectValue placeholder="Select category" />
              </SelectTrigger>
              <SelectContent>
                {categories.map((cat) => (
                  <SelectItem key={cat} value={cat}>{cat}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="isVeg" className="text-right">
              Vegetarian
            </Label>
            <Switch
              id="isVeg"
              checked={formData.isVeg}
              onCheckedChange={handleBooleanSwitchChange('isVeg')}
              className="col-span-3"
            />
          </div>
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="isKetoFriendly" className="text-right">
              Keto Friendly
            </Label>
            <Switch
              id="isKetoFriendly"
              checked={formData.isKetoFriendly}
              onCheckedChange={handleBooleanSwitchChange('isKetoFriendly')}
              className="col-span-3"
            />
          </div>
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="isHighProtein" className="text-right">
              High Protein
            </Label>
            <Switch
              id="isHighProtein"
              checked={formData.isHighProtein}
              onCheckedChange={handleBooleanSwitchChange('isHighProtein')}
              className="col-span-3"
            />
          </div>
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="isLowCarb" className="text-right">
              Low Carb
            </Label>
            <Switch
              id="isLowCarb"
              checked={formData.isLowCarb}
              onCheckedChange={handleBooleanSwitchChange('isLowCarb')}
              className="col-span-3"
            />
          </div>

          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="preparationTime" className="text-right">
              Prep. Time (min) *
            </Label>
            <Input
              id="preparationTime"
              value={formData.preparationTime}
              onChange={handleChange}
              className="col-span-3"
              placeholder="e.g., 20"
            />
          </div>

          {/* Existing Image URL Input */}
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="image" className="text-right">
              Image URL
            </Label>
            <Input
              id="image"
              value={formData.image}
              onChange={handleChange}
              className="col-span-3"
              placeholder="http://example.com/image.jpg"
              disabled={!!imageFile} // Disable if a file is selected
            />
          </div>

          {/* New Image File Upload Input */}
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="imageFile" className="text-right">
              Upload Image
            </Label>
            <Input
              id="imageFile"
              type="file"
              accept="image/*" // Restrict to image files
              onChange={handleFileChange}
              className="col-span-3"
            />
          </div>

          {/* Display current image or preview new one */}
          {(formData.image || imageFile) && (
            <div className="grid grid-cols-4 items-center gap-4 mt-2">
              <Label className="text-right">Preview</Label>
              <div className="col-span-3">
                {imageFile ? (
                  <img src={URL.createObjectURL(imageFile)} alt="New Image Preview" className="w-24 h-24 object-cover rounded" />
                ) : (
                  <img src={formData.image} alt="Current Item Image" className="w-24 h-24 object-cover rounded" />
                )}
              </div>
            </div>
          )}

        </div>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancel</Button>
          <Button onClick={handleSubmit}>Update</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default EditItemModal;