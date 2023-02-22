package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.CommonDB;
import main.be.ac.umons.g02.data_object.WalletBasic;
import main.be.ac.umons.g02.data_object.WalletFull;
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

public class ClientApi extends AbstractToken implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApi.class);

    private MyApi api;
    private final CommonDB commonDB = new CommonDB();

    public ClientApi(MyApi api)
    {
        this.api = api;
    }

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.get("/:token/wallets/page").handler(this::getAllWallets);
        subRouter.get("/:token/wallets/:address").handler(this::getWallet);
        subRouter.post("/:token/wallets").handler(this::createWallet);
        subRouter.delete("/:token/wallets/:address").handler(this::deleteWallet);
        subRouter.get("/:token/contracts/page").handler(this::getAllContracts);
        subRouter.get("/:token/proposals/page").handler(this::getAllProposals);
        subRouter.get("/:token/proposals/:id_proposal").handler(this::getProposal);
        subRouter.post("/:token/proposeContract").handler(this::clientProposeContract);

        return null;
    }

    private void getAllWallets(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllWallets...");

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

        page *= 10;
        
        final String stringLimit = routingContext.request().getParam("limit");
        int limit = api.getLimit(stringLimit);

        ArrayList<WalletBasic> wallets = commonDB.getWalletManager().getAllWallets(id, page, limit);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("wallets", wallets);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void getWallet(final RoutingContext routingContext)
    {
        LOGGER.info("GetWallet...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String address = routingContext.request().getParam("address");
        WalletFull wallet = commonDB.getWalletManager().getWallet(address);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("wallet", wallet);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void createWallet(final RoutingContext routingContext)
    {
        LOGGER.info("CreateWallet...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final JsonObject body = routingContext.getBodyAsJson();
		final String address = body.getString("address");
		final String name = body.getString("name");
		final String nameOwner = body.getString("nameOwner");

        WalletBasic wallet = new WalletBasic(address, name, nameOwner);
        commonDB.getWalletManager().createWallet(wallet);

		routingContext.response().setStatusCode(201).putHeader("contentType", "babaWallet/api");
    }

    private void deleteWallet(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteWallet...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }
        
        final String address = routingContext.request().getParam("address");

        if(commonDB.getWalletManager().walletIsEmpty(address))
        {
            commonDB.getWalletManager().deleteWallet(address);
            routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
        }
        else
        {
            final JsonObject errorJsonResponse = new JsonObject();
            errorJsonResponse.put("error", "Le portefeuille n'est pas vide.");
            routingContext.response().setStatusCode(405).putHeader("contentType", "babaWallet/api").end(Json.encode(errorJsonResponse));
        }
    }

    private void getAllContracts(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllContracts...");

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

        page *= 10;
        
        final String stringLimit = routingContext.request().getParam("limit");
        int limit = api.getLimit(stringLimit);

        ArrayList<ContractBasic> contracts = commonDB.getContractManager().getAllContracts(id, page, limit);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("contracts", contracts);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void getAllProposals(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllProposals...");

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

        page *= 10;
        
        final String stringLimit = routingContext.request().getParam("limit");
        int limit = api.getLimit(stringLimit);

        ArrayList<ProposalBasic> proposals = commonDB.getProposalManager().getAllProposals(page, limit);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("proposals", proposals);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void getProposal(final RoutingContext routingContext)
    {
        LOGGER.info("GetProposal...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final String id_proposal = routingContext.request().getParam("id_proposal");

        ProposalFull proposal = commonDB.getProposalManager().getProposal(id_proposal);

        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("proposal", proposal);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void clientProposeContract(final RoutingContext routingContext)
    {
        LOGGER.info("ClientProposeContract...");

        final String token = routingContext.request().getParam("token");
        final String id = checkToken(token);

        if(id == null)
        {
            api.sendMessageError(routingContext, "Le token est incorrecte.");
            return;
        }

        final JsonObject body = routingContext.getBodyAsJson();
		final String id_proposal = body.getString("id_proposal");
		final String id_provider = body.getString("id_provider");
		final String mail = body.getString("mail");
		final String ean = body.getString("ean");

        commonDB.getProposalManager().clientProposeContract(id_proposal, id, id_provider, mail, ean);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
    }
}
