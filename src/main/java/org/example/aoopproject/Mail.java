package org.example.aoopproject;

import jakarta.mail.*;
import jakarta.mail.internet.*;


import java.util.Properties;

public class Mail {

    public static void sendEmail(String recipientEmail, String otp) throws MessagingException {
        String senderEmail = "mislam2420710@bscse.uiu.ac.bd";
        String senderPassword = "jkfy igqt aqez dadt";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(senderEmail, senderPassword);
            }
        });


        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject("Your OTP for Registration");
        message.setText("Your One-Time Password (OTP) is: " + otp + "\nIt will expire in 5 minutes.");

        Transport.send(message);
    }
    public static void sendEmergencyAlert(String recipientEmail, String location) throws MessagingException {
        String senderEmail = "mislam2420710@bscse.uiu.ac.bd";
        String senderPassword = "";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject("🚨 EMERGENCY ALERT!");
        message.setText("An emergency has been reported.\n\n"
                + location + "\n"
                + "Time: " + java.time.LocalDateTime.now()
                + "\n\nPlease respond immediately.");

        Transport.send(message);
        System.out.println("Emergency email sent successfully!");
    }

}
