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

public class LogApi extends AbstractToken implements RouterApi
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
        subRouter.post("/create_account").handler(this::saveAccount);
        subRouter.put("/renitialize_pwd").handler(this::renitializePwd);
        subRouter.get("/code").handler(this::getCode);

        return null;
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
            final JsonObject errorJsonResponse = new JsonObject();
			errorJsonResponse.put("error", "Compte non trouvé, l'adresse mail ou le mot de passe n'est pas correct.");
			routingContext.response().setStatusCode(404).putHeader("contentType", "babaWallet/api").end(Json.encode(errorJsonResponse));
			return;
        }

        boolean isClient = commonDB.getLogManager().isClient(id);
        String token = createToken(id);
        final JsonObject jsonResponse = new JsonObject();
		jsonResponse.put("token", token);
		jsonResponse.put("isClient", isClient);
		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
    }

    private void disconnect(final RoutingContext routingContext)
    {
        LOGGER.info("Disconnect...");

        final JsonObject body = routingContext.getBodyAsJson();
		final String token = body.getString("token");

        deleteToken(token);

		routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
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
            final boolean isClient = body.getBoolean("isClient");
            final String name = body.getString("name");
            final String language = body.getString("language");

            try
            {
                String id = commonDB.getLogManager().saveAccount(mail, pwd, isClient, name, language);
                String token = createToken(id);

                final JsonObject jsonResponse = new JsonObject();
                jsonResponse.put("token", token);

                routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api").end(Json.encode(jsonResponse));
            }
            catch(Exception error)
            {
                final JsonObject errorJsonResponse = new JsonObject();
                errorJsonResponse.put("error", error.getMessage());
                routingContext.response().setStatusCode(404).putHeader("contentType", "babaWallet/api").end(Json.encode(errorJsonResponse));
            }
        }
        else
        {
            final JsonObject errorJsonResponse = new JsonObject();
            errorJsonResponse.put("error", "Mauvais code");
            routingContext.response().setStatusCode(404).putHeader("contentType", "babaWallet/api").end(Json.encode(errorJsonResponse));
        }
    }

    private void renitializePwd(final RoutingContext routingContext)
    {
        LOGGER.info("RenitializePwd...");

        final JsonObject body = routingContext.getBodyAsJson();
		final String mail = body.getString("mail");
		final String code = body.getString("code");

        if(App.checkCode(mail, code))
        {
            final String newPwd = body.getString("newPwd");

            try
            {
                commonDB.getLogManager().changePassword(mail, newPwd);
                routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
            }
            catch(Exception error)
            {
                final JsonObject errorJsonResponse = new JsonObject();
                errorJsonResponse.put("error", "Une erreur s'est produite");
                routingContext.response().setStatusCode(404).putHeader("contentType", "babaWallet/api").end(Json.encode(errorJsonResponse));
            }
        }
        else
        {
            final JsonObject errorJsonResponse = new JsonObject();
            errorJsonResponse.put("error", "Mauvais code");
            routingContext.response().setStatusCode(404).putHeader("contentType", "babaWallet/api").end(Json.encode(errorJsonResponse));
        }
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
            routingContext.response().setStatusCode(200).putHeader("contentType", "babaWallet/api");
        }
        catch(RuntimeException error)
        {
            final JsonObject errorJsonResponse = new JsonObject();
			errorJsonResponse.put("error", "Erreur de l'envoie du code");
			routingContext.response().setStatusCode(404).putHeader("contentType", "babaWallet/api").end(Json.encode(errorJsonResponse));
        }
    }
}
