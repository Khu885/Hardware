package com.hardwareaplications.hardware.Service;

import com.hardwareaplications.hardware.Exception.InventoryNotFoundException;
import com.hardwareaplications.hardware.Exception.InventoryValidationException;
import com.hardwareaplications.hardware.HardwaresRepo.InventoryRepo;
import com.hardwareaplications.hardware.HardwaresRepo.ProductRepo;
import com.hardwareaplications.hardware.Inventory;
import com.hardwareaplications.hardware.product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class inventoryService {

    @Autowired
    private InventoryRepo repo;

    @Autowired
    private ProductRepo productRepo;

    private void validateInventory(Inventory inv, boolean isUpdate) {
        if (inv == null) {
            throw new InventoryValidationException("Inventory must not be null");
        }
        if (isUpdate) {
            if (!repo.existsById(inv.getInventoryId())) {
                throw new InventoryValidationException("Inventory id must exist for update");
            }
        }
        if (inv.getProductId() <= 0) {
            throw new InventoryValidationException("Product ID must be positive");
        }
        if (!productRepo.existsById(inv.getProductId())) {
             throw new InventoryValidationException("Product with ID " + inv.getProductId() + " does not exist");
        }
        if (inv.getQuantity() < 0) {
            throw new InventoryValidationException("Quantity must be zero or positive");
        }
        if (inv.getLocation() == null || inv.getLocation().isBlank()) {
            throw new InventoryValidationException("Location must not be blank");
        }
    }

    public List<Inventory> getinventories() {
        return repo.findAll();
    }

    public Inventory getinventorybyID(int inventoryId) {
        return repo.findById(inventoryId).orElseThrow(() -> new InventoryNotFoundException("Inventory not found with id: " + inventoryId));
    }

    public Inventory addinventory(Inventory inv) {
        validateInventory(inv, false);
        return repo.save(inv);
    }

    public Inventory updateinventories(Inventory inv) {
        validateInventory(inv, true);
        repo.save(inv);
         return inv;
    }

    public String deleteinventory(int inventoryId) {
        if (!repo.existsById(inventoryId)) {
            throw new InventoryNotFoundException("Inventory not found with id: " + inventoryId);
        }
        repo.deleteById(inventoryId);
        return "Inventory of id : "+inventoryId+ " is Deleted";
    }

    @Transactional
    public void Load() {
        // Ensure referenced products exist before saving inventory
        if (!productRepo.existsById(1)) {
            productRepo.save(new product(1, "HIT", "HIT", "Red", "Large", "Spray", 50.0));
        }
        if (!productRepo.existsById(2)) {
            productRepo.save(new product(2, "Cycle_oil", "Raftaar", "White(botttle)", "large", "Oil", 25.05));
        }

        List<Inventory> inventoryList = new ArrayList<>(Arrays.asList(
                new Inventory(1, 1, 10, "Backroom showcase"),
                new Inventory(2, 2, 5, "bike room")
        ));
        repo.saveAll(inventoryList);
    }
}
