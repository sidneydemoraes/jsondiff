# JSON-DIFF

## Overview

This is an educational project for an assignment in a recruitment process.
What it does is to receive and compare two JSONs and their contents.


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


