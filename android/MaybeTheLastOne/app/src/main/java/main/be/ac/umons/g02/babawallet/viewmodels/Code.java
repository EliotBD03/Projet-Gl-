package main.be.ac.umons.g02.babawallet.viewmodels;

import java.util.HashMap;

import main.be.ac.umons.g02.babawallet.viewmodels.api.APICallback;
import main.be.ac.umons.g02.babawallet.viewmodels.api.APIClient;
import main.be.ac.umons.g02.babawallet.viewmodels.api.query.APICode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel qui s'occupe de la partie requête concernant l'envoie d'un code
 */
public class Code implements ViewModel
{
    private String email;
    private final String MESSAGE_CODE_SENT= "a code has been sent";
    private final String MESSAGE_CODE_NOT_SENT="the code couldn't be sent";
    private static HashMap<Integer, String> countSpamMessage;
    private static int countCodeSent = 0;

    public Code(String email)
    {
        countSpamMessage = new HashMap<Integer, String>()
        {
            {
                put(0, "a code has been sent");
                put(1, "Are you spamming bruh ?");
                put(2, "Seriously, it's not funny...");
                put(3, "user blacklisted");
            }
        };
        this.email = email;
    }

    /**
     * Envoie un code à l'adresse : email
     * @param callback instance permettant d'effectuer un retour de la requête
     */
    @Override
    public void doYourStuff(APICallback callback)
    {
        APICode apiCode = new APIClient().getRetrofit().create(APICode.class);
        Call<Void> call = apiCode.sendCode(email);
        if(countCodeSent <= 15)
        {
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response)
                {
                    if(response.isSuccessful())
                    {
                        countCodeSent ++;
                        callback.onAPIError(countSpamMessage.get(countCodeSent / 5));
                    }
                    else
                        callback.onAPIError(MESSAGE_CODE_NOT_SENT);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onAPIError(APIClient.MESSAGE_ERROR_CONNECTION);
                }
            });
        }
    }
}
