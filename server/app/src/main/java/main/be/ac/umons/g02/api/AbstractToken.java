package main.be.ac.umons.g02.api;

import java.util.HashMap;
import java.util.Timer;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public abstract class AbstractToken
{
    private static HashMap<String, String> listToken = new HashMap<>();
    private static HashMap<String, Timer> listTimerToDeleteToken = new HashMap<>();

    protected void sendMessageError(final RoutingContext routingContext)
    {
    }

    protected String createToken(String id)
    {
        return "";
    }

    protected String checkToken(String token)
    {
        return "";
    }

    protected void deleteToken(String token)
    {
    }

    private void automaticDeleteToken(String token)
    {
    }
}
