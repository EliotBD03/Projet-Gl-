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

public class CommonApi extends MyApi implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonApi.class);

    private final CommonDB commonDB = new CommonDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.get("/languages/page").handler(this::getAllLanguages);
        subRouter.get("/languages/current_language").handler(this::getCurrentLanguage);
        subRouter.get("/languages/favourite_language").handler(this::getFavouriteLanguage);
        subRouter.post("/languages/:language").handler(this::addLanguage);
        subRouter.put("/languages/actual_language/:language").handler(this::changeCurrentLanguage);
        subRouter.put("/languages/favourite_language/:language").handler(this::changeFavouriteLanguage);
        subRouter.put("/change_pwd").handler(this::changePassword);
        subRouter.get("/notifications/page").handler(this::getAllNotifications);
        subRouter.post("/notifications/accept_notification/:id_notification").handler(this::acceptNotification);
        subRouter.post("/notifications/refuse_notification/:id_notification").handler(this::refuseNotification);
        subRouter.delete("/notifications/:id_notification").handler(this::deleteNotification);
        subRouter.get("/contracts/:id_contract").handler(this::getContract);
        subRouter.delete("/contracts/:id_contract").handler(this::deleteContract);
        subRouter.get("/consumptions_month").handler(this::getConsumptionOfMonth);
        subRouter.get("/consumptions").handler(this::getConsumptions);
        subRouter.post("/consumptions").handler(this::addConsumption);
        subRouter.put("/consumptions").handler(this::changeConsumption);

        return subRouter;
    }

    private void getAllLanguages(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllLanguages...");

        String id = routingContext.user().principal().getString("id");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<String> allLanguages = commonDB.getLanguageManager().getAllLanguages(id, slice[0], slice[1]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allLanguages", allLanguages)));
    } 

    private void getFavouriteLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("GetFavouriteLanguage...");

        String id = routingContext.user().principal().getString("id");

        String language = commonDB.getLanguageManager().getFavouriteLanguage(id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("language", language)));
    }

    private void getCurrentLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("GetCurrentLanguage...");

        String id = routingContext.user().principal().getString("id");

        String language = commonDB.getLanguageManager().getCurrentLanguage(id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("language", language)));
    }

    private void addLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("AddLanguage...");

        String id = routingContext.user().principal().getString("id");

        final String language = routingContext.request().getParam("language");
        commonDB.getLanguageManager().addLanguage(id, language);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    private void changeCurrentLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeCurrentLanguage...");

        String id = routingContext.user().principal().getString("id");

        final String language = routingContext.request().getParam("language");
        commonDB.getLanguageManager().changeCurrentLanguage(id, language);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    private void changeFavouriteLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeFavouriteLanguage...");

        String id = routingContext.user().principal().getString("id");

        final String language = routingContext.request().getParam("language");
        commonDB.getLanguageManager().changeFavouriteLanguage(id, language);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    private void changePassword(final RoutingContext routingContext)
    {
        LOGGER.info("ChangePassword...");

        final String mail = routingContext.request().getParam("mail");
        final String code = routingContext.request().getParam("code");

        if(App.checkCode(mail, code))
        {
            String id = routingContext.user().principal().getString("id");

            final String newPwd = routingContext.request().getParam("new_pwd");

            commonDB.getLogManager().changePassword(id, newPwd);
            routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json");
        }
        else
            routingContext.response()
                .setStatusCode(401)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "Code incorrect.")));
    }

    private void getAllNotifications(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllNotifications...");

        String id = routingContext.user().principal().getString("id");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<Notification> allNotifications = commonDB.getNotificationManager().getAllNotifications(id, slice[0], slice[1]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allNotifications", allNotifications)));
    }

    private void acceptNotification(final RoutingContext routingContext)
    {
        LOGGER.info("AcceptNotification...");

        final String idNotification = routingContext.request().getParam("id_notification");
        commonDB.getNotificationManager().acceptNotification(idNotification);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    private void refuseNotification(final RoutingContext routingContext)
    {
        LOGGER.info("RefuseNotification...");

        final String idNotification = routingContext.request().getParam("id_notification");
        commonDB.getNotificationManager().refuseNotification(idNotification);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    private void deleteNotification(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteNotification...");

        final String idNotification = routingContext.request().getParam("id_notification");
        commonDB.getNotificationManager().deleteNotification(idNotification);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    private void getContract(final RoutingContext routingContext)
    {
        LOGGER.info("GetContract...");

        final String idContract = routingContext.request().getParam("id_contract");
        ContractFull contract = commonDB.getContractManager().getContract(idContract);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("contract", contract)));
    }

    private void deleteContract(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteContract...");

        final String idContract = routingContext.request().getParam("id_contract");
        commonDB.getContractManager().deleteContract(idContract);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    private void getConsumptionOfMonth(final RoutingContext routingContext)
    {
        LOGGER.info("GetConsumptionOfMonth...");

        final JsonObject body = routingContext.getBodyAsJson();
        final String ean = body.getString("ean");
        final String startDate = body.getString("start_date");
        final String endDate = body.getString("end_date");

        HashMap<String, Integer> listConsumption = commonDB.getConsumptionManager().getConsumptionOfMonth(ean, startDate, endDate);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("listConsumption", listConsumption)));
    }

    private void getConsumptions(final RoutingContext routingContext)
    {
        LOGGER.info("GetConsumption...");

        final JsonObject body = routingContext.getBodyAsJson();
        final String ean = body.getString("ean");
        final String startDate = body.getString("start_date");
        final String endDate = body.getString("end_date");

        HashMap<String, Double> listConsumption = commonDB.getConsumptionManager().getConsumptions(ean, startDate, endDate);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("listConsumption", listConsumption)));
    }

    private void addConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("AddConsumption...");

        final JsonObject body = routingContext.getBodyAsJson();
        final String ean = body.getString("ean");
        final JsonArray arrayListValue = body.getJsonArray("list_value");
        final JsonArray arrayListDate = body.getJsonArray("list_date");
        final boolean forcingChange = body.getBoolean("forcing");

        ArrayList<Double> listValue = new ArrayList<>();
        try
        {
            for(Object value : arrayListValue)
                listValue.add((Double) value);
        }
        catch(NumberFormatException error)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "Le tableau ne contient pas que des nombres.")));
            return;
        }

        ArrayList<String> listDate = new ArrayList<>(arrayListDate.getList());
        boolean valueAlreadyDefine = commonDB.getConsumptionManager().addConsumption(ean, listValue, listDate, forcingChange);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("valueAlreadyDefine", valueAlreadyDefine)));
    }

    private void changeConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeConsumption...");

        final JsonObject body = routingContext.getBodyAsJson();
        final String ean = body.getString("ean");
        final double value = body.getDouble("value");
        final String date = body.getString("date");

        commonDB.getConsumptionManager().changeConsumption(ean, value, date);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }
}
