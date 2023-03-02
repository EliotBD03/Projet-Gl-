package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.CommonDB;
import main.be.ac.umons.g02.data_object.ClientBasic;
import main.be.ac.umons.g02.data_object.ClientFull;
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
        subRouter.get("/clients/:id_client").handler(this::getClient);
        subRouter.delete("/clients/clients_of_provider/:id_client").handler(this::deleteClient);
        subRouter.get("/proposals/:id_provider/page").handler(this::getAllProposals);
        subRouter.get("/proposals/:id_provider/:name_proposal").handler(this::getProposal);
        subRouter.post("/proposals").handler(this::addProposal);
        subRouter.delete("/proposals/:id_provider/:nameProposal").handler(this::deleteProposal);
        subRouter.delete("/consumptions/:ean").handler(this::deleteAllConsumptions);
        subRouter.delete("/consumptions/:ean/:date").handler(this::deleteConsumption);
        subRouter.post("/propose_contract").handler(this::providerProposeContract);

        return subRouter;
    }

    /**
     * Méthode qui utilise le package base de donnée pour renvoyer une partie de la liste de tous les clients de l'application
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

        ArrayList<ClientBasic> allClients = commonDB.getClientManager().getAllClients(slice[0], slice[1]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allClients", allClients)));
    }

    /**
     * Méthode qui utilise le package base de donnée pour renvoyer une partie de la liste de tous les clients du fournisseurs en question
     * Cette méthode utilise la pagination
     *
     * @param - Le context de la requête
     * @see ClientManager
     */
    private void getAllHisClients(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllHisClients...");

        String id = routingContext.user().principal().getString("id");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<ClientBasic> allHisClients = commonDB.getClientManager().getAllHisClients(id, slice[0], slice[1]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allHisClients", allHisClients)));
    }

    /** 
     * Méthode qui utilise le package base de donnée pour renvoyer un client en particulier
     *
     * @param - Le context de la requête
     * @see ClientManager
     */
    private void getClient(final RoutingContext routingContext)
    {
        LOGGER.info("GetClient...");

        final String idClient = routingContext.request().getParam("id_client");
        ClientFull client = commonDB.getClientManager().getClient(idClient);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("client", client)));
    }

    /** 
     * Méthode qui utilise le package base de donnée pour effacer tous les contracts du client par rapport à ce fournisseur
     * Le fournisseur n'aura donc plus aucun lien avec le client
     *
     * @param - Le context de la requête
     * @see ClientManager
     */
    private void deleteClient(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteClient...");

        String id = routingContext.user().principal().getString("id");

        final String idClient = routingContext.request().getParam("id_client");
        commonDB.getClientManager().deleteClient(id, idClient);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    /** 
     * Méthode qui utilise le package base de donnée pour renvoyer une partie de la liste des propositions que le fournisseurs a créées
     * Cette méthode utilise la pagination
     *
     * @param - Le context de la requête
     * @see ProposalManager
     */
    private void getAllProposals(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllProposals...");

        String id = routingContext.user().principal().getString("id");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<ProposalBasic> allProposals = commonDB.getProposalManager().getAllProposals(id, slice[0], slice[1]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allProposals", allProposals)));
    }

    /** 
     * Méthode qui utilise le package base de donnée pour renvoyer une proposition du fournisseur en particulier
     *
     * @param - Le context de la requête
     * @see ProposalManager
     */
    private void getProposal(final RoutingContext routingContext)
    {
        LOGGER.info("GetProposals...");

        String id = routingContext.user().principal().getString("id");

        final String nameProposal = routingContext.request().getParam("name_proposal");
        ProposalFull proposal = commonDB.getProposalManager().getProposal(nameProposal, id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("proposal", proposal)));
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour ajouter une nouvelle proposition à celles du fournisseurs ou en modifie une déjà présente
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

        String id = routingContext.user().principal().getString("id");

        final JsonObject body = routingContext.getBodyAsJson();
        final String nameProposal = body.getString("name_proposal");
        final String nameProvider = body.getString("name_provider");
        final String typeOfEnergy = body.getString("type_of_energy");
        final String localization = body.getString("localization");
        final double basicPrice = body.getDouble("basic_price");
        final double variableDayPrice = body.getDouble("variable_day_price");
        final double variableNightPrice = body.getDouble("variable_night_price");
        final boolean isFixedRate = body.getBoolean("is_fixed_rate");
        final boolean isSingleHourCounter = body.getBoolean("is_single_hour_counter");
        final String startOffPeakHours = body.getString("start_off_peak_hours");
        final String endOffPeakHours = body.getString("end_off_peak_hours");

        ProposalFull newProposal = new ProposalFull(id, nameProvider, typeOfEnergy, localization, nameProposal);
        newProposal.setMoreInformation(basicPrice, variableDayPrice, variableNightPrice, isFixedRate, isSingleHourCounter, startOffPeakHours, endOffPeakHours);

        if(commonDB.getProposalManager().addProposal(newProposal))
        {
            ArrayList<String> listClient = commonDB.getContractManager().getAllClientsOfContract(nameProposal, id);

            for(String idClient : listClient)
                commonDB.getNotificationManager().createNotification(id, idClient, nameProposal, "Le contract a été changé.");
        }

        routingContext.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json");
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour effacer une proposition parmi celles que le fournisseur avait crées
     *
     * @param - Le context de la requête
     * @see ProposalManager
     */
    private void deleteProposal(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteProposal...");

        String id = routingContext.user().principal().getString("id");

        final String nameProposal = routingContext.request().getParam("name_proposal");
        commonDB.getProposalManager().deleteProposal(id, nameProposal);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour effacer tous les données de consommations du client par rapport à un contrat en particulier
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void deleteAllConsumptions(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteAllConsumptions...");

        final String ean = routingContext.request().getParam("ean");
        commonDB.getConsumptionManager().deleteAllConsumptions(ean);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour effacer une consommation précise d'un client en particulier pour un contrat en particulier
     *
     * @param - Le context de la requête
     * @see ConsumptionManager
     */
    private void deleteConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteConsumption...");

        final String ean = routingContext.request().getParam("ean");
        final String date = routingContext.request().getParam("date");

        commonDB.getConsumptionManager().deleteConsumption(ean, date);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour créer une nofication afin de prévenir le client d'une nouvelle proposition de contrat
     *
     * @param - Le context de la requête
     * @see NotificationManager
     */
    private void providerProposeContract(final RoutingContext routingContext)
    {
        LOGGER.info("ProviderProposeContract...");

        String id = routingContext.user().principal().getString("id");

        final JsonObject body = routingContext.getBodyAsJson();
        final String nameProposal = body.getString("name_proposal");
        final String idClient = body.getString("id_client");

        String nameProvider = commonDB.getLogManager().getName(id);
        commonDB.getNotificationManager().createNotification(id, idClient, nameProposal, id, "Demande de contrat de la part de "+ nameProvider + ".");

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json");
    }
}
