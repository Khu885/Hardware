package com.hardwareaplications.hardware;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Inventory {
    @Positive(message = "inventoryId must be a positive number")
    @Id
    private int inventoryId;

    @Positive(message = "productId must be a positive number")
    private int productId;

    @PositiveOrZero(message = "quantity must be zero or positive")
    private int quantity;

    @NotBlank(message = "location must not be blank")
    private String location;
}
