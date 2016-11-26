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

		return "Home Page"
	}
}
