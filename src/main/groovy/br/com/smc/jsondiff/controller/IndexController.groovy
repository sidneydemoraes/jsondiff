package br.com.smc.jsondiff.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller responsible for requests directed to Home page ('/').
 */
@RestController
class IndexController {

	@RequestMapping(value = "/")
	public String index() {

		return """
		Diff Service
		============

		Usage
		-----
		You need to provide two JSONs in order to evaluate their differences.
		If you do not provide both, you will be sent back to this page with a
		BAD_REQUEST Status.


		Sending your First JSON
		-----------------------
		You need to make a POST request with the following data:
			- URL
				http://localhost:8080/v1/diff/{YOUR_DIFF_ID}/left
		 	- Headers
				"Content-Type" = "application/json"
			_ Body
				A Valid JSON.

		{YOUR_DIFF_ID} means that you need to pass an alphanumeric identification to the service.
		This ID will be used to send your second JSON and to get the differences between them.
		Without that information you will get a NOT_FOUND status.


		Sending your Second JSON
		------------------------
		It works pretty much the same way. The only slight difference is on the URL:
			- URL
				http://localhost:8080/v1/diff/{YOUR_DIFF_ID}/right
			- Headers
				"Content-Type" = "application/json"
			_ Body
				A Valid JSON.

		Remember to use the same {YOUR_DIFF_ID} or else you will create a hole different comparison
		context. You will not lose your previous one, but will not be able to compare JSONs sent to
		different IDs.


		Getting the Diffs Between Both JSONs
		------------------------------------
		This time you can make POST or GET requests to the following URL:
			http://localhost:8080/v1/diff/{YOUR_DIFF_ID}

		The response body will be another JSON with the conclusion about the comparison.

		******
		* PS *
		******
		If you don't follow the above rules, you will be sent back to this page so you have the
		opportunity to read them again and follow them correctly.


		Enjoy!
		"""
	}
}
