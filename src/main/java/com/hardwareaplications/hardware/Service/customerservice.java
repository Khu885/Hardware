package com.hardwareaplications.hardware.Service;

import com.hardwareaplications.hardware.Exception.CustomerNotFoundException;
import com.hardwareaplications.hardware.Exception.CustomerValidationException;
import com.hardwareaplications.hardware.HardwaresRepo.CustomerRepo;
import com.hardwareaplications.hardware.customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class customerservice {

//    public List<customer> customers= new ArrayList<>(
//            Arrays.asList(new customer(1,"raman",1234567890,"xyz",0)));
    @Autowired
    private CustomerRepo repo;

    private void validateCustomer(customer c, boolean isUpdate) {
        if (c == null) {
            throw new CustomerValidationException("Customer must not be null");
        }
        if (isUpdate) {
            if (!repo.existsById(c.getCustomerId())) {
                throw new CustomerValidationException("Customer id must exist for update");
            }
        }
        if (c.getCustomername() == null || c.getCustomername().isBlank()) {
            throw new CustomerValidationException("Customer name must not be blank");
        }
        // Phone number validation could be more complex, but sticking to positive check as per entity
        if (c.getPhoneNo() <= 0) {
            throw new CustomerValidationException("Phone number must be positive");
        }
        if (c.getAddress() == null || c.getAddress().isBlank()) {
            throw new CustomerValidationException("Address must not be blank");
        }
        if (c.getDue_amt() < 0) {
            throw new CustomerValidationException("Due amount cannot be negative");
        }
    }

    public List<customer> getcustomer(){
        return repo.findAll();
    }

    public customer getcustomerbyid(int cid) {
        return repo.findById(cid).orElseThrow(()-> new CustomerNotFoundException("Customer with id " + cid + " not found"));

    }

    public customer addcustomer(customer cid) {
        validateCustomer(cid, false);
        return repo.save(cid);
    }

    public customer updatecustomer(customer cid) {
        validateCustomer(cid, true);
        return repo.save(cid);
    }

    public List<customer> deletecustomer(int cid) {
        if (!repo.existsById(cid)) {
            throw new CustomerNotFoundException("Customer with id " + cid + " not found for delete");
        }
        repo.deleteById(cid);
        return repo.findAll();
    }

    public void Load() {
        List<customer> customers= new ArrayList<>(
           Arrays.asList(new customer(1,"raman",1234567890,"xyz",0),
                   new customer(2,"suman",1234567891,"abc",0),
                   new customer(3,"gopal",1234567892,"pqr",0),
                   new customer(4,"mohan",1234567893,"lmn",0),
                   new customer(5,"suresh",1234567894,"def",0)));
        repo.saveAll(customers);
    }
}
