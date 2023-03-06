package main.be.ac.umons.g02.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ConsumptionManagerTest {

    private static String ean = "123456789123456789";
    @BeforeAll
    static void setUp()
    {
        DBTest.setUp();
        DB.getInstance().executeQuery("INSERT INTO counter(ean, contract_id) VALUES('"+ean+"', 1)",false);
    }

    @AfterAll
    static void clean()
    {
       DB.getInstance().executeQuery("DELETE FROM consumption", false);
       DB.getInstance().executeQuery("DELETE FROM counter",false);
    }

    @Test
    void addConsumption()
    {
        ConsumptionManager consumptionManager = new ConsumptionManager();
        ArrayList<Double> values = new ArrayList<>()
        {
            {
                add(10.0);
                add(11.0);
                add(12.0);
            }
        };
        ArrayList<String> dates = new ArrayList<>()
        {
            {
                add("2021-03-05");
                add("2022-03-05");
                add("2023-03-05");
            }
        };
        assertDoesNotThrow(() -> {consumptionManager.addConsumption(ean, values, dates, false);});
        System.out.println("je passe :b");
    }

    @Test
    void getConsumptionOfMonth()
    {
        //TODO voir pour plus tard
    }

    @Test
    void getConsumptions()
    {
        HashMap<String, Double> expected = new HashMap<>()
        {
            {
                put("2021-03-05",10.0);
                put("2022-03-05",11.0);
                put("2023-03-05",12.0);
            }
        };
        assertEquals(expected, new ConsumptionManager().getConsumptions(ean, "2021-03-01", "2023-04-01"));
    }

    @Test
    void changeConsumption()
    {

    }

    @Test
    void deleteAllConsumption() {
    }

    @Test
    void deleteConsumption() {
    }
}