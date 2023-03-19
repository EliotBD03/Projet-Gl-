package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LanguageManagerTest {

    @BeforeAll
    static void setUp() throws Exception {

        DBTest.setUp();
        new LogManager().saveAccount("test@gmail.com", "password", true, "name", "english");
    }

    @AfterAll
    static void clean()
    {
        new LogManager().deleteAccount("1");
        DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }

    @Test
    @Order(1)
    void getCurrentLanguage()
    {
        assertEquals(new LanguageManager().getCurrentLanguage("1"), "english");
    }

    @Test
    @Order(2)
    void getFavouriteLanguage()
    {
        assertNotEquals(new LanguageManager().getFavouriteLanguage("1"), "dutch");
    }

    @Test
    @Order(3)
    void getAllLanguages()
    {
        assertEquals(((ArrayList<String>) new LanguageManager().getAllLanguages("1", 0 , 1)[1]).get(0), "english");
    }

    @Test
    @Order(4)
    void addLanguage()
    {
        LanguageManager languageManager = new LanguageManager();
        assertNotEquals(languageManager.getCurrentLanguage("1"), "german");
        languageManager.addLanguage("1", "german");
        assertEquals(languageManager.getCurrentLanguage("1"), "german");
    }

    @Test
    @Order(5)
    void changeCurrentLanguage()
    {
        LanguageManager languageManager = new LanguageManager();
        assertNotEquals(languageManager.getCurrentLanguage("1"), "english");
        languageManager.changeCurrentLanguage("1", "english");
        assertEquals(languageManager.getCurrentLanguage("1"), "english");
    }

    @Test
    @Order(6)
    void changeFavouriteLanguage()
    {
        LanguageManager languageManager = new LanguageManager();
        languageManager.changeFavouriteLanguage("1", "dutch");
        assertEquals(languageManager.getFavouriteLanguage("1"), "dutch");
    }
}