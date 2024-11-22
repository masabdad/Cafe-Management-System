package com.cafe.management.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.ls.LSInput;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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

    public void forgotMail(String to, String subject, String password) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("masabmughal8@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlMsg = "<p><b> Your Login detail for Cafe Management System</b><br><b>Email: </b> " + to + " <br><b>Password: </b>  " + password + " <br><a href =\"http://localhost:4200/\"> Click here to login</a></p>";
        message.setContent(htmlMsg, "text/html");
        emailSender.send(message);


    }

}
