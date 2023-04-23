package main.be.ac.umons.g02.maybethelastone.viewmodels;

import main.be.ac.umons.g02.maybethelastone.model.request.SignUpRequest;
import main.be.ac.umons.g02.maybethelastone.model.response.SignUpResponse;
import main.be.ac.umons.g02.maybethelastone.viewmodels.api.APICallback;
import main.be.ac.umons.g02.maybethelastone.viewmodels.api.APIClient;
import main.be.ac.umons.g02.maybethelastone.viewmodels.api.query.APIAccount;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel qui s'occupe de la partie creation de compte
 */
public class SignUpViewModel implements ViewModel
{
    private SignUpRequest signUpRequest;
    private SignUpResponse signUpResponse;
    private static final String MESSAGE_ERROR_CODE = "incorrect code";
    private static final String MESSAGE_ERROR_ALREADY_EXISTS = "the account already exists";

    public SignUpViewModel(String name, String email, String password, String code, boolean isClient, String language)
    {
        signUpRequest = new SignUpRequest(name, email, password, code, isClient, language);
    }

    /**
     * Envoie une requête essayant de créer un compte avec les paramètres définis dans le constructeur
     * @param callback instance permettant d'effectuer un retour de la requête
     */
    @Override
    public void doYourStuff(APICallback callback)
    {
        APIAccount apiAccount = new APIClient().getRetrofit().create(APIAccount.class);
        Call<SignUpResponse> call = apiAccount.saveAccount(signUpRequest);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if(response.isSuccessful())
                {
                    assert response.body() != null;
                    signUpResponse = new SignUpResponse(response.body().getRole(), response.body().getToken());
                    callback.onAPISuccess();
                }
                else
                {
                    switch(response.code())
                    {
                        case 400:
                            callback.onAPIError(MESSAGE_ERROR_CODE);
                        case 503:
                            callback.onAPIError(MESSAGE_ERROR_ALREADY_EXISTS);
                    }
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                callback.onAPIError(APIClient.MESSAGE_ERROR_CONNECTION);
            }
        });
    }

}
