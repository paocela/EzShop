# Design assessment


```
<The goal of this document is to analyse the structure of your project, compare it with the design delivered
on April 30, discuss whether the design could be improved>
```

# Levelized structure map

![levelized_structured_map](WBimages/levelized_structured_map.png?raw=true "levelized_structured_map")
# Structural over complexity chart

![structural_over_complexity](WBimages/structural_over_complexity.png?raw=true "structural_over_complexity")


# Size metrics




| Metric                                    | Measure |
| ----------------------------------------- | ------- |
| Packages                                  |     6   |
| Classes (outer)                           |    41   |
| Classes (all)                             |    46   |
| NI (number of bytecode instructions)      |   6,104 |
| LOC (non comment non blank lines of code) |  ~ 2,625|



# Items with XS


| Item | Tangled | Fat  | Size | XS   |
| ---- | ------- | ---- | ---- | ---- |
|ezshop.it.polito.ezshop.data.EZShop|  	    |214 |4495 |1974|
|ezshop.it.polito.ezshop            |1,54%	| 4  |6104 | 94 |
|ezshop.it.polito.ezshop.data.EZShop.returnProduct(java.lang.Integer, java.lang.String, int):boolean	 |       | 17  |  182   | 21   |


# Package level tangles


![ezshop](WBimages/ezshop.png?raw=true "ezshop")
![data](WBimages/data.png?raw=true "data")
![model](WBimages/model.png?raw=true "model")

# Summary analysis


The main differences between the previous version of our project and the design delivered on April 30 are the following:
- the card class has been fully integrated in the customer class
- product discount has been fully integrated in SaleTransactionRecord class
- credit card class has been added 
- foreign IDs has been replaced with relationships provided by ORMLite (db)
- all daos has been added to ezShop class in order to interact with the db

Some of the weakness that could be fixed are the following:
- EzShop fatness could be reduced by creating multiple controllers for each gui section (User, Order, Transaction, etc...) 
- while tangling could be only reduced by modifying the interaction between gui and our code since right now EzShop is the single point of failure (the gui only calls method in this class) 