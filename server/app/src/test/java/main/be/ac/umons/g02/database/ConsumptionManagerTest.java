package main.be.ac.umons.g02.database;


import main.be.ac.umons.g02.data_object.ProposalFull;
import main.be.ac.umons.g02.data_object.WalletBasic;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConsumptionManagerTest {

    private static String ean = "123456789123456789";
    @BeforeAll
    static void setUp() throws Exception {
        DBTest.setUp();
        new LogManager().saveAccount("mail", "password", true, "juju", "english");
        new LogManager().saveAccount("providermail", "password", false, "jiji", "dutch");
        new WalletManager().createWallet(new WalletBasic("address", "wallet", "1", "juju"));
        DB.getInstance().executeQuery("INSERT INTO wallet_contract(address, contract_id) VALUES('address', 1)",false);
        DB.getInstance().executeQuery("INSERT INTO counter(ean, contract_id) VALUES('"+ean+"', 1)",false);
        new ProposalManager().addProposal(new ProposalFull("2", "jiji", "electricity", "100", "elec"));
        new ContractManager().createContract("elec", "785", "2", "address", "1");
    }

    @AfterAll
    static void clean()
    {
        DB.getInstance().executeQuery("DELETE FROM contract", false);
        DB.getInstance().executeQuery("DELETE FROM counter", false);
        DB.getInstance().executeQuery("DELETE FROM provider_contract",false);
        DB.getInstance().executeQuery("DELETE FROM wallet_contract",false);
        new ProposalManager().deleteProposal("elec", "2");
       DB.getInstance().executeQuery("DELETE FROM consumption", false);
       new WalletManager().deleteWallet("address");
       new LogManager().deleteAccount("1");
       new LogManager().deleteAccount("2");
       DB.getInstance().executeQuery("ALTER TABLE contract AUTO_INCREMENT = 1", false);
       DB.getInstance().executeQuery("ALTER TABLE wallet_contract AUTO_INCREMENT = 1", false);
       DB.getInstance().executeQuery("ALTER TABLE provider_contract AUTO_INCREMENT = 1", false);
       DB.getInstance().executeQuery("ALTER TABLE user AUTO_INCREMENT = 1", false);
    }


    @Test
    @Order(1)
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
        consumptionManager.addConsumption(ean, values, dates, false);
        assertDoesNotThrow(() -> {consumptionManager.addConsumption(ean, values, dates, false);});
    }

    @Test
    @Order(2)
    void getConsumptionOfMonth()
    {
        //TODO
    }

    @Test
    @Order(3)
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
    @Order(4)
    void changeConsumption()
    {
        ConsumptionManager consumptionManager = new ConsumptionManager();
        consumptionManager.changeConsumption(ean, 15.0, "2023-03-05");
        assertEquals(consumptionManager.getConsumptions(ean, "2023-00-00", "2023-12-31").get("2023-03-05"), 15.0);
    }

    @Test
    @Order(5)
    void deleteConsumption()
    {
        ConsumptionManager consumptionManager = new ConsumptionManager();
        consumptionManager.deleteConsumption(ean, "2023-03-05");
        HashMap<String, Double> expected = new HashMap<>()
        {
            {
                put("2021-03-05",10.0);
                put("2022-03-05",11.0);
            }
        };
        assertEquals(consumptionManager.getConsumptions(ean, "2021-00-00", "2024-00-00"),expected);
    }
    @Test
    @Order(6)
    void deleteAllConsumption()
    {
        ConsumptionManager consumptionManager = new ConsumptionManager();
        consumptionManager.deleteAllConsumptions(ean);
        assertEquals(consumptionManager.getConsumptions(ean, "2021-00-00", "2024-00-00"), new HashMap<String, Double>());
    }
}