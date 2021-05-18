# Unit Testing Documentation

Authors: Francesco Policastro, Paolo Celada, Luca Pezzolla, Teodoro Corbo

Date: 15/05/2021

Version: 1.0

# Contents

- [Black Box Unit Tests](#black-box-unit-tests)




- [White Box Unit Tests](#white-box-unit-tests)


# Black Box Unit Tests

    <Define here criteria, predicates and the combination of predicates for each function of each class.
    Define test cases to cover all equivalence classes and boundary conditions.
    In the table, report the description of the black box test case and (traceability) the correspondence with the JUnit test case writing the 
    class and method name that contains the test case>
    <JUnit test classes must be in src/test/java/it/polito/ezshop   You find here, and you can use,  class TestEzShops.java that is executed  
    to start tests
    >

- EZShop::hashPassword

- EZShop::byteToHex

- EZShop::validateCreditCard (my-done)

- EZShop::validateBarcode

- SaleTransactionRecord::refreshTotalPrice (my-done)

  


 ### **Class *EZShop* - method *hashPassword***



**Criteria for method *hashPassword*:**

 - Hashing algorithm validity

**Predicates for method *hashPassword*:**

| Criteria | Predicate |
| -------- | --------- |
|Hashing algorithm validity | true |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
| ...       | ...            |

**Combination of predicates**:


| hashing algorithm validity | Valid / Invalid | Description of the test case                                 | JUnit test case  |
| ---------------- | --------------- | ------------------------------------------------------------ | ---------------- |
| yes              | valid           | T1(*) | testStandardSHA1Implementation |


 ### **Class *EZShop* - method *byteToHex***

**Criteria for method *byteToHex*:**
 - encoding validity

**Predicates for method *byteToHex*:**

| Criteria | Predicate |
| -------- | --------- |
| encoding validity | yes       |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
| ...       | ...            |

**Combination of predicates**:


| encoding validity | Valid / Invalid | Description of the test case                                 | JUnit test case  |
| ---------------- | --------------- | ------------------------------------------------------------ | ---------------- |
| yes              | valid           | T1(*) | testValidByteToHexString |




### **Class *EZShop* - method *validateCreditCard***



**Criteria for method validateCreditCard:**
	

- Credit card number validity

- Credit card string format

  

**Predicates for method validateCreditCard:**

| Criterion                   | Predicate       |
| --------------------------- | --------------- |
| Credit card number validity | yes             |
|                             | no              |
| Credit card string format   | only numbers    |
|                             | numbers + chars |

**Boundaries for method validateCreditCard**:

| Criterion | Boundary values |
| --------- | --------------- |
| ...       | ...             |



 **Combination of predicates for method validateCreditCard**

| Credit card number validity | Credit card string format | Valid/Invalid | Description of the test case | JUnit test case         |
| --------------------------- | ------------------------- | ------------- | ---------------------------- | ----------------------- |
| yes                         | only numbers              | valid         | T1(12345674, "true")         | testCorrectCreditCard   |
| no                          | only numbers              | invalid       | T2(13245674, "false")        | testWrongCreditCard     |
| *                           | numbers + chars           | invalid       | T3(1324B5674, "false")       | testNonNumberCreditCard |



 ### **Class *EZShop* - method *validateBarcode***



**Criteria for method *validateBarcode*:**
	

 - Validity of the String parameter
 - Length of the String
 - Validity of barcode



**Predicates for method *validateBarcode*:**

| Criteria                         | Predicate                |
| -------------------------------- | ------------------------ |
| Validity of the String parameter | Valid                    |
|                                  | Null                     |
| Length of the String             | 12 <= length <= 14       |
|                                  | length <12 OR length >14 |
| Validity of barcode              | Yes                      |
|                                  | No                       |



**Boundaries for method validateBarcode**:

| Criteria | Boundary values |
| -------- | --------------- |
| ...      | ...             |



**Combination of predicates for method validateBarcode**:


| Validity of the String parameter | Length of the String     | Validity of barcode | Valid / Invalid | Description of the test case | JUnit test case     |
| -------------------------------- | ------------------------ | ------------------- | --------------- | ---------------------------- | ------------------- |
| Valid                            | 12 <= length <= 14       | Yes                 | Valid           | T0(0123456789012) -> true    | testValidBarCode    |
| "                                | "                        | No                  | Invalid         | T1(01234567890123) -> false  | testInvalidBarCode  |
| "                                | length <12 OR length >14 | -                   | Invalid         | T2(012345678) -> false       | testTooShortBarCode |
| NULL                             | -                        | -                   | Invalid         | T3(NULL) ->  error           | testNullBarCode     |



 ### **Class *SaleTransactionRecord* - method *refreshTotalPrice***



**Criteria for method *refreshTotalPrice*:**

- formula validity

**Predicates for method *refreshTotalPrice*:**

| Criteria         | Predicate |
| ---------------- | --------- |
| formula validity | yes       |



**Boundaries for method *refreshTotalPrice***:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |



**Combination of predicates for method *refreshTotalPrice***:


| formula validity | Valid / Invalid | Description of the test case                                 | JUnit test case  |
| ---------------- | --------------- | ------------------------------------------------------------ | ---------------- |
| yes              | valid           | ProductType productType = new ProductType()<br />SaleTransaction saleTransaction = new SaleTransaction()<br />check saleTransaction.getTotalPrice()<br />modify saleTransactions attributes<br />saleTransaction.refreshTotalPrice()<br />check saleTransaction.getTotalPrice() | testValidFormula |




# White Box Unit Tests

### Test cases definition

    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name | JUnit test case |
|-----------|-----------------|
| Class *EZShop* - method validateBarcode | testValidBarCode |
| | testInvalidBarCode |
| | testTooShortBarCode |
| | testNullBarCode |
| | testTooLongBarCode |
| |test12BarCode |
| Class *Order* - setters/getters methods | testSetGetOrderId |
|  | testGetSetBalanceId |
|  | testGetSetProductCode |
|  | testGetSetQuantity |
|  | testGetSetPricePerUnit |
|  | testGetSetStatus |
| Class *BalanceOperation* -  setters/getters methods | testSetGetBalanceId |
|  | testGetType |
|  | testSetType |
|  | testSetGetMoney |
|  | testSetGetDate |

### coverage report

    <Add here the screenshot report of the statement and branch coverage obtained using
    the Eclemma tool. >


### Loop coverage analysis

    <Identify significant loops in the units and reports the test cases
    developed to cover zero, one or multiple iterations >

|Unit name | Loop rows | Number of iterations | JUnit test case |
|---|---|---|---|
|Class EZShop - method validateBarcode|2338|11|test12BarCode|
|"|"|12|testValidBarCode|
|"|"|13|testInvalidBarCode|
|"|"|0|testNullBarCode,<br />testTooShortBarCode,<br />testTooLongBarCode|

