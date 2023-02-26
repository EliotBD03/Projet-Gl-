package main.be.ac.umons.g02.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.auth.jwt.JWTOptions;
//import io.vertx.ext.auth.jwt.JWTAuthOptions;
//import io.vertx.ext.auth.KeyStoreOptions;
import java.util.Map;
import java.lang.Integer;

public class MyApi extends AbstractVerticle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MyApi.class);

    private static final int pageDefaultSize = 10;
    private static final int pageMaxSize = 20;

    protected JWTAuth jwt;
    private LogApi logApi;
    private ClientApi clientApi;
    private ProviderApi providerApi;
    private CommonApi commonApi;

    @SuppressWarnings("removal")
    @Override
    public void start() throws Exception
    {
        LOGGER.info("Start...");

        String bind = "localhost";
        int port = 8080;
        String keystorePwd = "";

        Map<String, String> env  = System.getenv();

        if(env.containsKey("IP") && env.containsKey("PORT"))
        {
            bind = env.get("IP");
            port = new Integer(env.get("PORT"));
        }
        if(env.containsKey("KEYSTOREPWD"))
            keystorePwd = env.get("KEYSTOREPWD");

        final Router router = Router.router(vertx);

        /*
           JWTAuthOptions options = new JWTAuthOptions()
           .addAudience("https://babawallet.alwaysdata.net")
           .setKeyStore(new KeyStoreOptions()
           .setType("jceks")
           .setPath("keystore.jceks")
           .setPassword("keystorePwd"));

           jwt = JWTAuth.create(vertx, options);
           */

        jwt = JWTAuth.create(vertx, new JsonObject().put("keyStore", new JsonObject()
                    .put("type", "jceks")
                    .put("path", "keystore.jceks")
                    .put("password", keystorePwd)));

        logApi = new LogApi();
        clientApi = new ClientApi();
        providerApi = new ProviderApi();
        commonApi = new CommonApi();

        router.route("/api/client/*").handler(JWTAuthHandler.create(jwt).addAuthority("client"));
        router.route("/api/provider/*").handler(JWTAuthHandler.create(jwt).addAuthority("provider"));
        router.route("/api/common/*").handler(JWTAuthHandler.create(jwt).addAuthority("client").addAuthority("provider"));

        final Router logApiRouter = logApi.getSubRouter(vertx);
        router.mountSubRouter("api/log", logApiRouter);

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
        System.exit(1);
    }

    protected int[] getSlice(final RoutingContext routingContext)
    {
        final String stringPage = routingContext.request().getParam("page");
        final String stringLimit = routingContext.request().getParam("limit");

        try
        {
            int[] slice = {0, 0};
            int page = Integer.parseInt(stringPage);
            int limit = Integer.parseInt(stringLimit);

            if(page <= 0)
            {
                routingContext.response()
                    .setStatusCode(400)
                    .putHeader("error", "Le numéro de page doit être strictement plus grand que 0.");
                    return null;
            }

            if (limit <= 0 || limit > pageMaxSize)
                slice[1] = pageDefaultSize;
            else
                slice[1] = limit;

            slice[0] = (page - 1) * slice[1];

            return slice;
        }
        catch(NumberFormatException error)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("error", "Les numéros de pages et de limites doivent être des entiers.");
            return null;
        }
    }
}
