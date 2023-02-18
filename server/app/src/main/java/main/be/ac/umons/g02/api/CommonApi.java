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
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class CommonApi extends AbstractToken implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonApi.class);

    private final CommonDB commonDB = new CommonDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.get("/:token/languages").handler(this::getAllLanguages);
        subRouter.get("/:token/languages/language").handler(this::getLanguage);
        subRouter.get("/:token/languages/favourite_language").handler(this::getFavouriteLanguage);
        subRouter.post("/:token/languages").handler(this::addLanguage);
        subRouter.put("/:token/languages/language").handler(this::changeLanguage);
        subRouter.put("/:token/languages/favourite_language").handler(this::changeFavouriteLanguage);
        subRouter.put("/:token/change_pwd").handler(this::changePwd);
        subRouter.get("/:token/notifications").handler(this::getAllNotifications);
        subRouter.post("/:token/notifications/accept_notification").handler(this::acceptNotification);
        subRouter.post("/:token/notifications/refuse_notification").handler(this::refuseNotification);
        subRouter.delete("/:token/notifications/:id_notification").handler(this::deleteNotification);
        subRouter.get("/:token/contracts/:id_contract").handler(this::getContract);
        subRouter.delete("/:token/contracts/:id_contract").handler(this::deleteContract);
        subRouter.get("/:token/consumptions").handler(this::getConsumption);
        subRouter.post("/:token/consumptions").handler(this::addConsumption);
        subRouter.put("/:token/consumptions").handler(this::changeConsumption);

        return null;
    }
    
    private void getFavouriteLanguage(final RoutingContext routingContext)
    {
    }
    
    private void getLanguage(final RoutingContext routingContext)
    {
    }

    private void getAllLanguages(final RoutingContext routingContext)
    {
    }
    
    private void addLanguage(final RoutingContext routingContext)
    {
    }
    
    private void changeLanguage(final RoutingContext routingContext)
    {
    }
    
    private void changeFavouriteLanguage(final RoutingContext routingContext)
    {
    }
    
    private void changePwd(final RoutingContext routingContext)
    {
    }
    
    private void getAllNotifications(final RoutingContext routingContext)
    {
    }
    
    private void acceptNotification(final RoutingContext routingContext)
    {
    }
    
    private void refuseNotification(final RoutingContext routingContext)
    {
    }
    
    private void deleteNotification(final RoutingContext routingContext)
    {
    }
    
    private void getContract(final RoutingContext routingContext)
    {
    }
    
    private void deleteContract(final RoutingContext routingContext)
    {
    }
    
    private void getConsumption(final RoutingContext routingContext)
    {
    }
    
    private void addConsumption(final RoutingContext routingContext)
    {
    }

    private void changeConsumption(final RoutingContext routingContext)
    {
    }
}
