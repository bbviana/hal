package br.vom.viana.money

import groovy.transform.ToString
import groovy.xml.MarkupBuilder

@ToString(includePackage = false)
class Summary {
    List<Account> accounts = []

    List<Category> categories = []

    String toHTML() {
        def writer = new StringWriter()
        def markup = new MarkupBuilder(writer)

        markup.html {
            accounts.each { account ->
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
                        categories.each { category ->
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
