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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.lang.Integer;
import java.util.Base64;
import javax.json.JsonReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

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

    protected ArrayList<String> blackList = new ArrayList<>();

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

        Handler<RoutingContext> handleToken = ctx ->
        {
            JWTAuthHandler.create(jwt);

            String token = ctx.request().headers().get("Authorization");
            token = token.substring(7);

            if(blackList.contains(token))
                ctx.fail(401);
            else
                ctx.next();
        };

        Handler<RoutingContext> roleHandlerClient = ctx ->
        {
            String role = ctx.user().principal().getString("role");
            if(role.equals("client"))
                ctx.next();
            else
                ctx.fail(401);
        };

        Handler<RoutingContext> roleHandlerProvider = ctx ->
        {
            String role = ctx.user().principal().getString("role");
            if(role.equals("provider"))
                ctx.next();
            else
                ctx.fail(401);
        };

        Handler<RoutingContext> roleHandlerCommon = ctx ->
        {
            String role = ctx.user().principal().getString("role");
            if(role.equals("client") || role.equals("provider"))
                ctx.next();
            else
                ctx.fail(401);
        };

        router.route("/api/*").handler(handleToken);
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

        Vertx.vertx().setPeriodic(5 * 60 * 1000, this::cleanExpiredTokens);
    }

    @Override
    public void stop() throws Exception
    {
        LOGGER.info("Stop...");
        System.exit(1);
    }

    private void cleanExpiredTokens(long timerId)
    {
        Iterator<String> iterator = blackList.iterator();
        while(iterator.hasNext())
        {
            String token = iterator.next();

            String[] parts = token.split("\\.");
            String payload = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            javax.json.JsonObject jsonPayload = javax.json.Json.createReader(new StringReader(payload)).readObject();

            long exp = jsonPayload.getJsonNumber("exp").longValueExact();

            if(Instant.ofEpochSecond(exp).isBefore(Instant.now()))
                iterator.remove();
        }
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
