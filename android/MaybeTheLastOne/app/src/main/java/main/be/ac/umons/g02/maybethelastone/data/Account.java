package main.be.ac.umons.g02.maybethelastone.data;

import main.be.ac.umons.g02.maybethelastone.api.APIAccount;
import main.be.ac.umons.g02.maybethelastone.api.APIClient;
import main.be.ac.umons.g02.maybethelastone.api.APIEmail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Account
{
    private String name;
    private final String token;
    private final boolean isClient;
    private static Account account;

    public Account(String name, String token, boolean isClient)
    {
        this.name = name;
        this.token = token;
        this.isClient = isClient;
    }

    public Account(String token, boolean isClient)
    {
        this.token = token;
        this.isClient = isClient;
    }

    public String getName()
    {
        return name;
    }

    public String getToken()
    {
        return token;
    }

    public boolean isClient()
    {
        return isClient;
    }

    public static boolean checkAccount(String mail, String password)
    {
        APIClient apiClient = new APIClient();
        APIAccount apiAccount = apiClient.getRetrofit().create(APIAccount.class);
        Call<Account> call = apiAccount.checkAccount(mail, password);
        final Account[] account = new Account[1];

        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if(response.isSuccessful())
                    account[0] = response.body();
                else
                    System.out.println("Account not found"); //TODO
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                System.out.println("Connection error"); //TODO
            }
        });
        Account.account = account[0];

        return Account.account != null;
    }

    public static boolean saveAccount(String name, String mail, String password, boolean isClient, String code, String language)
    {
        APIClient apiClient = new APIClient();
        APIAccount apiAccount = apiClient.getRetrofit().create(APIAccount.class);
        Call<Account> call = apiAccount.saveAccount(name, mail, password, code, isClient, language);
        final Account[] account = new Account[1];

        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if(response.isSuccessful())
                    account[0] = response.body();
                else
                    System.out.println("Couldn't create account"); //TODO
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                System.out.println("Connection error"); //TODO
            }
        });
        Account.account = account[0];

        return Account.account != null;
    }

    public static void sendCode(String mail)
    {
        APIClient apiClient = new APIClient();
        APIEmail apiEmail = apiClient.getRetrofit().create(APIEmail.class);
        apiEmail.sendCode(mail);
    }
}
