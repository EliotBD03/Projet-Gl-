package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.api.MyApi;

import main.be.ac.umons.g02.database.CommonDB;
import main.be.ac.umons.g02.data_object.WalletBasic;
import main.be.ac.umons.g02.data_object.WalletFull;
import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.ProposalBasic;
import main.be.ac.umons.g02.data_object.ProposalFull;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import io.vertx.core.http.HttpHeaders;
import java.lang.Math;

/**
 * Classe qui gère la catégorie client des requêtes de l'API
 */
public class ClientApi extends MyApi implements RouterApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientApi.class);

  private final CommonDB commonDB = new CommonDB();

  @Override
  public Router getSubRouter(final Vertx vertx)
  {
    final Router subRouter = Router.router(vertx);
    subRouter.route("/*").handler(BodyHandler.create());

    subRouter.get("/wallets/page").handler(this::getAllWallets);
    subRouter.get("/wallets/:address").handler(this::getWallet);
    subRouter.post("/wallets").handler(this::createWallet);
    subRouter.delete("/wallets/:address").handler(this::deleteWallet);
    subRouter.get("/contracts/page").handler(this::getAllContracts);
    subRouter.get("/proposals/page").handler(this::getAllProposals);
    subRouter.get("/proposals/:id_provider/:name_proposal").handler(this::getProposal);
    subRouter.post("/proposeContract").handler(this::clientProposeContract);

    return subRouter;
  }

  /** 
   * Méthode qui utilise le package de base de données pour renvoyer une partie de la liste des portefeuilles d'un client
   * Cette méthode utilise la pagination 
   *
   * @param - Le context de la requête
   * @see WalletManager
   */
  private void getAllWallets(final RoutingContext routingContext)
  {
    LOGGER.info("GetAllWallets...");

    String id = null;
    if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

    int[] slice = getSlice(routingContext);
    if(slice == null)
      return;

    Object[] res = commonDB.getWalletManager().getAllWallets(id, slice[0], slice[1]);
    int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

    ArrayList<WalletBasic> wallets = (ArrayList<WalletBasic>) res[1];

    routingContext.response()
      .setStatusCode(200)
      .putHeader("Content-Type", "application/json")
      .end(Json.encodePrettily(new JsonObject()
            .put("wallets", wallets)
            .put("last_page", numberOfPagesRemaining)));
  }

  /** 
   * Méthode qui utilise le package de base de données pour renvoyer un portefeuille en particulier d'un client en particulier
   *
   * @param - Le context de la requête
   * @see WalletManager
   */
  private void getWallet(final RoutingContext routingContext)
  {
    LOGGER.info("GetWallet...");

    String address = routingContext.pathParam("address");

    WalletFull wallet = commonDB.getWalletManager().getWallet(address);

    routingContext.response()
      .setStatusCode(200)
      .putHeader("Content-Type", "application/json")
      .end(Json.encodePrettily(new JsonObject()
            .put("wallet", wallet)));
  }

  /** 
   * Méthode qui utilise le package de base de données pour créer un nouveau portefeuille dans le base de données
   *
   * @param - Le context de la requête
   * @see WalletManager
   */
  private void createWallet(final RoutingContext routingContext)
  {
    LOGGER.info("CreateWallet...");

    String id = null;
    if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

    JsonObject body = null;
    if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

    String address = null;
    if(checkParam((address = body.getString("address")), routingContext)) return;

    String name = null;
    if(checkParam((name = body.getString("name")), routingContext)) return;

    String nameOwner = commonDB.getLogManager().getName(id);

    WalletBasic wallet = new WalletBasic(address, name, id, nameOwner);

    if(commonDB.getWalletManager().createWallet(wallet))
      routingContext.response()
        .setStatusCode(201)
        .putHeader("Content-Type", "application/json")
        .end();
    else
      routingContext.response()
        .setStatusCode(400)
        .putHeader("Content-Type", "application/json")
        .end(Json.encodePrettily(new JsonObject()
              .put("error", "error.addressNotCorrect")));
  }

  /** 
   * Méthode qui utilise le package de base de données pour supprimer un portefeuille en particulier
   * Cette méthode renvoie le code 405 avec une explication dans le cas où le portefeuille contient encore des contrats
   *
   * @param - Le context de la requête
   * @see WalletManager
   */
  private void deleteWallet(final RoutingContext routingContext)
  {
    LOGGER.info("DeleteWallet...");

    String address = routingContext.pathParam("address");

    if(commonDB.getWalletManager().walletIsEmpty(address))
    {
      commonDB.getWalletManager().deleteWallet(address);
      routingContext.response()
        .setStatusCode(200)
        .putHeader("Content-Type", "application/json")
        .end();
    }
    else
      routingContext.response()
        .setStatusCode(405)
        .putHeader("Content-Type", "application/json")
        .end(Json.encodePrettily(new JsonObject()
              .put("error", "error.walletNotEmpty")));
  }

  /** 
   * Méthode qui utilise le package de base de données pour renvoyer une partie de la liste des contrats d'un client 
   * Cette méthode utilise la pagination
   *
   * @param - Le context de la requête
   * @see ContractManager
   */
  private void getAllContracts(final RoutingContext routingContext)
  {
    LOGGER.info("GetAllContracts...");

    String id = null;
    if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

    int[] slice = getSlice(routingContext);
    if(slice == null)
      return;

    Object[] res = commonDB.getContractManager().getAllContracts(id, slice[0], slice[1]);
    int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

    ArrayList<ContractBasic> contracts = (ArrayList<ContractBasic>) res[1];

    routingContext.response()
      .setStatusCode(200)
      .putHeader("Content-Type", "application/json")
      .end(Json.encodePrettily(new JsonObject()
            .put("contracts", contracts)
            .put("last_page", numberOfPagesRemaining)));
  }

  /** 
   * Méthode qui utilise le package de base de données pour renvoyer une partie de la liste de toutes les propositions de tous les fournisseurs
   * Cette méthode utilise la pagination
   *
   * @param - Le context de la requête
   * @see ProposalManager
   */
  private void getAllProposals(final RoutingContext routingContext)
  {
    LOGGER.info("GetAllProposals...");

    int[] slice = getSlice(routingContext);
    if(slice == null)
      return;

    String energyCategory = null;
    if(checkParam((energyCategory = routingContext.request().getParam("energy_category")), routingContext)) return;

    String regionCategory = null;
    if(checkParam((regionCategory = routingContext.request().getParam("region_category")), routingContext)) return;

    Object[] res = commonDB.getProposalManager().getAllProposals(energyCategory, regionCategory, slice[0], slice[1]);
    int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

    ArrayList<ProposalBasic> proposals = (ArrayList<ProposalBasic>) res[1];

    routingContext.response()
      .setStatusCode(200)
      .putHeader("Content-Type", "application/json")
      .end(Json.encodePrettily(new JsonObject()
            .put("proposals", proposals)
            .put("last_page", numberOfPagesRemaining)));
  }

  /** 
   * Méthode qui utilise le package de base de données pour renvoyer une proposition en particulier 
   *
   * @param - Le context de la requête
   * @see ProposalManager
   */
  private void getProposal(final RoutingContext routingContext)
  {
    LOGGER.info("GetProposal...");

    String idProvider = routingContext.pathParam("id_provider");
    String nameProposal = routingContext.pathParam("name_proposal");

    ProposalFull proposal = commonDB.getProposalManager().getProposal(nameProposal, idProvider);

    routingContext.response()
      .setStatusCode(200)
      .putHeader("Content-Type", "application/json")
      .end(Json.encodePrettily(new JsonObject()
            .put("proposal", proposal)));
  }

  /** 
   * Méthode qui utilise le package de base de données pour créer une nofication afin de prévenir le fournisseur d'une nouvelle proposition de contrat
   *
   * @param - Le context de la requête
   * @see NotificationManager
   */
  private void clientProposeContract(final RoutingContext routingContext)
  {
    LOGGER.info("ClientProposeContract...");

    String id = null;
    if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

    JsonObject body = null;
    if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

    String nameProposal = null;
    if(checkParam((nameProposal = body.getString("name_proposal")), routingContext)) return;

    String idProvider = null;
    if(checkParam((idProvider = body.getString("id_provider")), routingContext)) return;

    String ean = null;
    if(checkParam((ean = body.getString("ean")), routingContext)) return;

    String address = null;
    if(checkParam((address = body.getString("address")), routingContext)) return;

    if(commonDB.getWalletManager().doesTheWalletBelongToHim(id, address))
    {
      String nameClient = commonDB.getLogManager().getName(id);
      commonDB.getNotificationManager().createNotification(id, idProvider, nameProposal, idProvider, "Contract request from " + nameClient + ", ean: " + ean + ".", ean, address);

      routingContext.response()
        .setStatusCode(200)
        .putHeader("Content-Type", "application/json")
        .end();
    }
    else
      routingContext.response()
        .setStatusCode(400)
        .putHeader("Content-Type", "application/json")
        .end(Json.encodePrettily(new JsonObject()
              .put("error", "error.notHisWallet")));
  }
}
