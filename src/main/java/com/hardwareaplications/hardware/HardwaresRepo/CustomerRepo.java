package com.hardwareaplications.hardware.HardwaresRepo;

import com.hardwareaplications.hardware.customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends JpaRepository<customer, Integer> {
}
