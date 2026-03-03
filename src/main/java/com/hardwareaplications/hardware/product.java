package com.hardwareaplications.hardware;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class product {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ProductId;
    private String ProductName;
    private String ProductBrand;
    private String ProductColour;
    private String ProductSize;
    private String ProductType;
    private Double price; // added product price (nullable for backward compatibility)
}
