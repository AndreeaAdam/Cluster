package cluser.crm.services;

import cluser.crm.exceptions.MiscException;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

/**
 * Service class used to process mail-related services
 */
@Service
public class MailService {
    /***
     *
     *
     * @Description Sends  a generic e-mail through SMTP
     * @param recipient - recipient of the mail
     * @param subject - subject/title of the mail
     * @param contents - contents of the mail
     * @return boolean
     */
    public boolean sendMailSMTPGeneric(String recipient,String subject, String contents){
        String username;
        String password;

        /**
         * @Description Takes the properties from smtp.properties
         */
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("smtp.properties"));
            username=properties.getProperty("username");
            password=properties.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
            throw new MiscException("username and/or password could not be fetched from extra.properties");
        }

        /**
         * @Description SMTP properties
         */

        // ORIGINALS BELOW
        Properties property = new Properties();
        property.put("mail.smtp.host", "smtp.gmail.com");
        property.put("mail.smtp.port", "587");
        property.put("mail.smtp.auth", "true");
        property.put("mail.smtp.starttls.enable", "true");
//
        /**
         * @Description Authenticator
         */
        String finalUsername = username;
        String finalPassword = password;
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(finalUsername, finalPassword);
            }
        };

        /**
         * @Description Session instance
         */
        Session session = Session.getInstance(property,authenticator);
        try {

            MimeMessage message = new MimeMessage(session);
            //From : (Might require to be changed)
            message.setFrom(new InternetAddress(username));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject,"utf-8");
            message.setContent(contents,"text/html; charset=utf-8");
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
