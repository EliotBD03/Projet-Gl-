package main.be.ac.umons.g02.maybethelastone.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient
{
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
