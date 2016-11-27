package br.com.smc.jsondiff.service

import br.com.smc.jsondiff.exception.InvalidDiffIdException
import br.com.smc.jsondiff.model.DiffObject
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
			1 * repo.save(_)
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
			0 * repo.save(_)
	}

	def "processDiffId -> fail -> diffId empty"(){
		given:
			def diffId = ""
		when:
			handler.processDiffId(diffId)
		then:
			thrown InvalidDiffIdException
			0 * repo.findOne(_)
			0 * repo.save(_)
	}
}
