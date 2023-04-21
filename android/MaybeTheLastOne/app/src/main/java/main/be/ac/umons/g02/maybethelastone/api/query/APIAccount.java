package main.be.ac.umons.g02.maybethelastone.api.query;

import main.be.ac.umons.g02.maybethelastone.api.request.LoginRequest;
import main.be.ac.umons.g02.maybethelastone.api.request.SaveRequest;
import main.be.ac.umons.g02.maybethelastone.api.response.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIAccount
{
    @POST("/log/check_account")
    Call<LoginResponse> checkAccount(@Body LoginRequest loginRequest);

    @POST("/log/save_account")
    Call<LoginResponse> saveAccount(@Body SaveRequest saveRequest);


}
