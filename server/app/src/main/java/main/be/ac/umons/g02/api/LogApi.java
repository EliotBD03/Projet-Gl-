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
import io.vertx.core.http.HttpHeaders;

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
     * Méthode qui utilise le package de base de données pour vérifier le mail et le mot de passe de l'utilisateur
     * Si tout se passe bien, cette méthode génère un token et l'envoie à l'émetteur
     * En cas d'erreur, elle renvoie le code 400 avec une explication
     * Note que le token contient l'id et le rôle de l'utilisateur
     *
     * @param - Le context de la requête
     * @see LogManager
     */
    private void checkAccount(final RoutingContext routingContext)
    {
        LOGGER.info("CheckAccount...");

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String mail = null;
        if(checkParam((mail = body.getString("mail")), routingContext)) return;

        String pwd = null;
        if(checkParam((pwd = body.getString("pwd")), routingContext)) return;

        String id = commonDB.getLogManager().checkAccount(mail, pwd);

        if(id == null)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.accountNotFound")));
            return;
        }

        String role = "";
        if(commonDB.getLogManager().isClient(id))
            role = "client";
        else
            role = "supplier";

        JsonObject userInfo = new JsonObject()
            .put("id", id)
            .put("role", role);

        String token = jwt.generateToken(userInfo, (new JWTOptions()).setExpiresInMinutes(60));

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("token", "Bearer " + token)
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

        String token = routingContext.request().getHeader("Authorization");
        if(token == null || token.length() <= 7 || !(token.substring(0, 7).equals("Bearer ")))
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.tokenNotSend")));
            return;
        }

        token = token.substring(7);

        blackList.add(token);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour sauvegarder le compte de l'utilisateur
     * Elle vérifie le code que l'utilisateur a recu pour créer le compte de manière sécuriser
     * Si le code est incorrect, cette méthode renvoie le code 400 avec une explication
     * S'il y a eu une erreur lors de la création du compte, cette méthode renvoie le code 503 avec une explication 
     *
     * @param - Le context de la requête
     * @see App
     * @see LogManager
     */
    private void saveAccount(final RoutingContext routingContext)
    {
        LOGGER.info("SaveAccount...");

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String mail = null;
        if(checkParam((mail = body.getString("mail")), routingContext)) return;

        String code = null;
        if(checkParam((code = body.getString("code")), routingContext)) return;


        if(App.checkCode(mail, code))
        {
            String pwd = null;
            if(checkParam((pwd = body.getString("pwd")), routingContext)) return;

            boolean isClient = false;

            try
            {
                if(checkParam((isClient = body.getBoolean("is_client")), routingContext)) return;
            }
            catch(ClassCastException error)
            {
                routingContext.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "error.missingInformation")));
                return;
            }

            String name = null;
            if(checkParam((name = body.getString("name")), routingContext)) return;

            String language = null;
            if(checkParam((language = body.getString("language")), routingContext)) return;

            try
            {
                String id = commonDB.getLogManager().saveAccount(mail, pwd, isClient, name, language);

                String role = "";
                if(isClient)
                    role = "client";
                else
                    role = "supplier";

                JsonObject userInfo = new JsonObject()
                    .put("id", id)
                    .put("role", role);

                String token = jwt.generateToken(userInfo, (new JWTOptions()).setExpiresInMinutes(60));

                routingContext.response()
                    .setStatusCode(200)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("token", "Bearer " + token)
                                .put("role", role)));

            }
            catch(Exception error)
            {
                routingContext.response()
                    .setStatusCode(503)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "error.saveAccount")));
            }
        }
        else
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.incorrectCode")));
    }

    /** 
     * Méthode qui utilise le package de base de données pour rénitialiser le mot de passe de l'utilisateur
     * Elle vérifie le code que l'utilisateur a recu pour changer le mot de passe sans usurpation
     * Si le code est incorrect, cette méthode renvoie le code 400 avec une explication
     * S'il y a eu une erreur lors de la création du compte, cette méthode renvoie le code 503 avec une explication 
     *
     * @param - Le context de la requête
     * @see App
     * @see LogManager
     */
    private void renitializePwd(final RoutingContext routingContext)
    {
        LOGGER.info("RenitializePwd...");

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String mail = null;
        if(checkParam((mail = body.getString("mail")), routingContext)) return;

        String code = null;
        if(checkParam((code = body.getString("code")), routingContext)) return;

        if(App.checkCode(mail, code))
        {
            String newPwd = null;
            if(checkParam((newPwd = body.getString("new_pwd")), routingContext)) return;

            try
            {
                commonDB.getLogManager().changePassword(mail, newPwd);
                routingContext.response()
                    .setStatusCode(200)
                    .putHeader("Content-Type", "application/json")
                    .end();
            }
            catch(Exception error)
            {
                routingContext.response()
                    .setStatusCode(503)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "error.passwordReset")));
            }
        }
        else
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.codeNotCorrect")));
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

        String mail = null;
        if(checkParam(mail = routingContext.request().getParam("mail"), routingContext)) return;

        String code = App.createCode(mail);

        try
        {
            App.sendEmail(mail, "BabaWallet", "Thank you for your registration.\nHere is the code " + code + ".\nJust go back to the site and enter it in the right place.");
            routingContext.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end();
        }
        catch(RuntimeException error)
        {
            routingContext.response()
                .setStatusCode(503)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.codeNotSend")));
        }
    }
}
