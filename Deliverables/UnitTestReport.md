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

- hashPassword
- byteToHex
- validateCreditCard
- validateBarcode
- refreshTotalPrice

 ### **Class *EZShop* - method *hashPassword***



**Criteria for method *hashPassword*:**
	

 - 
 - 





**Predicates for method *hashPassword*:**

| Criteria | Predicate |
| -------- | --------- |
|          |           |
|          |           |
|          |           |
|          |           |





**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |



**Combination of predicates**:


| Criteria 1 | Criteria 2 | ... | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|-------|-------|
|||||||
|||||||
|||||||
|||||||
|||||||

 ### **Class *EZShop* - method *byteToHex***



**Criteria for method *byteToHex*:**
	

 - 
 - 





**Predicates for method *byteToHex*:**

| Criteria | Predicate |
| -------- | --------- |
|          |           |
|          |           |
|          |           |
|          |           |





**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |



**Combination of predicates**:


| Criteria 1 | Criteria 2 | ...  | Valid / Invalid | Description of the test case | JUnit test case |
| ---------- | ---------- | ---- | --------------- | ---------------------------- | --------------- |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |



### **Class *EZShop* - method *validateCreditCard***

**Criteria for method $validateCreditCard$:**
	

- Credit card number validity

  

**Predicates for method validateCreditCard:**

| Criterion                   | Predicate |
| --------------------------- | --------- |
| Credit card number validity | yes       |
|                             | no        |



**Boundaries for method validateCreditCard**:

| Criterion | Boundary values |
| --------- | --------------- |
| ...       | ...             |



 **Combination of predicates for method validateCreditCard**

| Credit card number validity | Valid/Invalid | Description of the test case | JUnit test case |
| --------------------------- | ------------- | ---------------------------- | --------------- |
| yes                         | valid         | T1(12345674, "true")<br />   |                 |
| no                          | invalid       | T2(13245674, "false")        |                 |

 ### **Class *EZShop* - method *validateBarcode***



**Criteria for method *validateBarcode*:**
	

 - 
 - 





**Predicates for method *validateBarcode*:**

| Criteria | Predicate |
| -------- | --------- |
|          |           |
|          |           |
|          |           |
|          |           |





**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |



**Combination of predicates**:


| Criteria 1 | Criteria 2 | ...  | Valid / Invalid | Description of the test case | JUnit test case |
| ---------- | ---------- | ---- | --------------- | ---------------------------- | --------------- |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |

 ### **Class *SaleTransactionRecord* - method *refreshTotalPrice***



**Criteria for method *refreshTotalPrice*:**
	

 - 
 - 





**Predicates for method *refreshTotalPrice*:**

| Criteria | Predicate |
| -------- | --------- |
|          |           |
|          |           |
|          |           |
|          |           |





**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |
|          |                 |
|          |                 |



**Combination of predicates**:


| Criteria 1 | Criteria 2 | ...  | Valid / Invalid | Description of the test case | JUnit test case |
| ---------- | ---------- | ---- | --------------- | ---------------------------- | --------------- |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |
|            |            |      |                 |                              |                 |




# White Box Unit Tests

### Test cases definition

    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name | JUnit test case |
|--|--|
|||
|||
||||

### Code coverage report

    <Add here the screenshot report of the statement and branch coverage obtained using
    the Eclemma tool. >


### Loop coverage analysis

    <Identify significant loops in the units and reports the test cases
    developed to cover zero, one or multiple iterations >

|Unit name | Loop rows | Number of iterations | JUnit test case |
|---|---|---|---|
|||||
|||||
||||||



