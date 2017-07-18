package br.com.viana.hal.money

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
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

    @Autowired
    private EmailSender emailSender

    @ResponseBody
    @RequestMapping(method = GET, path = "/fetch")
    String fetch() {
        fetcher.fetch()

        """
        <div>Importação concluída com sucesso</div>

        <div>
            <a href="/santander/">Consultar</a>
        </div>
        """
    }

    @ResponseBody
    @RequestMapping(method = GET, path = "/")
    String summary() {
        parser.parse().toHTML()
    }

    @ResponseBody
    @RequestMapping(method = GET, path = "/send-mail")
    String sendMail() {
        println "Enviar e-mail..."

        def summary = parser.parse()
        emailSender.send(summary)

        return "Email enviado com sucesso"
    }

}