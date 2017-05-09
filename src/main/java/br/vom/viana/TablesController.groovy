package br.vom.viana

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.annotation.PostConstruct

import static org.springframework.web.bind.annotation.RequestMethod.GET

@RestController
@RequestMapping(path = "/tables", produces = "application/json; charset=UTF-8")
class TablesController {

    private List<TableDefinition> tablesDefs = []

    @Autowired
    private Fetcher fetcher

    @PostConstruct
    void prepare() {
        List<TableColumnDefinition> columnsDef = [
                new TableColumnDefinition(name: "pos", selector: ".rank"),
                new TableColumnDefinition(name: "equipe", selector: ".club"),
                new TableColumnDefinition(name: "J", selector: "td:nth-child(5)"),
                new TableColumnDefinition(name: "PG", selector: "td:nth-child(18)"),
                new TableColumnDefinition(name: "V", selector: "td:nth-child(12)"),
                new TableColumnDefinition(name: "E", selector: "td:nth-child(13)"),
                new TableColumnDefinition(name: "D", selector: "td:nth-child(14)"),
                new TableColumnDefinition(name: "GP", selector: "td:nth-child(15)"),
                new TableColumnDefinition(name: "GC", selector: "td:nth-child(16)"),
                new TableColumnDefinition(name: "SG", selector: "td:nth-child(17)"),
        ]


        tablesDefs << new TableDefinition(
                name: "Campeonato Inglês",
                sourceURL: "http://pt.uefa.com/memberassociations/association=eng/domesticleague/standings/index.html",
                elementsSelector: ".tb_stand tbody tr",
                columns: columnsDef
        )

        tablesDefs << new TableDefinition(
                name: "Campeonato Espanhol",
                sourceURL: "http://pt.uefa.com/memberassociations/association=esp/domesticleague/standings/index.html",
                elementsSelector: ".tb_stand tbody tr",
                columns: columnsDef
        )

        tablesDefs << new TableDefinition(
                name: "Campeonato Italiano",
                sourceURL: "http://pt.uefa.com/memberassociations/association=ita/domesticleague/standings/index.html",
                elementsSelector: ".tb_stand tbody tr",
                columns: columnsDef
        )

        tablesDefs << new TableDefinition(
                name: "Oscar - Filmes",
                sourceURL: "https://pt.wikipedia.org/wiki/Oscar_de_melhor_filme",
                elementsSelector: ".wikitable tr",
                columns: [
                        new TableColumnDefinition(name: "ano", selector: "td:first-child a:last-child"),
                        new TableColumnDefinition(name: "filme", selector: "td:last-child b")
                ],
                reverseRows: true
        )

        tablesDefs << new TableDefinition(
                name: "Carnaval - RJ",
                sourceURL: "https://pt.wikipedia.org/wiki/Lista_de_campeãs_do_carnaval_do_Rio_de_Janeiro",
                elementsSelector: "#mw-content-text > table:contains(Escola campeã) tr",
                columns: [
                        new TableColumnDefinition(name: "ano", selector: "td:nth-child(1) > a"),
                        new TableColumnDefinition(name: "escola", selector: "td:nth-child(2) > a"),
                        new TableColumnDefinition(name: "enredo", selector: "td:nth-child(3)")
                ],
                reverseRows: true
        )

        tablesDefs << new TableDefinition(
                name: "NBA - Basquete",
                sourceURL: "https://en.wikipedia.org/wiki/List_of_NBA_champions#NBA_champions",
                elementsSelector: "h3:contains(NBA champions) + table tbody tr",
                columns: [
                        new TableColumnDefinition(name: "ano", selector: "td:nth-child(1) > a"),
                        new TableColumnDefinition(name: "campeão", selector: "td[style='background:#FFFF99'] a"),
                        new TableColumnDefinition(name: "oeste", selector: "td:nth-child(2) a"),
                        new TableColumnDefinition(name: "placar", selector: "td:nth-child(3)"),
                        new TableColumnDefinition(name: "leste", selector: "td:nth-child(4) a")
                ],
                reverseRows: true
        )

        tablesDefs << new TableDefinition(
                name: "Futebol - UCL Champions League",
                sourceURL: "https://pt.wikipedia.org/wiki/Lista_de_campeões_da_Taça_dos_Campeões_Europeus_e_Liga_dos_Campeões_da_UEFA",
                elementsSelector: "h2:contains(Vencedores) + .wikitable tbody tr",
                columns: [
                        new TableColumnDefinition(name: "Edição", selector: "td:nth-child(1)"),
                        new TableColumnDefinition(name: "Campeão", selector: "td:nth-child(3)"),
                        new TableColumnDefinition(name: "Resultado", selector: "td:nth-child(4)"),
                        new TableColumnDefinition(name: "Vice", selector: "td:nth-child(5)"),
                        new TableColumnDefinition(name: "Local", selector: "td:nth-child(8)")
                ],
                reverseRows: true
        )

    }

    @RequestMapping(method = GET, path = "/{query}")
    List<Table> tables(@PathVariable String query) {
        return tablesDefs
                .findAll { it.name.toLowerCase().contains(query.toLowerCase()) }
                .collect { fetcher.get(it) }
    }

}
