package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.App;
import main.be.ac.umons.g02.database.CommonDB;
import main.be.ac.umons.g02.data_object.Notification;
import main.be.ac.umons.g02.data_object.ContractFull;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.ArrayList;
import java.util.HashMap;

public class CommonApi extends AbstractToken implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonApi.class);

    private MyApi api;
    private final CommonDB commonDB = new CommonDB();

    public CommonApi(MyApi api)
    {
        this.api = api;
    }

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.get("/:token/languages/page").handler(this::getAllLanguages);
        subRouter.get("/:token/languages/language").handler(this::getLanguage);
        subRouter.get("/:token/languages/favourite_language").handler(this::getFavouriteLanguage);
        subRouter.post("/:token/languages/:language").handler(this::addLanguage);
        subRouter.put("/:token/languages/actual_language/:language").handler(this::changeCurrentLanguage);
        subRouter.put("/:token/languages/favourite_language/:language").handler(this::changeFavouriteLanguage);
        subRouter.put("/:token/change_pwd").handler(this::changePassword);
        subRouter.get("/:token/notifications/page").handler(this::getAllNotifications);
        subRouter.post("/:token/notifications/accept_notification/:id_notification").handler(this::acceptNotification);
        subRouter.post("/:token/notifications/refuse_notification/:id_notification").handler(this::refuseNotification);
        subRouter.delete("/:token/notifications/:id_notification").handler(this::deleteNotification);
        subRouter.get("/:token/contracts/:id_contract").handler(this::getContract);
        subRouter.delete("/:token/contracts/:id_contract").handler(this::deleteContract);
        subRouter.get("/:token/consumptions_month").handler(this::getConsumptionOfMonth);
        subRouter.get("/:token/consumptions").handler(this::getConsumption);
        subRouter.post("/:token/consumptions").handler(this::addConsumption);
        subRouter.put("/:token/consumptions").handler(this::changeConsumption);

        return null;
    }
    
    private void getAllLanguages(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllLanguages...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String stringPage = routingContext.request().getParam("page");
        int page = api.convertStringToInt(routingContext, stringPage);

        if(page == 0)
            return;

        final String stringLimit = routingContext.request().getParam("limit");
        int limit = api.getLimit(stringLimit);

        page = (page -1) * limit;

        ArrayList<String> allLanguages = commonDB.getLanguageManager().getAllLanguages(id, page, limit);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("allLanguages", allLanguages);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    } 

    private void getFavouriteLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("GetFavouriteLanguage...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        String language = commonDB.getLanguageManager().getFavouriteLanguage(id);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("language", language);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }
    
    private void getLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("GetLanguage...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        String language = commonDB.getLanguageManager().getLanguage(id);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("language", language);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }
    
    private void addLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("AddLanguage...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String language = routingContext.request().getParam("language");
        commonDB.getLanguageManager().addLanguage(id, language);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }
    
    private void changeCurrentLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeCurrentLanguage...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String language = routingContext.request().getParam("language");
        commonDB.getLanguageManager().changeLanguage(id, language);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }
    
    private void changeFavouriteLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeFavouriteLanguage...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String language = routingContext.request().getParam("language");
        commonDB.getLanguageManager().changeFavouriteLanguage(id, language);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }
    
    private void changePassword(final RoutingContext routingContext)
    {
        LOGGER.info("ChangePassword...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String mail = routingContext.request().getParam("mail");
        final String new_pwd = routingContext.request().getParam("new_pwd");
        final String code = routingContext.request().getParam("code");

        if(App.checkCode(mail, code))
        {
            commonDB.getLogManager().changePassword(id, new_pwd);
            routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
        }
        else
            api.sendMessageError(routingContext, "Mauvais code.");
    }
    
    private void getAllNotifications(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllNotifications...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String stringPage = routingContext.request().getParam("page");
        int page = api.convertStringToInt(routingContext, stringPage);

        if(page == 0)
            return;

        final String stringLimit = routingContext.request().getParam("limit");
        int limit = api.getLimit(stringLimit);

        page = (page -1) * limit;

        ArrayList<Notification> allNotifications = commonDB.getNotificationManager().getAllNotifications(id, page, limit);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("allNotifications", allNotifications);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }
    
    private void acceptNotification(final RoutingContext routingContext)
    {
        LOGGER.info("AcceptNotification...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_notification = routingContext.request().getParam("id_notification");
        commonDB.getNotificationManager().acceptNotification(id_notification);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }
    
    private void refuseNotification(final RoutingContext routingContext)
    {
        LOGGER.info("RefuseNotification...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_notification = routingContext.request().getParam("id_notification");
        commonDB.getNotificationManager().refuseNotification(id_notification);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }
    
    private void deleteNotification(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteNotification...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_notification = routingContext.request().getParam("id_notification");
        commonDB.getNotificationManager().deleteNotification(id_notification);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }
    
    private void getContract(final RoutingContext routingContext)
    {
        LOGGER.info("GetContract...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_contract = routingContext.request().getParam("id_contract");
        ContractFull contract = commonDB.getContractManager().getContract(id_contract);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("contract", contract);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }
    
    private void deleteContract(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteContract...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_contract = routingContext.request().getParam("id_contract");
        commonDB.getContractManager().deleteContract(id_contract);

        routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet.api");
    }
    
    private void getConsumptionOfMonth(final RoutingContext routingContext)
    {
        LOGGER.info("GetConsumptionOfMonth...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final JsonObject body = routingContext.getBodyAsJson();
		final String ean = body.getString("ean");
		final String startDate = body.getString("startDate");
		final String endDate = body.getString("endDate");

        HashMap<String, Integer> listConsumption = commonDB.getConsumptionManager().getConsumptionOfMonth(ean, startDate, endDate);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("listConsumption", listConsumption);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void getConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("GetConsumption...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final JsonObject body = routingContext.getBodyAsJson();
		final String ean = body.getString("ean");
		final String startDate = body.getString("startDate");
		final String endDate = body.getString("endDate");

        HashMap<String, Integer> listConsumption = commonDB.getConsumptionManager().getConsumption(ean, startDate, endDate);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("listConsumption", listConsumption);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }
    
    private void addConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("AddConsumption...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final JsonObject body = routingContext.getBodyAsJson();
		final String ean = body.getString("ean");
		final JsonArray arrayListValue = body.getJsonArray("listValue");
		final JsonArray arrayListDate = body.getJsonArray("listDate");
		final boolean forcingChange = body.getBoolean("forcing");

        ArrayList<Double> listValue = new ArrayList<>();
        try
        {
            for(Object value : arrayListValue)
                listValue.add((Double) value);
        }
        catch(NumberFormatException error)
        {
            api.sendMessageError(routingContext, "Le tableau ne contient pas que des nombres.");
            return;
        }

        ArrayList<String> listDate = new ArrayList<>(arrayListDate.getList());
        boolean valueAlreadyDefine = commonDB.getConsumptionManager().addConsumption(ean, listValue, listDate, forcingChange);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("valueAlreadyDefine", valueAlreadyDefine);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void changeConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeConsumption...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final JsonObject body = routingContext.getBodyAsJson();
		final String ean = body.getString("ean");
		final double value = body.getDouble("value");
		final String date = body.getString("date");

        commonDB.getConsumptionManager().changeConsumption(ean, value, date);

        routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }
}
