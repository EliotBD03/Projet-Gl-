package main.be.ac.umons.g02.database;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LanguageManagerTest {

    @BeforeAll
    public static void setUp()
    {
        try //If LogManagerTest has already been tested
        {
            new LogManager().saveAccount("test@gmail.com", "password", true, "name", "english");
        }
        catch (Exception e)
        {
            System.out.println("account already exists");
        }
    }

    @Test
    void getLanguage()
    {
        assertEquals(new LanguageManager().getLanguage("1"), "english");
    }

    @Test
    void getFavouriteLanguage()
    {
        assertNotEquals(new LanguageManager().getFavouriteLanguage("1"), "dutch");
    }

    @Test
    void getAllLanguages()
    {
        assertEquals(new LanguageManager().getAllLanguages("1").get(0), "english");
    }

    @Test
    void addLanguage()
    {
        LanguageManager languageManager = new LanguageManager();
        assertNotEquals(languageManager.getLanguage("5"), "german");
        languageManager.addLanguage("5", "german");
        assertEquals(languageManager.getLanguage("5"), "german");
    }

    @Test
    void changeLanguage()
    {
        LanguageManager languageManager = new LanguageManager();
        assertNotEquals(languageManager.getLanguage("1"), "english");
        languageManager.changeLanguage("1", "english");
        assertEquals(languageManager.getLanguage("1"), "english");
    }
}