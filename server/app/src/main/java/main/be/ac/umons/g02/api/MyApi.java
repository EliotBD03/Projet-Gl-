package main.be.ac.umons.g02.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Map;
import java.lang.Integer;

public class MyApi extends AbstractVerticle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MyApi.class);

    private static final int pageSize = 10;

    private LogApi logApi;
    private ClientApi clientApi;
    private ProviderApi providerApi;
    private CommonApi commonApi;

    @SuppressWarnings("removal")
    @Override
    public void start() throws Exception
    {
        logApi = new LogApi(this);
        clientApi = new ClientApi(this);
        providerApi = new ProviderApi(this);
        commonApi = new CommonApi(this);

        LOGGER.info("Start...");

        String bind = "localhost";
        int port = 8080;

        Map<String, String> env  = System.getenv();

		if(env.containsKey("IP") && env.containsKey("PORT"))
		{
			bind = env.get("IP");
		    port = new Integer(env.get("PORT"));
		}

        final Router router = Router.router(vertx);

		final Router logApiRouter = logApi.getSubRouter(vertx);
		router.mountSubRouter("/api/login", logApiRouter);

		final Router clientApiRouter = clientApi.getSubRouter(vertx);
		router.mountSubRouter("/api/client", clientApiRouter);

		final Router providerApiRouter = providerApi.getSubRouter(vertx);
		router.mountSubRouter("/api/provider", providerApiRouter);

		final Router commonApiRouter = commonApi.getSubRouter(vertx);
		router.mountSubRouter("/api/common", commonApiRouter);

        LOGGER.info("Lancement du serveur...");
		vertx.createHttpServer().requestHandler(router).listen(port, bind);
    }

    @Override
    public void stop() throws Exception
    {
        LOGGER.info("Stop...");
    }

    public void sendMessageError(final RoutingContext routingContext, String messageError)
    {
        final JsonObject errorJsonResponse = new JsonObject();
        errorJsonResponse.put("error", messageError);
        routingContext.response().setStatusCode(404).putHeader("contentType", "babaWallet/api").end(Json.encode(errorJsonResponse));
    }

    public int convertStringToInt(final RoutingContext routingContext, String param)
    {
        try
        {
            return Integer.parseInt(param);
        }
        catch(NumberFormatException error)
        {
            sendMessageError(routingContext, "Erreur de conversion.");
            return 0;
        }
    }

    public int getLimit(String limitParam)
    {
        try
        {
            int limit = Integer.parseInt(limitParam);
            if (limit <= 0 || limit > pageSize)
                return pageSize;
            return limit;
        }
        catch (NumberFormatException error)
        {
            return pageSize;
        }
    }
}
