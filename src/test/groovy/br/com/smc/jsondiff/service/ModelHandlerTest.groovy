package br.com.smc.jsondiff.service

import br.com.smc.jsondiff.exception.InvalidDiffIdException
import br.com.smc.jsondiff.model.DiffObject
import br.com.smc.jsondiff.model.JsonPosition
import br.com.smc.jsondiff.repository.DiffRepository
import org.apache.log4j.Logger
import spock.lang.Specification

/**
 * Created by Sidney de Moraes on 27/11/16.
 */
class ModelHandlerTest extends Specification {

	Logger log = Stub()
	DiffRepository repo = Mock()
	ModelHandler handler

	def setup(){
		handler = new ModelHandler(
				log: log,
				repo: repo
		)
	}

	def "processDiffId -> success -> new ObjectDiff"(){
		given:
			def diffId = "1234"
		when:
			def diff = handler.processDiffId(diffId)
		then:
			diff != null
			diff.id == "1234"
			1 * repo.findOne(_) >> null
			1 * repo.saveAndFlush(_) >> new DiffObject(id: diffId)
	}

	def "processDiffId -> success -> existing ObjectDiff"(){
		given:
			def diffId = "1234"
		when:
			def diff = handler.processDiffId(diffId)
		then:
			diff != null
			diff.id == "1234"
			1 * repo.findOne(_) >> new DiffObject(id: diffId)
			0 * repo.saveAndFlush(_)
	}

	def "processDiffId -> fail -> diffId empty"(){
		given:
			def diffId = ""
		when:
			handler.processDiffId(diffId)
		then:
			thrown InvalidDiffIdException
			0 * repo.findOne(_)
			0 * repo.saveAndFlush(_)
	}

	def "processJson -> fail -> null diff"(){
		given:
			def diff = null
			def json = "{}"
			def position = JsonPosition.LEFT
		when:
			handler.processJson(diff, json, position)
		then:
			thrown IllegalArgumentException
			0 * repo.findOne(_)
	}

	def "processJson -> fail -> null json"(){
		given:
			def diff = new DiffObject()
			def json = null
			def position = JsonPosition.LEFT
		when:
			handler.processJson(diff, json, position)
		then:
			thrown IllegalArgumentException
			0 * repo.findOne(_)
	}

	def "processJson -> fail -> null position"(){
		given:
			def diff = new DiffObject()
			def json = "{}"
			def position = null
		when:
			handler.processJson(diff, json, position)
		then:
			thrown IllegalArgumentException
			0 * repo.findOne(_)
	}

	def "processJson -> fail -> non existing diff id"(){
		given:
			def diff = new DiffObject(id: "123")
			def json = "{}"
			def position = JsonPosition.LEFT
		when:
			handler.processJson(diff, json, position)
		then:
			thrown NoSuchElementException
			1 * repo.findOne(_ as String) >> null
			0 * repo.saveAndFlush(_)
	}

	def "processJson -> success -> save new left json"(){
		given:
			def diff = new DiffObject(
					id: "123"
			)
			def json =  """
						{"teste" : "bla", "nivel1" : { "nivel2" : "blabla"}}
						"""
			def position = JsonPosition.LEFT
			def resultingDiff = diff
			resultingDiff.jsonLeft = json
		when:
			def result = handler.processJson(diff, json, position)
		then:
			result == resultingDiff
			1 * repo.findOne(_) >> diff
			1 * repo.saveAndFlush(_) >> resultingDiff
	}
}
