package br.com.smc.jsondiff.service

import br.com.smc.jsondiff.model.DiffObject
import br.com.smc.jsondiff.model.DiffResult
import br.com.smc.jsondiff.repository.DiffRepository
import com.fasterxml.jackson.databind.ObjectMapper
import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.node.Visit
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service that performs diff operations.
 */
@Service
class DiffExecutor {

	/**
	 * Method that finds out diffs given a {@link DiffObject}'s id.
	 *
	 * @param diffId
	 * @return
	 */

	public static final String CONCLUSION_NO_JSONS_PROVIDED = "No JSONs were provided."
	public static final String CONCLUSION_ONLY_LEFT_JSON_FOUND = "Only left JSON was provided."
	public static final String CONCLUSION_ONLY_RIGHT_JSON_FOUND = "Only right JSON was provided."
	public static final String CONCLUSION_JSONS_ARE_EQUAL = "JSONs are equal"
	public static final String CONCLUSION_JSONS_DIFFERENT_SIZE = "JSONs have different sizes"
	public static final String CONCLUSION_JSONS_DIFFERENT_WITH_SAME_SIZE = "JSONs have same size but are different"

	public DiffResult executeDiffForDiffId(final String diffId) {

		if (!diffId) {
			def message = "Invalid diffId [${diffId}] provided for diff execution."
			log.error(message)
			throw new IllegalArgumentException(message)
		}

		log.info("Preparing diffId [${diffId}] for diff generation.")

		def diff = repo.findOne(diffId)
		if (!diff) {
			log.error("Diff id ${diffId} not found on database.")
			throw new NoSuchElementException()
		}

		if (!diff.jsonLeft && !diff.jsonRight){
			return new DiffResult(conclusion: CONCLUSION_NO_JSONS_PROVIDED)
		}
		if (diff.jsonLeft && !diff.jsonRight) {
			return new DiffResult(conclusion: CONCLUSION_ONLY_LEFT_JSON_FOUND)
		}
		if (diff.jsonRight && !diff.jsonLeft) {
			return new DiffResult(conclusion: CONCLUSION_ONLY_RIGHT_JSON_FOUND)
		}

		def result = executeDiffForJsons(diff.jsonLeft, diff.jsonRight)

		return result
	}

	/**
	 * Method that finds out diffs given two JSON's in {@link Object} format.
	 * Reminder: in Groovy, we can compare String contents with '=='.
	 *
	 * @param jsonLeft
	 * @param jsonRight
	 * @return
	 */
	public DiffResult executeDiffForJsons(final String jsonLeft, final String jsonRight) {

		log.info("Generating diff.")

		DiffResult diffResult = new DiffResult()

		if (jsonLeft == jsonRight) {
			log.info(CONCLUSION_JSONS_ARE_EQUAL)
			diffResult.conclusion = CONCLUSION_JSONS_ARE_EQUAL
			return diffResult
		}

		if (jsonLeft.size() != jsonRight.size()) {
			log.info(CONCLUSION_JSONS_DIFFERENT_SIZE)
			diffResult.conclusion = CONCLUSION_JSONS_DIFFERENT_SIZE
			return diffResult
		}

		def conclusion = CONCLUSION_JSONS_DIFFERENT_WITH_SAME_SIZE
		log.info(conclusion)
		diffResult.conclusion = conclusion

		Map<String, Object> baseJson = mapper.readValue(jsonLeft, Map.class)
		log.debug("JsonLeft mapped to baseJson.")
		Map<String, Object> workingJson = mapper.readValue(jsonRight, Map.class)
		log.debug("JsonRight mapped to workingJson.")

		final DiffNode diff = ObjectDifferBuilder.buildDefault().compare(workingJson, baseJson)
		log.debug("Comparison complete.")

		diffResult.differences = new ArrayList<>()
		diff.visit(new DiffNode.Visitor(){
			@Override
			void node(DiffNode node, Visit visit) {
				if (node.hasChanges() && !node.hasChildren()) {
					final Object leftSide = node.canonicalGet(baseJson)
					final Object rightSide = node.canonicalGet(workingJson)
					def nodePath = node.path.toString().replaceAll(/[\{\}\/]+/, "/")
					def detectedDiff = "In ${nodePath}: from ${leftSide} to ${rightSide}".toString()
					log.debug("Diff found: ${detectedDiff}")
					diffResult.differences << detectedDiff
				}
			}
		})

		return diffResult
	}


	/* Properties */
	Logger log = Logger.getLogger(DiffExecutor.class)
	@Autowired
	DiffRepository repo
	@Autowired
	ObjectMapper mapper
}
