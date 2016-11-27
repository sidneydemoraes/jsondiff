package br.com.smc.jsondiff.service

import br.com.smc.jsondiff.model.DiffObject
import br.com.smc.jsondiff.model.DiffResult
import br.com.smc.jsondiff.repository.DiffRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.log4j.Logger
import spock.lang.Specification

/**
 * Created by Sidney de Moraes on 27/11/16.
 */
class DiffExecutorTest extends Specification {

	DiffExecutor executor
	DiffRepository repo
	Logger log = Stub()


	def setup() {
		repo = Mock()
		executor = Spy()
		executor.repo = repo
		executor.log = log
		executor.mapper = new ObjectMapper()
	}

	def "executeDiffForDiffId -> fail -> null diffId"() {
		given:
			def diffId = null
		when:
			executor.executeDiffForDiffId(diffId)
		then:
			thrown IllegalArgumentException
			0 * repo.findOne(_)
	}

	def "executeDiffForDiffId -> fail -> diffId not on database"() {
		given:
			def diffId = "NotFound"
		when:
			executor.executeDiffForDiffId(diffId)
		then:
			thrown NoSuchElementException
			1 * repo.findOne(_) >> null
	}

	def "executeDiffForDiffId -> fail -> No JSONs found"() {
		given:
			def diffId = "NoJsons"
			def jsonLeft = ""
			def jsonRight = ""
			def fullDiff = new DiffObject(id: diffId, jsonLeft: jsonLeft, jsonRight: jsonRight)
			def expectedConclusion = DiffExecutor.CONCLUSION_NO_JSONS_PROVIDED
		when:
			def result = executor.executeDiffForDiffId(diffId)
		then:
			result.conclusion == expectedConclusion
			1 * repo.findOne(_) >> fullDiff
	}

	def "executeDiffForDiffId -> fail -> Only left JSON found."() {
		given:
			def diffId = "OnlyLeftJson"
			def jsonLeft = """{"valid":"true"}"""
			def jsonRight = ""
			def fullDiff = new DiffObject(id: diffId, jsonLeft: jsonLeft, jsonRight: jsonRight)
			def expectedConclusion = DiffExecutor.CONCLUSION_ONLY_LEFT_JSON_FOUND
		when:
			def result = executor.executeDiffForDiffId(diffId)
		then:
			result.conclusion == expectedConclusion
			1 * repo.findOne(_) >> fullDiff
	}

	def "executeDiffForDiffId -> fail -> Only right JSON found"() {
		given:
		def diffId = "OnlyRightJson"
		def jsonLeft = ""
		def jsonRight = """{"valid":"true"}"""
		def fullDiff = new DiffObject(id: diffId, jsonLeft: jsonLeft, jsonRight: jsonRight)
		def expectedConclusion = DiffExecutor.CONCLUSION_ONLY_RIGHT_JSON_FOUND
		when:
		def result = executor.executeDiffForDiffId(diffId)
		then:
		result.conclusion == expectedConclusion
		1 * repo.findOne(_) >> fullDiff
	}

	def "executeDiffForDiffId -> success"() {
		given:
			def diffId = "Found"
			def fullDiff = new DiffObject(
					id: diffId,
					jsonLeft: """{"name":"KevinC", "accomplishment":"Nice Challenge!"}""",
					jsonRight: """{"name":"Jeroen", "accomplishment":"Great Challenge"}"""
			)
		when:
			def result = executor.executeDiffForDiffId(diffId)
		then:
			result != null
			1 * repo.findOne(_) >> fullDiff
			1 * executor.executeDiffForJsons(_ as String,_ as String) >> new DiffResult(
					conclusion: "Valid Result"
			)
	}

	def "executeDiffForJsons -> success -> JSONs are equal"() {
		given:
			def jsonLeft = """{"key":"value"}"""
			def jsonRight = """{"key":"value"}"""
			def expectedConclusion = DiffExecutor.CONCLUSION_JSONS_ARE_EQUAL
		when:
			def result = executor.executeDiffForJsons(jsonLeft, jsonRight)
		then:
			result.conclusion == expectedConclusion
	}

	def "executeDiffForJsons -> success -> JSONs have different sizes"() {
		given:
			def jsonLeft = """{"key":"value1"}"""
			def jsonRight = """{"key":"value"}"""
			def expectedConclusion = DiffExecutor.CONCLUSION_JSONS_DIFFERENT_SIZE
		when:
			def result = executor.executeDiffForJsons(jsonLeft, jsonRight)
		then:
			result.conclusion == expectedConclusion
	}

	def "executeDiffForJsons -> success -> JSONs are different but with same size"() {
		given:
			def jsonLeft = """{"key":"value1"}"""
			def jsonRight = """{"key":"value2"}"""
			def expectedConclusion = DiffExecutor.CONCLUSION_JSONS_DIFFERENT_WITH_SAME_SIZE
		when:
			def result = executor.executeDiffForJsons(jsonLeft, jsonRight)
		then:
			result.conclusion == expectedConclusion
	}
}
