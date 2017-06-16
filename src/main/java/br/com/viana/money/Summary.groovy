package br.com.viana.money

import groovy.transform.ToString
import groovy.xml.MarkupBuilder

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@ToString(includePackage = false)
class Summary {
    List<Account> accounts = []

    List<Category> categories = []

    String toHTML() {
        def writer = new StringWriter()
        def markup = new MarkupBuilder(writer)

        markup.html {
            head {
                style(this.class.classLoader.getResource("public/santander.css").text)
            }

            body {
                div(class: "section") {
                    table {
                        tbody {
                            thead {
                                tr {
                                    th("Categoria")
                                    th("Meta")
                                    th("Total")
                                    th("%")
                                }
                            }
                            categories.each { category ->
                                tr {
                                    td(category.name)
                                    td(category.budget.format())
                                    td(category.total.format())
                                    td(category.percent.format())
                                }
                            }
                        }
                    }

                    categories.each { category ->
                        div(class: "budget") {
                            div(category.name ?: "Não especificado", class: "title")

                            div(category.total.format(), class: "total")

                            div(class: "clearfix")

                            div(class: "progress-bar ${category.overflow ? 'overflow' : ''}", title: category.budget.format()) {
                                div(" ", class: "progress-bar-inner", style: "width: ${category.percent}%")
                            }
                        }
                    }
                }

                accounts.each { account ->
                    div(class: "section") {
                        h3(account.name)
                        h5("R\$ " + account.total.format())

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
                                        td(transaction.valueReal.format())
                                        td(transaction.category)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return writer.toString()
    }
}
