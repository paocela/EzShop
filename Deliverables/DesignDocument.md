# Design Document 


Authors: Francesco Policastro, Paolo Celada, Luca Pezzolla, Teodoro Corbo

Date: 28/04/2021

Version: 1.0


# Contents

- [Design Document](#design-document)
- [Contents](#contents)
- [Instructions](#instructions)
- [High level design](#high-level-design)
- [Low level design](#low-level-design)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)
  - [Scenario 1.1](#scenario-11)
  - [Scenario 1.2](#scenario-12)
  - [Scenario 1.3](#scenario-13)
  - [Scenario 2.1](#scenario-21)
  - [Scenario 2.3](#scenario-23)
  - [Scenario 3.1 - Order of product type X issued](#scenario-31---order-of-product-type-x-issued)
  - [Scenario 3.2 - Order of product type X payed](#scenario-32---order-of-product-type-x-payed)
  - [Scenario 4.2](#scenario-42)
  - [Scenario 4.3](#scenario-43)
  - [Scenario 4.4](#scenario-44)
  - [Scenario 5.1](#scenario-51)
  - [Scenario 5.2](#scenario-52)
  - [Scenario 6.1](#scenario-61)
  - [Scenario 6.2](#scenario-62)
  - [Scenario 6.3](#scenario-63)
  - [Scenario 6.5](#scenario-65)
  - [Scenario 7.1](#scenario-71)
  - [Scenario 7.2](#scenario-72)
  - [Scenario 7.3](#scenario-73)
  - [Scenario 7.4](#scenario-74)
  - [Scenario 8.1/8.2](#scenario-8182)
  - [Scenario 9.1 - List credits and debits](#scenario-91---list-credits-and-debits)
  - [Scenario 10.1 - Return payment by  credit card](#scenario-101---return-payment-by--credit-card)
  - [Scenario 10.2 - Return cash payment](#scenario-102---return-cash-payment)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design 

```plantuml

package it.polito.ezshop.exceptions
package it.polito.ezshop.data
package it.polito.ezshop.model
package EzShop_GUI
package EzShop

EzShop <|-- it.polito.ezshop.exceptions
EzShop <|-- it.polito.ezshop.data
EzShop <|-- it.polito.ezshop.model
EzShop -- EzShop_GUI

```
<br />
<br />

The software is designed in Java and only represents the application logic. The GUI is provided by a third party and will be subsequently integrated. Coherency is guaranteed by the shared interface, subsequently defined as data.EZShopInterface.





# Low level design

<for each package, report class diagram>

```plantuml

class EzShopInterface {
    + reset() :void
    + createUser(String username, String password, String role): Integer
    + deleteUser(Integer id): boolean
    + getAllUsers() : List<User>
    + getUser(Integer id) : User
    + updateUserRights(Integer id, String role) : boolean
    + login(String username, String password) : User
    + logout() : boolean
    + createProductType(String description, String productCode, double pricePerUnit, String note, String location) : Integer
    + updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) : boolean
    + deleteProductType(Integer id) : boolean
    + getAllProductTypes() : List<ProductType>
    + getProductTypeByBarCode(String barCode) : ProductType

    + getProductTypesByDescription(string description): List<ProductType>
    + updateQuantity(integer productId, int toBeAdded): boolean
    + updatePosition(integer productId, string newPos): boolean
    + issueOrder(string productCode, int quantity, double pricePerUnit): integer
    + payOrderFor(string productCode, int quantity, double pricePerUnit): integer
    + payOrder(integer orderId): boolean
    + recordOrderArrival(integer orderId): boolean
    + getAllOrders(): List<Order>
    + defineCustomer(string customerName)
    + modifyCustomer(integer Id, string newCustomerName, string newCustomerCard): boolean
    + deleteCustomer(integer Id): boolean

    + getCustomer(id: Integer): Customer
    + getAllCustomers(): List<Customer>
    + createCard(): String // Returns an assignable card code
    + attachCardToCustomer(customerCard: String, customerId: Integer): boolean
    + modifyPointsOnCard(customerCard: String, pointsToBeAdded: int): boolean
    + startSaleTransaction(): Integer
    + addProductToSale(transactionId: Integer, productCode: String, amount: int): boolean
    + deleteProductFromSale(Integer transactionId, String productCode, int amount): boolean
    + applyDiscountRateToProduct(transactionId: Integer, productCode: String, discountRate: double):boolean // TODO how to represent discountRate into models?
    + applyDiscountRateToSale(transactionId: Integer, discountRate: double): boolean // TODO how to represent discountRate into models?
    + computePointsForSale(transactionId: Integer): int
    + endSaleTransaction(transactionId: Integer): boolean

    + deleteSaleTransaction(Integer transactionId) : Boolean
    + getSaleTransaction(Integer transactionId) : SaleTransaction
    + startReturnTransaction(Integer transactionId) : Integer
    + returnProduct(Integer returnId, String productCode, int amount) : Boolean
    + endReturnTransaction(Integer returnId, boolean commit) : Boolean
    + deleteReturnTransaction(Integer returnId) : Boolean
    + receiveCashPayment(Integer transactionId, double cash) : double
    + receiveCreditCardPayment(Integer transactionId, String creditCard) : Boolean
    + returnCashPayment(Integer returnId) : double
    + returnCreditCardPayment(Integer returnId, String creditCard) : double
    + recordBalanceUpdate(toBeAdded: double): boolean
    + getCreditsAndDebits(LocalDate from, LocalDate to): List<BalanceOperation>
    + computeBalance(): double

}

class Shop {
    balance

    + getReturnTransaction()
    + getOrder()
    + getProductType()
    + validateCreditCard()
    + getUserByUsername()

}

class Customer {
    Id: Integer
    cardNumber: String
    customerName: String
}
class Card {
    cardNumber: String
    cardPoints: int

}
class User {
    id: Integer
    username : String   
    password : String
    role : String in <"Administrator", "Cashier", "ShopManager">

}
class ProductType {
    id: Integer
    code: String
    description: String
    sellPrice: double
    quantity: int
    notes: String
    position: String
    pricePerUnit: double

}
class SaleTransaction {
    id: Integer
    date : LocalDate
    time : LocalTime
    amount: double
    discountRateProducts : List <ProductDiscount>
    discountRateAmount : double
    paymentType: String in <cash, card>
    cash: double
    change: double
    creditCard: String
    recordList : List <SaleTransactionRecord>

}
class SaleTransactionRecord {
    transactionId: Integer
    productId: Integer
    quantity: int
    totalPrice : double

}
class ReturnTransaction {
    returnId : Integer
    transactionId: Integer
    returnedValue: double
    recordList : List <ReturnTransactionRecord>

}
class ReturnTransactionRecord {
    returnId: Integer
    productId: Integer
    quantity: int
    totalPrice : double

}
class ProductDiscount {
    id: Integer
    productId: Integer
    discountRate: double

}
class Order {
    id: Integer
    status: Enum< ISSUED, ORDERED, COMPLETED>
    productCode: String
    quantity: Integer
    pricePerUnit: double

}
class BalanceOperation {
    id: Integer
    date: LocalDate
    type: Enum<CREDIT,DEBIT,ORDER,SALE,RETURN>
    amount: double
    description: String

}
```







# Verification traceability matrix

| Functional requirement      | Shop | Customer | Card | User | ProductType | SaleTransaction | SaleTransactionRecord | ReturnTransaction | ReturnTransactionRecord | ProductDiscount | Order | BalanceOperation |
| ---------- | :-----------: | :-----------: | :-----------: | :-----------: | :-----------: | :-----------: | :-----------: | :-----------: | :-----------: | :-----------: | :-----------: | :-----------: |
| FR1.1 | X | | | X |
| FR1.2 | X | | | X |
| FR1.3 | X | | | X |
| FR1.4 | X | | | X |
| FR1.5 | X | | | X |
| FR3.1 | X | | | | X |
| FR3.2 | X | | | | X |
| FR3.3 | X | | | | X |
| FR3.4 | X | | | | X |
| FR4.1 | X | | | | X |
| FR4.2 | X | | | | X |
| FR4.3 | X | | | | X | | | | | | X |
| FR4.4 | X | | | | X | | | | | | X | 
| FR4.5 | X | | | |  | | | | | | X | X
| FR4.6 | X | | | |  | | | | | | X
| FR4.7 | X |
| FR5.1 | X |
| FR5.2 | X |
| FR5.3 | X |
| FR5.4 | X |
| FR5.5 | X |
| FR5.6 | X |
| FR5.7 | X |
| FR6.1 | X |
| FR6.2 | X |
| FR6.3 | X |
| FR6.4 | X |
| FR6.5 | X |
| FR6.6 | X |
| FR6.7 | X |
| FR6.8 | X |
| FR6.10 | X |
| FR6.11 | X |
| FR6.12 | X |
| FR6.13 | X |
| FR6.14 | X |
| FR6.15 | X |
| FR7.1 | X |
| FR7.2 | X |
| FR7.3 | X |
| FR7.4 | X |
| FR8.1 | X |
| FR8.2 | X |
| FR8.3 | X |
| FR8.4 | X |

# Verification sequence diagrams 

## Scenario 1.1

```plantuml
actor User
note over User: User ="Admin or\nShop Manager"
User -> EzShop : createProductType()
EzShop -> ProductType : Product()
EzShop <-- ProductType : return Product 
User <-- EzShop : p.getID()

```
## Scenario 1.2

```plantuml
actor User
note over User: User ="Admin or\nShop Manager"
User -> EzShop : UpdatePosition()
EzShop -> EzShop : p.getProductTypeByBarCode()
EzShop -> ProductType : p.setPosition()
EzShop <-- ProductType : return true 
User <-- EzShop : return true

```
## Scenario 1.3

```plantuml
actor User
note over User: User ="Admin or\nShop Manager"
User -> EzShop : UpdateProduct()
EzShop -> EzShop : p.getProductTypeByBarCode()
EzShop -> ProductType : p.setPricePerUnit()
EzShop <-- ProductType : return true 
User <-- EzShop : return true

```

## Scenario 2.1

```plantuml
actor Admin
Admin -> EzShop : createUser()
EzShop -> User : User()
EzShop <-- User : return User
Admin <-- EzShop : u.getId()

```

## Scenario 2.3

```plantuml
actor Admin
Admin -> EzShop : updateUserRights()
EzShop -> EzShop : u.getUser()
EzShop -> User : u.setUserRights()
EzShop <-- User : return true
Admin <-- EzShop : return true

```

## Scenario 3.1 - Order of product type X issued
```plantuml
actor ShopManager
ShopManager -> EzShop : issueOrder()
activate EzShop
EzShop -> Order ** : new
activate Order
EzShop -> Order  : getId()
deactivate Order
ShopManager <-- EzShop : orderId
deactivate EzShop

```
## Scenario 3.2 - Order of product type X payed
```plantuml
actor ShopManager
ShopManager -> EzShop : payOrder()
activate EzShop
EzShop -> EzShop : computeBalance()
EzShop -> EzShop : getOrder()
EzShop -> Order : setState()
activate Order
deactivate Order
EzShop -> BalanceOperation ** : new
ShopManager <-- EzShop : true 
deactivate EzShop

```
##Scenario 3.3 - Record order of product type X arrival
```plantuml
actor ShopManager
ShopManager -> EzShop: recordOrderArrival()
EzShop -> EzShop : getOrder()
EzShop -> Order : setState()
EzShop -> EzShop : getProductType()
EzShop -> Product : setUnits()
ShopManager <-- EzShop : true 
```

##Scenario 4.1

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : defineCustomer()
EzShop -> Customer : Customer()
EzShop <-- Customer : return Customer
User <-- EzShop : c.getId()

```

## Scenario 4.2

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : createCard()
EzShop -> Card : Card()
EzShop <-- Card : return Card
User <-- EzShop : card.getId()
User -> EzShop : getCustomer()
User <-- EzShop : customer.getId()
User -> EzShop : attachCardToCustomer()
EzShop -> EzShop : modifyCustomer()
EzShop -> Customer : c.setCardNumber()
EzShop <-- Customer : return true
User <-- EzShop : return true

```

## Scenario 4.3

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : getCustomer()
User <-- EzShop : customer.getId()
User <-- EzShop : customer.getCustomerName()
User -> EzShop : modifyCustomer()
EzShop -> Customer : c.setCardNumber()
EzShop <-- Customer : return true
User <-- EzShop : return true

```

## Scenario 4.4

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : getCustomer()
User <-- EzShop : customer.getId()
User -> EzShop : modifyCustomer()
EzShop -> Customer : c.setCardNumber()
EzShop <-- Customer : return true
EzShop -> Customer : c.setCustomerName()
EzShop <-- Customer : return true
User <-- EzShop : return true

```

## Scenario 5.1

```plantuml

actor user
note over user: User ="Admin or\nShop Manager or\nCashier"
user -> EzShop : login()
EzShop -> EzShop : getUserByUsername() //ADDED TOBECHECKED
EzShop -> User : user.getPassword()
EzShop <-- User : return password
user <-- EzShop : return user

```

## Scenario 5.2

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : logout()
User <-- EzShop : return true

```


## Scenario 6.1

```plantuml

actor Cashier
Cashier -> EzShop : startSaleTransaction()
EzShop -> SaleTransaction : SaleTransaction()
EzShop <- SaleTransaction : return SaleTransaction

Cashier -> EzShop : addProductToSale
EzShop -> ProductType : ProductType()
EzShop <- ProductType : return ProductType
EzShop -> ProductType : p.updateQuantity()
EzShop <- ProductType : return true
EzShop -> SaleTransactionRecord : SaleTransactionRecord(t.getId, p.getId)
EzShop <- SaleTransactionRecord : return SaleTransactionRecord
EzShop -> SaleTransaction : t.addSaleTransactionRecord(r)
EzShop <- SaleTransaction : return true
Cashier <- EzShop : return true

Cashier <- EzShop : return t.getId()
Cashier -> EzShop : endSaleTransaction (id)
Cashier <- EzShop : return true


Cashier -> Cashier : Manage payment (UC7)
```

## Scenario 6.2

```plantuml

actor Cashier
Cashier -> EzShop : startSaleTransaction()
EzShop -> SaleTransaction : SaleTransaction()
EzShop <- SaleTransaction : return SaleTransaction

Cashier -> EzShop : addProductToSale
EzShop -> ProductType : ProductType()
EzShop <- ProductType : return ProductType
EzShop -> ProductType : p.updateQuantity()
EzShop <- ProductType : return true
EzShop -> SaleTransactionRecord : SaleTransactionRecord(t.getId, p.getId)
EzShop <- SaleTransactionRecord : return SaleTransactionRecord
EzShop -> SaleTransaction : t.addSaleTransactionRecord(r)
EzShop <- SaleTransaction : return true
Cashier <- EzShop : return true

Cashier -> EzShop : applyDiscountRateToProduct (id, code, discount)

Cashier <- EzShop : return true


Cashier <- EzShop : return t.getId()
Cashier -> EzShop : endSaleTransaction (id)
Cashier <- EzShop : return true


Cashier -> Cashier : Manage payment (UC7)
```

## Scenario 6.3

```plantuml

actor Cashier
Cashier -> EzShop : startSaleTransaction()
EzShop -> SaleTransaction : SaleTransaction()
EzShop <- SaleTransaction : return SaleTransaction

Cashier -> EzShop : addProductToSale
EzShop -> ProductType : ProductType()
EzShop <- ProductType : return ProductType
EzShop -> ProductType : p.updateQuantity()
EzShop <- ProductType : return true
EzShop -> SaleTransactionRecord : SaleTransactionRecord(t.getId, p.getId)
EzShop <- SaleTransactionRecord : return SaleTransactionRecord
EzShop -> SaleTransaction : t.addSaleTransactionRecord(r)
EzShop <- SaleTransaction : return true
Cashier <- EzShop : return true

Cashier -> EzShop : applyDiscountRateToSale (id, discount)
Cashier <- EzShop : return true


Cashier <- EzShop : return t.getId()
Cashier -> EzShop : endSaleTransaction (id)
Cashier <- EzShop : return true


Cashier -> Cashier : Manage payment (UC7)
```


## Scenario 6.5

```plantuml

actor Cashier
Cashier -> EzShop : startSaleTransaction()
EzShop -> SaleTransaction : SaleTransaction()
EzShop <- SaleTransaction : return SaleTransaction

Cashier -> EzShop : addProductToSale
EzShop -> ProductType : ProductType()
EzShop <- ProductType : return ProductType
EzShop -> ProductType : p.updateQuantity()
EzShop <- ProductType : return true
EzShop -> SaleTransactionRecord : SaleTransactionRecord(t.getId, p.getId)
EzShop <- SaleTransactionRecord : return SaleTransactionRecord
EzShop -> SaleTransaction : t.addSaleTransactionRecord(r)
EzShop <- SaleTransaction : return true
Cashier <- EzShop : return true

Cashier -> EzShop : applyDiscountRateToSale (id, discount)
Cashier <- EzShop : return true


Cashier <- EzShop : return t.getId()
Cashier -> EzShop : endSaleTransaction (id)
Cashier <- EzShop : return true


Cashier -> Cashier : Manage payment (UC7)

Cashier -> EzShop : deleteSaleTransaction (id)
Cashier <- EzShop : return true
```

## Scenario 7.1

```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : receiveCreditCardPayment()
note right : via\nCreditCardCircuit\nAPI
EzShop -> EzShop : getSaleTransaction()
EzShop -> EzShop : validateCreditCard()
note left : via //Luhn algorithm//
EzShop -> SaleTransaction : getTotal()
EzShop <- SaleTransaction : total
EzShop -> SaleTransaction : setCreditCard()
EzShop <-- SaleTransaction : return true
EzShop -> BalanceOperation : recordBalanceUpdate() 
note right: type =<CREDIT>
EzShop <-- BalanceOperation : return true
note left: (or return false if toBeAdded + currentBalance < 0)
User <-- EzShop : return true
```

## Scenario 7.2

```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : receiveCreditCardPayment()
note right : via\nCreditCardCircuit\nAPI
EzShop -> EzShop : getSaleTransaction()
EzShop -> EzShop : validateCreditCard()
note left : via //Luhn algorithm//
User <-- EzShop : return false
note left : invalid credit\n card number

```

## Scenario 7.3

```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : receiveCreditCardPayment()
note right : via\nCreditCardCircuit\nAPI
EzShop -> EzShop : getSaleTransaction()
EzShop -> EzShop : validateCreditCard()
note left : via //Luhn algorithm//
EzShop -> SaleTransaction : getTotal()
EzShop <- SaleTransaction : total
User <-- EzShop : return false
note left : insufficient credit
```

## Scenario 7.4 

```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : receiveCashPayment()
EzShop -> EzShop : getSaleTransaction()
EzShop -> SaleTransaction : getTotal()
EzShop <- SaleTransaction : total
EzShop -> SaleTransaction : setCash()
EzShop <-- SaleTransaction : return true 
EzShop -> BalanceOperation : recordBalanceUpdate() 
note right: type =<CREDIT>
EzShop <-- BalanceOperation : return true
note left: (or return false if toBeAdded + currentBalance < 0)
User <-- EzShop : return t.change

```

## Scenario 8.1/8.2
```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : startReturnTransaction()
User <-- EzShop : return t.Id
User -> EzShop : returnProduct()
note left : N = return \nproduct quantity
EzShop -> ProductType : updateQuantity()
note right : p.quantity + N
EzShop <- ProductType : return true
User -> User : Manage payment(UC 10)
User -> EzShop : EndReturnTransaction()
User <- EzShop : return true

```

## Scenario 9.1 - List credits and debits
```plantuml
actor ShopManager
ShopManager -> EzShop : getCreditsAndDebits()
ShopManager <--  BalanceOperation  : List<BalanceOperation>
```

## Scenario 10.1 - Return payment by  credit card
```plantuml
actor Any
Any -> EzShop : returnCreditCardPayment()
activate EzShop
EzShop -> EzShop : getReturnTransaction()
activate ReturnTransaction
EzShop -> EzShop : validateCreditCard()
EzShop -> ReturnTransaction  : getTotal()
EzShop <-- ReturnTransaction  : total
deactivate ReturnTransaction
EzShop -> BalanceOperation  ** : new
Any <-- EzShop : total 
deactivate EzShop
```

## Scenario 10.2 - Return cash payment
```plantuml
actor Any
Any -> EzShop : returnCashPayment()
activate EzShop
EzShop -> EzShop : getReturnTransaction()
activate ReturnTransaction
EzShop -> ReturnTransaction  : getTotal()
EzShop <-- ReturnTransaction  : total
deactivate ReturnTransaction
EzShop -> BalanceOperation  ** : new
Any <-- EzShop : total 
deactivate EzShop
```
