package br.com.smc.jsondiff.controller

import br.com.smc.jsondiff.model.DiffObject
import br.com.smc.jsondiff.model.JsonPosition
import br.com.smc.jsondiff.service.ModelHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.log4j.Logger
import spock.lang.Specification

/**
 * Unit Testing for @{link DiffController}
 */
class DiffControllerTest extends Specification {

	DiffController controller
	Logger log
	ModelHandler handler

	def setup() {
		log = Stub()
		handler = Mock()
		controller = new DiffController(
			log: log,
			handler: handler
		)
	}

	def "receiveJsonForDiff -> success"() {
		given:
			def diffId = "1234"
			def json = ""
			controller.mapper = Spy(ObjectMapper.class)
		when:
			def result = controller.receiveJsonForDiff(diffId, JsonPosition.LEFT, json)
		then:
			result != null
			1 * handler.processDiffId(_) >> new DiffObject(id: diffId)
			1 * handler.processJson(_ as DiffObject, _ as String, _ as JsonPosition)
	}
}
