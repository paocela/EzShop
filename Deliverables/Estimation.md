1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
# Project Estimation  
Authors: Francesco Policastro, Paolo Celada, Luca Pezzolla, Teodoro Corbo

Date: 24/04/2021

Version: 1.0

# Contents
- [Estimate by product decomposition]
- [Estimate by activity decomposition ]
# Estimation approach
<Consider the EZGas  project as described in YOUR requirement document, assume that you are going to develop the project INDEPENDENT of the deadlines of the course>
# Estimate by product decomposition
### 
|             | Estimate                        |             
| ----------- | ------------------------------- |  
| NC =  Estimated number of classes to be developed   |              13               |             
|  A = Estimated average size per class, in LOC       |                  350          | 
| S = Estimated size of project, in LOC (= NC * A) | 4500 |
| E = Estimated effort, in person hours (here use productivity 10 LOC per person hour)  |                450                      |   
| C = Estimated cost, in euro (here use 1 person hour cost = 30 euro) | 13500 | 
| Estimated calendar time, in calendar weeks (Assume team of 4 people, 8 hours per day, 5 days per week ) |         3           |               

<br />
<br />

# Estimate by activity decomposition
### 
|         Activity name    | Estimated effort (person hours)   |             
| ----------- | ------------------------------- | 
| Requirement document| 66 |
| GUI prototype | 30 |
|Design| 32 |
|Code + unit test| 256 |
|Integration+GUI test| 64 |
###
<br />
<br />

```plantuml
scale 1.5
Project starts 2021-05-01
[Requirement document + GUI prototype] starts 2021-05-01
[Requirement document + GUI prototype] ends 2021-05-5
[Design] starts at [Requirement document + GUI prototype]'s end 
[Design] ends 2021-05-6
[Code + unit test] starts at [Design]'s end 
[Code + unit test] ends 2021-05-16
[Integr+GUI test] starts at [Code + unit test]'s end
[Integr+GUI test] ends 2021-05-18
```