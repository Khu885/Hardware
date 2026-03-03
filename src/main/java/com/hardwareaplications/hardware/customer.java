package com.hardwareaplications.hardware;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class customer {
    @Positive(message = "CustomerId must be a positive number")
    @Id
    private int CustomerId;

    @NotBlank(message = "Customername must not be blank")
    private String Customername;

    @Positive(message = "PhoneNo must be a positive number")
    private int PhoneNo;

    @NotBlank(message = "address must not be blank")
    private String address;

    @Min(value = 0, message = "due_amt must be zero or positive")
    private int due_amt;

    // Explicit getters to ensure methods are present even if Lombok isn't applied
    public int getCustomerId() {
        return CustomerId;
    }

    public String getCustomername() {
        return Customername;
    }

    public int getPhoneNo() {
        return PhoneNo;
    }

    public String getAddress() {
        return address;
    }

    public int getDue_amt() {
        return due_amt;
    }
}
