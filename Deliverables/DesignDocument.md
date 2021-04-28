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









# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>











# Verification sequence diagrams 

Scenario 1.1

```plantuml
actor User
note over User: User ="Admin or\nShop Manager"
User -> EzShop : createProductType()
EzShop -> ProductType : Product()
EzShop <-- ProductType : return Product 
User <-- EzShop : p.getID()

```
Scenario 1.2

```plantuml
actor User
note over User: User ="Admin or\nShop Manager"
User -> EzShop : UpdatePosition()
EzShop -> EzShop : p.getProductTypeByBarCode()
EzShop -> ProductType : p.setPosition()
EzShop <-- ProductType : return true 
User <-- EzShop : return true

```
Scenario 1.3

```plantuml
actor User
note over User: User ="Admin or\nShop Manager"
User -> EzShop : UpdateProduct()
EzShop -> EzShop : p.getProductTypeByBarCode()
EzShop -> ProductType : p.setPricePerUnit()
EzShop <-- ProductType : return true 
User <-- EzShop : return true

```

Scenario 2.1

```plantuml
actor Admin
Admin -> EzShop : createUser()
EzShop -> User : User()
EzShop <-- User : return User
Admin <-- EzShop : u.getId()

```

Scenario 2.3

```plantuml
actor Admin
Admin -> EzShop : updateUserRights()
EzShop -> EzShop : u.getUser()
EzShop -> User : u.setUserRights()
EzShop <-- User : return true
Admin <-- EzShop : return true

```







Scenario 4.1

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : defineCustomer()
EzShop -> Customer : Customer()
EzShop <-- Customer : return Customer
User <-- EzShop : c.getId()

```

Scenario 4.2

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

Scenario 4.3

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

Scenario 4.4

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

Scenario 5.1

```plantuml

actor user
note over user: User ="Admin or\nShop Manager or\nCashier"
user -> EzShop : login()
EzShop -> EzShop : getUserByUsername() //ADDED TOBECHECKED
EzShop -> User : user.getPassword()
EzShop <-- User : return password
user <-- EzShop : return user

```

Scenario 5.2

```plantuml

actor User
note over User: User ="Admin or\nShop Manager or\nCashier"
User -> EzShop : logout()
User <-- EzShop : return true

```