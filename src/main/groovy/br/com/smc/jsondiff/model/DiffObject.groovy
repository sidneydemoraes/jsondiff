package br.com.smc.jsondiff.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by Sidney de Moraes on 26/11/16.
 */
@Entity
@Table(name = "diff")
class DiffObject {

	@Id
	String id
	String jsonLeft
	String jsonRight
}
