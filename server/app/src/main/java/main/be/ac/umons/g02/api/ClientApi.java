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

public class ClientApi extends MyApi implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApi.class);

    private final CommonDB commonDB = new CommonDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.get("/wallets/page").handler(this::getAllWallets);
        subRouter.get("/wallets/:address").handler(this::getWallet);
        subRouter.post("/wallets").handler(this::createWallet);
        subRouter.delete("/wallets/:address").handler(this::deleteWallet);
        subRouter.get("/contracts/page").handler(this::getAllContracts);
        subRouter.get("/proposals/page").handler(this::getAllProposals);
        subRouter.get("/proposals/:id_proposal").handler(this::getProposal);
        subRouter.post("/proposeContract").handler(this::clientProposeContract);

        return subRouter;
    }

    private void getAllWallets(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllWallets...");

        String id = routingContext.user().principal().getString("id");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<WalletBasic> wallets = commonDB.getWalletManager().getAllWallets(id, slice[0], slice[1]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "getAllWallets")
            .end(Json.encodePrettily(new JsonObject()
                        .put("wallets", wallets)));
    }

    private void getWallet(final RoutingContext routingContext)
    {
        LOGGER.info("GetWallet...");

        final String address = routingContext.request().getParam("address");
        WalletFull wallet = commonDB.getWalletManager().getWallet(address);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "getWallet")
            .end(Json.encodePrettily(new JsonObject()
                        .put("wallet", wallet)));
    }

    private void createWallet(final RoutingContext routingContext)
    {
        LOGGER.info("CreateWallet...");

        final JsonObject body = routingContext.getBodyAsJson();
        final String address = body.getString("address");
        final String name = body.getString("name");
        final String nameOwner = body.getString("name_owner");

        WalletBasic wallet = new WalletBasic(address, name, nameOwner);
        commonDB.getWalletManager().createWallet(wallet);

        routingContext
            .response()
            .setStatusCode(201)
            .putHeader("content-type", "createWallet");
    }

    private void deleteWallet(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteWallet...");

        final String address = routingContext.request().getParam("address");

        if(commonDB.getWalletManager().walletIsEmpty(address))
        {
            commonDB.getWalletManager().deleteWallet(address);
            routingContext
                .response()
                .setStatusCode(200)
                .putHeader("content-type", "deleteWallet");
        }
        else
            routingContext
                .response()
                .setStatusCode(405)
                .putHeader("error", "Le portefeuille n'est pas vide.");
    }

    private void getAllContracts(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllContracts...");

        String id = routingContext.user().principal().getString("id");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<ContractBasic> contracts = commonDB.getContractManager().getAllContracts(id, slice[0], slice[1]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "getAllContracts")
            .end(Json.encodePrettily(new JsonObject()
                        .put("contracts", contracts)));
    }

    private void getAllProposals(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllProposals...");

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        ArrayList<ProposalBasic> proposals = commonDB.getProposalManager().getAllProposals(slice[0], slice[1]);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "getAllProposals")
            .end(Json.encodePrettily(new JsonObject()
                        .put("proposals", proposals)));
    }

    private void getProposal(final RoutingContext routingContext)
    {
        LOGGER.info("GetProposal...");

        final String id_proposal = routingContext.request().getParam("id_proposal");

        ProposalFull proposal = commonDB.getProposalManager().getProposal(id_proposal); //TODO doit changer en providerId, proposalName

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "getProposal")
            .end(Json.encodePrettily(new JsonObject()
                        .put("proposal", proposal)));
    }

    private void clientProposeContract(final RoutingContext routingContext)
    {
        LOGGER.info("ClientProposeContract...");

        String id = routingContext.user().principal().getString("id");

        final JsonObject body = routingContext.getBodyAsJson();
        final String id_proposal = body.getString("id_proposal");
        final String id_provider = body.getString("id_provider");
        final String mail = body.getString("mail");
        final String ean = body.getString("ean");

        commonDB.getProposalManager().clientProposeContract(id_proposal, id, id_provider, mail, ean);

        routingContext
            .response()
            .setStatusCode(200)
            .putHeader("content-type", "clientProposeContract");
    }
}
