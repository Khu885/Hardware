package com.hardwareaplications.hardware.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ExistsInventoryValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ExistsInventory {
    String message() default "Inventory with this ID does not exist in the database";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
