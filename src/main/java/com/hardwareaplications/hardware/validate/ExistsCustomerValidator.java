package com.hardwareaplications.hardware.validate;

import com.hardwareaplications.hardware.HardwaresRepo.CustomerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExistsCustomerValidator implements ConstraintValidator<ExistsCustomer, Integer> {

    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Use @NotNull or @Positive separately if null is not allowed
        }
        if (customerRepo == null) {
            return true;
        }
        return customerRepo.existsById(value);
    }
}
