package main.be.ac.umons.g02;

import java.util.HashMap;
import java.util.Timer;

public class App
{
    private static HashMap<String, String> listCode;
    private static HashMap<String, Timer> listTimerTodeleteCode;
    public static void main(String[] args)
    {
        System.out.println("Hello World");
    }

    public static void sendEmail(String recipient, String subject, String text)
    {
    }

    public static String createCode(String mailOrId)
    {
        return null;
    }

    public static boolean checkCode(String mailOrId)
    {
        return false;
    }

    private static void automaticDeleteCode(String code)
    {
    }
}
