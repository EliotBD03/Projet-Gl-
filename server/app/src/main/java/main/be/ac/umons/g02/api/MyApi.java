package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.App;

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
import io.vertx.core.http.HttpHeaders;
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
import java.util.concurrent.*;

/**
 * Classe qui représente le centre d'API
 */
public class MyApi extends AbstractVerticle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MyApi.class);

    private static final int pageDefaultSize = 10;
    private static final int pageMaxSize = 20;

    private String codeToClean = "";

    protected static JWTAuth jwt;
    private LogApi logApi;
    private ClientApi clientApi;
    private ProviderApi providerApi;
    private CommonApi commonApi;

    protected static ArrayList<String> blackList = new ArrayList<>();

    /**
     * Méthode pour lancer l'API, elle est lancé par Vertx
     * Elle défini l'ip et le port de l'API
     * Elle crée un JWTAuth avec une phrase secrête pour avoir des tokens uniques
     * Elle initie les classes sous-routes et les handler liés au token
     *
     * @see App
     */
    @SuppressWarnings("removal")
    @Override
    public void start() throws Exception
    {
        LOGGER.info("Start...");

        String bind = "localhost";
        int port = 8080;
        String passPhrase = "";

        Map<String, String> env  = System.getenv();

        if(env.containsKey("IP") && env.containsKey("PORT"))
        {
            bind = env.get("IP");
            port = new Integer(env.get("PORT"));
        }

        if(!env.containsKey("PASSPHRASE") || !env.containsKey("CODETOCLEAN"))
            System.exit(1);

        passPhrase = env.get("PASSPHRASE");
        codeToClean = env.get("CODETOCLEAN");

        jwt = JWTAuth.create(vertx, new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                    .setAlgorithm("HS256")
                    .setBuffer(passPhrase)));

        final Router router = Router.router(vertx);

		router.route().handler(routingContext -> HandlerUtils.handleSite(routingContext));
        router.options("/*").handler(this::handleOptionsRequest);
        router.route("/api/*").handler(routingContext -> HandlerUtils.handleToken(routingContext));
        router.route("/api/client/*").handler(routingContext -> HandlerUtils.handleRoleClient(routingContext));
        router.route("/api/provider/*").handler(routingContext -> HandlerUtils.handleRoleProvider(routingContext));
        router.route("/api/common/*").handler(routingContext -> HandlerUtils.handleRoleCommon(routingContext));
        router.get("/timer_task/clear_blacklist/:code").handler(this::cleanExpiredTokens);
        router.get("/timer_task/clear_codelist/:code").handler(routingContext -> App.automaticDeleteCode(routingContext));

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

        vertx.createHttpServer().requestHandler(router).listen(port, bind);

        LOGGER.info("Launching the server...");
    }

    @Override
    public void stop() throws Exception
    {
        LOGGER.info("Stop...");
        System.exit(1);
    }

    /**
     * Méthode qui permet de supprimer les tokens qui sont dans la blacklist et qui sont périmés
     * Cette méthode est appelée toutes les 5 minutes par une tâche planifiée d'alwaysdata
     *
     * @param routingContext - Le contexte de la requête
     */
    private void cleanExpiredTokens(final RoutingContext routingContext)
    {
        LOGGER.info("CleanExpiredTokens...");

        String code = routingContext.pathParam("code");

        if(codeToClean.equals(code))
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

            routingContext.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end();
        }
        else
            routingContext.response()
                .setStatusCode(401)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "You are not authorized to do this operation.")));
    }

    /**
     * Méthode qui permet de gérer la pagination
     * Elle récupère la page, la limite et la phrase  de recherche et effectue toutes les vérifications
     * L'émetteur doit envoyer obligatoirement un numéro de page
     * Si il y a un problème, elle renvoie une erreur à l'émetteur
     *
     * @param routingContext - Le contexte de la requête
     */
    protected int[] getSlice(final RoutingContext routingContext)
    {
        String stringPage = routingContext.request().getParam("page");
        String stringLimit = routingContext.request().getParam("limit");

        int[] slice = {0, 0};

        int page;
        int limit;

        try
        {
            if(stringPage == null)
                throw new NumberFormatException();
            else
                page = Integer.parseInt(stringPage);

            if(stringLimit == null)
                limit = pageDefaultSize;
            else
            {
                limit = Integer.parseInt(stringLimit);
                if (limit <= 0 || limit > pageMaxSize)
                    slice[1] = pageDefaultSize;
                else
                    slice[1] = limit;
            }

        }
        catch(NumberFormatException error)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "Page and limit numbers must be integers.")));
            return null;
        }

        if(page <= 0)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "The page number must be strictly greater than 0 or the search phrase must not be empty.")));
            return null;
        }

        slice[0] =(page - 1) * slice[1];

        return slice;
    }

    /**
     * Méthode qui permet de vérifier que les paramètres envoyé lors des requêtes ne soient pas null
     * Cette méthode retourne le code erreur 400 à l'émetteur s'il y a un problème dans la requêtea ainsi que true
     *
     * @param param - Le paramètre a tester
     * @param routingContext - Le contexte de la requête
     */
    protected boolean checkParam(Object param, final RoutingContext routingContext)
    {
        if(param == null)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "The query is missing information.")));
            return true;
        }

        return false;
    }
    
    /**
     * Méthode qui permet de vérifier que les headers de CORS sont corrects
     *
     * @param routingContext - Le contexte de la requête
     */
    private void handleOptionsRequest(RoutingContext routingContext)
    {
        String origin = routingContext.request().getHeader(HttpHeaders.ORIGIN);
        routingContext.response()
            .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin)
            .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS")
            .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type")
            .setStatusCode(200)
            .end();
    }

    /**
     * class qui permet de créer des handler afin de faire des vérifications sur les requêtes à plusieurs niveau
     */
    private class HandlerUtils
    {
        /**
         * Méthode qui permet de créer un handler qui vérifie d'où vient la requête pour accepter seulement les requêtes de notre site
         *
         * @param param - Le contexte de la requête
         * @see MyApi
         */
        public static void handleSite(RoutingContext routingContext)
        {
            LOGGER.info("Check Origin...");

            String origin = routingContext.request().getHeader(HttpHeaders.ORIGIN);
            LOGGER.info("origin " + origin);

            if(true)//routingContext.request().path().contains("/timer_task/") || origin != null && origin.startsWith("https://babawallet-site.alwaysdata.net"))
            {
                routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE, OPTIONS");
                routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type");

                routingContext.next();
            }
            else
                routingContext.response()
                    .setStatusCode(401)
                    .putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "You are not authorized to access this site.")));
        }

        /**
         * Méthode qui permet de créer un handler qui vérifie que la requête contient un token valide
         * Pour être valide, il ne doit pas être expiré et ne doit pas être dans la blacklist
         * Cette dernière contient le token des utilisateurs qui ce sont déconnectés mais que le token n'est pas encore expiré
         *
         * @param param - Le contexte de la requête
         * @see MyApi
         */
        public static void handleToken(RoutingContext routingContext)
        {
            LOGGER.info("Check Token...");

            JWTAuthHandler.create(MyApi.jwt);

            String token = routingContext.request().headers().get("Authorization");
            if(token == null || token.length() <= 7 || !token.substring(7).equals("Bearer "))
            {
                routingContext.response()
                    .setStatusCode(401)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "You are not authorized to access this part of the site.")));
                return;
            }

            token = token.substring(7);

            if(MyApi.blackList.contains(token))
                routingContext.response()
                    .setStatusCode(401)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "You are not authorized to access this part of the site.")));
            else
                routingContext.next();
        };

        /**
         * Méthode qui permet de créer un handler qui vérifie que le rôle de l'utilisateur est client
         *
         * @param param - Le contexte de la requête
         * @see MyApi
         */         
        public static void handleRoleClient(RoutingContext routingContext)
        {
            LOGGER.info("Check Role Client...");

            String role = routingContext.user().principal().getString("role");
            if(role != null && role.equals("client"))
                routingContext.next();
            else
                routingContext.response()
                    .setStatusCode(401)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "You are not authorized to access this part of the site.")));
        };

        /**
         * Méthode qui permet de créer un handler qui vérifie que le rôle de l'utilisateur est fournisseur
         *
         * @param param - Le contexte de la requête
         * @see MyApi
         */
        public static void handleRoleProvider(RoutingContext routingContext)
        {
            LOGGER.info("Check Role Provider...");

            String role = routingContext.user().principal().getString("role");
            if(role != null && role.equals("provider"))
                routingContext.next();
            else
                routingContext.response()
                    .setStatusCode(401)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "You are not authorized to access this part of the site.")));
        };

        /**
         * Méthode qui permet de créer un handler qui vérifie que le rôle de l'utilisateur est client ou fournisseur
         *
         * @param param - Le contexte de la requête
         * @see MyApi
         */
        public static void handleRoleCommon(RoutingContext routingContext)
        {
            LOGGER.info("Check Role Common...");

            String role = routingContext.user().principal().getString("role");
            if(role != null && (role.equals("client") || role.equals("provider")))
                routingContext.next();
            else
                routingContext.response()
                    .setStatusCode(401)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "You are not authorized to access this part of the site.")));
        };
    }
}
