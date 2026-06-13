# Black Rook Gloop-OpenAL
Or... (LightWeight Java) **G**ame **L**ibrary **O**bject-**O**riented **P**aradigm for **OpenAL**

Copyright (c) 2020-2026 Black Rook Software.  
[https://github.com/BlackRookSoftware/Gloop-OpenAL](https://github.com/BlackRookSoftware/Gloop-OpenAL)

[Latest Release](https://github.com/BlackRookSoftware/Gloop-OpenAL/releases/latest)


### NOTICE

This library is currently in **EXPERIMENTAL** status. This library's API
may change many times in different ways over the course of its development!


### Required Libraries

[LightWeight Java Game Library (LWJGL)](https://www.lwjgl.org/download) 3.0.0+  
[LWJGL-OpenAL](https://www.lwjgl.org/download) 3.0.0+


### Required Java Modules

[java.desktop](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/module-summary.html)  
* [java.xml](https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/module-summary.html)  
* [java.datatransfer](https://docs.oracle.com/en/java/javase/11/docs/api/java.datatransfer/module-summary.html)  
* [java.base](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/module-summary.html)  


### Where to Get

* [Maven Central](https://central.sonatype.com/artifact/com.blackrooksoftware/gloop-openal)  
* [GitHub Releases](https://github.com/BlackRookSoftware/Gloop-OpenAL/releases/latest)


### Introduction

This library contains classes for encapsulating LWJGL's OpenAL bindings. Manages multicontext seamlessly and in
an intuitive way.


### Why?

This library is for those that intensely dislike the bare-metal bindings of LWJGL and would prefer something
that jives with Java's Object-Oriented Paradigm.


### Library

Contained in this release is a series of classes that are used for driving LWJGL's OpenAL bindings.

The javadocs contain basic outlines of each package's contents.


### TODO...

* Audio capture via ALCCapture devices.


### Compiling with Maven

To install/compile this library and make all artifacts with Apache Maven, type:

	mvn install

To compile this library, type:

	mvn compile

To make Maven-compatible JARs of this library, type:

	mvn jar:jar

To make Javadocs:

	mvn javadoc:javadoc

To run tests, type:

	mvn test

To generate a coverage report, type:

	mvn test jacoco:report

To clean up everything:

	mvn clean


### Other

This program and the accompanying materials are made available under the 
terms of the LGPL v2.1 License which accompanies this distribution.

A copy of the LGPL v2.1 License should have been included in this release (LICENSE.txt).
If it was not, please contact us for a copy, or to notify us of a distribution
that has not included it. 

This contains code copied from Black Rook Base, under the terms of the MIT License (docs/LICENSE-BlackRookBase.txt).
