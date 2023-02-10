package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.ClientDB;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ClientApi extends AbstractToken implements RouterApi
{
    //private final ClientDB clientDB = ClientDB.getInstance();
    private final ClientDB clientDB = new ClientDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
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
