package main.be.ac.umons.g02.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Map;

public class MyApi extends AbstractVerticle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MyApi.class);

    private final LogApi logApi = new LogApi();
    private final ClientApi clientApi = new ClientApi();
    private final ProviderApi providerApi = new ProviderApi();
    private final CommonApi commonApi = new CommonApi();

    @SuppressWarnings("removal")
    @Override
    public void start() throws Exception
    {
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
}
