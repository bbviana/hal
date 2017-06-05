package br.com.viana.money

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Files

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING
import static java.util.concurrent.TimeUnit.MINUTES

/**
 * @author bbviana
 */
@Component
class SantanderFetcher {

    @Autowired
    private Settings config

    void fetch() {
        def settings = config.settings

        if (System.getProperty("os.name").contains("Mac")) {
            extractToTemp("phantomjs-mac")
        } else {
            extractToTemp("phantomjs")
        }

        extractToTemp("santander.js")

        def process = new ProcessBuilder(
                "/tmp/money/phantomjs",
                "/tmp/money/santander.js",
                "cpf=${settings.cpf}",
                "senha=${settings.senha}",
                "extratoPeriodo=${settings.extratoPeriodo}",
                "outDir=${settings.outDir}", // barra no fim
                "extratoFile=${settings.extratoFile}",
                "cartaoFile=${settings.cartaoFile}"
        ).start()

        process.inputStream.eachLine {
            System.out.println(it)
        }

        process.waitFor(2, MINUTES)
    }

    private extractToTemp(String fileName) {
        def resource = this.class.classLoader.getResource("phantomjs/${fileName}")
        def source = new File(resource.path)

        new File("/tmp/money").mkdir()
        def dest = new File("/tmp/money/${fileName}")

        Files.copy(source.toPath(), dest.toPath(), REPLACE_EXISTING, COPY_ATTRIBUTES)
    }
}
