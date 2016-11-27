package br.com.smc.jsondiff.service

import br.com.smc.jsondiff.exception.InvalidDiffIdException
import br.com.smc.jsondiff.model.DiffObject
import br.com.smc.jsondiff.model.JsonPosition
import br.com.smc.jsondiff.repository.DiffRepository
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service that handles diffId's and Json's.
 */
@Service
class ModelHandler {

	/**
	 * This method verifies if already exists a @{link DiffObject} with diffId
	 * on the database. If not it sends for storage, else it returns the @{DiffObject}
	 * to the caller.
	 *
	 * @param diffId
	 */
	public DiffObject processDiffId(String diffId) {

		log.info("Start processing diffId ${diffId}")
		if(!diffId) {
			log.info("Invalid diffId. It cannot be empty.")
			throw new InvalidDiffIdException()
		}

		def diff = repo.findOne(diffId)
		if(!diff) {
			diff = new DiffObject(
					id: diffId
			)
			diff = repo.saveAndFlush(diff)
		}

		log.info("Diff with diffId ${diffId} stored.")
		return diff
	}

	/**
	 * This method applies a new JSON to the passed @{link DiffObject} and saves the new
	 * composition to the database.
	 *
	 * @param diff
	 * @param json
	 * @param position
	 * @return
	 */
	public DiffObject processJson(DiffObject diff, String json, JsonPosition position) {

		if (!(diff && json && position)){
			log.error("Failure: unable to process with null argument. diffId: ${diff?.id} | json: ${json} | position: ${position?.name()}")
			throw new IllegalArgumentException("Unable to process with null argument. Check log.")
		}

		log.info("Applying JSON to diff object with id ${diff.id} on position ${position.name()}.")

		def syncronizedDiff = repo.findOne(diff.id)
		if (!syncronizedDiff) {
			log.error("Diff id ${diff.id} not found on database.")
			throw new NoSuchElementException()
		}

		if(position == JsonPosition.LEFT) {
			diff.jsonLeft = json
		} else {
			diff.jsonRight = json
		}

		diff = repo.saveAndFlush(diff)

		log.info("Diff object ${diff.id} udpated on database.")

		return diff
	}


	/* Properties */
	Logger log = Logger.getLogger(ModelHandler.class)
	@Autowired
	DiffRepository repo
}
