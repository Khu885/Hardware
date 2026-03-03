package com.hardwareaplications.hardware.validate;

import com.hardwareaplications.hardware.HardwaresRepo.ProductRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExistsProductValidator implements ConstraintValidator<ExistsProduct, Integer> {

    @Autowired
    private ProductRepo productRepo;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (productRepo == null) {
            // Validator not Spring-managed; skip DB check to avoid NPE
            return true;
        }
        return productRepo.existsById(value);
    }
}
