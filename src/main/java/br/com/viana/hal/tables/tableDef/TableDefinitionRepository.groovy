package br.com.viana.hal.tables.tableDef

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

/**
 * Implementaçao gerada pelo Spring Data
 * https://spring.io/guides/gs/accessing-data-rest/
 */
@RepositoryRestResource(collectionResourceRel = "table-defs", path = "table-defs")
interface TableDefinitionRepository extends PagingAndSortingRepository<TableDefinition, Long> {

    List<TableDefinition> findByNameContainingIgnoreCase(@Param("name") String name)
}
