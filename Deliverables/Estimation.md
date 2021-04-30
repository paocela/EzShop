# Project Estimation  
Authors: Francesco Policastro, Paolo Celada, Luca Pezzolla, Teodoro Corbo

Date: 24/04/2021

Version: 1.0

# Contents
- [Estimate by product decomposition]
- [Estimate by activity decomposition ]
# Estimation approach

# Estimate by product decomposition
### 
|             | Estimate                        |             
| ----------- | ------------------------------- |  
| NC =  Estimated number of classes to be developed   |              13               |             
|  A = Estimated average size per class, in LOC       |                  290         | 
| S = Estimated size of project, in LOC (= NC * A) | 3770 |
| E = Estimated effort, in person hours (here use productivity 10 LOC per person hour)  |                370                      |   
| C = Estimated cost, in euro (here use 1 person hour cost = 30 euro) | 11100 | 
| Estimated calendar time, in calendar weeks (Assume team of 4 people, 8 hours per day, 5 days per week ) |         2.3           |               

<br />
<br />

# Estimate by activity decomposition
### 
|         Activity name    | Estimated effort (person hours)   |             
| ----------- | ------------------------------- | 
| Requirement| 66 |
| GUI prototype | 30 |
|Design| 32 |
|Code implementation| 250 |
|Unit test| 40 |
|Integration test| 30 |
|GUI test| 34 |
|Total| 455 |
###
<br />
<br />


```plantuml
saturday are closed
sunday are closed
Project starts 2021-04-01
[Requirement] lasts 6 days
[GUI prototype] starts at [Requirement]'s end 
[GUI prototype] lasts 3 days

[Design] starts at [GUI prototype]'s end 
[Design] lasts 3 day
[Code implementation] starts at [Design]'s end 
[Code implementation] lasts 9 days
[Unit test] starts at [Code implementation]'s end 
[Unit test] lasts 3 days

[Integration test] starts at [Unit test]'s end
[Integration test] lasts 3 days
[GUI test] starts at [Unit test]'s end
[GUI test] lasts 3 days
```

Some activities in the Gantt diagram are not parallelizable so the total number of days in person hour is greater than estimated effort shown above