package main.be.ac.umons.g02.maybethelastone.api;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIEmail
{
    @GET("/log/code?mail={mail}")
    void sendCode(@Path("mail") String mail);
}
