package br.com.viana.tables.fetch

import br.com.viana.tables.tableDef.TableDefinitionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ScheduledFetch {

    @Autowired
    private Fetcher fetcher

    @Autowired
    private TableDefinitionRepository repository

    //@Scheduled(cron = "0 0 0/5 * * ?") // de 5 em 5 horas
    //@Scheduled(fixedRate = 10000L)
    void execute() {
        println "Fetch automático agendado..."

        fetcher.clearCache()

        repository
                .findAll()
                .collect { fetcher.get(it) }
    }
}
