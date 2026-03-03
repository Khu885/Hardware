package com.hardwareaplications.hardware.Controller;


import com.hardwareaplications.hardware.Service.customerservice;
import com.hardwareaplications.hardware.customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
public class customercontroller {
    @Autowired
    customerservice Cservice;

    @GetMapping("/customers")
    public List<customer> getcustomer(){return Cservice.getcustomer();}
    @GetMapping("/customers/{cid}")
    public customer getcustomerbyid(@PathVariable int cid){return Cservice.getcustomerbyid(cid);}
    @PostMapping("/customers")
    public customer addcustomer(@Valid @RequestBody customer cid){return Cservice.addcustomer(cid);}
    @PutMapping("/customers")
    public customer updatecustomer(@Valid @RequestBody customer cid){return Cservice.updatecustomer(cid);}
    @DeleteMapping("/customers/{cid}")
    public List<customer> deletecustomer(@PathVariable int cid){return Cservice.deletecustomer(cid);}
    @GetMapping("/LoadCustomers")
    public String Load(){
        Cservice.Load();
        return "Customer Data Loaded";
    }
}
