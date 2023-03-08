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
import java.util.concurrent.*;

/**
 * Classe qui représente le centre d'API
 */
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

		if(!env.containsKey("PASSPHRASE"))
			System.exit(1);

		passPhrase = env.get("PASSPHRASE");

		final Router router = Router.router(vertx);

		jwt = JWTAuth.create(vertx, new JWTAuthOptions()
				.addPubSecKey(new PubSecKeyOptions()
					.setAlgorithm("HS256")
					.setBuffer(passPhrase)));

		Handler<RoutingContext> handleToken = ctx ->
		{
			JWTAuthHandler.create(jwt);

			String token = ctx.request().headers().get("Authorization");
			if(token == null || token.length() <= 7 || !token.substring(7).equals("Bearer "))
			{
				ctx.fail(401);
				return;
			}

			token = token.substring(7);

			if(blackList.contains(token))
				ctx.fail(401);
			else
				ctx.next();
		};

		Handler<RoutingContext> roleHandlerClient = ctx ->
		{
			String role = ctx.user().principal().getString("role");
			if(role != null && role.equals("client"))
				ctx.next();
			else
				ctx.fail(401);
		};

		Handler<RoutingContext> roleHandlerProvider = ctx ->
		{
			String role = ctx.user().principal().getString("role");
			if(role != null && role.equals("provider"))
				ctx.next();
			else
				ctx.fail(401);
		};

		Handler<RoutingContext> roleHandlerCommon = ctx ->
		{
			String role = ctx.user().principal().getString("role");
			if(role != null && (role.equals("client") || role.equals("provider")))
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

		vertx.createHttpServer().requestHandler(router).listen(port, bind);

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> {
			cleanExpiredTokens();
		}, 5, 5, TimeUnit.MINUTES);

		LOGGER.info("Lancement du serveur...");
	}

	@Override
	public void stop() throws Exception
	{
		LOGGER.info("Stop...");
		System.exit(1);
	}

	/**
	 * Méthode qui permet de supprimer les tokens qui sont dans la blacklist et qui sont périmés
	 * Cette méthode est appelée toutes les 5 minutes
	 *
	 */
	private void cleanExpiredTokens()
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

	/**
	 * Méthode qui permet de gérer la pagination
	 * Elle récupère la page, la limite et la phrase  de recherche et effectue toutes les vérifications
	 * L'émetteur doit envoyer obligatoirement un numéro de page ou une phrase de recherche non vide
	 * Si il y a un problème, elle renvoie une erreur à l'émetteur
	 *
	 * @param routingContext - Le contexte de la requête
	 */
	protected Object[] getSlice(final RoutingContext routingContext)
	{
		final String stringPage = routingContext.request().getParam("page");
		final String search = routingContext.request().getParam("search");
		final String stringLimit = routingContext.request().getParam("limit");

		Object[] slice = {null, null, null};

		int page;
		int limit;

		try
		{
			if(stringPage == null)
				page = -1;
			else
				page = Integer.parseInt(stringPage);

			limit = Integer.parseInt(stringLimit);
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

		if (limit <= 0 || limit > pageMaxSize)
			slice[1] = "" + pageDefaultSize;
		else
			slice[1] = "" + limit;

		if(page <= 0)
		{
			if(search == null || search.length() == 0)
			{
				routingContext.response()
					.setStatusCode(400)
					.putHeader("content-type", "application/json")
					.end(Json.encodePrettily(new JsonObject()
								.put("error", "Le numéro de page doit être strictement plus grand que 0 ou la phrase de recherche ne doit pas être vide.")));
				return null;
			}
			else
			{
				slice[0] = -1;
				slice[2] = search;
			}
		}
		else
		{
			slice[0] = "" + ((page - 1) * ((int) slice[1]));
			slice[2] = "";
		}

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
				.putHeader("content-type", "application/json")
				.end(Json.encodePrettily(new JsonObject()
							.put("error", "Il manque des informations dans la requête.")));
			return true;
		}

		return false;
	}
}
