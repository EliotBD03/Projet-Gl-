package main.be.ac.umons.g02;

import io.vertx.core.Vertx;
import main.be.ac.umons.g02.api.MyApi;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/*
// Les variables d'envirronements:
EMAIL=
EMAILPWD=
IP=
PORT=
PASSPHRASE=
CODETOCLEAN=
CODETODELETECODE=
NAMEDB=
USERNAME=
PWDDB=
*/

/**
 * Classe principale pour lancer le programme
 */
public class App
{
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static HashMap<String, String[]> listCode = new HashMap<>();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String username = "";
    private static String password = "";
    private static String codeToDeleteCode = "";

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

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(formatter);
        String[] data = {code, nowString};
        listCode.put(mail, data);

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
            if(listCode.get(mail)[0].equals(code))
            {
                listCode.remove(mail);
                return true;
            }
        }

        return false;
    }

    /**
     * Méthode qui permet de supprimer les codes qui ont été créés il y a plus de 15 minutes
     * Cette méthode est appelée toutes les 5 minutes par une tâche planifiée d'alwaysdata
     *
     * @param routingContext - Le contexte de la requête
     */
    public static void automaticDeleteCode(RoutingContext routingContext)
    {
        LOGGER.info("AutomaticDeleteCode...");

        Map<String, String> env  = System.getenv();

        if(env.containsKey("CODETODELETECODE"))
            codeToDeleteCode = env.get("CODETODELETECODE");

        String code = routingContext.pathParam("code");

        if(codeToDeleteCode.equals(code))
        {
            if(!listCode.isEmpty())
            {
                LocalDateTime now = LocalDateTime.now();

                for(String key : listCode.keySet())
                {
                    String stringTime = listCode.get(key)[1];
                    LocalDateTime time = LocalDateTime.parse(stringTime, formatter);
                    long minutes = ChronoUnit.MINUTES.between(time, now);

                    if(minutes > 15)
                        listCode.remove(key);
                }
            }

            routingContext.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end();
        }
        else
            routingContext.response()
                .setStatusCode(401)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "You are not authorized to do this operation.")));
    }
}
