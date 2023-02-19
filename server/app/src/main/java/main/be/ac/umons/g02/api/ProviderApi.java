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
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProviderApi extends AbstractToken implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderApi.class);

    private final CommonDB commonDB = new CommonDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.get("/:token/clients").handler(this::getAllClients);
        subRouter.get("/:token/clients/:id_provider").handler(this::getAllHisClients);
        subRouter.get("/:token/clients/:id_client").handler(this::getClient);
        subRouter.delete("/:token/clients/:id_provider/:id_client").handler(this::deleteClient);
        subRouter.get("/:token/proposals/:id_provider").handler(this::getAllProposals);
        subRouter.get("/:token/proposals/:id_provider/:name_proposal").handler(this::getProposal);
        subRouter.post("/:token/proposals").handler(this::addProposal);
        subRouter.put("/:token/proposals").handler(this::changeProposal);
        subRouter.delete("/:token/proposals/:id_provider/:nameProposal").handler(this::deleteProposal);
        subRouter.delete("/:token/consumptions/:ean").handler(this::deleteAllConsumptions);
        subRouter.delete("/:token/consumptions/:ean/:date").handler(this::deleteConsumption);
        subRouter.post("/:token/propose_contract").handler(this::providerProposeContract);

        return subRouter;
    }

    private void getAllClients(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllClients...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        ArrayList<ClientBasic> allClients = commonDB.getClientManager().getAllClients();

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("allClients", allClients);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void getAllHisClients(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllHisClients...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_provider = routingContext.request().getParam("id_provider");
        ArrayList<ClientBasic> allHisClients = commonDB.getClientManager().getAllHisClients(id_provider);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("allHisClients", allHisClients);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void getClient(final RoutingContext routingContext)
    {
        LOGGER.info("GetClient...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_client = routingContext.request().getParam("id_client");
        ClientFull client = commonDB.getClientManager().getClient(id_client);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("client", client);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void deleteClient(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteClient...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_provider = routingContext.request().getParam("id_provider");
        final String id_client = routingContext.request().getParam("id_client");
        commonDB.getClientManager().deleteClient(id_provider, id_client);

        routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }

    private void getAllProposals(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllProposals...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }
        
        final String id_provider = routingContext.request().getParam("id_provider");
        ArrayList<ProposalBasic> allProposals = commonDB.getProposalManager().getAllProposals(id_provider);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("allProposals", allProposals);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void getProposal(final RoutingContext routingContext)
    {
        LOGGER.info("GetProposals...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String name_proposal = routingContext.request().getParam("name_proposal");
        final String id_provider = routingContext.request().getParam("id_provider");
        ProposalFull proposal = commonDB.getProposalManager().getProposal(name_proposal, id_provider);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("proposal", proposal);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void addProposal(final RoutingContext routingContext)
    {
        LOGGER.info("AddProposal...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final JsonObject body = routingContext.getBodyAsJson();
		final String nameProposal = body.getString("nameProposal");
		final String id_provider = body.getString("id_provider");
		final String nameProvider = body.getString("nameProvider");
		final String stringTypeOfEnergy = body.getString("typeOfEnergy");
		final String localization = body.getString("localization");
		final double basicPrice = body.getDouble("basicPrice");
		final double variableDayPrice = body.getDouble("variableDayPrice");
		final double variableNightPrice = body.getDouble("variableNightPrice");
		final boolean isFixedRate = body.getBoolean("isFixedRate");
		final boolean isSingleHourCounter = body.getBoolean("isSingleHourCounter");
		final String stringStartOffPeakHours = body.getString("startOffPeakHours");
		final String stringEndOffPeakHours = body.getString("endOffPeakHours");

        final TypeEnergy typeOfEnergy;
        if(stringTypeOfEnergy == "water")
            typeOfEnergy = TypeEnergy.WATER;
        else if(stringTypeOfEnergy == "gaz")
            typeOfEnergy = TypeEnergy.GAS;
        else
            typeOfEnergy = TypeEnergy.ELECTRICITY;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startOffPeakHours = new GregorianCalendar();
        Calendar endOffPeakHours = new GregorianCalendar();
        startOffPeakHours.setTime(format.parse(stringStartOffPeakHours));
        endOffPeakHours.setTime(format.parse(stringEndOffPeakHours));

        ProposalFull new_proposal = new ProposalFull(id_provider, nameProvider, typeOfEnergy, localization, nameProposal);
        new_proposal.setMoreInformation(basicPrice, variableDayPrice, variableNightPrice, isFixedRate, isSingleHourCounter, startOffPeakHours, endOffPeakHours);

        commonDB.getProposalManager().addProposal(new_proposal);

		routingContext.response().setStatusCode(201).putHeader("contentType", "babaWallet/api");
    }

    private void changeProposal(final RoutingContext routingContext)
    {
        LOGGER.info("ChangeProposal...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final JsonObject body = routingContext.getBodyAsJson();
		final String nameProposal = body.getString("nameProposal");
		final String id_provider = body.getString("id_provider");
		final String nameProvider = body.getString("nameProvider");
		final String stringTypeOfEnergy = body.getString("typeOfEnergy");
		final String localization = body.getString("localization");
		final double basicPrice = body.getDouble("basicPrice");
		final double variableDayPrice = body.getDouble("variableDayPrice");
		final double variableNightPrice = body.getDouble("variableNightPrice");
		final boolean isFixedRate = body.getBoolean("isFixedRate");
		final boolean isSingleHourCounter = body.getBoolean("isSingleHourCounter");
		final String stringStartOffPeakHours = body.getString("startOffPeakHours");
		final String stringEndOffPeakHours = body.getString("endOffPeakHours");

        final TypeEnergy typeOfEnergy;
        if(stringTypeOfEnergy == "water")
            typeOfEnergy = TypeEnergy.WATER;
        else if(stringTypeOfEnergy == "gaz")
            typeOfEnergy = TypeEnergy.GAS;
        else
            typeOfEnergy = TypeEnergy.ELECTRICITY;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startOffPeakHours = new GregorianCalendar();
        Calendar endOffPeakHours = new GregorianCalendar();
        startOffPeakHours.setTime(format.parse(stringStartOffPeakHours));
        endOffPeakHours.setTime(format.parse(stringEndOffPeakHours));

        ProposalFull new_proposal = new ProposalFull(id_provider, nameProvider, typeOfEnergy, localization, nameProposal);
        new_proposal.setMoreInformation(basicPrice, variableDayPrice, variableNightPrice, isFixedRate, isSingleHourCounter, startOffPeakHours, endOffPeakHours);

        commonDB.getProposalManager().changeProposal(new_proposal);

		routingContext.response().setStatusCode(201).putHeader("contentType", "babaWallet/api");
    }

    private void deleteProposal(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteProposal...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_provider = routingContext.request().getParam("id_provider");
        final String name_proposal = routingContext.request().getParam("name_proposal");
        commonDB.getProposalManager().deleteProposal(id_provider, name_proposal);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }

    private void deleteAllConsumptions(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteAllConsumptions...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String ean = routingContext.request().getParam("ean");
        commonDB.getConsumptionManager().deleteAllConsumptions(ean);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }

    private void deleteConsumption(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteConsumption...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String ean = routingContext.request().getParam("ean");
        final String stringDate = routingContext.request().getParam("date");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = new GregorianCalendar();
        date.setTime(format.parse(stringDate));

        commonDB.getConsumptionManager().deleteConsumption(ean, date);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }

    private void providerProposeContract(final RoutingContext routingContext)
    {
        LOGGER.info("...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final JsonObject body = routingContext.getBodyAsJson();
		final String name_proposal = body.getString("name_proposal");
		final String id_provider = body.getString("id_provider");
		final String id_client = body.getString("id_client");
        
        commonDB.getContractManager().providerProposeContract(name_proposal, id_provider, id_client);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }
}
