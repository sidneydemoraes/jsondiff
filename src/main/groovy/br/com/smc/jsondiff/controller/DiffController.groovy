package br.com.smc.jsondiff.controller

import br.com.smc.jsondiff.model.JsonPosition
import br.com.smc.jsondiff.model.JsonPositionBinder
import br.com.smc.jsondiff.service.ModelHandler
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*

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
	@RequestMapping(value = "/{diffId}/{position}",
			method = RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public String receiveJsonForDiff(
			@Valid @Pattern(regexp = "[\\w\\d]{1,16}") @PathVariable String diffId,
			@PathVariable JsonPosition position,
			@RequestBody String jsonString) {

		log.debug("New ${position.name()} Json sent for id [${diffId}].")
		log.debug(jsonString)

		handler.processDiffId(diffId)
		handler.processJson(jsonString, position)

		return "${position.name()} Json stored successfully for id ${diffId}"
	}

	/**
	 * Binder responsible for allowing the enum @{JsonPosition} to be used for validation of
	 * path parameters.
	 *
	 * @param binder
	 */
	@InitBinder
	void initBinder(final WebDataBinder binder) {
		binder.registerCustomEditor(JsonPosition.class, new JsonPositionBinder());
	}


	/* Properties */
	Logger log = Logger.getLogger(DiffController.class)
	@Autowired
	ModelHandler handler
}
