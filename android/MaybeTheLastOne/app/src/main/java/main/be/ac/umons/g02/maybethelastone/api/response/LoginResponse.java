package main.be.ac.umons.g02.maybethelastone.api.response;
import com.google.gson.annotations.SerializedName;

public class LoginResponse
{
    @SerializedName("role")
    private String role;
    @SerializedName("token")
    private String token;

    public String getRole()
    {
        return role;
    }

    public String getToken() {
        return token;
    }
}
