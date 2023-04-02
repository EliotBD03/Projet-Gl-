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

public class App
{
    public static void main(String[] args)
    {
        if(args.length != 3)
        {
            System.out.println("Le programme a besoin de 3 arguments: [le mail] [le mot de passe] [le code ean]\nVeuillez utiliser cette commande pour ajouter des arguments: gradle run --args='arg1 arg2 arg3'");
            System.exit(1);
        }

        App self = new App();

        String token = self.connect(args[0], args[1]);

        System.out.println("Tu es maintenant connecté");

        //url = "https://babawallet.alwaysdata.net/api/common/consumptions";
    }

    private String connect(String mail, String pwd)
    {
        String url = "https://babawallet.alwaysdata.net/log/check_account";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("mail", mail);
        parameters.put("pwd", pwd);

        Tuple<Integer, String> res = sendRequestPost(url, parameters);
        if(res == null)
        {
            System.out.println("Une erreur s'est produite");
            System.exit(1);
        }

        System.out.println(res.y);

        return "";
    }

    private void disconnect(String token)
    {
        String url = "https://babawallet.alwaysdata.net/log/check_account";
        Tuple<Integer, String> res = sendRequestPost(url, null);
        if(res == null)
        {
            System.out.println("Une erreur s'est produite");
            System.exit(1);
        }
    }

    private Tuple<Integer, String> sendRequestPost(String stringUrl, Map<String, String> parameters)
    {
        try
        {
            URL url = new URL(stringUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
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
        }

        return null;
    }

    private class ParameterStringBuilder
    {
        public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet())
            {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
        }
    }

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
