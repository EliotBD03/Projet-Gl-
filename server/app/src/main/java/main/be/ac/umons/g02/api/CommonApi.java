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

/**
 * Classe qui gère la catégorie commune des requêtes de l'API
 */
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

    /** 
     * Méthode qui utilise le package de base de donnée pour renvoyer une partie de la liste des langues de l'utilisateur
     * Cette méthode utilise la pagination 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void getAllLanguages(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllLanguages...");

        String id = null;
        if(checkParam((id = routingContext.user().principal().getString("id")), routingContext)) return;

        Object[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<String> allLanguages = commonDB.getLanguageManager().getAllLanguages(id, (int) slice[0], (int) slice[1], (String) slice[2]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allLanguages", allLanguages)));
    } 

    /** 
     * Méthode qui utilise le package de base de donnée pour renvoyer la langue favorite de l'utilisateur 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void getFavouriteLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("GetFavouriteLanguage...");

        String id = null;
        if(checkParam((id = routingContext.user().principal().getString("id")), routingContext)) return;

        String language = commonDB.getLanguageManager().getFavouriteLanguage(id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("language", language)));
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour renvoyer la langue actuelle de l'utilisateur
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void getCurrentLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("GetCurrentLanguage...");

        String id = null;
        if(checkParam((id = routingContext.user().principal().getString("id")), routingContext)) return;

        String language = commonDB.getLanguageManager().getCurrentLanguage(id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("language", language)));
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour ajouter une nouvelle langue à la liste des langues de l'utilisateur 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void addLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("AddLanguage...");

        String id = null;
        if(checkParam((id = routingContext.user().principal().getString("id")), routingContext)) return;

        String language = routingContext.pathParam("language");

        commonDB.getLanguageManager().addLanguage(id, language);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour changer la langue actuelle de l'utilisateur 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void changeCurrentLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeCurrentLanguage...");

        String id = null;
        if(checkParam((id = routingContext.user().principal().getString("id")), routingContext)) return;

        String language = routingContext.pathParam("language");

        commonDB.getLanguageManager().changeCurrentLanguage(id, language);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour changer la langue favorite de l'utilisateur 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void changeFavouriteLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeFavouriteLanguage...");

        String id = null;
        if(checkParam((id = routingContext.user().principal().getString("id")), routingContext)) return;

        String language = routingContext.pathParam("language");

        commonDB.getLanguageManager().changeFavouriteLanguage(id, language);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour changer le mot de passe de l'utilisateur
     * Si le code de vérification est incorrect, cette méthode renvoie le code 400 avec une explication  
     *
     * @param - Le context de la requête
     */
    private void changePassword(final RoutingContext routingContext)
    {
        LOGGER.info("ChangePassword...");

        String mail = routingContext.pathParam("mail");
        String code = routingContext.pathParam("code");

        if(App.checkCode(mail, code))
        {
            String id = null;
            if(checkParam((id = routingContext.user().principal().getString("id")), routingContext)) return;

            String newPwd = routingContext.pathParam("new_pwd");

            commonDB.getLogManager().changePassword(id, newPwd);
            routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end();
        }
        else
            routingContext.response()
                .setStatusCode(400)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "Invalid code.")));
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour renvoyer une partie de la liste des notifications de l'utilisateur
     * Cette méthode utilise la pagination 
     *
     * @param - Le context de la requête
     * @see NotificationManager
     */
    private void getAllNotifications(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllNotifications...");

        String id = null;
        if(checkParam((id = routingContext.user().principal().getString("id")), routingContext)) return;

        Object[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<Notification> allNotifications = commonDB.getNotificationManager().getAllNotifications(id, (int) slice[0], (int) slice[1], (String) slice[2]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allNotifications", allNotifications)));
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour faire passer la notification qui a été acceptée 
     * La traitement est différent selon la requête, tout se passe côté base de donnée
     *
     * @param - Le context de la requête
     * @see NotificationManager
     */
    private void acceptNotification(final RoutingContext routingContext)
    {
        LOGGER.info("AcceptNotification...");

        String idNotification = routingContext.pathParam("id_notification");

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String ean = null;
        if(checkParam((ean = body.getString("ean")), routingContext)) return;

        String address = null;
        if(checkParam((address = body.getString("address")), routingContext)) return;

        commonDB.getNotificationManager().acceptNotification(idNotification, ean, address);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour supprimer la notification et prévenir l'émetteur
     *
     * @param - Le context de la requête
     * @see NotificationManager
     */
    private void refuseNotification(final RoutingContext routingContext)
    {
        LOGGER.info("RefuseNotification...");

        String idNotification = routingContext.pathParam("id_notification");

        commonDB.getNotificationManager().refuseNotification(idNotification);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour juste supprimer la notification 
     *
     * @param - Le context de la requête
     * @see NotificationManager
     */
    private void deleteNotification(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteNotification...");

        String idNotification = routingContext.pathParam("id_notification");

        commonDB.getNotificationManager().deleteNotification(idNotification);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour renvoyer un contrat en particulier
     *
     * @param - Le context de la requête
     * @see ContractManager
     */
    private void getContract(final RoutingContext routingContext)
    {
        LOGGER.info("GetContract...");

        String idContract = routingContext.pathParam("id_contract");

        ContractFull contract = commonDB.getContractManager().getContract(idContract);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("contract", contract)));
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour supprimer un contrat
     *
     * @param - Le context de la requête
     * @see ContractManager
     */
    private void deleteContract(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteContract...");

        String idContract = routingContext.pathParam("id_contract");

        commonDB.getContractManager().deleteContract(idContract);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour renvoyer les données de consommation par moi
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void getConsumptionOfMonth(final RoutingContext routingContext)
    {
        LOGGER.info("GetConsumptionOfMonth...");

        String ean = null;
        if(checkParam((ean = routingContext.request().getHeader("tete")), routingContext)) return;

        String startDate = null;
        if(checkParam((startDate = routingContext.request().getHeader("start_date")), routingContext)) return;

        String endDate = null;
        if(checkParam((endDate = routingContext.request().getHeader("end_date")), routingContext)) return;

        HashMap<String, Double> listConsumption = commonDB.getConsumptionManager().getConsumptionOfMonth(ean, startDate, endDate);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("listConsumption", listConsumption)));
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour renvoyer toutes les données de consommations sur un moi 
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void getConsumptions(final RoutingContext routingContext)
    {
        LOGGER.info("GetConsumption...");

        String ean = null;
        if(checkParam((ean = routingContext.request().getHeader("ean")), routingContext)) return;

        String startDate = null;
        if(checkParam((startDate = routingContext.request().getHeader("start_date")), routingContext)) return;

        String endDate = null;
        if(checkParam((endDate = routingContext.request().getHeader("end_date")), routingContext)) return;

        HashMap<String, Double> listConsumption = commonDB.getConsumptionManager().getConsumptions(ean, startDate, endDate);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("listConsumption", listConsumption)));
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour ajouter une liste de données de consommations par rapport à un contrat
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void addConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("AddConsumption...");

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String ean = null;
        if(checkParam((ean = body.getString("ean")), routingContext)) return;

        JsonArray arrayListValue = null;
        JsonArray arrayListDate = null;
        boolean forcingChange = false;

        try
        {
            if(checkParam((arrayListValue = body.getJsonArray("list_value")), routingContext)) return;
            if(checkParam((arrayListDate = body.getJsonArray("list_date")), routingContext)) return;
            if(checkParam((forcingChange = body.getBoolean("forcing")), routingContext)) return;
        }
        catch(ClassCastException error)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "The query is missing information.")));
            return;
        }

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
                            .put("error", "The table does not only contain numbers.")));
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

    /** 
     * Méthode qui utilise le package de base de donnée pour changer une donnée de consommation par rapport à un contrat
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void changeConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeConsumption...");

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String ean = null;
        if(checkParam((ean = body.getString("ean")), routingContext)) return;

        double value = 0;

        try
        {
            if(checkParam((value = body.getDouble("value")), routingContext)) return;
        }
        catch(ClassCastException error)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "The query is missing information.")));
            return;
        }

        String date = null;
        if(checkParam((date = body.getString("date")), routingContext)) return;

        commonDB.getConsumptionManager().changeConsumption(ean, value, date);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end();
    }
}
