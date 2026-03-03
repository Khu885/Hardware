package com.hardwareaplications.hardware.Service;

import com.hardwareaplications.hardware.product;
import com.hardwareaplications.hardware.Exception.ProductValidationException;
import com.hardwareaplications.hardware.Exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import com.hardwareaplications.hardware.productRepo;
import com.hardwareaplications.hardware.HardwaresRepo.ProductRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class productService {
   // List<product> products = new ArrayList<>((Arrays.asList(new product(1, "HIT", "HIT", "Red", "Large", "Spray"),
           // new product(2, "Cycle_oil", "Raftaar", "White(botttle)", "large", "Oil"))));

    // CamelCase pattern: start with a lowercase letter, no spaces, allow letters and digits, can have internal uppercase letters
    private static final Pattern CAMEL_CASE = Pattern.compile("^[a-z][a-zA-Z0-9]*$");

    /**
     * Validate product. If isUpdate==true, ProductId must be present and must exist in DB.
     * If isUpdate==false (create), ProductId may be null (we use @GeneratedValue).
     */
    private void validateProduct(product p, boolean isUpdate) {
        if (p == null) {
            throw new ProductValidationException("Product must not be null");
        }
        if (isUpdate) {
            if (p.getProductId() == null || !repo.existsById(p.getProductId())) {
                throw new ProductValidationException("Product id must exist for update");
            }
        }
        if (p.getProductName() == null || p.getProductName().isBlank()) {
            throw new ProductValidationException("Product name must not be blank");
        }
        if (!CAMEL_CASE.matcher(p.getProductName()).matches()) {
            throw new ProductValidationException("Product name must follow camelCase pattern");
        }
        if (p.getProductType() == null || p.getProductType().isBlank()) {
            throw new ProductValidationException("Product type must not be blank");
        }
    }


    @Autowired
    private ProductRepo repo;

    public List<product> getproducts() {
        return repo.findAll();
    }

    public product getproductbyID(int pId) {
        return repo.findById(pId).orElseThrow(() -> new ProductNotFoundException("Product with id " + pId + " not found"));
    }

    public product addproduct(product prd) {
        validateProduct(prd, false); // Validate for create (id optional)
        if (prd.getProductId() == null) {
            throw new ProductValidationException("Product ID must be provided");
        }
        if (repo.existsById(prd.getProductId())) {
            throw new ProductValidationException("Product with ID " + prd.getProductId() + " already exists");
        }
        return repo.save(prd);
    }

    public product updateproducts(product p)  {
        validateProduct(p, true);
        return repo.save(p);
    }

    public List<product> deleteproduct(int pId) {
        if (!repo.existsById(pId)) {
            throw new ProductNotFoundException("Product with id " + pId + " not found for delete");
        }
        repo.deleteById(pId);
        return repo.findAll();
    }
    public void Load(){
        List<product> products = new ArrayList<>((Arrays.asList(
                new product(1, "HIT", "HIT", "Red", "Large", "Spray", 50.0),
                new product(2, "Cycle_oil", "Raftaar", "White(botttle)","large", "Oil", 25.05)
        )));
        repo.saveAll(products);
    }
}
