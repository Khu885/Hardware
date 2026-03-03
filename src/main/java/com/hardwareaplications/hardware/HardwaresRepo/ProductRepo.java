package com.hardwareaplications.hardware.HardwaresRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hardwareaplications.hardware.product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<product, Integer>{

}

