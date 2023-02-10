package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.CommonDB;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class CommonApi extends AbstractToken implements RouterApi
{
    //private final CommonDB commonDB = CommonDB.getInstance();
    private final CommonDB commonDB = new CommonDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        return null;
    }
    
    private void getLanguage(final RoutingContext rountingContext)
    {
    }
    
    private void getFavouriteLanguage(final RoutingContext rountingContext)
    {
    }
    
    private void getAllLanguages(final RoutingContext rountingContext)
    {
    }
    
    private void addLanguage(final RoutingContext rountingContext)
    {
    }
    
    private void changeLanguage(final RoutingContext rountingContext)
    {
    }
    
    private void changeFavouriteLanguage(final RoutingContext rountingContext)
    {
    }
    
    private void changePwd(final RoutingContext rountingContext)
    {
    }
    
    private void getAllNotifications(final RoutingContext rountingContext)
    {
    }
    
    private void acceptNotification(final RoutingContext rountingContext)
    {
    }
    
    private void refuseNotification(final RoutingContext rountingContext)
    {
    }
    
    private void deleteNotification(final RoutingContext rountingContext)
    {
    }
    
    private void getContract(final RoutingContext rountingContext)
    {
    }
    
    private void deleteContract(final RoutingContext rountingContext)
    {
    }
    
    private void getConsumption(final RoutingContext rountingContext)
    {
    }
    
    private void addConsumption(final RoutingContext rountingContext)
    {
    }

    private void changeConsumption(final RoutingContext rountingContext)
    {
    }
}
