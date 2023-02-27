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
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.core.Handler;
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

        jwt = JWTAuth.create(vertx, new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                    .setAlgorithm("HS256")
                    .setBuffer("mqkfj m( mlksqe ç' mlksf, mqsldjf ? qsf / :")));

        Handler<RoutingContext> roleHandlerClient = ctx ->
        {
            String role = ctx.user().principal().getString("role");
            if (role.equals("client"))
                ctx.next();
            else
                ctx.fail(401);
        };

        Handler<RoutingContext> roleHandlerProvider = ctx ->
        {
            String role = ctx.user().principal().getString("role");
            if (role.equals("provider"))
                ctx.next();
            else
                ctx.fail(401);
        };

        Handler<RoutingContext> roleHandlerCommon = ctx ->
        {
            String role = ctx.user().principal().getString("role");
            if (role.equals("client") || role.equals("provider"))
                ctx.next();
            else
                ctx.fail(401);
        };

        router.route("/api/*").handler(JWTAuthHandler.create(jwt));
        router.route("/api/client/*").handler(roleHandlerClient);
        router.route("/api/provider/*").handler(roleHandlerProvider);
        router.route("/api/common/*").handler(roleHandlerCommon);

        logApi = new LogApi();
        clientApi = new ClientApi();
        providerApi = new ProviderApi();
        commonApi = new CommonApi();

        final Router logApiRouter = logApi.getSubRouter(vertx);
        router.mountSubRouter("/log", logApiRouter);

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
                    .putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "Le numéro de page doit être strictement plus grand que 0.")));
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
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "Les numéros de pages et de limites doivent être des entiers.")));
            return null;
        }
    }
}
