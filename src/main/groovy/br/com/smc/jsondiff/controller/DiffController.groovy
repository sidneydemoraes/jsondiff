package br.com.smc.jsondiff.controller

import br.com.smc.jsondiff.model.JsonPosition
import br.com.smc.jsondiff.model.JsonPositionBinder
import br.com.smc.jsondiff.service.ModelHandler
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.Pattern

/**
 * Controller responsible for requests directed to the Diff function.
 */
@Controller
@RequestMapping(value = "/v1/diff")
@Validated
class DiffController {

	/**
	 * Method that receives a JSON for storage for a diff identified by diffId.
	 *
	 * @param diffId - alphanumeric data limited to 16 digits.
	 * @param position - limited to left or right.
	 * @param jsonString - any JSON.
	 * @return
	 */
	@RequestMapping(value = "/{diffId}/{position}",
			method = RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	@ResponseBody
	public String receiveJsonForDiff(
			@Valid @Pattern(regexp = "[\\w\\d]{1,16}") @PathVariable String diffId,
			@PathVariable JsonPosition position,
			@RequestBody String jsonString) {

		log.info("New ${position.name()} Json sent for id [${diffId}].")
		log.debug(jsonString)

		def diffObject = handler.processDiffId(diffId)
		handler.processJson(diffObject, jsonString, position)

		return "${position.name()} Json stored successfully for id ${diffId}"
	}

	/**
	 * Binder responsible for allowing the enum {@link JsonPosition} to be used for validation of
	 * path parameters.
	 *
	 * @param binder
	 */
	@InitBinder
	void initBinder(final WebDataBinder binder) {
		binder.registerCustomEditor(JsonPosition.class, new JsonPositionBinder());
	}

	/**
	 * Handler responsible for errors raised when validating path variables from this controller.
	 *
	 * @param ex
	 * @param response
	 * @return
	 */
	@ExceptionHandler(value = ConstraintViolationException.class)
	public ModelAndView handleValidationException() {
		log.info("Detected attempt to enter invalid character on URL.")
		ModelAndView mv = new ModelAndView()
		mv.setStatus(HttpStatus.BAD_REQUEST)
		mv.setViewName("forward:/")
		return mv
	}

	/**
	 * Handler responsible for errors raised due to invalid diffId that may pass through.
	 *
	 * @param ex
	 * @param response
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public ModelAndView handleInvalidDiffIdException(Exception e) {
		ModelAndView mv = new ModelAndView()
		mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
		mv.setViewName("forward:/")
		mv.addObject("errorMessage",
				"Something that should not have happened has happened! ${e.message}")
		return mv
	}


	/* Properties */
	Logger log = Logger.getLogger(DiffController.class)
	@Autowired
	ModelHandler handler
}
