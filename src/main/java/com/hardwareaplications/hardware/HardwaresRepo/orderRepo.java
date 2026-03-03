package com.hardwareaplications.hardware.HardwaresRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hardwareaplications.hardware.orders;
import org.springframework.stereotype.Repository;

@Repository
public interface orderRepo extends JpaRepository<orders, Integer>{

}