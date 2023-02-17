package main.be.ac.umons.g02.api;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractToken
{
    private static HashMap<String, String> listToken = new HashMap<>();
    private static String chaine = "0123456789azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN";

    protected void sendMessageError(final RoutingContext routingContext)
    {
    }

    protected String createToken(String id) 
    {   
        String token = ""; 
        Random rand = new Random();

        for(int i = 0; i < 10; i++)
            token += chaine.charAt(rand.nextInt(chaine.length()));

        listToken.put(token, id);
        automaticDeleteToken(token);

        return token;
    }   

    protected String checkToken(String token)
    {
        if(listToken.containsKey(token))
            return listToken.get(token);

        return null;
    }

    protected void deleteToken(String token)
    {
        listToken.remove(token);
    }

    private void automaticDeleteToken(String token)
    {
        Timer timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                if(listToken.containsKey(token))
                    listToken.remove(token);
                timer.cancel();
            }
        };
        
        timer.schedule(task, 15 * 60 * 1000);
    }
}
