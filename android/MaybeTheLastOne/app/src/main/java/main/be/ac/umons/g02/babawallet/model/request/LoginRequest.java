package main.be.ac.umons.g02.babawallet.model.request;
import com.google.gson.annotations.SerializedName;

public class LoginRequest
{
    @SerializedName("mail")
    private String email;
    @SerializedName("pwd")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

