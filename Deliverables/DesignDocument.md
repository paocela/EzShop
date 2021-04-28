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
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

