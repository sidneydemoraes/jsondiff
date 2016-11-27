package br.com.smc.jsondiff.controller

import br.com.smc.jsondiff.exception.InvalidJsonPositionException
import br.com.smc.jsondiff.model.DiffResult
import br.com.smc.jsondiff.model.JsonPosition
import br.com.smc.jsondiff.model.JsonPositionBinder
import br.com.smc.jsondiff.service.DiffExecutor
import br.com.smc.jsondiff.service.ModelHandler
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
@RestController
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

	public static final String DIFF_ID_PATTERN = "[\\w\\d]{1,16}"

	@RequestMapping(value = "/{diffId}/{position}",
			method = RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public String receiveJsonForDiff(
			@Valid @Pattern(regexp = DiffController.DIFF_ID_PATTERN) @PathVariable String diffId,
			@PathVariable JsonPosition position,
			@RequestBody String json) {

		log.info("New ${position.name()} Json sent for id [${diffId}].")
		log.debug(json)
		log.debug("Removing tabs from json.")
		def transformedJson = json.replaceAll(/[\t]+/, '')
		log.debug("Transformed JSON is ${transformedJson}")

		def diffObject = handler.processDiffId(diffId)
		handler.processJson(diffObject, transformedJson, position)

		return "{\"sucess\": \"${position.name()} Json stored successfully for id ${diffId}\"}"
	}


	@RequestMapping(value = "/{diffId}",
			method = [RequestMethod.POST, RequestMethod.GET],
			produces = "application/json")
	public DiffResult performDiff(
			@Valid @Pattern(regexp = DiffController.DIFF_ID_PATTERN)
			@PathVariable String diffId) {

		log.info("Request received to generate diff for id ${diffId}")

		def diffResult = executor.executeDiffForDiffId(diffId)

		return diffResult
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
		return generateBadRequestModelAndView()
	}

	/**
	 * Handler responsible for errors raised when validating {@link JsonPosition} in this
	 * controller.
	 *
	 * @return
	 */
	@ExceptionHandler(value = InvalidJsonPositionException.class)
	public ModelAndView handleInvalidJsonPosition() {
		log.debug("Invalid JsonPosition attempt detected.")
		return generateBadRequestModelAndView()
	}

	/**
	 * Handler responsible for errors raised when user tried to execute diff without
	 * storing JSONs first.
	 *
	 * @return
	 */
	@ExceptionHandler(value = NoSuchElementException.class)
	public ModelAndView handleNoSuchElement() {
		log.debug("Diff execution attempt with no Json stored.")
		return generateBadRequestModelAndView()
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
		def message = "Something that should not have happened has happened! ${e.message}"
		log.warn(message)
		mv.addObject("errorMessage", message)
		return mv
	}



	private ModelAndView generateBadRequestModelAndView() {
		ModelAndView mv = new ModelAndView()
		mv.setStatus(HttpStatus.BAD_REQUEST)
		mv.setViewName("forward:/")
		return mv
	}


	/* Properties */
	Logger log = Logger.getLogger(DiffController.class)
	@Autowired
	ModelHandler handler
	@Autowired
	DiffExecutor executor
}
