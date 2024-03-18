package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.io.*;

public class EmailSender {
    private final String senderEmail;
    private final String senderPassword;

    public EmailSender(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    public void sendConfirmationEmail(String recipientEmail, String customerFirstName, String customerSurname) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587"); // 587 là cổng sử dụng TLS cho Gmail | SSL thì là 465
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Set protocol version

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "MessUp Community"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Welcome to MessUp - Registration Successful!");

            // Nội dung email
            String emailContent = "Hello " + customerFirstName + " " + customerSurname + ",\n\n" +
                    "Congratulations on successfully registering your account on the MessUp app - a convenient and fun chat community. We are delighted to welcome you aboard!\n" +
                    "Below are some features and benefits you can experience on MessUp:\n" +
                    "\t- Chat with friends and family anytime, anywhere.\n" +
                    "\t- Send text messages, images, videos, and more.\n" +
                    "\t- Create private conversations or join group chats based on shared interests.\n" +
                    "\t- Explore emoticons and emojis to add extra fun to your chats.\n" +
                    "\t- And many more exciting features waiting for you!\n\n" +
                    "Log in to your account and start connecting with the MessUp community right away. If you have any questions or need assistance, please don't hesitate to contact us at messupcommunity@gmail.com.\n\n" +
                    "We hope you have an enjoyable experience on MessUp!\n\n" +
                    "Best regards,\n" +
                    "MessUp Community Team\n\n" +
                    "P.S.: Don't forget to check your account details and update your profile!";
            message.setText(emailContent);

            Transport.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Send error!!!!!!!");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
