package com.example.dissertation_backend.solution.Configuration;

import java.nio.file.Files;
import java.nio.file.Paths;
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

        Dotenv dotenv = null;

        if (Files.exists(Paths.get(".env"))) {
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