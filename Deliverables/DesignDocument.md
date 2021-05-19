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
  - [Scenario 1.1 - Create product type X](#scenario-11---create-product-type-x)
  - [Scenario 1.2 - Modify product type location](#scenario-12---modify-product-type-location)
  - [Scenario 1.3 - Modify product type price per unit](#scenario-13---modify-product-type-price-per-unit)
  - [Scenario 2.1 - Create user and define rights](#scenario-21---create-user-and-define-rights)
  - [Scenario 2.3 - Modify user rights](#scenario-23---modify-user-rights)
  - [Scenario 3.1 - Order of product type X issued](#scenario-31---order-of-product-type-x-issued)
  - [Scenario 3.2 - Order of product type X payed](#scenario-32---order-of-product-type-x-payed)
  - [Scenario 3.3 - Record order of product type X arrival](#scenario-33---record-order-of-product-type-x-arrival)
  - [Scenario 4.1 - Create customer record](#scenario-41---create-customer-record)
  - [Scenario 4.2 - Attach Loyalty card to customer record](#scenario-42---attach-loyalty-card-to-customer-record)
  - [Scenario 4.3 - Detach Loyalty card from customer record](#scenario-43---detach-loyalty-card-from-customer-record)
  - [Scenario 4.4 - Update customer record](#scenario-44---update-customer-record)
  - [Scenario 5.1 - Login](#scenario-51---login)
  - [Scenario 5.2 - Logout](#scenario-52---logout)
  - [Scenario 6.1 - Sale of product type X completed](#scenario-61---sale-of-product-type-x-completed)
  - [Scenario 6.2 - Sale of product type X with product discount](#scenario-62---sale-of-product-type-x-with-product-discount)
  - [Scenario 6.3 - Sale of product type X with sale discount](#scenario-63---sale-of-product-type-x-with-sale-discount)
  - [Scenario 6.5 - Sale of product type X cancelled](#scenario-65---sale-of-product-type-x-cancelled)
  - [Scenario 7.1 - Manage payment by valid credit card](#scenario-71---manage-payment-by-valid-credit-card)
  - [Scenario 7.2 - Manage payment by invalid credit card](#scenario-72---manage-payment-by-invalid-credit-card)
  - [Scenario 7.3 - Manage credit card payment with not enough credit](#scenario-73---manage-credit-card-payment-with-not-enough-credit)
  - [Scenario 7.4 - Manage cash payment](#scenario-74---manage-cash-payment)
  - [Scenario 8.1/8.2 - Return transaction of product type X completed, credit card / cash](#scenario-8182---return-transaction-of-product-type-x-completed-credit-card--cash)
  - [Scenario 9.1 - List credits and debits](#scenario-91---list-credits-and-debits)
  - [Scenario 10.1 - Return payment by credit card](#scenario-101---return-payment-by-credit-card)
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

```plantuml

package it.polito.ezshop.data {

   
    abstract class EzShopInterface {
    + reset() : void
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
    + createCard(): String
    + attachCardToCustomer(customerCard: String, customerId: Integer): boolean
    + modifyPointsOnCard(customerCard: String, pointsToBeAdded: int): boolean
    + startSaleTransaction(): Integer
    + addProductToSale(transactionId: Integer, productCode: String, amount: int): boolean
    + deleteProductFromSale(Integer transactionId, String productCode, int amount): boolean
    + applyDiscountRateToProduct(transactionId: Integer, productCode: String, discountRate: double):boolean
    + applyDiscountRateToSale(transactionId: Integer, discountRate: double): boolean
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
    
       class EZShop {
        - {static} DATABASE_URL: String
        - {static} CREDIT_CARDS_FILE_PATH: String
        - connectionSource: ConnectionSource
        - userDao: Dao<User, Integer>
        - productTypeDao: Dao<ProductType, Integer>
        - customerDao: Dao<Customer, Integer>
        - saleTransactionDao: Dao<SaleTransaction, Integer>
        - returnTransactionDao: Dao<ReturnTransaction, Integer>
        - saleTransactionRecordDao: Dao<SaleTransactionRecord, Integer>
        - returnTransactionRecordDao: Dao<ReturnTransactionRecord, Integer>
        - orderDao: Dao<Order, Integer>
        - balanceOperationDao: Dao<BalanceOperation, Integer>
        - creditCardDao: Dao<CreditCard, String>
        - userLogged: User
        - ongoingTransaction: SaleTransaction
        - ongoingReturnTransaction: ReturnTransaction
        
        - authorize(): void
        - updateInventoryByPaidTransaction(): void
        - updateInventoryByReturnTransaction(): void
        - loadCreditCardsFromUtils(): void
        - loadCreditCard(): void
        
        + getOngoingTransactionById(): SaleTransaction
        + getOngoingReturnTransactionById(): ReturnTransaction
        + hashPassword(): String
        + byteToHex(): String
        + validateCreditCard(): String
        + validateBarcode(): String
        

    } 
    EZShop --^ EzShopInterface
}
```

```plantuml

package it.polito.ezshop.model {

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
        RoleEnum: enum
        id: Integer
        username : String   
        password : String
        role : String

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
    
        StatusEnum: enum
        id: Integer
        status: StatusEnum
        amount: double
        discountRate : double
        date : LocalDate
        time : LocalTime
        paymentType: String
        cash: double
        change: double
        creditCard: CreditCard
        {static} createdAt: long
        records : ForeignCollection <SaleTransactionRecord>
        + refreshAmount(): void
        + addProductToRecords(): boolean
        + removeProductFromRecords(): boolean

    }
    class SaleTransactionRecord {
        id: Integer
        amount: int
        totalPrice : double
        productType: ProductType
        discountRate: double
        saleTransaction: SaleTransaction
        + refreshTotalPrice(): void
    }
    
    class ReturnTransaction {
        returnId : Integer
        transactionId: Integer
        status: StatusEnum
        returnedValue: double
        recordList : List <ReturnTransactionRecord>        

    }
    class ReturnTransactionRecord {
        returnId: Integer
        productType: ProductType
        quantity: int
        totalPrice : double
        returnTransaction: ReturnTransaction

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
        type: Enum<CREDIT,DEBIT>
        amount: double

    }
    
    class CreditCard {
       code: String
       amount: double
    }
}

SaleTransaction - "*" SaleTransactionRecord
ReturnTransaction - "*" ReturnTransactionRecord
CreditCard  -- "*" SaleTransaction 
SaleTransactionRecord -- ProductType
ReturnTransactionRecord - ProductType

Customer - "0..1" Card
ProductType -- Order

BalanceOperation ^- SaleTransaction
BalanceOperation ^- ReturnTransaction
BalanceOperation ^- Order
SaleTransaction -- ReturnTransaction

```


```plantuml

package it.polito.ezshop.exceptions {
class InvalidUsernameException {}
class InvalidPasswordException {}
class InvalidRoleException {}
class InvalidUserIdException {}
class UnauthorizedException {}
class InvalidPasswordException {}
class InvalidProductDescriptionException {}
class InvalidProductCodeException {}
class InvalidPricePerUnitException {} 
class InvalidProductIdException {} 
class InvalidLocationException {} 
class InvalidQuantityException {} 
class InvalidOrderIdException {} 
class InvalidCustomerNameException {} 
class InvalidCustomerCardException {}
class InvalidCustomerIdException {}
class InvalidTransactionIdException {}
class InvalidPaymentException{}
class InvalidCreditCardException{}
hide members
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
| FR4.5 | X | | | | | | | | | | X | X
| FR4.6 | X | | | | | | | | | | X
| FR4.7 | X | | | | | | | | | | X
| FR5.1 | X | X
| FR5.2 | X | X
| FR5.3 | X | X
| FR5.4 | X | X
| FR5.5 | X | | X
| FR5.6 | X | X | X
| FR5.7 | X | | X
| FR6.1 | X | | | | | X
| FR6.2 | X | | | | X | X | X
| FR6.3 | X | | | | X | X | X
| FR6.4 | X | | | | | X
| FR6.5 | X | | | |  | X | | | | X
| FR6.6 | X | | X| | | X
| FR6.7 | X | | | | X
| FR6.8 | X | | | | | X
| FR6.10 | X | | | | | X
| FR6.11 | X | | | | | X
| FR6.12 | X | | | | | | | X
| FR6.13 | X | | | | X | X | X | X | X
| FR6.14 | X | | | | | | | X
| FR6.15 | X | | | | | | | X | X
| FR7.1 | X | | | | | X | | | | | | X
| FR7.2 | X | | | | | X | | | | | | X
| FR7.3 | X | | | | | | | X | | | | X
| FR7.4 | X | | | | | | |X  | | | | X
| FR8.1 | X | | | | | | |  | | | | X
| FR8.2 | X | | | | | | |  | | | | X
| FR8.3 | X | | | | | | |  | | | | X
| FR8.4 | X | | | | | | |  | | | | X

# Verification sequence diagrams 

## Scenario 1.1 - Create product type X

```plantuml
actor User
note over User: User ="Admin or\nShop Manager"
User -> EzShop : createProductType()
activate EzShop
EzShop -> ProductType : Product()
activate ProductType
EzShop <-- ProductType : return Product 
deactivate ProductType
User <-- EzShop : p.getID()
deactivate EzShop

```
## Scenario 1.2 - Modify product type location

```plantuml
actor User
note over User: User ="Admin or\nShop Manager"
User -> EzShop : UpdatePosition()
activate EzShop
EzShop -> EzShop : p.getProductTypeByBarCode()
EzShop -> ProductType : p.setPosition()
activate ProductType
EzShop <-- ProductType : return true 
deactivate ProductType
User <-- EzShop : return true
deactivate EzShop

```
## Scenario 1.3 - Modify product type price per unit

```plantuml
actor User
note over User: User ="Admin or\nShop Manager"
User -> EzShop : UpdateProduct()
activate EzShop
EzShop -> EzShop : p.getProductTypeByBarCode()
EzShop -> ProductType : p.setPricePerUnit()
activate ProductType
EzShop <-- ProductType : return true 
deactivate ProductType
User <-- EzShop : return true
deactivate EzShop

```

## Scenario 2.1 - Create user and define rights

```plantuml
actor Admin
Admin -> EzShop : createUser()
activate EzShop
EzShop -> User : User()
activate User
EzShop <-- User : return User
deactivate User
Admin <-- EzShop : u.getId()
deactivate EzShop

```

## Scenario 2.3 - Modify user rights

```plantuml
actor Admin
Admin -> EzShop : updateUserRights()
activate EzShop
EzShop -> EzShop : getUser()
EzShop -> User : u.setUserRights()
activate User
EzShop <-- User : return true
deactivate User
Admin <-- EzShop : return true
deactivate EzShop

```

## Scenario 3.1 - Order of product type X issued
```plantuml
actor ShopManager
ShopManager -> EzShop : issueOrder()
activate EzShop
EzShop -> Order  : Order()
activate Order
EzShop <-- Order : return order
ShopManager <-- EzShop : o.getId()
deactivate Order
deactivate EzShop

```
## Scenario 3.2 - Order of product type X payed
```plantuml
actor ShopManager
ShopManager -> EzShop : payOrder()
activate EzShop
EzShop -> EzShop : getOrder()
activate Order
EzShop -> Order : o.setState()
EzShop <-- Order : true
deactivate Order
EzShop -> BalanceOperation : BalanceOperation()
activate BalanceOperation
EzShop <-- BalanceOperation : balanceOperation
deactivate BalanceOperation
ShopManager <-- EzShop : true 
deactivate EzShop

```
## Scenario 3.3 - Record order of product type X arrival
```plantuml
actor ShopManager
ShopManager -> EzShop: recordOrderArrival()
activate EzShop
EzShop -> EzShop : getOrder()
activate Order
EzShop -> Order : o.setState()
EzShop <-- Order : true

deactivate Order
EzShop -> EzShop : getProductType()
activate Product
EzShop -> Product : p.setUnits()
EzShop <-- Product : true
deactivate Product
ShopManager <-- EzShop : true 
deactivate EzShop
```

## Scenario 4.1 - Create customer record

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : defineCustomer()
activate EzShop
EzShop -> Customer : Customer()
activate Customer
EzShop <-- Customer : return Customer
deactivate Customer
User <-- EzShop : c.getId()
deactivate EzShop

```

## Scenario 4.2 - Attach Loyalty card to customer record

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : createCard()
activate EzShop
EzShop -> Card : Card()
activate Card
EzShop <-- Card : return Card
deactivate Card
User <-- EzShop : card.getId()
User -> EzShop : getCustomer()
EzShop -> EzShop : attachCardToCustomer()
EzShop -> EzShop : modifyCustomer()
EzShop -> Customer : c.setCardNumber()
activate Customer
EzShop <-- Customer : return true
deactivate Customer
User <-- EzShop : return true
deactivate EzShop

```

## Scenario 4.3/4.4 - Detach Loyalty card from customer record / Update customer record

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : getCustomer()
activate EzShop
User <-- EzShop : customer.getId()
User <-- EzShop : customer.getName()
User -> EzShop : modifyCustomer()
EzShop -> Customer : c.setCardNumber()
activate Customer
EzShop <-- Customer : return true
EzShop -> Customer : c.setCustomerName()
EzShop <-- Customer : return true
deactivate Customer
User <-- EzShop : return true
deactivate EzShop

```


## Scenario 5.1 - Login

```plantuml

actor user
note over user: User ="Admin or\nShop Manager or\nCashier"
user -> EzShop : login()
activate EzShop
EzShop -> EzShop : getUserByUsername()
EzShop -> User : user.getPassword()
activate User
EzShop <-- User : return password
deactivate User
user <-- EzShop : return user
deactivate EzShop

```

## Scenario 5.2 - Logout

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : logout()
activate EzShop
User <-- EzShop : return true
deactivate EzShop

```


## Scenario 6.1 - Sale of product type X completed

```plantuml

actor Cashier
Cashier -> EzShop : startSaleTransaction()
activate EzShop
EzShop -> SaleTransaction : SaleTransaction()
activate SaleTransaction
EzShop <-- SaleTransaction : return SaleTransaction
Cashier <-- EzShop : return t.getId()
Cashier -> EzShop : addProductToSale
EzShop -> EzShop : getProductTypeByBarCode()
activate ProductType
EzShop -> ProductType : p.updateQuantity()
EzShop <-- ProductType : return true
deactivate ProductType
EzShop -> SaleTransactionRecord : SaleTransactionRecord(t.getId, p.getId)
activate SaleTransactionRecord
EzShop <-- SaleTransactionRecord : return SaleTransactionRecord
deactivate SaleTransactionRecord
EzShop -> SaleTransaction : t.addSaleTransactionRecord(r)
EzShop <-- SaleTransaction : return true
deactivate SaleTransaction
Cashier <-- EzShop : return true
Cashier -> EzShop : endSaleTransaction (id)
Cashier <-- EzShop : return true
deactivate EzShop
Cashier -> Cashier : Manage payment (UC7)
```

## Scenario 6.2 - Sale of product type X with product discount

```plantuml

actor Cashier
Cashier -> EzShop : startSaleTransaction()
activate EzShop
EzShop -> SaleTransaction : SaleTransaction()
activate SaleTransaction
EzShop <-- SaleTransaction : return SaleTransaction
Cashier <-- EzShop : return t.getId()
Cashier -> EzShop : addProductToSale
EzShop -> EzShop : getProductTypeByBarCode()
activate ProductType
EzShop -> ProductType : p.updateQuantity()
EzShop <-- ProductType : return true
deactivate ProductType
EzShop -> SaleTransactionRecord : SaleTransactionRecord(t.getId, p.getId)
activate SaleTransactionRecord
EzShop <-- SaleTransactionRecord : return SaleTransactionRecord
deactivate SaleTransactionRecord
EzShop -> SaleTransaction : t.addSaleTransactionRecord(r)
EzShop <-- SaleTransaction : return true
Cashier <-- EzShop : return true
Cashier -> EzShop : applyDiscountRateToProduct (id, code, discount)
EzShop -> ProductDiscount : ProductDiscount()
activate ProductDiscount
EzShop <-- ProductDiscount : productDiscount
deactivate ProductDiscount
EzShop -> SaleTransaction : t.addProductDiscount(pD)
EzShop <-- SaleTransaction : return true
deactivate SaleTransaction
Cashier <-- EzShop : return true
Cashier -> EzShop : endSaleTransaction (id)
Cashier <-- EzShop : return true
deactivate EzShop
Cashier -> Cashier : Manage payment (UC7)
```

## Scenario 6.3 - Sale of product type X with sale discount

```plantuml

actor Cashier
Cashier -> EzShop : startSaleTransaction()
activate EzShop
EzShop -> SaleTransaction : SaleTransaction()
activate SaleTransaction
EzShop <-- SaleTransaction : return SaleTransaction
Cashier <-- EzShop : return t.getId()
Cashier -> EzShop : addProductToSale
EzShop -> EzShop : getProductTypeByBarCode()
activate ProductType
EzShop -> ProductType : p.updateQuantity()
EzShop <-- ProductType : return true
deactivate ProductType
EzShop -> SaleTransactionRecord : SaleTransactionRecord(t.getId, p.getId)
activate SaleTransactionRecord
EzShop <-- SaleTransactionRecord : return SaleTransactionRecord
deactivate SaleTransactionRecord
EzShop -> SaleTransaction : t.addSaleTransactionRecord(r)
EzShop <-- SaleTransaction : return true
Cashier <-- EzShop : return true
Cashier -> EzShop : applyDiscountRateToSale (id, discount)
EzShop -> SaleTransaction : t.setDiscountRateAmount()
EzShop <-- SaleTransaction : return true
deactivate SaleTransaction
Cashier <-- EzShop : return true
Cashier -> EzShop : endSaleTransaction (id)
Cashier <-- EzShop : return true
deactivate EzShop
Cashier -> Cashier : Manage payment (UC7)
```


## Scenario 6.5 - Sale of product type X cancelled

```plantuml

actor Cashier
Cashier -> EzShop : startSaleTransaction()
activate EzShop
EzShop -> SaleTransaction : SaleTransaction()
activate SaleTransaction
EzShop <-- SaleTransaction : return SaleTransaction
Cashier <-- EzShop : return t.getId()
Cashier -> EzShop : addProductToSale
EzShop -> EzShop : getProductTypeByBarCode()
activate ProductType
EzShop -> ProductType : p.updateQuantity()
EzShop <-- ProductType : return true
deactivate ProductType
EzShop -> SaleTransactionRecord : SaleTransactionRecord(t.getId, p.getId)
activate SaleTransactionRecord
EzShop <-- SaleTransactionRecord : return SaleTransactionRecord
deactivate SaleTransactionRecord
EzShop -> SaleTransaction : t.addSaleTransactionRecord(r)
EzShop <-- SaleTransaction : return true
Cashier <-- EzShop : return true
Cashier -> EzShop : endSaleTransaction (id)
deactivate SaleTransaction
Cashier <-- EzShop : return true
Cashier -> Cashier : Manage payment (UC7)
Cashier -> EzShop : deleteSaleTransaction (id)
EzShop ->x SaleTransaction :
Cashier <-- EzShop : return true
deactivate EzShop
```

## Scenario 7.1 - Manage payment by valid credit card

```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : receiveCreditCardPayment()
activate EzShop
note right : via\nCreditCardCircuit\nAPI
EzShop -> EzShop : getSaleTransaction()
EzShop -> EzShop : validateCreditCard()
note left : via //Luhn algorithm//
EzShop -> SaleTransaction : getTotal()
activate SaleTransaction
EzShop <-- SaleTransaction : total
EzShop -> SaleTransaction : setCreditCard()
EzShop <-- SaleTransaction : return true
deactivate SaleTransaction
EzShop -> BalanceOperation : recordBalanceUpdate() 
activate BalanceOperation
note right: type =<CREDIT>
EzShop <-- BalanceOperation : return true
deactivate BalanceOperation
note left: (or return false if toBeAdded + currentBalance < 0)
User <-- EzShop : return true
deactivate EzShop
```

## Scenario 7.2 - Manage payment by invalid credit card

```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : receiveCreditCardPayment()
activate EzShop
note right : via\nCreditCardCircuit\nAPI
EzShop -> EzShop : getSaleTransaction()
EzShop -> EzShop : validateCreditCard()
note left : via //Luhn algorithm//
User <-- EzShop : return false
deactivate EzShop
note left : invalid credit\n card number

```

## Scenario 7.3 - Manage credit card payment with not enough credit

```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : receiveCreditCardPayment()
activate EzShop
note right : via\nCreditCardCircuit\nAPI
EzShop -> EzShop : getSaleTransaction()
EzShop -> EzShop : validateCreditCard()
note left : via //Luhn algorithm//
EzShop -> SaleTransaction : getTotal()
activate SaleTransaction
EzShop <- SaleTransaction : total
deactivate SaleTransaction
User <-- EzShop : return false
deactivate EzShop
note left : insufficient credit
```

## Scenario 7.4 - Manage cash payment

```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : receiveCashPayment()
activate EzShop
EzShop -> EzShop : getSaleTransaction()
EzShop -> SaleTransaction : getTotal()
activate SaleTransaction
EzShop <- SaleTransaction : total
EzShop -> SaleTransaction : setCash()
EzShop <-- SaleTransaction : return true 
deactivate SaleTransaction
EzShop -> BalanceOperation : recordBalanceUpdate() 
activate BalanceOperation
note right: type =<CREDIT>
EzShop <-- BalanceOperation : return true
deactivate BalanceOperation
note left: (or return false if toBeAdded + currentBalance < 0)
User <-- EzShop : return t.change
deactivate EzShop

```

## Scenario 8.1/8.2 - Return transaction of product type X completed, credit card / cash
```plantuml
actor User
note over User : User = "Admin,\nShop Manager,\nCashier"
User -> EzShop : startReturnTransaction()
activate EzShop
EzShop -> EzShop : getSaleTransaction()
EzShop -> ReturnTransaction  : ReturnTransaction()
activate ReturnTransaction
EzShop <-- ReturnTransaction  : return ReturnTransaction
User <-- EzShop : return r.getId()
User -> EzShop : returnProduct()
note left : N = return \nproduct quantity
EzShop -> EzShop : getProductTypeByBarCode()
EzShop -> ProductType : p.updateQuantity()
activate ProductType
note right : p.quantity + N
EzShop <-- ProductType : return true
deactivate ProductType
EzShop -> ReturnTransactionRecord : returnTransactionRecord(r.getId, p.getId)
activate ReturnTransactionRecord
EzShop <-- ReturnTransactionRecord : return returnTransactionRecord
deactivate ReturnTransactionRecord
EzShop -> ReturnTransaction : t.addReturnTransactionRecord(r)
EzShop <-- ReturnTransaction : return true
deactivate ProductType
deactivate ReturnTransaction
User <-- EzShop : return true
User -> User : Manage payment(UC 10)
User -> EzShop : EndReturnTransaction()
User <-- EzShop : return true
deactivate EzShop

```

## Scenario 9.1 - List credits and debits
```plantuml
actor ShopManager
ShopManager -> EzShop : getCreditsAndDebits(startDate, endDate)
activate EzShop
EzShop ->  BalanceOperation  : select from db where in daterange
activate BalanceOperation
EzShop <--  BalanceOperation  : List<BalanceOperation>
deactivate BalanceOperation
ShopManager <--  EzShop  : List<BalanceOperation>
deactivate EzShop
```

## Scenario 10.1 - Return payment by credit card
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
EzShop -> BalanceOperation  : recordBalanceUpdate() 
note right: type = <DEBIT>
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
EzShop -> BalanceOperation : recordBalanceUpdate() 
note right: type = <DEBIT>
Any <-- EzShop : total 
deactivate EzShop
```
