package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.CommonDB;
import main.be.ac.umons.g02.data_object.ClientBasic;
import main.be.ac.umons.g02.data_object.ClientFull;
import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;
import main.be.ac.umons.g02.data_object.TypeEnergy;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;

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

    private void getAllClients(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllClients...");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<ClientBasic> allClients = commonDB.getClientManager().getAllClients(slice[0], slice[1]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "getAllClients")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allClients", allClients)));
    }

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
            .putHeader("content-type", "getAllHisClients")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allHisClients", allHisClients)));
    }

    private void getClient(final RoutingContext routingContext)
    {
        LOGGER.info("GetClient...");

        final String id_client = routingContext.request().getParam("id_client");
        ClientFull client = commonDB.getClientManager().getClient(id_client);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "getClient")
            .end(Json.encodePrettily(new JsonObject()
                        .put("client", client)));
    }

    private void deleteClient(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteClient...");

        String id = routingContext.user().principal().getString("id");

        final String id_client = routingContext.request().getParam("id_client");
        commonDB.getClientManager().deleteClient(id, id_client); //TODO pq un id autre que celui du client

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "deleteClient");
    }

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
            .putHeader("content-type", "getAllProposals")
            .end(Json.encodePrettily(new JsonObject()
                        .put("allProposals", allProposals)));
    }

    private void getProposal(final RoutingContext routingContext)
    {
        LOGGER.info("GetProposals...");

        String id = routingContext.user().principal().getString("id");

        final String nameProposal = routingContext.request().getParam("name_proposal");
        ProposalFull proposal = commonDB.getProposalManager().getProposal(nameProposal, id);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "getProposal")
            .end(Json.encodePrettily(new JsonObject()
                        .put("proposal", proposal)));
    }

    private void addProposal(final RoutingContext routingContext)
    {
        LOGGER.info("AddProposal...");

        String id = routingContext.user().principal().getString("id");

        final JsonObject body = routingContext.getBodyAsJson();
        final String nameProposal = body.getString("name_proposal");
        final String nameProvider = body.getString("name_provider");
        final String stringTypeOfEnergy = body.getString("type_of_energy");
        final String localization = body.getString("localization");
        final double basicPrice = body.getDouble("basic_price");
        final double variableDayPrice = body.getDouble("variable_day_price");
        final double variableNightPrice = body.getDouble("variable_night_price");
        final boolean isFixedRate = body.getBoolean("is_fixed_rate");
        final boolean isSingleHourCounter = body.getBoolean("is_single_hour_counter");
        final String startOffPeakHours = body.getString("start_off_peak_hours");
        final String endOffPeakHours = body.getString("end_off_peak_hours");

        final TypeEnergy typeOfEnergy;
        if(stringTypeOfEnergy == "water")
            typeOfEnergy = TypeEnergy.WATER;
        else if(stringTypeOfEnergy == "gaz")
            typeOfEnergy = TypeEnergy.GAS;
        else
            typeOfEnergy = TypeEnergy.ELECTRICITY;

        ProposalFull new_proposal = new ProposalFull(id, nameProvider, typeOfEnergy, localization, nameProposal); //TODO on fait quoi ? un tableau ou une loc pour une proposal
        new_proposal.setMoreInformation(basicPrice, variableDayPrice, variableNightPrice, isFixedRate, isSingleHourCounter, startOffPeakHours, endOffPeakHours);

        if(commonDB.getProposalManager().addProposal(new_proposal))
        {
            ArrayList<String> listClient = commonDB.getContractManager().getAllClientsOfContract(nameProposal, nameProvider); //TODO ATTENTION : providerID

            for(String id_client : listClient)
                commonDB.getNotificationManager().createNotification(id, id_client, nameProposal, "Le contract a été changé.");
        }

        routingContext.response()
            .setStatusCode(201)
            .putHeader("content-type", "addProposal");
    }

    private void deleteProposal(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteProposal...");

        String id = routingContext.user().principal().getString("id");

        final String name_proposal = routingContext.request().getParam("name_proposal");
        commonDB.getProposalManager().deleteProposal(id, name_proposal);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "deleteProposal");
    }

    private void deleteAllConsumptions(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteAllConsumptions...");

        final String ean = routingContext.request().getParam("ean");
        commonDB.getConsumptionManager().deleteAllConsumptions(ean);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "deleteAllConsumptions");
    }

    private void deleteConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteConsumption...");

        final String ean = routingContext.request().getParam("ean");
        final String date = routingContext.request().getParam("date");

        commonDB.getConsumptionManager().deleteConsumption(ean, date);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "deleteConsumption");
    }

    private void providerProposeContract(final RoutingContext routingContext)
    {
        LOGGER.info("ProviderProposeContract...");

        String id = routingContext.user().principal().getString("id");

        final JsonObject body = routingContext.getBodyAsJson();
        final String name_proposal = body.getString("name_proposal");
        final String id_client = body.getString("id_client");

        commonDB.getContractManager().providerProposeContract(name_proposal, id, id_client);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "providerProposeContract");
    }
}
