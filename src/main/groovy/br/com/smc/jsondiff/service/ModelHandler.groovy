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
			repo.save(diff)
		}

		return diff
	}

	def processJson(DiffObject o, String s, JsonPosition position) {

	}


	/* Properties */
	Logger log = Logger.getLogger(ModelHandler.class)
	@Autowired
	DiffRepository repo
}
