package br.com.viana.money

import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.text.DecimalFormat

/**
 * @author bbviana
 */
@Component
class Settings {

    Map<String, String> settings = [:]

    List<String> ignoreList = []

    Map<String, String> categories = [:]

    Map<String, Double> budget = [:]

    @PostConstruct
    void read() {
        ignoreList = Settings.classLoader.getResource("ignore.txt").readLines()

        Settings.classLoader.getResource("settings.txt").eachLine {
            def parts = it.split("=")
            if (parts.length == 2) {
                settings[parts[0].trim()] = parts[1].trim()
            }
        }

        Settings.classLoader.getResource("categories.txt").eachLine {
            def parts = it.split("=")
            if (parts.length == 2) {
                categories[parts[0].trim()] = parts[1].trim()
            }
        }

        def nf = DecimalFormat.getInstance(new Locale("pt", "BR"))
        Settings.classLoader.getResource("budget.txt").eachLine {
            def parts = it.split("=")
            if (parts.length == 2) {
                budget[parts[0].trim()] = nf.parse(parts[1].trim()) as double
            }
        }
    }
}
