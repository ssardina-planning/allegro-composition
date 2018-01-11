# ALLEGRO Composition System

ALLEGRO is a system for solving behavior composition problems.  A behavior composition problem involves realising a virtual target behavior by coordinating the available behaviors. 

ALLEGRO includes a simulation-based regression technique with its two optimisations and a search-based progression technique. The simulation-based technique and its optimisations have been implemented by the authors of ALLEGRO. The simulation-based technique for non-deterministic composition problems was developed here:
* Giuseppe De Giacomo and Sebastian Sardina. [Automatic synthesis of new behaviors from a library of available behaviors](https://www.ijcai.org/Proceedings/07/Papers/301.pdf). In Manuela M. Veloso, editor, Proceedings of the International Joint Conference on Artificial Intelligence (IJCAI), pages 1866-1871, Hyderabad, India, January 2007.
* Giuseppe De Giacomo, Fabio Patrizi, and Sebastian Sardina. [Automatic behavior composition synthesis](http://dx.doi.org/10.1016/j.artint.2012.12.001). Artificial Intelligence Journal, 196:106-142, 2013. (author free copy [here](https://sites.google.com/site/ssardina/publications/aij13-dgps-autocomp.pdf?attredirects=0)

The search-based technique has been implemented by Thomas Stroder and Maurice Pagnucco as a proof-of-concept for their IJCAI'09 article:
* Thomas Ströder, Maurice Pagnucco. [Realising Deterministic Behavior from Multiple Non-Deterministic Behaviors](http://www.ijcai.org/Proceedings/09/Papers/159.pdf). IJCAI 2009: 936-941

This readme outlines the basic steps to run the Allegro system. The distribution contains both the binary and the source code. An example has been included, in the examples folder, which can serve as a sample to create new problems.

## Table of Contents

1. Compiling and Running Allegro
    1. How to run the pre-compiled binary version.
    2. How to compile Allegro from the source.
2. Creating a new behavior composition problem.
    2. How to define behaviors in a text file.
    2. How to define a composition problem using xml.

## 1. Compiling and Running Allegro

Allegro has been purely written in Java. No third party libraries are required to run the core system. The system also contains the [JAPEX](http://www.japex.co.jp/english/) benchmarking drivers, for which the libraries have been included. This readme does not detail into JAPEX. However, sample XML files are present in the root directory for anyone who wishes to explore more.

### 1.1 How to run the pre-compiled binary version.

The pre-compiled jar package is present in the root directory by the name of `Allegro.jar`. In order to run allegro one needs to pass the behavior composition problem defined as a XML file (see Section 2.2). A sample problem definition is present in the `examples/KR` folder. The solution to the problem can either be redirected to the console or saved in a file. The arguments are passed to the system using the following syntax:

    ```
    java -jar allegro.jar --i=example/KR/KR1.xml --o=sample.txt
    ```

where `--i` denotes the input XML file and `--o` denotes the output file (if  omitted the result is shown in the console).

### 1.2 How to compile Allegro from the source.

All the source code is present in the `src` directory. In order to compile ALLEGRO one just needs to compile the `Main.java` file, i.e., 

    ```
    javac Main.java
    ```
    
To run it from the compiled sources (for example after compilation inside IntelliJ):

    ```
    java -cp out/production/allegro-composer.git/ Main --i=example/KR/KR1.xml
    ```

## 2. Creating a new behavior composition problem.

The components of the problem are defined in text files using a notation similar to [**DOT**](https://en.wikipedia.org/wiki/DOT_(graph_description_language)). All the components are then bound together using an XML file.

### 2.1 How to define behaviors in a text file.

Both the environment and the behaviors use a similar notion except the following differences:

1. Environment does not have final states.
2. Transitions in behaviors have guards.

The following example shows an available behavior. The legal argument is used to define guards. The wildcard (*) signifies no guards, or the transition is legal in with respect to all environment states.

```
digraph TS_A {
a1 -> a1[label ="dispose"][legal={*}]
a1 -> a1[label ="recharge"][legal={*}]
a1 -> a2 [label="clean"][legal={e1,e2}]
a2 -> a2 [label = "recharge"][legal={*}]
a2 -> a1 [label = "dispose"][legal={*}]
[initial = {a1}]
[final = {a1}]
}
```

Similarly, the target and the environment are also defined in different `txt` files. Please view the example provided with the package for more details.

### 2.2 How to define a composition problem using XML.

After defining the components in text files the problem definition is created
in a XML file as :

```
<?xml version="1.0" encoding="UTF-8"?>
<tests>
<test>
	<environment>environment.txt</environment>
	<behaviours>
		<behaviour times="1">armA.txt</behaviour>
		<behaviour times="1">armB.txt</behaviour>
		<behaviour times="1">armC.txt</behaviour>
	</behaviours>
	<target>target.txt</target>
</test>
</tests>
```

this XML file is then given as an input to the system.


## Who to contact?

* **Author:** Nitin Yadav (as part of his Maser Thesis - 9/11/2009)
* **Supervisor:** Sebastian Sardina


This project is using the GPLv3 for open source licensing for information and the license visit GNU website (https://www.gnu.org/licenses/gpl-3.0.en.html).

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.