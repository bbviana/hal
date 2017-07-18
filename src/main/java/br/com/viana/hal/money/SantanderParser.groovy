package br.com.viana.hal.money

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.DecimalFormat

/**
 * @author bbviana
 */
@Component
class SantanderParser {

    @Autowired
    private Settings config

    Summary parse() {
        // TODO recuperar date do arquivo
        Summary summary = new Summary(new Date())

        summary.accounts.addAll(parseCartao())
        summary.accounts << parseConta()

        processCategories(summary)

        return summary
    }

    private List<Account> parseCartao() {
        def nf = DecimalFormat.getInstance(new Locale("pt", "BR"))

        def document = Jsoup.parse(new File("/tmp/money/cartao.txt"), "UTF-8")
        List<Account> accounts = []
        Account account

        for (Element element : document.select("tbody tr")) {
            // ignore list
            if (config.ignoreList.find { element.text().contains(it) } != null) {
                continue
            }

            // nome da conta
            if (element.text().contains("Cartão XXXX XXXX")) {
                account = new Account(
                        name: element.text().trim()
                )
                accounts << account
                continue
            }

            // process categories?
            account.processCategories = config.settings["processCategories"].split(",").contains {
                account.name.contains(it)
            }

            if (account == null) {
                throw new RuntimeException("Não foi possivel determinar o nome da conta.")
            }

            def transaction = new Transaction(
                    date: Date.parse("dd/MM/yyyy", element.select("td:nth-child(1)").text().trim()),
                    description: element.select("td:nth-child(2)").text().trim(),
                    valueReal: nf.parse(element.select("td:nth-child(3)").text().trim()),
                    valueDolar: nf.parse(element.select("td:nth-child(4)").text().trim()),
            )
            convertDolarToReal(transaction)
            assignCategory(transaction)
            account.transactions << transaction
        }

        accounts.each {
            it.total = it.transactions.valueReal.sum() as double
        }

        return accounts
    }

    private Account parseConta() {
        def nf = DecimalFormat.getInstance(new Locale("pt", "BR"))

        def document = Jsoup.parse(new File("/tmp/money/extrato.txt"), "UTF-8")
        def account = new Account(name: "Conta Corrente", processCategories: true)

        for (Element element : document.select("table").get(0).select("tbody tr")) {
            if (config.ignoreList.find { element.text().contains(it) } != null) {
                continue
            }

            def transaction = new Transaction(
                    date: Date.parse("dd/MM/yyyy", element.select("td:nth-child(1)").text().trim()),
                    description: element.select("td:nth-child(3)").text().trim(),
                    valueReal: (-1) * nf.parse(element.select("td:nth-child(6)").text().trim()),
            )
            assignCategory(transaction)
            account.transactions << transaction
        }

        // Total
        account.total = nf.parse(document.select("table").get(1).select("tbody tr:nth-child(1) td:nth-child(2)").text().trim())

        return account
    }


    private void convertDolarToReal(Transaction transaction) {
        transaction.valueReal = transaction.valueReal ?: transaction.valueDolar * 3.3
    }

    private void assignCategory(Transaction transaction) {
        config.categories.each { rule, category ->
            if (transaction.description.replaceAll("\\s", "").contains(rule.replaceAll("\\s", ""))) {
                transaction.category = category
            }
        }
    }

    private void processCategories(Summary summary) {
        List<Transaction> allTransactions = summary.accounts.findAll {
            it.processCategories
        }.transactions.flatten() as List<Transaction>

        allTransactions.
                groupBy { it.category }.
                each { category, transactions ->
                    summary.categories << new Category(
                            name: category,
                            total: transactions.valueReal.sum() as double,
                            budget: config.budget[category] ?: 0
                    )
                }
    }
}
