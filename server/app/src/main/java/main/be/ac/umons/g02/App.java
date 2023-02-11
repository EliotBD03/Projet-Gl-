package main.be.ac.umons.g02;

import java.util.HashMap;
import java.util.Timer;
import io.vertx.core.Vertx;
import main.be.ac.umons.g02.api.MyApi;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Map;

public class App
{
    private static HashMap<String, String> listCode;
    private static HashMap<String, Timer> listTimerTodeleteCode;
    private static String username = "";
    private static String password = "";

    public static void main(String[] args)
    {
        final Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MyApi());
    }

    public static void sendEmail(String recipient, String subject, String text)
    {
        Map<String, String> env  = System.getenv();


		if(env.containsKey("EMAILID") && env.containsKey("EMAILPWD"))
        {
            username = env.get("EMAILID");
            password = env.get("EMAILPWD");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-babawallet.alwaysdata.net");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator()
                {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(username, password);
                    }
                });
        //session.setDebug(true);

        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        }

        catch (MessagingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String createCode(String mailOrId)
    {
        return null;
    }

    public static boolean checkCode(String mailOrId)
    {
        return false;
    }

    private static void automaticDeleteCode(String code)
    {
    }
}
