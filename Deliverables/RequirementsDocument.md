# Requirements Document

Authors: Luca Pezzolla, Teodoro Corbo, Francesco Policastro, Paolo Celada

Date: 18/4/2021

Version: 2.0

# Contents

- [Essential description](#essential-description)
- [Stakeholders](#stakeholders)
- [Context Diagram and interfaces](#context-diagram-and-interfaces)
    + [Context Diagram](#context-diagram)
    + [Interfaces](#interfaces)

- [Stories and personas](#stories-and-personas)
- [Functional and non functional requirements](#functional-and-non-functional-requirements)
    + [Functional Requirements](#functional-requirements)
    + [Non functional requirements](#non-functional-requirements)
- [Use case diagram and use cases](#use-case-diagram-and-use-cases)
    + [Use case diagram](#use-case-diagram)
    + [Use cases](#use-cases)
        + [Relevant scenarios](#relevant-scenarios)
- [Glossary](#glossary)
- [System design](#system-design)
- [Deployment diagram](#deployment-diagram)

# Essential description

Small shops require a simple application to support the owner or manager. A small shop (ex a food shop) occupies 50-200
square meters, sells 500-2000 different item types, has one or a few cash registers EZShop is a software application to:

* manage sales
* manage inventory
* manage customers
* support accounting

# Stakeholders

| Stakeholder name  | Description | 
| ----------------- |:-----------:|
|   Owner    |    Owns the shop, makes final decisions regarding investments         | 
|    Manager   |  Handles employees and accounting operations | 
|    Cashier   |  Interact with customers through POS to handle sales | 
|    Inventory manager   |  Handles product refill based on inventory status in EZShop | 
|    Shelf stacker   |  Stacks product refills on shelves, updates product labels through POS | 
|    Customer   |  Buys products interacting with Cashier | 
|    IT administrator   |  Manages all applications in the supermarket | 
|    Security manager   |  Responsible for security issues | 
|    DB administrator   |  Manages DBMSs on which applications are based | 
|    Product   |  Sold goods, interacts with POS through the barcode reader | 
|    Credit card system   |  Handles credit card payments, interacts with POS via internet  | 

# Context Diagram and interfaces

## Context Diagram
```plantuml
left to right direction
skinparam PackageStyle rect

rectangle system{
usecase EzShop
}

:cashier: --> EzShop
:manager: --> EzShop
:inventory_manager: --> EzShop
:product: -up-> EzShop
:credit_card_system: -up->EzShop
```

## Interfaces

| Actor | Logical Interface | Physical Interface  |
| ------------- |:-------------:| -----:|
|   Cashier     | GUI | Screen, keyboard, mouse |
|   Manager     | GUI |Screen, keyboard, mouse  |
|   Inventory manager     | GUI  | Screen, keyboard, mouse |
|   Product     | bar code | laser beam |
|   Credit card system     | API | internet connection |

# Stories and personas

Guido Saracco is the *shop owner*: she's 65 years old and has worked in the sale industry for all her life.
She does not have a great relationship with technology, but she understands it can benefit the daily activity of her employee.
Her goal is to maximize the profitability of her shop: she decided to finance the creation of EZShop following the suggestions provided by Maurizio, the store manager.

Maurizio Morisio is the *store manager*: he's an experienced manager in the hospitality industry, over the years he practiced data analysis to understand customer behavior.
He started working in the shop from just a couple of months, but he brought change with him as soon as he arrived.
Elizabeth trusts him to make the shop more profitable, and his goal is to do so by understanding and supporting the needs of the employee. After all, who knows best how to improve processes? 
His main need from EZShop is to obtain greater visibility over revenue information and customer retention.

Luca Pezzolla is the *inventory manager*: he works in the shop from 20 years, and his role is to manage product restock. 
Over time he got to know all the shop suppliers, and is always able to find a way to have the store shelf refilled. His network of relationships are of extreme value for the shop. 
He used to remember the information for all the products in the inventory, but lately he's been having some trouble with memory.
EZShop can help him glance at the current stock numbers to notice if he needs to make some calls.


Francesco Policastro is the *store cashier*: he's 30, this is his first job, and he's trying hard to make it work.
Right now he's the only store cashier, and there are moments in which he feels overwhelmed by the infinite line of customers.
Elizabeth explained to him that talking to customers and making them feel welcome is an important time for the job, but he's just too slow at manually inserting all receipt data.
He hopes the change from an analogic cash register to the new system will help him to keep the queue short, lessening his anxiety and giving him more time to interact with his customers.

# Functional and non functional requirements

## Functional Requirements

| ID        | Description  |
| ------------- |:-------------:| 
|  FR1         | Handle sale |
|  FR1.1       | Start sale |
|  FR1.2       | Add customer fidelity card |
|  FR1.2.1     | Scan customer fidelity card |
|  FR1.2.2     | Apply discount if card points are above threshold |
|  FR1.3       | Add product to cart |
|  FR1.3.1     | Scan or insert product barcode |
|  FR1.3.2     | Retrieve product info & price |
|  FR1.3.2     | Update shopping cart |
|  FR1.4       | Remove product from cart|
|  FR1.4.1     | Scan or insert product barcode |
|  FR1.4.2     | Update shopping cart |
|  FR1.5       | End sale |
|  FR1.5.1     | Consolidate cart content |
|  FR1.5.2     | Choose payment method |
|  FR1.5.3     | Verify successful payment |
|  FR1.5.4     | Update customer fidelity card points |
|  FR1.5.5     | Update inventory |
|  FR2         | Authorize and authenticate  |
|  FR2.1       | Log in  |
|  FR2.2       | Log out  |
|  FR3         | Manage inventory | 
|  FR3.1       | Add product to inventory (descriptors,supplier cost, sale price) |
|  FR3.2       | Edit product from inventory| 
|  FR3.2.1       | Update inventory quantity | 
|  FR3.2.2       | Update product sale price |   
|  FR3.3       | Remove product from inventory|
|  FR4         | Manage customer | 
|  FR4.1       | Add new customer |
|  FR4.2       | Link fidelity card to customer | 
|  FR4.3       | Unlink fidelity card | 
|  FR4.4       | Update customer data |
|  FR4.5       | Delete customer | 
|  FR5         | Manage accounting | 
|  FR5.1       | Show revenue |  
|  FR5.2       | Show profit |  
|  FR5.3       | Show current inventory value |  
|  FR6       | Manage Users |
|  FR6.1       | List Users |
|  FR6.2       | Create User |
|  FR6.3       | Edit User |
|  FR6.4       | Delete User |

## Non Functional Requirements

| ID        | Type (efficiency, reliability, ..)           | Description  | Refers to |
| ------------- |:-------------:| :-----:| -----:|
|  NFR1     | Usability | Company users should be able to use EZShop with an introductory training only | All FR |
|  NFR2     | Performance | All functions should complete in < 0.5s | All FR |
|  NFR3     | Correctness  | Sales data should be persisted in case of failures | All FR |
| NFR4      | Security | Prevent unauthorize usage  | All FR | 
| NFR5      | Maintainability | Allow simple integration of new features in the future | All FR | 
| NFR6      | Privacy | Guarantee GDPR compliance | All FR | 
| NFR7      | Availability | Guarantee POS functionality even if the credit card system is not available | All FR | 

# Use case diagram and use cases

## Use case diagram
```plantuml
actor Cashier
actor Credit_Card_System
actor Product
actor Manager
actor Inventory_Manager

skinparam PackageStyle rect

rectangle EzShop{
    
    Cashier -> (Manage Customers)
    Cashier -> (Handle Sales)
    (Handle Sales) --down-> Credit_Card_System
    (Handle Sales) -down-> Product

    Inventory_Manager -up-> (Manage Inventory)
    Inventory_Manager -up--> (Authorize & Authenticate)
    Cashier --> (Authorize & Authenticate)
    Manager --> (Authorize & Authenticate)
    

    Manager -> (Manage Users)
    Manager -> (Manage Accounting)

    (Manage Inventory) ---> Product

}
```
<br/><br/><br/>
```plantuml
(Handle Sales) .> (End Sale) :include
(Handle Sales) .> (Add customer\n fidelity card) :include
(Handle Sales) .> (Remove Product\n to cart) :include
(Handle Sales) .> (Add Product\n to cart) :include
```
<br/><br/><br/>

```plantuml
(Authorize & Authenticate) .> (Log out) :include
(Authorize & Authenticate) .> (Log in) :include

(Manage Inventory) .> (Edit product\n from inventory) :include
(Manage Inventory) .> (Add product\n to inventory) :include
```
<br/><br/><br/>
```plantuml
(Manage Customer) .> (Edit customer data) :include
(Manage Customer) .> (Add new customer) :include

(Manage User) .> (Edit user) :include
(Manage User) .> (Create user) :include
```

### Use case 1, UC1 - Add product to cart

| Actors Involved        | Cashier, Product |
| ------------- |:-------------:| 
|  Precondition     | Sale started |  
|  Post condition     | - |
|  Nominal Scenario     | Scan barcode, barcode present into products table, add product to cart |
|  Variants     | Scan barcode, barcode not present into products table, notify error |

##### Scenario 1.1

| Scenario 1.1 | product is defined |
| ------------- |:-------------:| 
|  Precondition     |   sale started |
|  Post condition     | - |
| Step#        | Description  |
|  1     | Scan barcode  |  
|  2     | Barcode present into products table |
|  3     |  Update shopping cart: items list, total |

##### Scenario 1.2
| Scenario 1.2 | product is not defined |
| ------------- |:-------------:| 
|  Precondition     |  sale started |
|  Post condition     | - |
| Step#        | Description  |
|  1     | Scan barcode  |  
|  2     | Barcode not present into products table |
|  3     |  Notify error |

### Use case 2, UC2 - Add fidelity card to cart

| Actors Involved        | Cashier |
| ------------- |:-------------:| 
|  Precondition     | sale started |
|  Post condition     | - |
|  Nominal Scenario     | Fidelity card points are not enough for a discount |
|  Variants     | Fidelity card points are enough for a discount |
|  Variants     | Invalid fidelity card number |

##### Scenario 2.1

| Scenario 2.1 | Fidelity card points are not enough for a discount |
| ------------- |:-------------:| 
|  Precondition     | sale started |
|  Post condition     | - |
| Step#        | Description  |
|  1     | Insert fidelity card number to retrieve customer data |  
|  2     | Fidelity card points < 500: no discount |
|  3     | Link sale with customer |

##### Scenario 2.2
| Scenario 2.2 |  Fidelity card points are enough for a discount |
| ------------- |:-------------:| 
|  Precondition     | sale started |
|  Post condition     | - |
| Step#        | Description  |
|  1     | Insert fidelity card number to retrieve customer data |  
|  2     | Fidelity card points >= 500: 20% discount |
|  3     | Link sale with customer |


##### Scenario 2.2
| Scenario 2.2 |  Invalid fidelity card number |
| ------------- |:-------------:| 
|  Precondition     | sale started |
|  Post condition     | - |
| Step#        | Description  |
|  1     | Insert fidelity card number to retrieve customer data |  
|  2     | Notify invalid fidelity card provided |

### Use case 3, UC3 - Remove product from cart

| Actors Involved        | Cashier, Product |
| ------------- |:-------------:| 
|  Precondition     |  sale started, product present into cart |
|  Post condition     | Update shopping cart: items list, total |
|  Nominal Scenario     | Remove product from cart |

### Use case 4, UC4 - End sale

| Actors Involved        | Cashier |
| ------------- |:-------------:| 
|  Precondition     |  sale started, at least 1 product in cart |  
|  Post condition     | - |
|  Nominal Scenario     | Customer pays successfully with cash and gets change, receipt printed, inventory updated |
|  Variants     | Customer pays with credit card, payment fails, customer is asked to provide another form of payment |
|  Variants     | Customer pays with credit card, payment fails, customer aborts sale |
|  Variants     | Customer pays successfully with credit card, receipt printed, inventory updated  |

##### Scenario 4.1

| Scenario 4.1 | successful sale with cash |
| ------------- |:-------------:| 
|  Precondition     |  sale started, at least 1 product in cart |
|  Post condition     | - |
| Step#        | Description  |
|  1     | Cart is finalized  |  
|  2     | Customer chooses to pay with cash  |  
|  3     | Cashier records received money and handles change  |  
|  4     | Receipt is printed  |  
|  5     | Product quantity is decreased for all products included in sale  |  
|  6     | If a fidelity card was provided, update points accordingly |  

##### Scenario 4.2

| Scenario 4.2 | failed sale with credit card, with retry |
| ------------- |:-------------:| 
|  Precondition     |  sale started, at least 1 product in cart |
|  Post condition     | - |
| Step#        | Description  |
|  1     | Cart is finalized  |  
|  2     | Customer chooses to pay with credit card, inserts PIN  |  
|  3     | Cashier receives failed payment notification  |
|  4     | Cashier asks customer to try with a different payment method  |  

##### Scenario 4.3

| Scenario 4.3 | failed sale with credit card, aborted |
| ------------- |:-------------:| 
|  Precondition     |  sale started, at least 1 product in cart |
|  Post condition     | - |
| Step#        | Description  |
|  1     | Cart is finalized  |  
|  2     | Customer chooses to pay with credit card, inserts PIN  |  
|  3     | Cashier receives failed payment notification  |
|  4     | Customer asks cashier to abort sale  |

##### Scenario 4.4

| Scenario 4.4 | successful sale with credit card |
| ------------- |:-------------:| 
|  Precondition     |  sale started, at least 1 product in cart |
|  Post condition     | - |
| Step#        | Description  |
|  1     | Cart is finalized  |  
|  2     | Customer chooses to pay with credit card, inserts PIN  |  
|  3     | Cashier receives successful payment confirmation  |  
|  4     | Receipt is printed  |  
|  5     | Product quantity is decreased for all products included in sale  |  
|  6     | If a fidelity card was provided, update points accordingly |

### Use case 5, UC5 - Log in

| Actors Involved        | Cashier, Manager, Inventory Manager |
| ------------- |:-------------:| 
|  Precondition     | User already exists |  
|  Post condition     | - |
|  Nominal Scenario     | User wants to authenticate in the system, hence populate its fields, and correctly logs in |
|  Variants     | User makes mistakes while populating the fields, access is denied |

##### Scenario 5.1

| Scenario 5.1 | user correctly log in |
| ------------- |:-------------:| 
|  Precondition     |  User already exists |
|  Post condition     | - |
| Step#        | Description  |
|  1     | User correctly populates the fields (username, password)  |  
|  2     | Access is permitted  |

##### Scenario 5.2
| Scenario 5.2 | user access denied |
| ------------- |:-------------:| 
|  Precondition     |  User already exists |
|  Post condition     | - |
| Step#        | Description  |
|  1     | User makes mistakes while populating the fields |  
|  2     | Access is denied |
|  3     | User tries to log in again |
|  4     | ... |

### Use case 6, UC6 - Log out

| Actors Involved        | Cashier, Manager, Inventory Manager |
| ------------- |:-------------:| 
|  Precondition     | User is already logged in |  
|  Post condition     | User logged out |
|  Nominal Scenario     | User clicks the log out icon and exits the system |
|  Variants     | - |

### Use case 7, UC7 - Add product to inventory

| Actors Involved        | Inventory manager, Product |
| ------------- |:-------------:| 
|  Precondition     | Authorized Inventory manager, non-existing product |  
|  Post condition     | - |
|  Nominal Scenario     | Insert product information, add product |


### Use case 8, UC8 - Edit product from inventory

| Actors Involved        | Inventory manager, Product |
| ------------- |:-------------:| 
|  Precondition     |  Authorized Inventory manager, existing product |  
|  Post condition     | Inventory updated |
|  Nominal Scenario     | Increase inventory quantity and/or sale price, update inventory |

### Use case 9, UC9 - Add new customer

| Actors Involved        | Cashier |
| ------------- |:-------------:| 
|  Precondition     | Customer doesn't exist |  
|  Post condition     | Customer owns a fidelity card, fidelity card on system is linked to the customer |
|  Nominal Scenario     | Customer provide cashier personal data, cashier record them on system, system link fidelity card to user data, system provide a fidelity card, cashier issues fidelity card to customer |


### Use case 10, UC10 - Edit customer data

| Actors Involved        | Cashier |
| ------------- |:-------------:| 
|  Precondition     | Customer identity verified, customer account exists |  
|  Post condition     | - |
|  Nominal Scenario     | Customer provides data to be updated, cashier applies changes |
|  Variants     | Customer asks cashier to delete its data, cashier performs customer deletion |


##### Scenario 10.1
| Scenario 10.1 | Customer update |
| ------------- |:-------------:| 
|  Precondition     | Customer identity verified, customer account exists |  
|  Post condition     | - |
| Step#        | Description  |
|  1     | Customer provides data to be changed |
|  2     | Cashier applies changes |

##### Scenario 10.2
| Scenario 10.2 | Customer deletion |
| ------------- |:-------------:| 
|  Precondition     | Customer identity verified, customer account exists |  
|  Post condition     | - |
| Step#        | Description  |
|  1     | Customer asks cashier to delete its data |
|  2     | Cashier removes customer data |

### Use case 11, UC11 - Add new user

| Actors Involved        | Manager |
| ------------- |:-------------:| 
|  Precondition     | Account user doesn't exist |  
|  Post condition     | Account added in the system |
|  Nominal Scenario     | Manager creates a new account and populate its fields  |
|  Variants     | |

### Use case 12, UC12 - Edit user 

| Actors Involved        | Manager |
| ------------- |:-------------:| 
|  Precondition     | Account user exist |  
|  Post condition     | - |
|  Nominal Scenario     | Manager modifies one or more fields of account, Account fields updated |
|  Variants     | Manager deletes the account |

##### Scenario 12.1
| Scenario 12.1 | User update |
| ------------- |:-------------:| 
|  Precondition     | Account user exist |  
|  Post condition     | - |
| Step#        | Description  |
|  1     | Manager modifies one or more fields of account  |  
|  2     | Account fields updated |

##### Scenario 12.2
| Scenario 12.2 | User delete |
| ------------- |:-------------:| 
|  Precondition     | Account user exist |  
|  Post condition     | - |
| Step#        | Description  |
|  1     | Manager requests user account deletion  |  
|  2     | User account deleted |

### Use case 13, UC13 - Management dashboard

| Actors Involved        | Manager |
| ------------- |:-------------:| 
|  Precondition     | - |  
|  Post condition     | - |
|  Nominal Scenario     | Manager can access revenue & customer stats |

# Glossary

```plantuml
class EZShop

class Sale_Transaction{
    Id
    date
    cart value
    discount
    total amount
    payment method
    payment reference
    received cash
    cash change
}

class Product_Record{
    Id
    name
    quantity
    unit price
    price
    barcode
}

class Product{
    Id
    name
    unit price
    supplier cost
    barcode
    inventory quantity

}

class User{
    Id
    first name
    last name
    physical address
    email address
    phone number
    password
    role
    created at
    updated at
    deleted at
}

class Customer {
    Id
    first name
    last name
    physical address
    email address
    phone number
    created at
    updated at
}

class Fidelity_Card{
    number
    points
    created at
}

EZShop - "*" User
EZShop - "*" Product
EZShop -- "*" Sale_Transaction

Sale_Transaction o-- "*" Product_Record
Sale_Transaction -- "0..1" Customer
Sale_Transaction -- User :handles\n role='cashier'

Product_Record -- Product :describe

Customer -- "1" Fidelity_Card :owns

note right of Sale_Transaction : Transaction between customer and retailer.\nIf payment method is card the transaction\n will include a payment reference.\nIf payment method is cash received cash\n will include the cash provided by the customer,\n while cash change is the change provided by the cashier.
note bottom of Product : physical product
note bottom of Product_Record : descriptor of physical\nproducts with same bar code.
note right of User : an employee of the shop having\nEZShop access credentials.
note bottom of Fidelity_Card : the card associated\nwith a specific customer
```

# System Design
```plantuml
class EZShop_System{
    Handle sales()
    Authorize & authenticate()
    Manage inventory()
    Manage customers()
    Manage accounting()
    Manage users()
}

EZShop_System o-- Bar_Code_Reader
EZShop_System o-- Computer
EZShop_System o-- Printer
EZShop_System o-- Credit_Card_Reader
EZShop_System o-- Cash_Drawer
Computer -- Software

hide EZShop_System circle
hide Bar_Code_Reader circle
hide Computer circle
hide Printer circle
hide Credit_Card_Reader circle
hide Cash_Drawer circle
hide Software circle
```
# Deployment Diagram
```plantuml
node Server
node Shop_Client
artifact EZShop_application

Server -- "*" Shop_Client
Server -- EZShop_application
```

