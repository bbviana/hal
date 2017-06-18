package br.com.viana.money

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ
import static java.nio.file.attribute.PosixFilePermission.OTHERS_WRITE
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ
import static java.util.concurrent.TimeUnit.MINUTES

/**
 * @author bbviana
 */
@Component
class SantanderFetcher {

    @Autowired
    private Settings config

    void fetch() {
        cleanTempFolder()

        def settings = config.settings

        if (System.getProperty("os.name").contains("Mac")) {
            extractToTemp("phantomjs-mac", "phantomjs")
        } else if (System.getProperty("os.arch").contains("arm")) {
            extractToTemp("phantomjs-arm", "phantomjs")
        } else {
            extractToTemp("phantomjs")
        }

        extractToTemp("santander.js")

        def process = new ProcessBuilder(
                "/tmp/money/phantomjs",
                "/tmp/money/santander.js",
                "stepInterval=300",
                "timeout=${1 * 1000 * 60}",
                "cpf=${settings.cpf}",
                "senha=${settings.senha}",
                "extratoPeriodo=${settings.extratoPeriodo}",
                "outDir=${settings.outDir}", // barra no fim
                "extratoFile=${settings.extratoFile}",
                "cartaoFile=${settings.cartaoFile}"
        ).start()


        process.inputStream.eachLine {
            System.out.println(it)

            if (it.contains("[PROCESS_TIMEOUT]")) {
                throw new RuntimeException("PROCESS_TIMEOUT")
            }
        }

        process.waitFor(1, MINUTES)
    }

    private void cleanTempFolder() {
        println "Apagando /tmp/money..."

        def dest = new File("/tmp/money")
        if (dest.exists()) {
            dest.listFiles().each { it.delete() }
            dest.delete()
        }
    }

    private extractToTemp(String fileName, String fileNameDest = null) {
        println "Extraindo $fileName para /tmp/money..."

        def source = this.class.classLoader.getResource("phantomjs/${fileName}").openStream()

        new File("/tmp/money").mkdir()
        def dest = new File("/tmp/money/${fileNameDest ?: fileName}").toPath()

        Files.copy(source, dest, REPLACE_EXISTING)
        Files.setPosixFilePermissions(dest, [OWNER_EXECUTE, OWNER_READ] as HashSet)
    }
}
