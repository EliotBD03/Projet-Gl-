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
import io.vertx.ext.auth.JWTOptions;

/**
 * Classe qui gère la catégorie login des requêtes de l'API
 */
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

    /** 
     * Méthode qui utilise le package de base de donnée pour vérifier le mail et le mot de passe de l'utilisateur
     * Si tout se passe bien, cette méthode génère un token et l'envoie à l'émetteur
     * En cas d'erreur, elle renvoie le code 401 avec une explication
     * Note que le token contient l'id et le rôle de l'utilisateur
     *
     * @param - Le context de la requête
     * @see LogManager
     */
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

        String token = jwt.generateToken(userInfo, (new JWTOptions()).setExpiresInMinutes(60));

        routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("token", token)
                        .put("role", role)));
    }

    /** 
     * Méthode qui permet de simuler une déconnexion en mettant le token de l'utilisateur dans une blacklist
     *
     * @param - Le context de la requête
     */
    private void disconnect(final RoutingContext routingContext)
    {
        LOGGER.info("Disconnect...");

        String token = routingContext.request().headers().get("Authorization");
        token = token.substring(7);

        blackList.add(token);
    }

    /** 
     * Méthode qui utilise le package de base de donnée pour sauvegarder le compte de l'utilisateur
     * Elle vérifie le code que l'utilisateur a recu pour créer le compte de manière sécuriser
     * Si le code est incorrect, cette méthode renvoie le code 401 avec une explication
     * S'il y a eu une erreur lors de la création du compte, cette méthode renvoie le code 503 avec une explication 
     *
     * @param - Le context de la requête
     * @see App
     * @see LogManager
     */
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

                String token = jwt.generateToken(userInfo, (new JWTOptions()).setExpiresInMinutes(60));

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

    /** 
     * Méthode qui utilise le package de base de donnée pour rénitialiser le mot de passe de l'utilisateur
     * Elle vérifie le code que l'utilisateur a recu pour changer le mot de passe sans usurpation
     * Si le code est incorrect, cette méthode renvoie le code 401 avec une explication
     * S'il y a eu une erreur lors de la création du compte, cette méthode renvoie le code 503 avec une explication 
     *
     * @param - Le context de la requête
     * @see App
     * @see LogManager
     */
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

    /** 
     * Méthode qui utilise la classe App pour envoyer un code qui sera lié au mail
     * Elle renvoie le code 503 avec une explication si le mail n'a pas pu s'envoyer
     *
     * @param - Le context de la requête
     * @see App
     */
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
