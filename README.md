# Black Rook Gloop-OpenAL
Or... (LightWeight Java) **G**ame **L**ibrary **O**bject-**O**riented **P**aradigm for **OpenAL**

Copyright (c) 2020 Black Rook Software.  
[https://github.com/BlackRookSoftware/Gloop-OpenAL](https://github.com/BlackRookSoftware/Gloop-OpenAL)

[Latest Release](https://github.com/BlackRookSoftware/Gloop-OpenAL/releases/latest)

### Required Libraries

[LightWeight Java Game Library (LWJGL)](https://www.lwjgl.org/download) 3.0.0+  
[LWJGL-OpenAL](https://www.lwjgl.org/download) 3.0.0+


### Required Java Modules

[java.desktop](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/module-summary.html)  
* [java.xml](https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/module-summary.html)  
* [java.datatransfer](https://docs.oracle.com/en/java/javase/11/docs/api/java.datatransfer/module-summary.html)  
* [java.base](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/module-summary.html)  


### Introduction

This library contains classes for encapsulating LWJGL's OpenAL bindings.


### Why?

This library is those that intensely dislike the bare-metal bindings of LWJGL and would prefer something
that jives with Java's Object-Oriented Paradigm.


### Library

Contained in this release is a series of classes that are used for driving LWJGL's OpenAL bindings.

The javadocs contain basic outlines of each package's contents.


### Compiling with Ant

To download dependencies for this project, type (`build.properties` will also be altered/created):

	ant dependencies

To compile this library with Apache Ant, type:

	ant compile

To make Maven-compatible JARs of this library (placed in the *build/jar* directory), type:

	ant jar

To make Javadocs (placed in the *build/docs* directory):

	ant javadoc

To compile main and test code and run tests (if any):

	ant test

To make Zip archives of everything (main src/resources, bin, javadocs, placed in the *build/zip* directory):

	ant zip

To compile, JAR, test, and Zip up everything:

	ant release

To clean up everything:

	ant clean
	
### Other

This program and the accompanying materials are made available under the 
terms of the LGPL v2.1 License which accompanies this distribution.

A copy of the LGPL v2.1 License should have been included in this release (LICENSE.txt).
If it was not, please contact us for a copy, or to notify us of a distribution
that has not included it. 

This contains code copied from Black Rook Base, under the terms of the MIT License (docs/LICENSE-BlackRookBase.txt).
