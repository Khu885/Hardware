# Hardware Application — System Architecture

---

## 1. System Architecture Overview

```mermaid
flowchart TD

    subgraph CLIENT["🌐 Client Layer"]
        HTTP["HTTP Request\n(REST / Browser / Postman)"]
    end

    subgraph CONTROLLER["🎮 Controller Layer"]
        PC[ProductController]
        CC[customercontroller]
        IC[InventoryController]
        OC[ordercontroller]
        UC[usersController]
    end

    subgraph CROSSCUT["⚙️ Cross-Cutting Layer"]
        SEC[SpringSecurityConfig]
        GEH[GlobalExceptionHandler]
        PMA[PerfoMonAspect]
        MUD[MyUserDetaileService]
    end

    subgraph SERVICE["🔧 Service Layer"]
        PS[productService]
        CS[customerservice]
        IS[inventoryService]
        OS[ordersService]
        US[usersService]
    end

    subgraph REPO["🗄️ Repository Layer"]
        PR[(ProductRepo)]
        CR[(CustomerRepo)]
        IR[(InventoryRepo)]
        OR[(orderRepo)]
        UR[(UserRepo)]
    end

    subgraph DATA["💾 Data Layer  — MySQL / H2"]
        PT[("products\ntable")]
        CT[("customers\ntable")]
        INV[("inventory\ntable")]
        OT[("orders\ntable")]
        UT[("adm_users\ntable")]
    end

    subgraph EXCEPTION["🚨 Exception Package"]
        PNF[ProductNotFoundException]
        PVE[ProductValidationException]
        CNF[CustomerNotFoundException]
        CVE[CustomerValidationException]
        INF[InventoryNotFoundException]
        IVE[InventoryValidationException]
        ONF[OrderNotFoundException]
        OVE[OrderValidationException]
    end

    %% Client → Controllers
    HTTP --> PC & CC & IC & OC & UC

    %% Security wraps all controllers
    SEC -. "guards all endpoints" .-> CONTROLLER

    %% AOP wraps service layer
    PMA -. "monitors performance" .-> SERVICE
    GEH -. "handles exceptions" .-> CONTROLLER

    %% Controllers → Services
    PC --> PS
    CC --> CS
    IC --> IS
    OC --> OS
    UC --> US
    MUD --> UR

    %% ordersService cross-calls
    OS --> CS
    OS --> IS

    %% Services → Repos
    PS --> PR
    CS --> CR
    IS --> IR
    IS --> PR
    OS --> OR
    OS --> IR
    OS --> PR

    %% Repos → DB Tables
    PR --> PT
    CR --> CT
    IR --> INV
    OR --> OT
    UR --> UT

    %% Exceptions thrown by Services
    PS -- "throws" --> PNF & PVE
    CS -- "throws" --> CNF & CVE
    IS -- "throws" --> INF & IVE
    OS -- "throws" --> ONF & OVE & CNF & INF
```

---

## 2. Service Layer — Methods Detail

```mermaid
flowchart LR

    subgraph PS["productService"]
        PS1[getproducts]
        PS2[getproductbyID]
        PS3[addproduct]
        PS4[updateproducts]
        PS5[deleteproduct]
        PS6[validateProduct 🔒]
        PS7[Load]
    end

    subgraph CS["customerservice"]
        CS1[getcustomer]
        CS2[getcustomerbyid]
        CS3[addcustomer]
        CS4[updatecustomer]
        CS5[deletecustomer]
        CS6[validateCustomer 🔒]
        CS7[Load]
    end

    subgraph IS["inventoryService"]
        IS1[getinventories]
        IS2[getinventorybyID]
        IS3[addinventory]
        IS4[updateinventories]
        IS5[deleteinventory]
        IS6[validateInventory 🔒]
        IS7[Load]
    end

    subgraph OS["ordersService"]
        OS1[getorders]
        OS2[getorderbyID]
        OS3[addorder]
        OS4[updateorders]
        OS5[returnOrder ⭐]
        OS6[deleteorder]
        OS7[validateOrder 🔒]
        OS8[calculateTotalAmount 🔒]
        OS9[processOrder 🔒]
        OS10[Load]
    end

    subgraph MU["MyUserDetaileService"]
        MU1[loadUserByUsername]
    end

    PS3 --> PS6
    PS4 --> PS6
    CS3 --> CS6
    CS4 --> CS6
    IS3 --> IS6
    IS4 --> IS6
    OS3 --> OS7
    OS3 --> OS8
    OS3 --> OS9
    OS4 --> OS7
    OS5 --> OS8
    OS5 --> OS9
```

---

## 3. Return Order Flow

