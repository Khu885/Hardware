package com.hardwareaplications.hardware.Controller;


import com.hardwareaplications.hardware.Inventory;
import com.hardwareaplications.hardware.Service.inventoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Valid;
import java.util.List;

@RestController
public class InventoryController {
    @Autowired
    private inventoryService service;

    @GetMapping("/inventories")
    public List<Inventory> getinventories() {
        return service.getinventories();
    }

    @GetMapping("/inventories/{inventoryId}")
    public Inventory getinventorybyID(@PathVariable int inventoryId) {
        return service.getinventorybyID(inventoryId);
    }

    @PostMapping("/inventories")
    public Inventory addinventory(@Valid @RequestBody Inventory i) {
        return service.addinventory(i);
    }

    @PutMapping("/inventories")
    public Inventory updateinventories(@Valid @RequestBody Inventory i) {
        return service.updateinventories(i);
    }

    @DeleteMapping("/inventories/{inventoryId}")
    public String deleteinventory(@PathVariable int inventoryId) {
        return service.deleteinventory(inventoryId);
    }

    @GetMapping("/LoadInventories")
    public String Load() {
        try {
            service.Load();
            return "Inventory Data Loaded";
        } catch (Exception ex) {
            // log for server console
            ex.printStackTrace();
            // return message for debugging (temporary)
            return "Load failed: " + ex.getClass().getSimpleName() + ": " + ex.getMessage();
        }
    }
}
