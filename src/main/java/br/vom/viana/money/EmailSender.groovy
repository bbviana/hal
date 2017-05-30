package br.vom.viana.money

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

/**
 * @author bbviana
 */
@Component
class EmailSender {

    public static void main(String[] args) {
        def sender = new EmailSender()
        def impl = new JavaMailSenderImpl()

        impl.protocol = "smtp"
        impl.host = "smtp.gmail.com"
        impl.port = 587
        impl.username = "bbviana@gmail.com"
        impl.password = ""

        def properties = new Properties()
        properties.setProperty("mail.smtp.auth", "true")
        properties.setProperty("mail.smtp.starttls.enable", "true")
        impl.setJavaMailProperties(properties)

        sender.javaMailSender = impl
        sender.send(null)
    }

    @Autowired
    private JavaMailSender javaMailSender

    void send(Summary summary) {
        SimpleMailMessage mailMessage = new SimpleMailMessage()
        mailMessage.setTo("bbviana@touchtec.com.br")
        mailMessage.setSubject("Teste")
        mailMessage.setText("Teste <a href='foo'>Link</a>")
        mailMessage.setFrom("Bruno Viana")
        javaMailSender.send(mailMessage)
    }
}
