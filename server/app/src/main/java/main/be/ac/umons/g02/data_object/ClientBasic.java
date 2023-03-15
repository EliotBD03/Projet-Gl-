package main.be.ac.umons.g02.data_object;

public class ClientBasic
{
    protected String clientId;
    protected String name;
    protected String mail;

    public ClientBasic(String clientId, String name, String mail)
    {
        this.clientId = clientId;
        this.name = name;
        this.mail = mail;
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getName()
    {
        return name;
    }

    public String getMail()
    {
        return mail;
    }
}
