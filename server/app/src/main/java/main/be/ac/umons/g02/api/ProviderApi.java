package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.ProviderDB;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ProviderApi extends AbstractToken implements RouterApi
{
    //private final ProviderDB providerDB = ProviderDB.getInstance();
    private final ProviderDB providerDB = new ProviderDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        return subRouter;
    }

    private void getAllClients(final RoutingContext routingContext)
    {
    }

    private void getAllHisClients(final RoutingContext routingContext)
    {
    }

    private void getClient(final RoutingContext routingContext)
    {
    }

    private void deleteClient(final RoutingContext routingContext)
    {
    }

    private void getAllProposals(final RoutingContext routingContext)
    {
    }

    private void getProposal(final RoutingContext routingContext)
    {
    }

    private void addProposal(final RoutingContext routingContext)
    {
    }

    private void changeProposal(final RoutingContext routingContext)
    {
    }

    private void deleteProposal(final RoutingContext routingContext)
    {
    }

    private void deleteAllConsumptions(final RoutingContext routingContext)
    {
    }

    private void deleteConsumption(final RoutingContext routingContext)
    {
    }

    private void providerProposeContract(final RoutingContext routingContext)
    {
    }
}
