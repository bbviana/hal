package br.vom.viana.money

import br.vom.viana.tables.Table
import br.vom.viana.tables.fetch.Fetcher
import br.vom.viana.tables.tableDef.TableDefinitionRepository
import groovy.xml.MarkupBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

import static org.springframework.web.bind.annotation.RequestMethod.GET

@Controller
@RequestMapping(path = "/santander")
class SantanderController {

    @Autowired
    private SantanderFetcher fetcher

    @Autowired
    private SantanderParser parser

    @ResponseBody
    @RequestMapping(method = GET, path = "/fetch")
    String fetch(){
        fetcher.fetch()

        """
        <div>Importação concluída com sucesso</div>

        <div>
            <a href="/santander">Consultar</a>
        </div>
        """
    }

    @ResponseBody
    @RequestMapping(method = GET, path = "/")
    String summary() {
        def summary = parser.parse()

        def writer = new StringWriter()
        def markup = new MarkupBuilder(writer)

        markup.html {
            summary.accounts.each { account ->
                section {
                    h3(account.name)
                    h5("R\$ " + account.total)

                    table {
                        thead {
                            tr {
                                th("Data")
                                th("Descrição")
                                th("Valor")
                                th("Categoria")
                            }
                        }
                        tbody {
                            account.transactions.each { transaction ->
                                tr {
                                    td(transaction.date.format("dd/MM"))
                                    td(transaction.description)
                                    td(transaction.valueReal)
                                    td(transaction.category)
                                }
                            }
                        }
                    }
                }
            }

            section {
                table {
                    tbody {
                        thead {
                            tr {
                                th("Categoria")
                                th("Meta")
                                th("Total")
                            }
                        }
                        summary.categories.each { category ->
                            tr {
                                td(category.name)
                                td(category.budget)
                                td(category.total)
                            }
                        }
                    }
                }
            }
        }

        return writer.toString()
    }

}