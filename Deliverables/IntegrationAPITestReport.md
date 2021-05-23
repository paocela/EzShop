# Integration and API Test Documentation

Authors: Paolo Celada, Teodoro Corbo, Luca Pezzolla, Francesco Policastro

Date: 22/05/2021

Version: 1.0

# Contents

- [Dependency graph](#dependency-graph)

- [Integration and API Test Documentation](#integration-and-api-test-documentation)
- [Contents](#contents)
- [Dependency graph](#dependency-graph)
- [Integration approach](#integration-approach)
- [Tests](#tests)
  - [Step 1](#step-1)
  - [Step 2](#step-2)
  - [Step n](#step-n)
- [Scenarios](#scenarios)
  - [Scenario UCx.y](#scenario-ucxy)
- [Coverage of Scenarios and FR](#coverage-of-scenarios-and-fr)
- [Coverage of Non Functional Requirements](#coverage-of-non-functional-requirements)
    - [](#)

- [Tests](#tests)

- [Scenarios](#scenarios)

- [Coverage of scenarios and FR](#scenario-coverage)
- [Coverage of non-functional requirements](#nfr-coverage)



# Dependency graph 

```plantuml

EzShop -down-> BalanceOperation
EzShop -down-> Customer
EzShop -down-> Order
EzShop -down-> User
EzShop -down-> ReturnTransaction
EzShop -down-> SaleTransaction

SaleTransaction -down-> CreditCard
SaleTransaction -down-> SaleTransactionRecord
SaleTransaction -down-> ProductType

SaleTransactionRecord -down-> ProductType

ReturnTransaction -down-> ReturnTransactionRecord
ReturnTransactionRecord -down-> ProductType

EzShop -down-> SaleTransactionRecord
EzShop -down-> ReturnTransactionRecord
EzShop -down-> CreditCard
EzShop -down-> ProductType

```

# Integration approach

    <Write here the integration sequence you adopted, in general terms (top down, bottom up, mixed) and as sequence
    (ex: step1: class A, step 2: class A+B, step 3: class A+B+C, etc)> 
    <Some steps may  correspond to unit testing (ex step1 in ex above), presented in other document UnitTestReport.md>
    <One step will  correspond to API testing>



#  Tests

   <define below a table for each integration step. For each integration step report the group of classes under test, and the names of
     JUnit test cases applied to them> JUnit test classes should be here src/test/java/it/polito/ezshop

## Step 1
| Classes  | JUnit test cases |
|--|--|
|||


## Step 2
| Classes  | JUnit test cases |
|--|--|
|||


## Step n 

   

| Classes  | JUnit test cases |
|--|--|
|||




# Scenarios


<If needed, define here additional scenarios for the application. Scenarios should be named
 referring the UC in the OfficialRequirements that they detail>

## Scenario UC3.4

| Scenario | Order of product type X issued and payed |
| ------------- |:-------------:|
|  Precondition     | ShopManager S exists and is logged in |
|  | Product type X exists |
|  | Balance >= Order.units * Order.pricePerUnit |
|  Post condition     | Order O exists and is in PAYED state |
|  | Balance -= Order.units * Order.pricePerUnit |
|  | X.units not changed |
| Step#        | Description  |
|  1     | S creates order O |
|  2     | S fills quantity of product to be ordered and the price per unit |
| 3 | O's state is updated to PAYED |



# Coverage of Scenarios and FR


<Report in the following table the coverage of  scenarios (from official requirements and from above) vs FR. 
Report also for each of the scenarios the (one or more) API JUnit tests that cover it. >




| Scenario ID | Functional Requirements covered | JUnit  Test(s) |
| ----------- | ------------------------------- | ----------- |
|  1-1       | FR3.1,                  | testCreateProductType |
|  1-2      | FR3.4, FR4.2                 | testUpdateLocation |
| 1-3       | FR4.1 | testUpdateProductType |
| 2-1      |                                 |             |
| 2-2      |                                 |             |
| 2-3      |                                 |             |
| 3-1 | FR4.3, | testValidIssueOrder |
| 3-2 | FR4.5 | testValidPayOrder |
| 3-3 | FR4.6 | testValidRecordOrderArrival |
| 3-4 | FR4.4 | testValidPayOrderFor |
| 4-1 | | |
| 4-2 | | |
| 4-3 | | |
| 4-4 | | |
| 5-1 | | |
| 5-2 | | |
| 6-1 | | |
| 6-2 | | |
| 6-3 | | |
| 6-4 | | |
| 6-5 | | |
| 6-6 | | |
| 7-1 | | |
| 7-2 | | |
| 7-3 | | |
| 7-4 | | |
| 8-1 | | |
| 8-2 | | |
| 9-1 | FR8.1,FR8.2, FR8.3, | testListBalance (to fix) |
| 10-1 | | |
| 10-2 | | |



# Coverage of Non Functional Requirements


<Report in the following table the coverage of the Non Functional Requirements of the application - only those that can be tested with automated testing frameworks.>


### 

| Non Functional Requirement | Test name                |
| -------------------------- | ------------------------ |
| NFR4                       | barCodeValidationTest    |
| NFR5                       | creditCardValidationTest |

