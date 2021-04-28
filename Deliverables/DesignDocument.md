# Design Document 


Authors: Francesco Policastro, Paolo Celada, Luca Pezzolla, Teodoro Corbo

Date: 28/04/2021

Version: 1.0


# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
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


