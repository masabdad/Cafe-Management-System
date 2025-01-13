package com.cafe.management.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.ls.LSInput;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
@Component

public class EmailUtils {

    @Autowired
    private JavaMailSender emailSender;




    public void sendSimpleMessage(String to, String subject, String text, List<String> List) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("masabmughal8@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if (List != null && List.size() > 0)

            message.setCc(getCcArray(List));
        emailSender.send(message);
    }

    private String[] getCcArray(List<String> ccList) {
        String[] cc = new String[ccList.size()];
        for (int i = 0; i < ccList.size(); i++) {
            cc[i] = ccList.get(i);
        }

        return cc;
    }

    public void forgotMail(String to, String subject, String password) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set email details
            helper.setFrom("masabmughal8@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);

            // Email content
            String htmlMsg = """
            <p><b>Your Login Details for Cafe Management System</b></p>
            <p><b>Email:</b> %s</p>
            <p><b>Password:</b> %s</p>
            <p><a href="http://localhost:4200/">Click here to login</a></p>
        """.formatted(to, password);

            helper.setText(htmlMsg, true);

            // Send the email
            emailSender.send(message);
        } catch (Exception e) {
            // Log the error and rethrow as a runtime exception
            throw new RuntimeException("Failed to send email", e);
        }
    }


}
