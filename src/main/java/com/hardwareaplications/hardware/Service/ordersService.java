package com.hardwareaplications.hardware.Service;

import com.hardwareaplications.hardware.Exception.*;

import com.hardwareaplications.hardware.HardwaresRepo.InventoryRepo;
import com.hardwareaplications.hardware.HardwaresRepo.ProductRepo;
import com.hardwareaplications.hardware.HardwaresRepo.orderRepo;
import com.hardwareaplications.hardware.Inventory;
import com.hardwareaplications.hardware.OrderItem;
import com.hardwareaplications.hardware.orders;
import com.hardwareaplications.hardware.product;
import com.hardwareaplications.hardware.customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ordersService {

    @Autowired
    private orderRepo ordRepo;

    @Autowired
    private InventoryRepo inventoryRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private customerservice customerService;

    // VALIDATION

    private void validateOrder(orders o, boolean isUpdate) {
        if (o == null) {
            throw new OrderValidationException("Order must not be null");
        }
        if (isUpdate) {
            if (!ordRepo.existsById(o.getOrderId())) {
                throw new OrderValidationException("Order id must exist for update");
            }
        }
        // Validate customer exists
        try {
            customerService.getcustomerbyid(o.getCustomerId());
        } catch (CustomerNotFoundException e) {
            throw new OrderValidationException("Customer with ID " + o.getCustomerId() + " does not exist");
        }
        // Validate items
        if (o.getItems() == null || o.getItems().isEmpty()) {
            throw new OrderValidationException("Order must contain at least one item");
        }
        for (OrderItem item : o.getItems()) {
            if (!productRepo.existsById(item.getProductId())) {
                throw new OrderValidationException("Product with ID " + item.getProductId() + " does not exist");
            }
            if (item.getQuantity() <= 0) {
                throw new OrderValidationException(
                        "Item quantity must be positive for product ID: " + item.getProductId());
            }
        }
        if (o.getOrderDate() == null || o.getOrderDate().isBlank()) {
            throw new OrderValidationException("Order date must not be blank");
        }
    }

    private double calculateTotalAmount(orders o) {
        if (o == null || o.getItems() == null)
            return 0.0;
        double total = 0.0;
        for (OrderItem item : o.getItems()) {
            product p = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            "Product with id " + item.getProductId() + " not found"));
            if (p.getPrice() == null) {
                throw new RuntimeException("Product price is not set for product id: " + item.getProductId());
            }
            double unitPrice = p.getPrice();
            item.setUnitPrice(unitPrice); // price at order time
            total += unitPrice * item.getQuantity();
        }
        return total;
    }

    // ─────────────────────────────────────────────
    // PROCESS ORDER (deduct inventory for every item)
    private void processOrder(orders o) {
        for (OrderItem item : o.getItems()) {
            List<Inventory> inventories = inventoryRepo.findByProductId(item.getProductId());
            if (inventories.isEmpty()) {
                throw new InventoryNotFoundException("No inventory found for product ID: " + item.getProductId());
            }

            int remaining = item.getQuantity();
            // Calculate total available across all inventory records
            int totalAvailable = inventories.stream().mapToInt(Inventory::getQuantity).sum();
            if (totalAvailable < remaining) {
                throw new InventoryValidationException("Insufficient inventory for product ID: " + item.getProductId()
                        + " (available: " + totalAvailable + ", requested: " + item.getQuantity() + ")");
            }

            // Deduct across inventory rows until the requested quantity is fulfilled
            for (Inventory inventory : inventories) {
                if (remaining <= 0)
                    break;
                int take = Math.min(inventory.getQuantity(), remaining);
                inventory.setQuantity(inventory.getQuantity() - take);
                remaining -= take;
                inventoryRepo.save(inventory);
            }

            // remaining should be 0 here because we checked totalAvailable above
        }
    }

    // CRUD methords

    public List<orders> getorders() {
        return ordRepo.findAll();
    }

    public orders getorderbyID(int oId) {
        return ordRepo.findById(oId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + oId + " not found"));
    }

    // Ensure each OrderItem has its parent `orders` reference set (owner side of
    // relation)
    private void linkOrderItems(orders o) {
        if (o == null || o.getItems() == null)
            return;
        for (OrderItem item : o.getItems()) {
            item.setOrder(o);
        }
    }

    public orders addorder(orders o) {
        validateOrder(o, false);
        o.setTotalAmount(calculateTotalAmount(o));

        if (o.isProcessed()) {
            processOrder(o);
            // Increase customer due_amt
            customer c = customerService.getcustomerbyid(o.getCustomerId());
            int addAmt = (int) Math.round(o.getTotalAmount());
            c.setDue_amt(c.getDue_amt() + addAmt);
            customerService.updatecustomer(c);
        }

        // link items to parent before saving so the FK 'order_id' is populated
        linkOrderItems(o);
        return ordRepo.save(o);
    }

    public orders updateorders(orders o) {
        validateOrder(o, true);
        Optional<orders> existingOrder = ordRepo.findById(o.getOrderId());
        o.setTotalAmount(calculateTotalAmount(o));

        if (o.isProcessed()) {
            // Only deduct inventory / update due if order wasn't already processed
            if (existingOrder.isEmpty() || !existingOrder.get().isProcessed()) {
                processOrder(o);
                customer c = customerService.getcustomerbyid(o.getCustomerId()); // get customer to update due_amt
                int addAmt = (int) Math.round(o.getTotalAmount());
                c.setDue_amt(c.getDue_amt() + addAmt); // add previous due amount with total of current amt
                customerService.updatecustomer(c);
            }
        }

        linkOrderItems(o);
        return ordRepo.save(o);
    }

    // RETURN ORDER (restore inventory, reduce due_amt, )

    public orders returnOrder(int oId) {
        orders o = ordRepo.findById(oId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + oId + " not found"));

        if (!o.isProcessed()) {
            throw new OrderValidationException("Order with id " + oId + " is not processed or already returned");
        }

        // Restore inventory for every item: distribute returned qty across all
        // inventory rows
        for (OrderItem item : o.getItems()) {
            List<Inventory> inventories = inventoryRepo.findByProductId(item.getProductId());
            if (inventories.isEmpty()) {
                throw new InventoryNotFoundException(
                        "No inventory found for product ID: " + item.getProductId() + " to restore");
            }

            int remaining = item.getQuantity();
            int n = inventories.size();
            int baseAdd = remaining / n;
            int rem = remaining % n;

            for (int i = 0; i < n; i++) {
                int add = baseAdd + (i < rem ? 1 : 0);
                Inventory inv = inventories.get(i);
                inv.setQuantity(inv.getQuantity() + add);
                inventoryRepo.save(inv);
            }
        }

        // Reduce customer due_amt
        customer c = customerService.getcustomerbyid(o.getCustomerId());
        int reduceAmt = (int) Math.round(o.getTotalAmount());
        int newDue = Math.max(0, c.getDue_amt() - reduceAmt); // floor at 0
        c.setDue_amt(newDue);
        customerService.updatecustomer(c);

        // Mark as returned
        o.setProcessed(false);
        return ordRepo.save(o);
    }

    public String deleteorder(int oId) {
        // Load order or fail fast
        orders o = ordRepo.findById(oId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + oId + " not found"));

        // If order was processed, we must reverse its side-effects before deletion
        if (o.isProcessed()) {
            // 1) Restore inventory for every item (distribute across inventory rows)
            for (OrderItem item : o.getItems()) {
                List<Inventory> inventories = inventoryRepo.findByProductId(item.getProductId()); // get all inventory
                                                                                                  // rows for the
                                                                                                  // product
                if (inventories.isEmpty()) {
                    throw new InventoryNotFoundException(
                            "Cannot delete processed order: no inventory record found for product ID "
                                    + item.getProductId());
                }

                int remaining = item.getQuantity();
                int n = inventories.size();
                int baseAdd = remaining / n;
                int rem = remaining % n;

                for (int i = 0; i < n; i++) {
                    int add = baseAdd + (i < rem ? 1 : 0);
                    Inventory inv = inventories.get(i);
                    inv.setQuantity(inv.getQuantity() + add);
                    inventoryRepo.save(inv);
                }
            }

            // 2) Reduce customer's due amount by the order total ( > 0)
            customer c = customerService.getcustomerbyid(o.getCustomerId());
            int reduceAmt = (int) Math.round(o.getTotalAmount());
            int newDue = Math.max(0, c.getDue_amt() - reduceAmt);
            c.setDue_amt(newDue);
            customerService.updatecustomer(c);
        }

        // Finally delete the order. Cascade will remove OrderItem entries
        // (orphanRemoval: true)
        ordRepo.deleteById(oId);
        return "Order of id: " + oId + " deleted";
    }

    public void Load() {
        // If a sample order with the same customer and date already exists, skip creating another
        boolean sampleExists = ordRepo.findAll().stream()
                .anyMatch(x -> x.getCustomerId() == 1 && "2026-01-01".equals(x.getOrderDate()));
        if (sampleExists) {
            // Avoid creating duplicate sample orders which causes the auto-increment id to increase
            return;
        }

        // Ensure customer 1 exists to bypass @ExistsCustomer validation
        try {
            customerService.getcustomerbyid(1);
        } catch (CustomerNotFoundException e) {
            customer defaultCustomer = new customer(1, "Test Customer", 1234567890, "123 Test St", 0);
            customerService.addcustomer(defaultCustomer);
        }

        // Ensure products 1 and 2 exist and have prices
        product p1 = productRepo.findById(1).orElse(null);
        if (p1 == null) {
            productRepo.save(new product(1, "TestProductOne", "BrandA", "Red", "Large", "TypeA", 10.0));
        } else if (p1.getPrice() == null) {
            p1.setPrice(10.0);
            productRepo.save(p1);
        }

        product p2 = productRepo.findById(2).orElse(null);
        if (p2 == null) {
            productRepo.save(new product(2, "TestProductTwo", "BrandB", "Blue", "Small", "TypeB", 20.0));
        } else if (p2.getPrice() == null) {
            p2.setPrice(20.0);
            productRepo.save(p2);
        }

        // Sample order: 1 customer, 2 different products in one order
        orders o = new orders();
        o.setCustomerId(1);
        o.setOrderDate("2026-01-01");
        o.setProcessed(false);

        // create OrderItems linked to the same order `o`
        OrderItem item1 = new OrderItem(0, o, 1, 2, 0.0); // itemId=0, order=o, productId=1, qty=2
        OrderItem item2 = new OrderItem(0, o, 2, 3, 0.0); // itemId=0, order=o, productId=2, qty=3 (fix: previously used o1)

        // Use mutable ArrayList — List.of() creates an immutable list that Hibernate
        // cannot work with
        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        o.setItems(items);

        // Only calculate total if products exist; otherwise leave as 0
        try {
            o.setTotalAmount(calculateTotalAmount(o));
        } catch (ProductNotFoundException e) {
            // Products not yet loaded — set total to 0
            o.setTotalAmount(0.0);
        }

        linkOrderItems(o);
        ordRepo.save(o);
    }
}
