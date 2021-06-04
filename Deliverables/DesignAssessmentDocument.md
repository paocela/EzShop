# Design assessment


```
<The goal of this document is to analyse the structure of your project, compare it with the design delivered
on April 30, discuss whether the design could be improved>
```

# Levelized structure map
```
<Applying Structure 101 to your project, version to be delivered on june 4, produce the Levelized structure map,
with all elements explosed, all dependencies, NO tangles; and report it here as a picture>
```
![levelized_structured_map](WBimages/levelized_structured_map.png?raw=true "levelized_structured_map")
# Structural over complexity chart
```
<Applying Structure 101 to your project, version to be delivered on june 4, produce the structural over complexity chart; and report it here as a picture>
```
![structural_over_complexity](WBimages/structural_over_complexity.png?raw=true "structural_over_complexity")


# Size metrics

```
<Report here the metrics about the size of your project, collected using Structure 101>
```



| Metric                                    | Measure |
| ----------------------------------------- | ------- |
| Packages                                  |     6   |
| Classes (outer)                           |    41   |
| Classes (all)                             |    46   |
| NI (number of bytecode instructions)      |   6,104 |
| LOC (non comment non blank lines of code) |  ~ 2,625|



# Items with XS

```
<Report here information about code tangles and fat packages>
```

| Item | Tangled | Fat  | Size | XS   |
| ---- | ------- | ---- | ---- | ---- |
|ezshop.it.polito.ezshop.data.EZShop|  	    |214 |4495 |1974|
|ezshop.it.polito.ezshop            |1,54%	| 4  |6104 | 94 |
|ezshop.it.polito.ezshop.data.EZShop.returnProduct(java.lang.Integer, java.lang.String, int):boolean	 |       | 17  |  182   | 21   |


# Package level tangles

```
<Report screen captures of the package-level tangles by opening the items in the "composition perspective" 
(double click on the tangle from the Views->Complexity page)>
```
![ezshop](WBimages/ezshop.png?raw=true "ezshop")
![data](WBimages/data.png?raw=true "data")
![model](WBimages/model.png?raw=true "model")

# Summary analysis
```
<Discuss here main differences of the current structure of your project vs the design delivered on April 30>
<Discuss if the current structure shows weaknesses that should be fixed>
```
