package br.com.viana.hal.money

import org.springframework.mail.javamail.JavaMailSenderImpl

/**
 * @author bbviana
 */
class EmailSenderSpec {


//    @Test
    void "test"(){
        def sender = new EmailSender()
        def impl = new JavaMailSenderImpl()

        Properties config = new Properties()
        config.load(EmailSender.classLoader.getResourceAsStream("application.properties"))

        impl.protocol = config.getProperty("spring.mail.protocol")
        impl.host = config.getProperty("spring.mail.host")
        impl.port = Integer.parseInt(config.getProperty("spring.mail.port"))
        impl.username = config.getProperty("spring.mail.username")
        impl.password = config.getProperty("spring.mail.password")

        def properties = new Properties()
        properties.setProperty("mail.smtp.auth", "true")
        properties.setProperty("mail.smtp.starttls.enable", "true")
        impl.setJavaMailProperties(properties)

        sender.javaMailSender = impl
        sender.send(new Summary(new Date()))
    }
}
