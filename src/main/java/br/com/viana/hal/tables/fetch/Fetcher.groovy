package br.com.viana.hal.tables.fetch

import br.com.viana.hal.tables.Table
import br.com.viana.hal.tables.tableDef.TableColumnDefinition
import br.com.viana.hal.tables.tableDef.TableDefinition
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.stereotype.Component

@Component
class Fetcher {

    private Map<String, Table> cache = [:]


    Table get(TableDefinition tableDef) {

        Table table = cache.get(tableDef.name)

        if (table != null) {
            println "Recuperando ${tableDef.name} do cache"
            return table
        }

        def t = System.currentTimeMillis()

        table = new Table()
        table.name = tableDef.name
        table.lastUpdated = new Date()
        table.columnsNames = tableDef.columns*.name

        Document document

        try {
            document = Jsoup.connect(tableDef.sourceURL).get()
        } catch (Exception e) {
            throw new RuntimeException(e)
        }


        Elements rowSources = document.select(tableDef.elementsSelector)
        for (Element rowSource : rowSources) {
            Map row = buildRow(tableDef, rowSource)

            if (row == null) {
                continue
            }

            table.rows << row
        }

        if (tableDef.reverseRows) {
            table.rows = table.rows.reverse()
        }

        println "Tempo de parse: " + (System.currentTimeMillis() - t) + "ms"

        cache.put(tableDef.name, table)

        return table
    }

    private Map buildRow(TableDefinition tableDef, Element rowSource) {
        Map row = [:]

        for (TableColumnDefinition columnDef : tableDef.columns) {
            def cell = rowSource.select(columnDef.selector)
            if (cell.isEmpty()) {
                return null
            }
            row[columnDef.name] = cell.text()
        }

        return row
    }

    void clearCache() {
        cache.clear()
    }

}
