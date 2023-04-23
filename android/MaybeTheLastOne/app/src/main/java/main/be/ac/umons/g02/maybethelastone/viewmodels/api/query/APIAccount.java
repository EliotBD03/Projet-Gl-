package main.be.ac.umons.g02.maybethelastone.viewmodels.api.query;

import main.be.ac.umons.g02.maybethelastone.model.request.LoginRequest;
import main.be.ac.umons.g02.maybethelastone.model.request.ReinitializePasswordRequest;
import main.be.ac.umons.g02.maybethelastone.model.request.SignUpRequest;
import main.be.ac.umons.g02.maybethelastone.model.response.LoginResponse;
import main.be.ac.umons.g02.maybethelastone.model.response.SignUpResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface APIAccount
{
    @POST("/log/check_account")
    Call<LoginResponse> checkAccount(@Body LoginRequest loginRequest);

    @POST("/log/save_account")
    Call<SignUpResponse> saveAccount(@Body SignUpRequest saveRequest);

    @PUT("/log/renitialize_pwd")
    Call<Void> reinitializePassword(@Body ReinitializePasswordRequest reinitializePasswordRequest);


}
