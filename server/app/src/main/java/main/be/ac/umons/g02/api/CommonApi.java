package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.App;
import main.be.ac.umons.g02.api.MyApi;
import main.be.ac.umons.g02.database.CommonDB;
import main.be.ac.umons.g02.database.WalletManager;
import main.be.ac.umons.g02.data_object.Notification;
import main.be.ac.umons.g02.data_object.ContractFull;
import main.be.ac.umons.g02.data_object.WalletFull;

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
import java.util.TreeMap;
import io.vertx.core.http.HttpHeaders;
import java.lang.Math;
import java.util.Random;

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
        subRouter.get("/notifications/page").handler(this::getAllNotifications);
        subRouter.post("/notifications/accept_notification/:id_notification").handler(this::acceptNotification);
        subRouter.post("/notifications/refuse_notification/:id_notification").handler(this::refuseNotification);
        subRouter.delete("/notifications/:id_notification").handler(this::deleteNotification);
        subRouter.get("/contracts/:id_contract").handler(this::getContract);
        subRouter.get("/contracts/type_of_energy/:id_contract").handler(this::getTypeOfEnergyOfContract);
        subRouter.delete("/contracts/:id_contract").handler(this::deleteContract);
        subRouter.get("/consumptions_month/:ean").handler(this::getConsumptionOfMonth);
        subRouter.get("/consumptions/:ean").handler(this::getConsumptions);
        subRouter.post("/consumptions").handler(this::addConsumption);
        subRouter.get("/other_consumptions/:id_contract").handler(this::getOtherConsumptions);

        return subRouter;
    }

    /** 
     * Méthode qui utilise le package de base de données pour renvoyer une partie de la liste des langues de l'utilisateur
     * Cette méthode utilise la pagination 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void getAllLanguages(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllLanguages...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        Object[] res = commonDB.getLanguageManager().getAllLanguages(id, slice[0], slice[1]);
        int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

        ArrayList<String> allLanguages = (ArrayList<String>) res[1];

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allLanguages", allLanguages)
                        .put("last_page", numberOfPagesRemaining)));
    } 

    /** 
     * Méthode qui utilise le package de base de données pour renvoyer la langue favorite de l'utilisateur 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void getFavouriteLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("GetFavouriteLanguage...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String language = commonDB.getLanguageManager().getFavouriteLanguage(id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("language", language)));
    }

    /** 
     * Méthode qui utilise le package de base de données pour renvoyer la langue actuelle de l'utilisateur
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void getCurrentLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("GetCurrentLanguage...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String language = commonDB.getLanguageManager().getCurrentLanguage(id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("language", language)));
    }

    /** 
     * Méthode qui utilise le package de base de données pour ajouter une nouvelle langue à la liste des langues de l'utilisateur 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void addLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("AddLanguage...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String language = routingContext.pathParam("language");

        commonDB.getLanguageManager().addLanguage(id, language);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour changer la langue actuelle de l'utilisateur 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void changeCurrentLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeCurrentLanguage...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String language = routingContext.pathParam("language");

        commonDB.getLanguageManager().changeCurrentLanguage(id, language);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour changer la langue favorite de l'utilisateur 
     *
     * @param - Le context de la requête
     * @see LanguageManager
     */
    private void changeFavouriteLanguage(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeFavouriteLanguage...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String language = routingContext.pathParam("language");

        commonDB.getLanguageManager().changeFavouriteLanguage(id, language);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour renvoyer une partie de la liste des notifications de l'utilisateur
     * Cette méthode utilise la pagination 
     *
     * @param - Le context de la requête
     * @see NotificationManager
     */
    private void getAllNotifications(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllNotifications...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        Object[] res = commonDB.getNotificationManager().getAllNotifications(id, slice[0], slice[1]);
        int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

        ArrayList<Notification> allNotifications = (ArrayList<Notification>) res[1];

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allNotifications", allNotifications)
                        .put("last_page", numberOfPagesRemaining)));
    }

    /** 
     * Méthode qui utilise le package de base de données pour faire passer la notification qui a été acceptée 
     * La traitement est différent selon la requête, tout se passe côté base de données
     *
     * @param - Le context de la requête
     * @see NotificationManager
     */
    private void acceptNotification(final RoutingContext routingContext)
    {
        LOGGER.info("AcceptNotification...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String idNotification = routingContext.pathParam("id_notification");

        String role = null;
        if(((role = MyApi.getDataInToken(routingContext, "role")) == null)) return;

        if(role.equals("client"))
        {
            JsonObject body = null;
            if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

            String ean = null;
            if(checkParam((ean = body.getString("ean")), routingContext)) return;

            String address = null;
            if(checkParam((address = body.getString("address")), routingContext)) return;

            if(commonDB.getWalletManager().doesTheWalletBelongToHim(id, address))
                commonDB.getNotificationManager().acceptNotification(idNotification, ean, address);
            else
            {
                routingContext.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "error.notHisWallet")));
                return;
            }
        }
        else
            commonDB.getNotificationManager().acceptNotification(idNotification);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour supprimer la notification et prévenir l'émetteur
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
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour juste supprimer la notification 
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
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour renvoyer un contrat en particulier
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
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("contract", contract)));
    }

    /**
     * Méthode qui utilise le package de base de données pour savoir le type d'énergie d'un contrat
     *
     * @param - Le context de la requête
     * @see ContractManager
     */
    private void getTypeOfEnergyOfContract(final RoutingContext routingContext)
    {
        LOGGER.info("GetTypeOfEnergyOfContract...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String contractId = routingContext.pathParam("id_contract");

        String type = commonDB.getContractManager().getTypeOfEnergyFromContract(contractId);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("type_of_energy", type)));
    }

    /** 
     * Méthode qui utilise le package de base de données pour supprimer un contrat
     *
     * @param - Le context de la requête
     * @see ContractManager
     */
    private void deleteContract(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteContract...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String idContract = routingContext.pathParam("id_contract");

        commonDB.getContractManager().deleteContractAndNotify(idContract, id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour renvoyer les données de consommation par moi
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void getConsumptionOfMonth(final RoutingContext routingContext)
    {
        LOGGER.info("GetConsumptionOfMonth...");

        String ean = null;
        if(checkParam((ean = routingContext.request().getParam("ean")), routingContext)) return;

        String month = routingContext.request().getParam("month");
        String year = routingContext.request().getParam("year");

        TreeMap<String, Double> listConsumption = new TreeMap<>(commonDB.getConsumptionManager().getConsumptionOfMonth(ean, month, year));

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("listConsumption", listConsumption)));
    }

    /** 
     * Méthode qui utilise le package de base de données pour renvoyer les 10 dernières données avant la date reçu
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void getConsumptions(final RoutingContext routingContext)
    {
        LOGGER.info("GetConsumption...");

        String ean = null;
        if(checkParam((ean = routingContext.request().getParam("ean")), routingContext)) return;

        String date = routingContext.request().getParam("date");

        boolean isAfter = false;

        String stringIsAfter;
        if(checkParam((stringIsAfter = routingContext.request().getParam("is_after")), routingContext)) return;
        if(stringIsAfter.equals("true"))
            isAfter = true;
        else
            isAfter = false;
        
        TreeMap<String, Double> listConsumption = new TreeMap<>(commonDB.getConsumptionManager().getConsumptions(ean, date, isAfter));

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("listConsumption", listConsumption)));
    }

    /** 
     * Méthode qui utilise le package de base de données pour ajouter une liste de données de consommations par rapport à un contrat
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void addConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("AddConsumption...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        boolean isClient = commonDB.getLogManager().isClient(id);

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
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.missingInformation")));
            return;
        }

        ArrayList<Double> listValue = new ArrayList<>();
        try
        {
            for(Object value : arrayListValue)
            {
                double doubleValue = Double.parseDouble((String) value);
                listValue.add(doubleValue);
            }
        }
        catch(NumberFormatException error)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.tableauType")));
            return;
        }

        ArrayList<String> listDate = new ArrayList<>(arrayListDate.getList());
        boolean valueChange = commonDB.getConsumptionManager().addConsumption(ean, listValue, listDate, forcingChange, isClient);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("valueChange", valueChange)));

        if(valueChange)
        {
            String mail = commonDB.getLogManager().getMail(id);

            for(int i = 0; i < listDate.size(); i++)
            {
                if(!dataIsNormal(listValue.get(i), listDate.get(i), ean))
                {
                    App.sendEmail(mail, "Warning Consumption", "This is an alert mail because the consumption data of " + listDate.get(i) + " is worth " + listValue.get(i) + " is strange. Please check your installation.");
                }
            }
        }
    }

    /**
     * Méthode qui permet de retourner une liste de 10 consommations estimés en fonction des paramètres de l'utilisateur
     *
     * @param routingContext - Le context de la requête
     */
    @SuppressWarnings("removal")
    private void getOtherConsumptions(final RoutingContext routingContext)
    {
        LOGGER.info("GetOtherConsumptions...");

        String idContract = null;
        if(checkParam((idContract = routingContext.request().getParam("id_contract")), routingContext)) return;

        String address = commonDB.getContractManager().getAddress(idContract);

        Integer month = new Integer(routingContext.request().getParam("month"));
        if(month == null)
            month = 1;

        WalletFull wallet = commonDB.getWalletManager().getWallet(address);
        String typeOfEnergy = commonDB.getContractManager().getTypeOfEnergyFromContract(idContract);

        ArrayList<Double> listConsumption = generateValue(typeOfEnergy, wallet, month);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("listConsumption", listConsumption)));
    }

    /**
     * Méthode qui permet de vérifier qu'une valeur entrée est normale en regardant les 10 dernières conosmmations
     * Cette méthode retourne true s'il y a moins de 5 consommations avec lesquelles on peut effectuer un calcul
     *
     * @param value - La valeur entrée
     * @param date - La date de la consommation
     * @param ean - Le code ean lié à cette consommation
     */
    private boolean dataIsNormal(double value, String date, String ean)
    {
        HashMap<String, Double> lastConsumptions = commonDB.getConsumptionManager().getConsumptions(ean, date, false);

        if(lastConsumptions.size() < 5)
            return true;

        Double critiqueValue = 0.0;

        for(String key : lastConsumptions.keySet())
            critiqueValue += lastConsumptions.get(key);

        critiqueValue = (critiqueValue/lastConsumptions.size()) * 1.5;

        if(value > critiqueValue)
            return false;
        return true;
    }

    /**
     * Méthode qui permet de générer des données à partir des paramètres d'un portefeuille pour un type d'énergie
     *
     * @param typeOfEnergy - Le type d'énergie
     * @param wallet - Le portefeuill qui contient les paramètres
     * @param month - le moi de l'année
     */
    private ArrayList<Double> generateValue(String typeOfEnergy, WalletFull wallet, int month)
    {
        ArrayList<Double> listValue = new ArrayList<>();
        Random rand = new Random();

        for(int i = 0; i < 10; i++)
        {
            double value = 0.0;

            switch(typeOfEnergy)
            {
                case "water":
                    value = 96 * wallet.getNumberOfResidents();
                    break;

                case "gas":
                    value = 45 + (wallet.getNumberOfResidents() * 0.8) + Math.floorMod(wallet.getSizeOfHouse(), 50);
                    if(wallet.getIsHouse())
                        value += 0.3;
                    if(!wallet.getIsElectricityToCharge())
                        value += 1.3;
                    if(month >= 4 && month <= 10)
                        value -= 0.3;
                    break;

                case "electricity":
                    value = 1.5 + (wallet.getNumberOfResidents() * 0.8) + Math.floorMod(wallet.getSizeOfHouse(), 50);
                    if(wallet.getIsHouse())
                        value += 0.5;
                    if(!wallet.getIsElectricityToCharge())
                        value += 1.3;
                    if(month >= 4 && month <= 10)
                        value -= 0.5;
                    if(wallet.getSolarPanels())
                        value -= 0.7;
                    break;
            }

            double max = value + value * 0.1;
            double min = value - value * 0.1;

            value = rand.nextDouble() * (max - min) + min;
            listValue.add(value);
        }

        return listValue;
    }
}
