package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.CommonDB;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class LogApi extends AbstractToken implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogApi.class);

    //private final CommonDB commonDB = CommonDB.getInstance();
    private final CommonDB commonDB = new CommonDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.post("/check_account").handler(this::checkAccount);
        subRouter.post("/disconnect").handler(this::disconnect);
        subRouter.post("/create_account").handler(this::saveAccount);
        subRouter.put("/renitialize_pwd").handler(this::renitializePwd);
        subRouter.get("/code").handler(this::getCode);

        return null;
    }

    private void checkAccount(final RoutingContext routingContext)
    {
    }

    private void disconnect(final RoutingContext routingContext)
    {
    }

    private void saveAccount(final RoutingContext routingContext)
    {
    }

    private void renitializePwd(final RoutingContext routingContext)
    {
    }

    private void getCode(final RoutingContext routingContext)
    {
    }
}
