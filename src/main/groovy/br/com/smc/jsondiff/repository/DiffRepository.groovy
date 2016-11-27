package br.com.smc.jsondiff.repository

import br.com.smc.jsondiff.model.DiffObject
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Class responsible for managing ORM to @{link DiffObject}.
 */
interface DiffRepository extends JpaRepository<DiffObject, String> {
}
