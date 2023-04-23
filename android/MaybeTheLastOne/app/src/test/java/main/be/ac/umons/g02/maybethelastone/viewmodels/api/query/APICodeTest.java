package main.be.ac.umons.g02.maybethelastone.viewmodels.api.query;


import org.junit.Test;
import main.be.ac.umons.g02.maybethelastone.viewmodels.Code;
import static org.junit.Assert.*;

public class APICodeTest {

    @Test
    public void sendCodeTest()
    {
        assertTrue(Code.sendCode("220101@umons.ac.be"));
    }
}