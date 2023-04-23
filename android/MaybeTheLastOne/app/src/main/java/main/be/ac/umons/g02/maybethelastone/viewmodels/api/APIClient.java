package main.be.ac.umons.g02.maybethelastone.viewmodels.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Crée l'instance retrofit permettant d'envoyer des requêtes HTTP
 */
public class APIClient
{
    public static final String MESSAGE_ERROR_CONNECTION = "you might not have connection";
    private static Retrofit retrofit;
    private final String URL = "https://babawallet.alwaysdata.net/";

    public APIClient()
    {
        if(retrofit == null)
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
    }

    public Retrofit getRetrofit()
    {
        return retrofit;
    }

}
