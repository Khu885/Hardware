package com.hardwareaplications.hardware.HardwaresRepo;

import com.hardwareaplications.hardware.AdmUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<AdmUsers, Integer> {
    AdmUsers findByUsername(String username);
}
