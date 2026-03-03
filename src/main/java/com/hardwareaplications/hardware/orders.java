package com.hardwareaplications.hardware;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class orders {

    @PositiveOrZero(message = "orderId must be zero or a positive number")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @Positive(message = "customerId must be a positive number")
    private Integer customerId;

    @NotBlank(message = "orderDate must not be blank")
    private String orderDate;

    @PositiveOrZero(message = "totalAmount must be zero or positive")
    private double totalAmount;

    // make nullable so Hibernate can add column without failing on existing rows
    private Boolean isProcessed;

    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    // keep boolean-style accessor expected by service code
    public boolean isProcessed() {
        return Boolean.TRUE.equals(this.isProcessed);
    }

    public void setProcessed(boolean processed) {
        this.isProcessed = processed;
    }
}
