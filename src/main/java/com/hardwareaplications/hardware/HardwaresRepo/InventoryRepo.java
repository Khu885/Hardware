package com.hardwareaplications.hardware.HardwaresRepo;

import com.hardwareaplications.hardware.Inventory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Integer> {
    List<Inventory> findByProductId(int productId);
}
