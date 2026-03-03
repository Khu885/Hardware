package com.hardwareaplications.hardware.validate;

import com.hardwareaplications.hardware.HardwaresRepo.orderRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExistsOrderValidator implements ConstraintValidator<ExistsOrder, Integer> {

    @Autowired
    private orderRepo orderRepo;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Use @NotNull or @Positive separately if null is not allowed
        }
        if (orderRepo == null) {
            return true;
        }
        return orderRepo.existsById(value);
    }
}
