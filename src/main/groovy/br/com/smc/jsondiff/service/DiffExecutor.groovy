package br.com.smc.jsondiff.service

import br.com.smc.jsondiff.model.DiffObject
import br.com.smc.jsondiff.model.DiffResult
import br.com.smc.jsondiff.repository.DiffRepository
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
	public DiffResult executeDiffForDiffId(final String  diffId) {

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

		if (diff.jsonLeft && !diff.jsonRight) {
			return new DiffResult(
					conclusion: "Only left JSON was provided."
			)
		}
		if (diff.jsonRight && !diff.jsonLeft) {
			return new DiffResult(
					conclusion: "Only right JSON was provided."
			)
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
			def conclusion = "JSONs are equal"
			log.info(conclusion)
			diffResult.conclusion = conclusion
			return diffResult
		}

		if (jsonLeft.size() != jsonRight.size()) {
			def conclusion = "JSONs have different sizes"
			log.info(conclusion)
			diffResult.conclusion = conclusion
			return diffResult
		}

		def conclusion = "JSONs have same size but are different"
		log.info(conclusion)
		diffResult.conclusion = conclusion

		final DiffNode diff = ObjectDifferBuilder.buildDefault().compare(jsonRight, jsonLeft)

		diffResult.differences = new ArrayList<>()
		diff.visit(new DiffNode.Visitor(){
			@Override
			void node(DiffNode node, Visit visit) {
				if (node.hasChanges() && !node.hasChildren()) {
					final Object leftSide = node.canonicalGet(jsonLeft)
					final Object rightSide = node.canonicalGet(jsonRight)
					diffResult.differences << "${node.path} from ${leftSide} to ${rightSide}".toString()
				}
			}
		})

		return diffResult
	}


	/* Properties */
	Logger log = Logger.getLogger(DiffExecutor.class)
	@Autowired
	DiffRepository repo

}
