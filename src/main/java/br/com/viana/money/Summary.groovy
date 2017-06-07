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

        DecimalFormat df = new DecimalFormat("#,##0.00")
        df.decimalFormatSymbols = DecimalFormatSymbols.getInstance(new Locale("pt", "BR"))

        markup.html {
            accounts.each { account ->
                section {
                    h3(account.name)
                    h5("R\$ " + df.format(account.total))

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
                                    td(df.format(transaction.valueReal))
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
                                td(df.format(category.budget))
                                td(df.format(category.total))
                            }
                        }
                    }
                }
            }
        }

        return writer.toString()
    }
}
