package com.hardwareaplications.hardware.Service;

import com.hardwareaplications.hardware.AdmUsers;
import com.hardwareaplications.hardware.HardwaresRepo.UserRepo;
import com.hardwareaplications.hardware.User_Detail_Implementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetaileService implements UserDetailsService {
    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdmUsers user = repo.findByUsername(username);
        if(user == null){
            System.out.println("User having usename :" + username+" is not found in database");
            throw new UsernameNotFoundException("User not found");
        }
        return new User_Detail_Implementation(user);
    }
}
