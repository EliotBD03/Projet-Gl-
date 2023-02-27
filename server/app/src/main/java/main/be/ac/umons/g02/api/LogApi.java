package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.database.CommonDB;
import main.be.ac.umons.g02.App;

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

public class LogApi extends MyApi implements RouterApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogApi.class);

    private final CommonDB commonDB = new CommonDB();

    @Override
    public Router getSubRouter(final Vertx vertx)
    {
        final Router subRouter = Router.router(vertx);
        subRouter.route("/*").handler(BodyHandler.create());

        subRouter.post("/check_account").handler(this::checkAccount);
        subRouter.post("/disconnect").handler(this::disconnect);
        subRouter.post("/save_account").handler(this::saveAccount);
        subRouter.put("/renitialize_pwd").handler(this::renitializePwd);
        subRouter.get("/code").handler(this::getCode);

        return subRouter;
    }

    private void checkAccount(final RoutingContext routingContext)
    {
        LOGGER.info("CheckAccount...");

        final JsonObject body = routingContext.getBodyAsJson();
        final String mail = body.getString("mail");
        final String pwd = body.getString("pwd");

        String id = commonDB.getLogManager().checkAccount(mail, pwd);

        if(id == null)
        {
            routingContext.response()
                .setStatusCode(401)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "Compte non trouvé, l'adresse mail ou le mot de passe n'est pas correct.")));
            return;
        }

        String role = "";
        if(commonDB.getLogManager().isClient(id))
            role = "client";
        else
            role = "provider";

        JsonObject userInfo = new JsonObject()
            .put("id", id)
            .put("role", role);

        String token = jwt.generateToken(userInfo);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("token", token)
                        .put("role", role)));
    }

    // A refaire
    private void disconnect(final RoutingContext routingContext)
    {
        LOGGER.info("Disconnect...");
    }

    private void saveAccount(final RoutingContext routingContext)
    {
        LOGGER.info("SaveAccount...");

        final JsonObject body = routingContext.getBodyAsJson();
        final String mail = body.getString("mail");
        final String code = body.getString("code");

        if(App.checkCode(mail, code))
        {
            final String pwd = body.getString("pwd");
            final boolean isClient = body.getBoolean("is_client");
            final String name = body.getString("name");
            final String language = body.getString("language");

            try
            {
                String id = commonDB.getLogManager().saveAccount(mail, pwd, isClient, name, language);

                String role = "";
                if(isClient)
                    role = "client";
                else
                    role = "provider";

                JsonObject userInfo = new JsonObject()
                    .put("id", id)
                    .put("role", role);

                String token = jwt.generateToken(userInfo);

                routingContext.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("token", token)
                                .put("role", role)));

            }
            catch(Exception error)
            {
                routingContext.response()
                    .setStatusCode(503)
                    .putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "La sauvegarde du compte n'a pas pu se faire.")));
            }
        }
        else
            routingContext.response()
                .setStatusCode(401)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "Mauvais code.")));
    }

    private void renitializePwd(final RoutingContext routingContext)
    {
        LOGGER.info("RenitializePwd...");

        final JsonObject body = routingContext.getBodyAsJson();
        final String mail = body.getString("mail");
        final String code = body.getString("code");

        if(App.checkCode(mail, code))
        {
            final String newPwd = body.getString("new_pwd");

            try
            {
                commonDB.getLogManager().changePassword(mail, newPwd);
                routingContext.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json");
            }
            catch(Exception error)
            {
                routingContext.response()
                    .setStatusCode(503)
                    .putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "Erreur de rénitialisation de mot de passe.")));
            }
        }
        else
            routingContext.response()
                .setStatusCode(401)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "Le code entré n'est pas correct.")));
    }

    private void getCode(final RoutingContext routingContext)
    {
        LOGGER.info("GetCode...");

        final JsonObject body = routingContext.getBodyAsJson();
        final String mail = body.getString("mail");

        String code = App.createCode(mail);

        try
        {
            App.sendEmail(mail, "BabaWallet", "Voici le code " + code);
            routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json");
        }
        catch(RuntimeException error)
        {
            routingContext.response()
                .setStatusCode(503)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "Erreur de l'envoie du code.")));
        }
    }
}
