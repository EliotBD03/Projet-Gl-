package main.be.ac.umons.g02.babawallet.viewmodels;

import main.be.ac.umons.g02.babawallet.model.request.ReinitializePasswordRequest;
import main.be.ac.umons.g02.babawallet.viewmodels.api.APICallback;
import main.be.ac.umons.g02.babawallet.viewmodels.api.APIClient;
import main.be.ac.umons.g02.babawallet.viewmodels.api.query.APIAccount;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel qui s'occupe de la partie mot de passe oublié
 */
public class ForgottenPasswordViewModel implements ViewModel
{
    private ReinitializePasswordRequest reinitializePasswordRequest;
    private static final String PASSWORD_REINITIALIZED = "password reinitialized";
    private static final String PASSWORD_NOT_REINITIALIZED = "password couldn't be reinitialized";

    public ForgottenPasswordViewModel(String email, String code, String newPassword)
    {
        reinitializePasswordRequest = new ReinitializePasswordRequest(email, code, newPassword);
    }

    /**
     * Reset le mot de passe de l'utilisateur
     * @param callback instance permettant d'effectuer un retour de la requête
     */
    @Override
    public void doYourStuff(APICallback callback)
    {
        APIAccount apiAccount = new APIClient().getRetrofit().create(APIAccount.class);
        Call<Void> call = apiAccount.reinitializePassword(reinitializePasswordRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful())
                {
                    callback.onAPIError(PASSWORD_REINITIALIZED);
                    callback.onAPISuccess();
                }
                else
                {
                    callback.onAPIError(PASSWORD_NOT_REINITIALIZED);
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onAPIError(APIClient.MESSAGE_ERROR_CONNECTION);
            }
        });
    }
}
