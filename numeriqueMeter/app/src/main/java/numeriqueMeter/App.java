package numeriqueMeter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;
import java.util.Random;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe qui permet de simuler un compteur numérique en envoyant des données alétoires pour un certain compteur
 *
 * Cette classe prend la date actuelle et ajoute une consommation toutes les 5 secondes en sachant que 5 secondes représentent 1 jour
 */
public class App
{
    public static final App self = new App();
    public static Random rand = new Random();
    private static Scanner sc = new Scanner(System.in);
    public static String ean = "";
    public static String token = "";
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Méthode principal du programme
     * Elle vérifie les arguments entrés
     * Et lance un TimerTask pour envoyer des données toutes les 5 secondes
     * Cette méthode tourne en continue tant que l'utilisateur n'a pas entré de caractère dans le terminal
     *
     * @param args - Les arguments du programme (le mail, le mot de passe, le code ean)
     */
    public static void main(String[] args)
    {
        if(args.length != 3)
        {
            System.out.println("Le programme a besoin de 3 arguments: [le mail] [le mot de passe] [le code ean]\nVeuillez utiliser cette commande pour ajouter des arguments: gradle run --args='arg1 arg2 arg3'");
            System.exit(1);
        }

        token = self.connect(args[0], args[1]);
        ean = args[2];

        System.out.println("Tu es maintenant connecté");
        System.out.println("Pour quitter le programme: Entrez un caractère + ENTER");

        LocalDate date = LocalDate.now();

        Timer timeNewConso = new Timer();
        timeNewConso.scheduleAtFixedRate(new TimerTask()
                {
                    public void run()
                    {
                        int conso = rand.nextInt(3000) + 1500;
                        String url = "https://babawallet.alwaysdata.net/api/common/consumptions";
                        Map<String, String> parameters = new HashMap<>();
                        parameters.put("ean", ean);
                        parameters.put("list_value", "["+conso+"]");
                        parameters.put("list_date", date.format(formatter));
                        parameters.put("forcing", "true");

                        Tuple<Integer, String> res = self.sendRequestPost(url, parameters, token);
                        if(res.x == 200)
                            System.out.println("La consommation " + conso + " a bien été envoyée.");
                        else
                            self.endProgram();

                        date.plusDays(1);
                    }
                }, 0, 5000);
        while (!sc.hasNext()){}
        self.endProgram();
    }

    /**
     * Méthode pour arrêter le programme depuis plusieurs endroit
     * Elle se déconnecte du serveur grâce à la méthode disconnect
     *
     * @see disconnect
     */
    public void endProgram()
    {
        sc.close();
        if(self.disconnect(token) == 200)
            System.out.println("tu es maintenant déconnecté.");
        System.exit(0);
    }

    /**
     * Méthode qui permet de se connecter au serveur en s'identifiant
     *
     * @param mail - L'adresse mail
     * @param pwd - Le mot de passe
     * @return Le token de connexion
     */
    private String connect(String mail, String pwd)
    {
        String url = "https://babawallet.alwaysdata.net/log/check_account";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("mail", mail);
        parameters.put("pwd", pwd);

        Tuple<Integer, String> res = sendRequestPost(url, parameters, null);
        if(res == null)
        {
            System.out.println("Une erreur s'est produite");
            System.exit(1);
        }

        try
        {
            Map<String, String> result = new ObjectMapper().readValue(res.y, HashMap.class);
            return result.get("token");
        }
        catch(JsonProcessingException error)
        {
            System.out.println("La réponse n'a pas pû être convertir en Map!");
        }

        return "";
    }

    /**
     * Méthode qui permet de se déconnecter du serveur
     *
     * @param token - Le token de connexion
     */
    private int disconnect(String token)
    {
        String url = "https://babawallet.alwaysdata.net/log/disconnect";
        Tuple<Integer, String> res = sendRequestPost(url, null, token);
        if(res == null)
        {
            System.out.println("Une erreur s'est produite");
            System.exit(1);
        }

        return res.x;
    }

    /**
     * Méthode qui permet d'envoyer une requête POST au server
     * 
     * @param stringUrl - L'url du serveur
     * @param parameters - Une Map réprésentant les paramètres dans le body (ce paramètre peut-être null)
     * @param token - Le token de connexion (ce paramètre peut-être null)
     * @return Elle retourne un tuple contenant le code statut et la réponse
     */
    private Tuple<Integer, String> sendRequestPost(String stringUrl, Map<String, String> parameters, String token)
    {
        try
        {
            URL url = new URL(stringUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Origin", "https://babawallet-site.alwaysdata.net");
            if(token != null)
                con.setRequestProperty("Authorization", token);

            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(MapToJson.convert(parameters));
            out.flush();
            out.close();

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                content.append(inputLine);

            in.close();
            con.disconnect();

            return new Tuple<Integer, String>(status, content.toString());
        }
        catch(UnsupportedEncodingException error)
        {
            System.out.println("Le message n'a pas pû être encodé!");
        }
        catch(MalformedURLException error)
        {
            System.out.println("L'URL n'est pas correct!");
        }
        catch(IOException error)
        {
            System.out.println("Erreur de lecture!");
            System.out.println(error);
        }

        return null;
    }

    /**
     * Classe qui permet de transformer un objet Map en une chaine de caractère json
     */
    private class MapToJson
    {
        /**
         * Méthode qui permet de convertir un objet Map en chaine de caractère json
         *
         * @param parameters - Les paramètres de la requête
         * @return Elle retourne le json converti
         */
        public static String convert(Map<String, String> parameters)
        {
            ObjectMapper mapper = new ObjectMapper();
            String json = null;
            try
            {
                json = mapper.writeValueAsString(parameters);
            }
            catch (JsonProcessingException e)
            {
                e.printStackTrace();
            }

            return json;
        }
    }

    /**
     * Classe qui permet de stocker un tuple pour faciliter le retour de la méthode sendRequestPost
     *
     * @see sendRequestPost
     */
    private class Tuple<X, Y>
    { 
        public final X x; 
        public final Y y; 
        public Tuple(X x, Y y)
        { 
            this.x = x; 
            this.y = y; 
        } 
    }
}
