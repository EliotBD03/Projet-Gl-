package main.be.ac.umons.g02.babawallet.model.request;

import com.google.gson.annotations.SerializedName;

public class ReinitializePasswordRequest
{
    @SerializedName("mail")
    private String email;

    @SerializedName("code")
    private String code;

    @SerializedName("new_pwd")
    private String newPassword;

    public ReinitializePasswordRequest(String email, String code, String newPassword)
    {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getEmail()
    {
        return email;
    }

    public String getCode()
    {
        return code;
    }

    public String getNewPassword()
    {
        return newPassword;
    }
}
