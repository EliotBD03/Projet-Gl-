package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.api.MyApi;

import main.be.ac.umons.g02.database.CommonDB;
import main.be.ac.umons.g02.data_object.ClientBasic;
import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;

/**
 * Classe qui gère la catégorie provider des requêtes de l'API
 */
public class ProviderApi extends MyApi implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderApi.class);

    private final CommonDB commonDB = new CommonDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.get("/clients/page").handler(this::getAllClients);
        subRouter.get("/clients/clients_of_provider/page").handler(this::getAllHisClients);
        subRouter.get("/clients/:id_client/contrats/page").handler(this::getContractOfClient);
        subRouter.delete("/clients/clients_of_provider/:id_client").handler(this::deleteClient);
        subRouter.get("/proposals/page").handler(this::getAllProposals);
        subRouter.get("/proposals/:name_proposal").handler(this::getProposal);
        subRouter.post("/proposals").handler(this::addProposal);
        subRouter.delete("/proposals/:name_proposal").handler(this::deleteProposal);
        subRouter.delete("/consumptions/:ean").handler(this::deleteAllConsumptions);
        subRouter.delete("/consumptions/:ean/:date").handler(this::deleteConsumption);
        subRouter.post("/propose_contract").handler(this::providerProposeContract);

        return subRouter;
    }

    /**
     * Méthode qui utilise le package base de données pour renvoyer une partie de la liste de tous les clients de l'application
     * Cette méthode utilise la pagination
     *
     * @param - Le context de la requête
     * @see ClientManager
     */
    private void getAllClients(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllClients...");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        Object[] res = commonDB.getClientManager().getAllClients(slice[0], slice[1]);
        int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

        ArrayList<ClientBasic> allClients = (ArrayList<ClientBasic>) res[1];

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allClients", allClients)
                        .put("last_page", numberOfPagesRemaining)));
    }

    /**
     * Méthode qui utilise le package base de données pour renvoyer une partie de la liste de tous les clients du fournisseurs en question
     * Cette méthode utilise la pagination
     *
     * @param - Le context de la requête
     * @see ClientManager
     */
    private void getAllHisClients(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllHisClients...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        Object[] res = commonDB.getClientManager().getAllHisClients(id, slice[0], slice[1]);
        int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

        ArrayList<ClientBasic> allHisClients = (ArrayList<ClientBasic>) res[1];

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allHisClients", allHisClients)
                        .put("last_page", numberOfPagesRemaining)));
    }

    /** 
     * Méthode qui utilise le package base de données pour renvoyer une partie de la liste des contrats en commun entre un fournisseur et un client
     * Cette méthode utilise la pagination
     *
     * @param - Le context de la requête
     * @see ContractManager
     */
    private void getContractOfClient(final RoutingContext routingContext)
    {
        LOGGER.info("GetContractOfClient...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String idClient = routingContext.pathParam("id_client");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        Object[] res = commonDB.getContractManager().getCommonContracts(idClient, id, slice[0], slice[1]);
        int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

        ArrayList<ContractBasic> contracts = (ArrayList<ContractBasic>) res[1];

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("contracts", contracts)
                        .put("last_page", numberOfPagesRemaining)));
    }

    /** 
     * Méthode qui utilise le package base de données pour effacer tous les contracts du client par rapport à ce fournisseur
     * Le fournisseur n'aura donc plus aucun lien avec le client
     *
     * @param - Le context de la requête
     * @see ClientManager
     */
    private void deleteClient(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteClient...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String idClient = routingContext.pathParam("id_client");

        commonDB.getClientManager().deleteClient(id, idClient);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package base de données pour renvoyer une partie de la liste des propositions que le fournisseurs a créées
     * Cette méthode utilise la pagination
     *
     * @param - Le context de la requête
     * @see ProposalManager
     */
    private void getAllProposals(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllProposals...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        Object[] res = commonDB.getProposalManager().getAllProposals(id, slice[0], slice[1]);
        int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

        ArrayList<ProposalBasic> allProposals = (ArrayList<ProposalBasic>) res[1];

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allProposals", allProposals)
                        .put("last_page", numberOfPagesRemaining)));
    }

    /** 
     * Méthode qui utilise le package base de données pour renvoyer une proposition du fournisseur en particulier
     *
     * @param - Le context de la requête
     * @see ProposalManager
     */
    private void getProposal(final RoutingContext routingContext)
    {
        LOGGER.info("GetProposals...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String nameProposal = routingContext.pathParam("name_proposal");

        ProposalFull proposal = commonDB.getProposalManager().getProposal(nameProposal, id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("proposal", proposal)));
    }

    /** 
     * Méthode qui utilise le package de base de données pour ajouter une nouvelle proposition à celles du fournisseurs ou en modifie une déjà présente
     * Le cas dépend de si le nom  de la proposition existe déjà ou non
     * Dans le cas où la proposition a changé, on crée une notification pour prévenir tous les clients du changements
     *
     * @param - Le context de la requête
     * @see ProposalManager
     * @see ContractManager
     */
    private void addProposal(final RoutingContext routingContext)
    {
        LOGGER.info("AddProposal...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String nameProvider = commonDB.getLogManager().getName(id);

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String nameProposal = null;
        if(checkParam((nameProposal = body.getString("name_proposal")), routingContext)) return;

        String typeOfEnergy = null;
        if(checkParam((typeOfEnergy = body.getString("type_of_energy")), routingContext)) return;

        String localization = null;
        if(checkParam((localization = body.getString("localization")), routingContext)) return;

        double variableDayPrice = 0;
        double variableNightPrice = 0;
        boolean isFixedRate = false;
        int duration = 0;

        try
        {
            if(checkParam((variableDayPrice = body.getDouble("variable_day_price")), routingContext)) return;
            if(checkParam((variableNightPrice = body.getDouble("variable_night_price")), routingContext)) return;
            if(checkParam((isFixedRate = body.getBoolean("is_fixed_rate")), routingContext)) return;
            if(checkParam((duration = body.getInteger("duration")), routingContext)) return;
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

        String startOffPeakHours = null;
        String endOffPeakHours = null;

        if(variableNightPrice >= 0)
        {
            if(checkParam((startOffPeakHours = body.getString("start_off_peak_hours")), routingContext)) return;
            if(checkParam((endOffPeakHours = body.getString("end_off_peak_hours")), routingContext)) return;
        }
        else
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.missingInformation")));
            return;
        }


        ProposalFull newProposal = new ProposalFull(id, nameProvider, typeOfEnergy, localization, nameProposal);
        newProposal.setMoreInformation(variableDayPrice, variableNightPrice, isFixedRate, startOffPeakHours, endOffPeakHours, duration);

        if(commonDB.getProposalManager().addProposal(newProposal))
        {
            ArrayList<String> listClient = commonDB.getContractManager().getAllClientsOfContract(nameProposal, id);

            for(String idClient : listClient)
                commonDB.getNotificationManager().createNotification(id, idClient, nameProposal, "The contract has been changed.");
        }

        routingContext.response()
            .setStatusCode(201)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour effacer une proposition parmi celles que le fournisseur avait crées
     *
     * @param - Le context de la requête
     * @see ProposalManager
     */
    private void deleteProposal(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteProposal...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String nameProposal = routingContext.pathParam("name_proposal");

        commonDB.getProposalManager().deleteProposal(nameProposal, id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour effacer tous les données de consommations du client par rapport à un contrat en particulier
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void deleteAllConsumptions(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteAllConsumptions...");

        String ean = routingContext.pathParam("ean");

        commonDB.getConsumptionManager().deleteAllConsumptions(ean);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour effacer une consommation précise d'un client en particulier pour un contrat en particulier
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void deleteConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteConsumption...");

        String ean = routingContext.pathParam("ean");
        String date = routingContext.pathParam("date");

        commonDB.getConsumptionManager().deleteConsumption(ean, date);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour créer une nofication afin de prévenir le client d'une nouvelle proposition de contrat
     *
     * @param - Le context de la requête
     * @see NotificationManager
     */
    private void providerProposeContract(final RoutingContext routingContext)
    {
        LOGGER.info("ProviderProposeContract...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String nameProposal = null;
        if(checkParam((nameProposal = body.getString("name_proposal")), routingContext)) return;

        String idClient = null;
        if(checkParam((idClient = body.getString("id_client")), routingContext)) return;

        String nameProvider = commonDB.getLogManager().getName(id);
        commonDB.getNotificationManager().createNotification(id, idClient, nameProposal, id, "Contract request from "+ nameProvider);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }
}
