package main.be.ac.umons.g02.maybethelastone.api;

import main.be.ac.umons.g02.maybethelastone.data.Account;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIAccount
{
    @POST("/log/check_account")
    Call<Account> checkAccount(@Body String mail, @Body String password);

    @POST("/log/save_account")
    Call<Account> saveAccount(@Body String name, @Body String mail, @Body String password,@Body String code,@Body boolean isClient,@Body String language);


}
