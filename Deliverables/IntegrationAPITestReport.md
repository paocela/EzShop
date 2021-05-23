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
We chose to apply a bottom up approach, since we obtained high coverage on all models via unit testing, and DAOs are provided by the ORMLite, which we selected as a development platform.
Therefore, our tests will only apply to methods contained in EZShop and declared in the EZShopInterface.

#  Tests
All integration tests are contained in src/test/java/it/polito/ezshop/integrationTests.
Please run them through the main file (TestEZShopIntegration) to obtain a valid suite execution.

## Step 1
| Classes  | JUnit test cases |
| ------------- |:-------------:|
|it.polito.ezshop.data.EZShop|*|



# Scenarios

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
| 1-1       | FR3.1,                  | testCreateProductType |
| 1-2      | FR3.4, FR4.2                 | testUpdateLocation |
| 1-3       | FR4.1 | testUpdateProductType |
| 2.1         | FR1.1                 |    testCreateUser         |             
| 2.2         | FR1.2                 |      testDeleteUser       |             
| 2.3         | FR1.1                |     testModifyUserRights<br />testGetUser        |   
| 3-1 | FR4.3, | testValidIssueOrder |
| 3-2 | FR4.5 | testValidPayOrder |
| 3-3 | FR4.6 | testValidRecordOrderArrival |
| 3-4 | FR4.4 | testValidPayOrderFor |
| 4.1         | FR5.1                 |      testCreateValidCustomer<br /> testCreateCustomerUnauthorized      |             
| 4.2         | FR5.6            |    testCreateCard <br /> testGetCustomer <br /> testAttachCardToCustomer         |             
| 4.3         | FR5.1                 | testDetachValidCardFromCustomer            |
| 4.4        | FR5.1, FR5.2         |  testModifyValidNameCustomerRecord <br />testModifyValidCardCustomerRecord<br />testModifyInvalidNameCustomerRecord<br />testDeleteCustomerRecord     |
| 5.1       | FR1.5      |   testValidLogin <br /> testValidLogout    |
| 6-1 | FR6.1, FR6.2, FR6.3, FR6.7, FR6.10, FR6.11, FR7.1, FR7.2, FR8.2 | testCompletedCreditCard <br /> testCompletedCash |
| 6-2 | FR6.1, FR6.2, FR6.5, FR6.7, FR6.10, FR6.11, FR7.1, FR8.2 | testProductDiscount |
| 6-3 | FR6.1, FR6.2, FR6.4, FR6.7, FR6.10, FR6.11, FR7.1, FR8.2 | testSaleDiscount |
| 6-4 | FR5.7, FR6.1, FR6.2, FR6.7, FR6.10, FR6.11, FR7.1, FR8.2 | testLoyaltyCard |
| 6-5 | FR6.1, FR6.2, FR6.7, FR6.10, FR6.11 | testCancelled |
| 6-6 | FR6.1, FR6.2, FR6.3, FR6.7, FR6.10, FR6.11, FR7.1, FR8.2 | testCompletedCash |
| 7-1 | FR6.1, FR6.2, FR6.7, FR6.10, FR6.11, FR7.2, FR8.2 | testCompletedCreditCard |
| 7-2 | FR6.1, FR6.2, FR6.7, FR6.10, FR6.11, FR7.2 | testFailedCreditCard |
| 7-3 | FR6.1, FR6.2, FR6.7, FR6.10, FR6.11, FR7.2 | testInsufficientFunds |
| 7-4 | FR6.1, FR6.2, FR6.3, FR6.7, FR6.10, FR6.11, FR7.1, FR8.2 | testCompletedCash |
| 8-1 | FR6.12, FR6.13, FR6.14, FR6.15, FR7.4, FR8.1 | testCompletedReturnTransactionCreditCard |
| 8-2 | FR6.12, FR6.13, FR6.14, FR6.15, FR7.3, FR8.1 | testCompletedReturnTransactionCash |
| 9-1 | FR8.1, FR8.2, FR8.3, | testListBalance (to fix) |
| 10-1 | | |
| 10-2 | | |
        




# Coverage of Non Functional Requirements


<Report in the following table the coverage of the Non Functional Requirements of the application - only those that can be tested with automated testing frameworks.>


### 

| Non Functional Requirement | Test name |
| -------------------------- | --------- |
|      NFR4                      |  /acceptanceTests/barCodeValidationTest         |
|      NFR5                      |  /acceptanceTests/creditCardValidationTest         |
|      NFR6                      |  testInvalidCustomerCard         |


