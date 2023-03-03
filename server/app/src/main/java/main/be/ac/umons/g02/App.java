package main.be.ac.umons.g02;

import java.util.HashMap;
import io.vertx.core.Vertx;
import main.be.ac.umons.g02.api.MyApi;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
// Les variables d'envirronements:
EMAIL=
EMAILPWD=
IP=
PORT=
PASSPHRASE=
NAMEDB=
USERNAME=
PWDDB=
*/

/**
 * Classe principale pour lancer le programme
 */
public class App
{
    private static HashMap<String, String> listCode = new HashMap<>();
    private static String username = "";
    private static String password = "";

    /**
     * Méthode pour lancer le verticle
     *
     * @param args - Les arguments au lancement du programme
     * @see MyApi
     */
    public static void main(String[] args)
    {
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MyApi());
    }

    /**
     * Méthode qui permet d'envoyer un mail à un utilisateur
     * Cela peut se produire quand un utilisateur souhaite créer un compte, changer ou rénitialiser son mot de passe
     *
     * @param recipient - L'utilisateur a qui on doit envoyer le mail
     * @param subjet - Le sujet du mail
     * @param text - Le contenue du mail
     */
    public static void sendEmail(String recipient, String subject, String text)
    {
        Map<String, String> env  = System.getenv();


        if(env.containsKey("EMAIL") && env.containsKey("EMAILPWD"))
        {
            username = env.get("EMAIL");
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

    /**
     * Méthode qui permet de créer un code pour changer ou rénitialiser son mot de passe
     *
     * @param mail - Le mail de l'utilisateur
     */
    public static String createCode(String mail)
    {
        String code = "";
        Random rand = new Random();

        for(int i = 0; i < 5; i++)
        {
            int number = rand.nextInt(10);
            code += number;
        }

        listCode.put(mail, code);
        automaticDeleteCode(mail);

        return code;
    }

    /**
     * Méthode qui permet de vérifier que l'utilisateur a entré le bon code
     * pour changer ou rénitialiser son mot de passe 
     *
     * @param mail - Le mail de l'utilisateur
     * @param code - Le code qu'il a normalement recu par mail
     */
    public static boolean checkCode(String mail, String code)
    {
        if(listCode.containsKey(mail))
        {
            if(listCode.get(mail) == code)
            {
                listCode.remove(mail);
                return true;
            }
        }

        return false;
    }

    /**
     * Méthode qui permet de supprimer le code après un certain pour qu'il ne serve plus a rien 
     *
     * @param mail - Le mail de l'utilisateur
     */
    private static void automaticDeleteCode(String mail)
    {
        Timer timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                if(listCode.containsKey(mail))
                    listCode.remove(mail);
                timer.cancel();
            }
        };

        timer.schedule(task, 3 * 60 * 1000);
    }
}
