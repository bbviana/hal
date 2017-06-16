package br.com.viana.money

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

import javax.mail.internet.MimeMessage

/**
 * @author bbviana
 */
@Component
class EmailSender {

    @Autowired
    private JavaMailSender javaMailSender

    void send(Summary summary) {
        MimeMessage mail = javaMailSender.createMimeMessage()

        MimeMessageHelper messageHelper = new MimeMessageHelper(mail, true)
        messageHelper.setTo("bbviana@gmail.com")
        messageHelper.setSubject("Santander Status ${new Date().format('dd/MM/yyyy')}")
        messageHelper.setText(summary.toHTML(), true)

        println "Enviando e-mail..."

        javaMailSender.send(mail)
    }
}
