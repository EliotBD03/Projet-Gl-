package main.be.ac.umons.g02.maybethelastone.data;



import main.be.ac.umons.g02.maybethelastone.api.query.APIAccount;
import main.be.ac.umons.g02.maybethelastone.api.APIClient;
import main.be.ac.umons.g02.maybethelastone.api.query.APIEmail;
import main.be.ac.umons.g02.maybethelastone.api.request.LoginRequest;
import main.be.ac.umons.g02.maybethelastone.api.request.SaveRequest;
import main.be.ac.umons.g02.maybethelastone.api.response.LoginResponse;
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

    private static void handleLoginResponse(LoginResponse loginResponse)
    {
        account = new Account(loginResponse.getToken(), loginResponse.getRole().equals("client"));
    }

    public static boolean checkAccount(String mail, String password)
    {
        APIClient apiClient = new APIClient();
        APIAccount apiAccount = apiClient.getRetrofit().create(APIAccount.class);
        Call<LoginResponse> call = apiAccount.checkAccount(new LoginRequest(mail, password));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful())
                {
                    assert response.body() != null;
                    handleLoginResponse(response.body());
                }
                else
                    System.out.println("Account not found"); //TODO
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                System.out.println("Connection error"); //TODO
            }
        });

        return Account.account != null;
    }

    public static boolean saveAccount(String name, String mail, String password, boolean isClient, String code, String language)
    {
        APIClient apiClient = new APIClient();
        APIAccount apiAccount = apiClient.getRetrofit().create(APIAccount.class);
        Call<LoginResponse> call = apiAccount.saveAccount(new SaveRequest(name, mail, password, code, isClient, language));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful())
                {
                    assert response.body() != null;
                    handleLoginResponse(response.body());
                }
                else
                    System.out.println("Couldn't create account"); //TODO
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                System.out.println("Connection error"); //TODO
            }
        });

        return Account.account != null;
    }

    public static void sendCode(String mail)
    {
        APIClient apiClient = new APIClient();
        APIEmail apiEmail = apiClient.getRetrofit().create(APIEmail.class);
        apiEmail.sendCode(mail);
    }
}
