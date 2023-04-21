package main.be.ac.umons.g02.maybethelastone.api.request;

import com.google.gson.annotations.SerializedName;

public class SaveRequest
{
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("code")
    private String code;

    @SerializedName("isClient")
    private boolean isClient;

    @SerializedName("language")
    private String language;

    public SaveRequest(String name, String email, String password, String code, boolean isClient, String language)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.code = code;
        this.isClient = isClient;
        this.language = language;
    }
}
