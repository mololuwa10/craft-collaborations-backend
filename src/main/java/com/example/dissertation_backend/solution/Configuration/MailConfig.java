package com.example.dissertation_backend.solution.Configuration;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        // Dotenv dotenv = Dotenv.load();
        // JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Set the properties for the mail sender
        // mailSender.setHost(System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST")));
        // mailSender.setPort(Integer.parseInt(System.setProperty("MAIL_PORT",
        // dotenv.get("MAIL_PORT"))));
        // mailSender.setUsername(System.setProperty("MAIL_USERNAME",
        // dotenv.get("MAIL_USERNAME")));
        // mailSender.setPassword(System.setProperty("MAIL_PASSWORD",
        // dotenv.get("MAIL_PASSWORD")));

        Dotenv dotenv = null;
        if (System.getenv("HEROKU") == null) {
            dotenv = Dotenv.load(); // Load .env file in local development
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Set the properties for the mail sender
        mailSender.setHost(getEnvValue(dotenv, "MAIL_HOST"));
        mailSender.setPort(Integer.parseInt(getEnvValue(dotenv, "MAIL_PORT")));
        mailSender.setUsername(getEnvValue(dotenv, "MAIL_USERNAME"));
        mailSender.setPassword(getEnvValue(dotenv, "MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    private static String getEnvValue(Dotenv dotenv, String key) {
        if (dotenv != null) {
            return dotenv.get(key); // Use .env file values in development
        }
        return System.getenv(key); // Use Heroku config vars in production
    }
}