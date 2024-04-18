package com.agmg.carsparadise.Util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMail {

    private static String emailAzienda = "";
    private static String password = "";

    public static void sendEmail(String email, String messaggio, String oggetto) {

            // Creiamo la sessione
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put ("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.user", emailAzienda);
        properties.put("mail.password", password);


            // Connessione all'account gmail
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailAzienda, password);
                }
            });

            //Creiamo il messaggio
            Message message = prepareMessage(session, email, oggetto, messaggio);
            try {
                Transport.send(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private static Message prepareMessage(Session session, String email, String oggetto, String messaggio){
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailAzienda));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
                message.setSubject(oggetto);
                message.setText(messaggio);
                return message;
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(ServerMail.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
        /*
        Properties properties = new Properties();
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", port);
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.user", userName);
    properties.put("mail.password", password);

    // creates a new session with an authenticator
    Authenticator auth = new Authenticator() {
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    };
    Session session = Session.getInstance(properties, auth);

    // creates a new e-mail message
    Message msg = new MimeMessage(session);

    msg.setFrom(new InternetAddress(userName));
    InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
    msg.setRecipients(Message.RecipientType.TO, toAddresses);
    msg.setSubject(subject);
    msg.setSentDate(new Date());

    // creates message part
    MimeBodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setContent(message, "text/html");

    // creates multi-part
    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBodyPart);

    // adds attachments
    if (attachFiles != null && attachFiles.length > 0) {
        for (String filePath : attachFiles) {
            MimeBodyPart attachPart = new MimeBodyPart();

            try {
                attachPart.attachFile(filePath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            multipart.addBodyPart(attachPart);
        }
    }

    // sets the multi-part as e-mail's content
    msg.setContent(multipart);

    // sends the e-mail
    Transport.send(msg);
    }
         */
