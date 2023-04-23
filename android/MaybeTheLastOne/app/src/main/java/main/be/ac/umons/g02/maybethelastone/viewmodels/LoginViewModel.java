package main.be.ac.umons.g02.maybethelastone.viewmodels;

import android.util.Log;

import main.be.ac.umons.g02.maybethelastone.model.request.LoginRequest;
import main.be.ac.umons.g02.maybethelastone.model.response.LoginResponse;
import main.be.ac.umons.g02.maybethelastone.viewmodels.api.APICallback;
import main.be.ac.umons.g02.maybethelastone.viewmodels.api.APIClient;
import main.be.ac.umons.g02.maybethelastone.viewmodels.api.query.APIAccount;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel qui s'occupe de la partie log in
 */
public class LoginViewModel implements ViewModel
{
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private static final String MESSAGE_ERROR = "Account not found";


    public LoginViewModel(String email, String password)
    {
        loginRequest = new LoginRequest(email,password);
    }

    /**
     * Essaye d'effectuer une connexion avec le compte de l'utilisateur.
     * @param callback instance permettant d'effectuer un retour de la requête
     */
    @Override
    public void doYourStuff(APICallback callback)
    {
        APIAccount apiAccount = new APIClient().getRetrofit().create(APIAccount.class);
        Call<LoginResponse> call = apiAccount.checkAccount(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("TAG", response.code() +"");
                if(response.isSuccessful())
                {
                    assert response.body() != null;
                    loginResponse = new LoginResponse(response.body().getRole(), response.body().getToken());
                    callback.onAPISuccess();
                }
                else
                    callback.onAPIError(MESSAGE_ERROR);

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onAPIError(APIClient.MESSAGE_ERROR_CONNECTION);
            }
        });
    }
}
