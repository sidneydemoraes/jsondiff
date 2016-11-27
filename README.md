# JSON-DIFF

## Overview

This is an educational project for an assignment in a recruitment process.
What it does is to receive and compare two JSONs and their contents.


## Technical Decisions

I made some choices for this project and would like to explain them.

#### Spring Boot

I usually say Spring Boot is how Java should be. It makes developer's life really easy in both setup and development. 
First of all, no XMLs. The worst thing in Spring Framework was finally exterminated by Spring itself with Spring Boot. It lets you use all of Spring's potential with very few configuration, usually with `application.properties` or some annotated classes/methods.

#### Groovy

I just LOVE Groovy. I confess I didn't play with Java 8 and its Lambda yet, but Groovy makes many things easier during development.

#### Gradle

I use Gradle for dependency management and build tool due to its incredible power. Having Groovy behind the curtains enables Gradle to do almost anything from a build file.

#### Spock

Since the day I played with Spock for the first time, I just can't imagine tests without it. The simplicity to work with Mocks, Stubs and Spies, as well as the semantic way to write tests got me.

#### H2 In Memory Database

As this is an educational project, I decided to use an embedded in memory database. However, Spring Boot and Gradle also allow us to easily change that by creating new profiles and applying them to a different database. Let Hibernate do the rest.

#### Java-Object-Diff

[Java-Object-Diff](https://github.com/SQiShER/java-object-diff)) is an API created by Daniel Bechler to perform Diffs on generic type objects, which means that any object type can be passed to it, as long as the other object is from the same hierarchy type. He did a pretty good job over months and trying to reinvent the wheel would take too much from my time (and it wasn't much already).


## Setup

There are two ways to download this application:
- You can just download the file `jsondiff-0.0.1-SNAPSHOT.jar`.
- You can clone this GIT repository with the command `git clone https://github.com/sidneydemoraes/jsondiff.git`.


## Usage

### Downloaded JAR File

In order to run the JAR file you downloaded, just run the following command:
   `java -jar jsondiff-0.0.1-SNAPSHOT.jar`. 
It will automagically prepare a Tomcat server, deploy the application and run it.
It will be accessible through the URL `http://localhost:8080/`.

### Cloned GIT Repository

In order to run the application through the cloned repository, you need to run the following command from the project root directory:
   `. gradlew bootRun`
The project will be handled the same way as the downloaded JAR file. a Tomcat server will be setup and the application will be deployed automagically.


### How to Use After It Is Deployed and Running

You need to provide two JSONs in order to evaluate their differences.
If you do not provide both, you will be sent back to this page with a BAD_REQUEST Status.


#### Sending your First JSON

You need to make a POST request with the following data:
	- URL
		`http://localhost:8080/v1/diff/{YOUR_DIFF_ID}/left`
	- Headers
		"Content-Type" = "application/json"
	_ Body
		A valid plain JSON or Base64 encoded JSON.

{YOUR_DIFF_ID} means that you need to pass an alphanumeric identification to the service.
This ID will be used to send your second JSON and to get the differences between them.
Without that information you will get a NOT_FOUND status.

**IMPORTANT**: Notice that your JSON can be Base64 encoded or plain JSON.


#### Sending your Second JSON

It works pretty much the same way. The only slight difference is on the URL:
	- URL
		`http://localhost:8080/v1/diff/{YOUR_DIFF_ID}/right`
	- Headers
		"Content-Type" = "application/json"
	_ Body
		A valid plain JSON or Base64 encoded JSON.

Remember to use the same {YOUR_DIFF_ID} or else you will create a hole different comparison
context. You will not lose your previous one, but will not be able to compare JSONs sent to
different IDs.


#### Getting the Diffs Between Both JSONs

This time you can make POST or GET requests to the following URL:
	`http://localhost:8080/v1/diff/{YOUR_DIFF_ID}`

The response body will be another JSON with the conclusion about the comparison.

**Important**: If you don't follow the above rules, you will be sent back to home page so you have the
opportunity to read them again and follow them correctly.


Enjoy!


