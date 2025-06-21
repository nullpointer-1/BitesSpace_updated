package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.VendorService;
import com.example.shopmanagement.dto.AddVendorDto;
import com.example.shopmanagement.dto.UpdateVendorDto;
import com.example.shopmanagement.dto.VendorDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vendors") // This new controller handles admin-specific vendor operations
public class AdminVendorController {

    private final VendorService vendorService;

    @Autowired
    public AdminVendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Retrieves a list of all vendors. (Admin access)
     * GET /api/admin/vendors
     * @return ResponseEntity containing a list of VendorDto objects.
     */
    @GetMapping
    public ResponseEntity<List<VendorDto>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    /**
     * Retrieves a vendor by their ID. (Admin access)
     * GET /api/admin/vendors/{id}
     * @param id The ID of the vendor to retrieve.
     * @return ResponseEntity with the VendorDto or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendorDto> getVendorById(@PathVariable Long id) {
        try {
            VendorDto vendorDto = vendorService.getVendorById(id);
            return ResponseEntity.ok(vendorDto);
        } catch (RuntimeException e) {
            System.err.println("Error retrieving vendor by ID: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint to add a new vendor. (Admin access)
     * POST /api/admin/vendors
     * @param addVendorDto DTO containing details for the new vendor.
     * @return The created VendorDto with HTTP status 201 Created, or an error.
     */
    @PostMapping
    public ResponseEntity<VendorDto> addVendor(@Valid @RequestBody AddVendorDto addVendorDto) {
        try {
            VendorDto newVendor = vendorService.addVendor(addVendorDto);
            return new ResponseEntity<>(newVendor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.err.println("Error adding vendor: " + e.getMessage()); // Log error message
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error adding vendor: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to update an existing vendor. (Admin access)
     * PUT /api/admin/vendors/{id}
     * @param id The ID of the vendor to update.
     * @param updateVendorDto DTO containing updated vendor details.
     * @return The updated VendorDto with HTTP status 200 OK, or an error.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VendorDto> updateVendor(@PathVariable Long id, @Valid @RequestBody UpdateVendorDto updateVendorDto) {
        try {
            if (!id.equals(updateVendorDto.getId())) {
                System.err.println("Error updating vendor: Path ID does not match DTO ID.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            VendorDto updatedVendor = vendorService.updateVendor(id, updateVendorDto);
            return ResponseEntity.ok(updatedVendor);
        } catch (IllegalArgumentException e) {
            System.err.println("Error updating vendor: " + e.getMessage()); // Log error message
            return ResponseEntity.notFound().build(); // Or HttpStatus.BAD_REQUEST depending on specific IllegalArgumentException
        } catch (Exception e) {
            System.err.println("Error updating vendor: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a vendor by their ID. (Admin access)
     * DELETE /api/admin/vendors/{id}
     * @param id The ID of the vendor to delete.
     * @return HTTP status 204 No Content on successful deletion, or an error.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        try {
            vendorService.deleteVendor(id);
            return ResponseEntity.noContent().build();
        }
        catch (RuntimeException e) {
            System.err.println("Error deleting vendor: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}