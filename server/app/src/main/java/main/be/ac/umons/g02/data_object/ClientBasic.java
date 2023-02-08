package main.be.ac.umons.g02.data_object;

public class ClientBasic
{
    protected String id_client;
    protected String name;
    protected String mail;

    public ClientBasic(String id_client, String name, String mail)
    {
        this.id_client = id_client;
        this.name = name;
        this.mail = mail;
    }
}
