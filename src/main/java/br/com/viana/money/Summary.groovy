package br.com.viana.money

import groovy.transform.ToString
import groovy.xml.MarkupBuilder

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

import static java.util.Calendar.DATE
import static java.util.Calendar.DAY_OF_MONTH
import static java.util.Calendar.DAY_OF_MONTH
import static java.util.Calendar.MONTH

@ToString(includePackage = false)
class Summary {

    Date date

    List<Account> accounts = []

    List<Category> categories = []

    Summary(Date date) {
        this.date = date
    }

    String toHTML() {
        def writer = new StringWriter()
        def markup = new MarkupBuilder(writer)

        def monthProgress = 0

        // se estamos no mesmo mês do Summary, mostramos a barra de progresso
        // o mês já passou, não faz sentido mostrar
        def cal = Calendar.instance
        if(cal[MONTH] == date[MONTH]){
            def lastDayOfMonth = cal.getActualMaximum(DAY_OF_MONTH)
            monthProgress = 100 * date[DATE] / lastDayOfMonth
        }


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

                    div(class: "budget") {
                        div(" ", class: "estimate", style: "width: ${monthProgress}%")

                        categories.each { category ->
                            div(class: "category") {

                                div(category.name ?: "Não especificado", class: "title")

                                div(category.total.format(), class: "total")

                                div(" ", class: "clearfix")

                                div(class: "progress-bar ${category.overflow ? 'overflow' : ''}", title: category.budget.format()) {
                                    div(" ", class: "progress-bar-inner", style: "width: ${category.percent}%")
                                }
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
