package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.CommonDB;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

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
        subRouter.get("/:token/clients/:id_provider/:id_client").handler(this::getClient);
        subRouter.delete("/:token/clients/:id_provider/:id_client").handler(this::deleteClient);
        subRouter.get("/:token/proposals").handler(this::getAllProposals);
        subRouter.get("/:token/proposals/:id_proposal").handler(this::getProposal);
        subRouter.post("/:token/proposals").handler(this::addProposal);
        subRouter.put("/:token/proposals").handler(this::changeProposal);
        subRouter.delete("/:token/proposals/:id_proposal").handler(this::deleteProposal);
        subRouter.delete("/:token/consumptions/:ean").handler(this::deleteAllConsumptions);
        subRouter.delete("/:token/consumptions/:ean/:date").handler(this::deleteConsumption);
        subRouter.post("/:token/propose_contract").handler(this::providerProposeContract);

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
