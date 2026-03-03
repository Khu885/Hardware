package com.hardwareaplications.hardware.validate;

import com.hardwareaplications.hardware.HardwaresRepo.InventoryRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExistsInventoryValidator implements ConstraintValidator<ExistsInventory, Integer> {

    @Autowired
    private InventoryRepo inventoryRepo;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Use @NotNull or @Positive separately if null is not allowed
        }
        if (inventoryRepo == null) {
            return true;
        }
        return inventoryRepo.existsById(value);
    }
}
