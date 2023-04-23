package main.be.ac.umons.g02.babawallet.model.request;

import com.google.gson.annotations.SerializedName;

public class SignUpRequest
{
    @SerializedName("name")
    private String name;

    @SerializedName("mail")
    private String email;

    @SerializedName("pwd")
    private String password;

    @SerializedName("code")
    private String code;

    @SerializedName("is_client")
    private boolean isClient;

    @SerializedName("language")
    private String language;

    public SignUpRequest(String name, String email, String password, String code, boolean isClient, String language)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.code = code;
        this.isClient = isClient;
        this.language = language;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPassword()
    {
        return password;
    }

    public String getCode()
    {
        return code;
    }

    public boolean isClient()
    {
        return isClient;
    }

    public String getLanguage()
    {
        return language;
    }
}