```mermaid
sequenceDiagram
    participant Client
    participant ordercontroller
    participant ordersService
    participant orderRepo
    participant InventoryRepo
    participant customerservice
    participant CustomerRepo

    Client->>ordercontroller: POST /orders/{oId}/return
    ordercontroller->>ordersService: returnOrder(oId)
    ordersService->>orderRepo: findById(oId)
    orderRepo-->>ordersService: orders
    ordersService->>ordersService: check isProcessed() == true
    ordersService->>InventoryRepo: findByProductId(productId)
    InventoryRepo-->>ordersService: List~Inventory~
    ordersService->>InventoryRepo: save( qty + orderQty restored )
    ordersService->>customerservice: getcustomerbyid(customerId)
    customerservice->>CustomerRepo: findById(customerId)
    CustomerRepo-->>customerservice: customer
    customerservice-->>ordersService: customer
    ordersService->>customerservice: updatecustomer( due_amt − totalAmount )
    customerservice->>CustomerRepo: save(customer)
    ordersService->>orderRepo: save( order.processed = false )
    orderRepo-->>ordersService: updated order
    ordersService-->>ordercontroller: updated order
    ordercontroller-->>Client: 200 OK — returned order
```

---

## 4. Add Order Flow

```mermaid
sequenceDiagram
    participant Client
    participant ordercontroller
    participant ordersService
    participant orderRepo
    participant ProductRepo
    participant InventoryRepo
    participant customerservice
    participant CustomerRepo

    Client->>ordercontroller: POST /orders
    ordercontroller->>ordersService: addorder(orders)
    ordersService->>ordersService: validateOrder()
    ordersService->>ProductRepo: findById(productId)
    ProductRepo-->>ordersService: product (price)
    ordersService->>ordersService: calculateTotalAmount()
    alt isProcessed == true
        ordersService->>InventoryRepo: findByProductId(productId)
        InventoryRepo-->>ordersService: List~Inventory~
        ordersService->>InventoryRepo: save( qty − orderQty )
        ordersService->>customerservice: getcustomerbyid(customerId)
        customerservice->>CustomerRepo: findById(customerId)
        CustomerRepo-->>customerservice: customer
        customerservice-->>ordersService: customer
        ordersService->>customerservice: updatecustomer( due_amt + totalAmount )
        customerservice->>CustomerRepo: save(customer)
    end
    ordersService->>orderRepo: save(order)
    orderRepo-->>ordersService: saved order
    ordersService-->>ordercontroller: saved order
    ordercontroller-->>Client: 200 OK — saved order
```

---

## 5. Repository Layer

```mermaid
flowchart TD

    subgraph JPA["JpaRepository (Spring Data)"]
        J1[findAll]
        J2[findById]
        J3[existsById]
        J4[save]
        J5[saveAll]
        J6[deleteById]
        J7[count]
    end

    subgraph REPOS["Repositories"]
        PR[ProductRepo]
        CR[CustomerRepo]
        IR[InventoryRepo\n+ findByProductId]
        OR[orderRepo]
        UR[UserRepo\n+ findByUsername]
    end

    JPA --> PR & CR & IR & OR & UR
```

---

## 6. Exception Hierarchy

```mermaid
flowchart TD
    RE[RuntimeException]

    RE --> PNF[ProductNotFoundException]
    RE --> PVE[ProductValidationException]
    RE --> CNF[CustomerNotFoundException]
    RE --> CVE[CustomerValidationException]
    RE --> INF[InventoryNotFoundException]
    RE --> IVE[InventoryValidationException]
    RE --> ONF[OrderNotFoundException]
    RE --> OVE[OrderValidationException]

    GEH["GlobalExceptionHandler\n@ControllerAdvice"] -. "handles all" .-> PNF & PVE & CNF & CVE & INF & IVE & ONF & OVE
```

---

## 7. Validation Strategy

| Service               | Validate Method          | Exceptions Thrown                                                                               |
|-----------------------|--------------------------|-------------------------------------------------------------------------------------------------|
| `productService`      | `validateProduct()`      | `ProductValidationException`, `ProductNotFoundException`                                        |
| `customerservice`     | `validateCustomer()`     | `CustomerValidationException`, `CustomerNotFoundException`                                      |
| `inventoryService`    | `validateInventory()`    | `InventoryValidationException`, `InventoryNotFoundException`                                    |
| `ordersService`       | `validateOrder()`        | `OrderValidationException`, `OrderNotFoundException`, `CustomerNotFoundException`, `InventoryNotFoundException` |
| `MyUserDetaileService`| Spring Security          | `UsernameNotFoundException`                                                                     |
| `usersService`        | *(in-memory, no DB)*     | —                                                                                               |

