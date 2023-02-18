package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
    void getCurrentLanguage()
    {
        assertEquals(new LanguageManager().getCurrentLanguage("1"), "english");
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
        assertNotEquals(languageManager.getCurrentLanguage("5"), "german");
        languageManager.addLanguage("5", "german");
        assertEquals(languageManager.getCurrentLanguage("5"), "german");
    }

    @Test
    void changeCurrentLanguage()
    {
        LanguageManager languageManager = new LanguageManager();
        assertNotEquals(languageManager.getCurrentLanguage("1"), "english");
        languageManager.changeCurrentLanguage("1", "english");
        assertEquals(languageManager.getCurrentLanguage("1"), "english");
    }

    @Test
    void changeFavouriteLanguage()
    {
        LanguageManager languageManager = new LanguageManager();
        languageManager.changeFavouriteLanguage("1", "dutch");
        assertEquals(languageManager.getFavouriteLanguage("1"), "dutch");
    }
}