package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.ClientDB;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ClientApi extends AbstractToken implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApi.class);

    //private final ClientDB clientDB = ClientDB.getInstance();
    private final ClientDB clientDB = new ClientDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.get("/:token/wallets").handler(this::getAllWallets);
        subRouter.get("/:token/wallets/:adress").handler(this::getWallet);
        subRouter.post("/:token/wallets").handler(this::createWallet);
        subRouter.delete("/:token/wallets/:adress").handler(this::deleteWallet);
        subRouter.get("/:token/contracts").handler(this::getAllContracts);
        subRouter.get("/:token/proposals").handler(this::getAllProposals);
        subRouter.get("/:token/proposals/:id_proposal").handler(this::getProposal);

        return null;
    }

    private void getAllWallets(final RoutingContext routingContext)
    {
    }

    private void getWallet(final RoutingContext routingContext)
    {
    }

    private void createWallet(final RoutingContext routingContext)
    {
    }

    private void deleteWallet(final RoutingContext routingContext)
    {
    }

    private void getAllContracts(final RoutingContext routingContext)
    {
    }

    private void getAllProposals(final RoutingContext routingContext)
    {
    }

    private void getProposal(final RoutingContext routingContext)
    {
    }
}
