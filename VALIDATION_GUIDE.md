# Database Validation Implementation Guide

## Overview
This implementation provides custom Bean Validation annotations to validate that foreign key references (productId, customerId, orderId, inventoryId) exist in the database before allowing add or update operations.

## Architecture

### 1. Custom Validation Annotations (in `validate` package)

#### @ExistsProduct
- **Purpose**: Validates that a productId exists in the products table
- **Location**: `com.hardwareaplications.hardware.validate.ExistsProduct`
- **Validator**: `ExistsProductValidator`
- **Usage**: Applied to productId fields

#### @ExistsCustomer
- **Purpose**: Validates that a customerId exists in the customers table
- **Location**: `com.hardwareaplications.hardware.validate.ExistsCustomer`
- **Validator**: `ExistsCustomerValidator`
- **Usage**: Applied to customerId fields

#### @ExistsOrder
- **Purpose**: Validates that an orderId exists in the orders table
- **Location**: `com.hardwareaplications.hardware.validate.ExistsOrder`
- **Validator**: `ExistsOrderValidator`
- **Usage**: Applied to orderId fields (if needed for future relationships)

#### @ExistsInventory
- **Purpose**: Validates that an inventoryId exists in the inventory table
- **Location**: `com.hardwareaplications.hardware.validate.ExistsInventory`
- **Validator**: `ExistsInventoryValidator`
- **Usage**: Applied to inventoryId fields (if needed for future relationships)

### 2. Validators (in `validate` package)

Each validator:
- Is a Spring `@Component` (allowing dependency injection)
- Implements `ConstraintValidator<AnnotationType, Integer>`
- Autowires the appropriate repository
- Uses `repository.existsById(id)` to check database existence
- Returns `true` if the value is null (use `@NotNull` separately if required)
- Returns `true` if the ID exists in the database, `false` otherwise

### 3. Repositories Created

#### CustomerRepo
- **Location**: `com.hardwareaplications.hardware.HardwaresRepo.CustomerRepo`
- **Purpose**: JPA repository for customer entity (needed for validation)
- **Note**: The customer entity was updated to include `@Entity` and `@Id` annotations

## Updated Entities

### Inventory Entity
```java
@Entity
public class Inventory {
    @Positive(message = "inventoryId must be a positive number")
    @Id
    private int inventoryId;

    @Positive(message = "productId must be a positive number")
    @ExistsProduct(message = "Product with this ID does not exist in the database")
    private int productId;  // <-- Validates product exists before save/update

    @PositiveOrZero(message = "quantity must be zero or positive")
    private int quantity;

    @NotBlank(message = "location must not be blank")
    private String location;
}
```

### Orders Entity
```java
@Entity
public class orders {
    @Positive(message = "orderId must be a positive number")
    @Id
    private int orderId;

    @Positive(message = "customerId must be a positive number")
    @ExistsCustomer(message = "Customer with this ID does not exist in the database")
    private int customerId;  // <-- Validates customer exists before save/update

    @NotBlank(message = "orderDate must not be blank")
    private String orderDate;

    @PositiveOrZero(message = "totalAmount must be zero or positive")
    private double totalAmount;

    @Positive(message = "Quantity must be a positive number")
    private int Quantity;
}
```

## How It Works

### Adding a New Inventory Item

1. **Request**: Admin sends POST to `/inventories` with JSON body:
```json
{
  "inventoryId": 5,
  "productId": 999,
  "quantity": 10,
  "location": "Warehouse A"
}
```

2. **Controller**: Receives request with `@Valid` annotation:
```java
@PostMapping("/inventories")
public Inventory addinventory(@Valid @RequestBody Inventory i) {
    return service.addinventory(i);
}
```

3. **Validation Execution**:
   - `@Positive` validates inventoryId > 0
   - `@Positive` validates productId > 0
   - `@ExistsProduct` triggers `ExistsProductValidator`
   - Validator calls `productRepo.existsById(999)`
   - If product 999 doesn't exist → validation fails

4. **Response**:
   - **Success**: Inventory saved, returns 200 with inventory object
   - **Failure**: Returns 400 Bad Request with error:
     ```json
     {
       "productId": "Product with this ID does not exist in the database"
     }
     ```

### Adding a New Order

1. **Request**: Admin sends POST to `/orders` with JSON body:
```json
{
  "orderId": 10,
  "customerId": 555,
  "orderDate": "2026-02-09",
  "totalAmount": 1500.00,
  "Quantity": 5
}
```

2. **Validation Execution**:
   - `@Positive` validates orderId > 0
   - `@Positive` validates customerId > 0
   - `@ExistsCustomer` triggers `ExistsCustomerValidator`
   - Validator calls `customerRepo.existsById(555)`
   - If customer 555 doesn't exist → validation fails

3. **Response**:
   - **Success**: Order saved, returns 200 with order object
   - **Failure**: Returns 400 Bad Request with error:
     ```json
     {
       "customerId": "Customer with this ID does not exist in the database"
     }
     ```

## Benefits

1. **Data Integrity**: Ensures referential integrity at the application layer
2. **Early Failure**: Catches invalid references before database operations
3. **Reusable**: Annotations can be applied to any field across different entities
4. **Clear Error Messages**: Provides specific validation messages
5. **Declarative**: Uses annotations instead of manual validation code
6. **Automatic**: Works with Spring's `@Valid` annotation in controllers

## Testing

To test the validation:

1. **Test Invalid Product ID**:
   ```bash
   POST /inventories
   {
     "inventoryId": 10,
     "productId": 99999,  # Non-existent product
     "quantity": 5,
     "location": "Store"
   }
   ```
   Expected: 400 Bad Request with "Product with this ID does not exist in the database"

2. **Test Invalid Customer ID**:
   ```bash
   POST /orders
   {
     "orderId": 20,
     "customerId": 99999,  # Non-existent customer
     "orderDate": "2026-02-10",
     "totalAmount": 500.00,
     "Quantity": 2
   }
   ```
   Expected: 400 Bad Request with "Customer with this ID does not exist in the database"

3. **Test Valid Data**:
   - First create a product with ID 1
   - Then create inventory referencing productId: 1
   - Expected: 200 OK with created inventory

## Files Created

### Validation Annotations:
- `ExistsProduct.java`
- `ExistsCustomer.java`
- `ExistsOrder.java`
- `ExistsInventory.java`

### Validators:
- `ExistsProductValidator.java`
- `ExistsCustomerValidator.java`
- `ExistsOrderValidator.java`
- `ExistsInventoryValidator.java`

### Repository:
- `CustomerRepo.java`

### Modified Files:
- `Inventory.java` - Uses service-level validation via `validateInventory()` in `inventoryService`
- `orders.java` - Uses service-level validation via `validateOrder()` in `ordersService`
- `customer.java` - Added `@Entity` and `@Id` annotations
- `inventoryService.java` - Fixed return statement, added `validateInventory()`

## Notes

- The validators allow `null` values. Use `@NotNull` if the field is required.
- Validation happens automatically when `@Valid` is used in the controller.
- Spring Boot automatically registers validators as beans, enabling dependency injection.
- The validation integrates seamlessly with Spring's error handling mechanism.
