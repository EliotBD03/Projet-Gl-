package main.be.ac.umons.g02.maybethelastone.viewmodels.api.query;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APICode
{
    @GET("/log/code")
    Call<Void> sendCode(@Query("mail") String mail);
}
