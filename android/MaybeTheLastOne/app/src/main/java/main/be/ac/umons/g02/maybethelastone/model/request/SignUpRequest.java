package main.be.ac.umons.g02.maybethelastone.model.request;

import com.google.gson.annotations.SerializedName;

public class SignUpRequest
{
    @SerializedName("name")
    private String name;

    @SerializedName("mail")
    private String mail;

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
        this.mail = email;
        this.password = password;
        this.code = code;
        this.isClient = isClient;
        this.language = language;
    }
}
