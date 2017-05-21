package br.vom.viana.tables

import br.vom.viana.tables.fetch.Fetcher
import br.vom.viana.tables.tableDef.TableDefinitionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.web.bind.annotation.RequestMethod.GET

@RestController
@RequestMapping(path = "/api/tables", produces = "application/json; charset=UTF-8")
class TablesController {

    @Autowired
    private TableDefinitionRepository repository

    @Autowired
    private Fetcher fetcher

    @RequestMapping(method = GET, path = "/{query}")
    List<Table> tables(@PathVariable String query) {
        return repository
                .findByNameContainingIgnoreCase(query)
                .collect { fetcher.get(it) }
    }

}
