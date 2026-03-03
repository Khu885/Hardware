package com.hardwareaplications.hardware.Controller;

import com.hardwareaplications.hardware.product;
import com.hardwareaplications.hardware.Service.productService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
public class ProductController {
    @Autowired
    private productService service;
    @GetMapping("/hello")
    public String greet(HttpServletRequest request){
        return  "hello " + request.getSession().getId();
    }
    @GetMapping("/products")
    public List<product> getproduct(){
        return service.getproducts();
    }
    @GetMapping("/products/{productId}")
    public product getproductbyID(@PathVariable int productId){
        return service.getproductbyID(productId);
    }
    @PostMapping("/products")
    public product addproduct(@RequestBody product p) {
        return service.addproduct(p);
    }
    @PutMapping("/products")
    public product updateproducts( @RequestBody product p){
        return service.updateproducts(p);
    }
    @DeleteMapping("/products/{productId}")
    public List<product> deleteproduct(@PathVariable int productId){
        return service.deleteproduct(productId);
    }

    @GetMapping("/Load")
    public String Load(){
        service.Load();
        return "Data Loaded";
        }

    }
