package main.be.ac.umons.g02.api;

import main.be.ac.umons.g02.api.MyApi;

import main.be.ac.umons.g02.data_object.*;
import main.be.ac.umons.g02.database.CommonDB;

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

        //Extension Claire
        subRouter.get("/invitedWallets/page").handler(this::getAllInvitedWallets);
        subRouter.delete("/invitedClients/:address/:id_invited").handler(this::deleteInvitedClient);
        subRouter.put("/invitedClients/permission").handler(this::changePermission);
        subRouter.get("/invitedWallets/invitations/page").handler(this::getAllInvitation);
        subRouter.post("/invitedWallets/proposeInvitation").handler(this::proposeInvitation);
        subRouter.post("/invitedWallets/acceptInvitation/:id_invitation").handler(this::acceptInvitation);
        subRouter.post("/invitedWallets/refuseInvitation/:id_invitation").handler(this::refuseInvitation);
        subRouter.delete("/invitedWallets/invitations/:id_invitation").handler(this::deleteInvitation);

        //Extension Maxime
        subRouter.get("/invoices/page").handler(this::getAllInvoices);
        subRouter.get("/invoices/:id_invoice").handler(this::getInvoice);
        subRouter.delete("/invoices/:invoice_id").handler(this::deleteInvoice);
        subRouter.put("/invoices/:id_invoice").handler(this::changePaymentMethod);
        subRouter.put("/invoices/:id_invoice/account").handler(this::changeAccountInformation);
        subRouter.post("/invoices").handler(this::createInvoice);
        subRouter.post("/invoices/account").handler(this::addBank);
        subRouter.get("/invoices/account/:client_id").handler(this::getBank);

        //base
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
     * Méthode qui utilise le package de base de données pour renvoyer une partie de la liste des portefeuilles 
     * où un client est invité.
     * Cette méthode utilise la pagination
     *
     * @param - Le contexte de la requête
     * @see WalletManager
     * @author Extension Claire
     */
    private void getAllInvitedWallets(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllInvitedWallets...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        Object[] res = commonDB.getWalletManager().getAllInvitedWallets(id, slice[0], slice[1]);
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
     * Méthode qui utilise le package de base de données pour supprimer un invité d'un portefeuille en particulier.
     *
     * @param - Le contexte de la requête
     * @see InvitedClientManager
     * @author Extension Claire
     */
    private void deleteInvitedClient(final RoutingContext routingContext)
    {
        LOGGER.info("deleteInvitedClient...");

        String address = routingContext.pathParam("address");
        String invitedId = routingContext.pathParam("id_invited");
        if(invitedId.equals("no")){
            if(((invitedId = MyApi.getDataInToken(routingContext, "id")) == null)) return;
        }

        commonDB.getInvitedClientManager().deleteInvitedClient(address, invitedId);
        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour modifier la permission d'un client invité.
     *
     * @param - Le contexte de la requête
     * @see InvitedClientManager
     * @author Extension Claire
     */
    private void changePermission(final RoutingContext routingContext)
    {
        LOGGER.info("changePermission...");

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String address = null;
        if(checkParam((address = body.getString("address")), routingContext)) return;

        String InvitedId = null;
        if(checkParam((InvitedId = body.getString("id_client_invited")), routingContext)) return;

        String permission = null;
        if(checkParam((permission = body.getString("permission")), routingContext)) return;

        commonDB.getInvitedClientManager().changePermission(address, InvitedId, permission);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour renvoyer une partie de la liste des invitations.
     * Cette méthode utilise la pagination 
     *
     * @param - Le contexte de la requête
     * @see InvitationManager
     * @author Extension Claire
     */
    private void getAllInvitation(final RoutingContext routingContext)
    {
        LOGGER.info("getAllInvitation...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        Object[] res = commonDB.getInvitationManager().getAllInvitation(id, slice[0], slice[1]);
        int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

        ArrayList<Invitation> invitations = (ArrayList<Invitation>) res[1];

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("invitations", invitations)
                        .put("last_page", numberOfPagesRemaining)));
    }

    /** 
     * Méthode qui utilise le package de base de données pour créer une invitation afin de prévenir le client 
     * d'une nouvelle invitation à un portefeuille.
     *
     * @param - Le contexte de la requête
     * @see InvitationManager
     * @author Extension Claire
     */
    private void proposeInvitation(final RoutingContext routingContext)
    {
        LOGGER.info("proposeInvitation...");

        String senderId = null;
        if(((senderId = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String receiverId = null;
        if(checkParam((receiverId = body.getString("id_client_invited")), routingContext)) return;

        String address = null;
        if(checkParam((address = body.getString("address")), routingContext)) return;

        String permission = null;
        if(checkParam((permission = body.getString("permission")), routingContext)) return;

        String nameSender = commonDB.getLogManager().getName(senderId);

        if(commonDB.getInvitationManager().createInvitation(senderId, receiverId, address, permission, nameSender, "request") == false){
            routingContext.response()
                .setStatusCode(500)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.id")));
            return;
        }
        else{
            routingContext.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end();
            return;
        }
    }

    /** 
     * Méthode qui utilise le package de base de données pour faire passer l'invitation à un portefeuille
     * qui a été acceptée.
     *
     * @param - Le contexte de la requête
     * @see InvitationManager
     * @author Extension Claire
     */
    private void acceptInvitation(final RoutingContext routingContext)
    {
        LOGGER.info("AcceptInvitation...");

        String idInvitation = routingContext.pathParam("id_invitation");

        if(commonDB.getInvitationManager().acceptInvitation(idInvitation) == true){
            routingContext.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end();
            return;
        }
        else{
            routingContext.response()
                .setStatusCode(500)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.already")));
            return;
        }

    }

    /** 
     * Méthode qui utilise le package de base de données pour supprimer l'invitation à un portefeuille 
     * et prévenir l'émetteur de l'invitation du refus.
     *
     * @param - Le contexte de la requête
     * @see InvitationManager
     * @author Extension Claire
     */
    private void refuseInvitation(final RoutingContext routingContext)
    {
        LOGGER.info("RefuseInvitation...");

        String idInvitation = routingContext.pathParam("id_invitation");

        commonDB.getInvitationManager().refuseInvitation(idInvitation);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    /** 
     * Méthode qui utilise le package de base de données pour supprimer l'invitation.
     *
     * @param - Le contexte de la requête
     * @see InvitationManager
     * @author Extension Claire
     */
    private void deleteInvitation(final RoutingContext routingContext)
    {
        LOGGER.info("DeleteInvitation...");

        String idInvitation = routingContext.pathParam("id_invitation");

        commonDB.getInvitationManager().deleteInvitation(idInvitation);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
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

        int numberOfResidents = 0;
        int sizeOfHouse = 0;
        boolean isHouse = true;
        boolean isElectricityToCharge = true;
        boolean solarPanels = false;

        try
        {
            if(checkParam((numberOfResidents = body.getInteger("number_of_residents")), routingContext)) return;
            if(checkParam((sizeOfHouse = body.getInteger("size_of_house")), routingContext)) return;
            if(checkParam((isHouse = body.getBoolean("is_house")), routingContext)) return;
            if(checkParam((isElectricityToCharge = body.getBoolean("is_electricity_to_charge")), routingContext)) return;
            if(checkParam((solarPanels = body.getBoolean("solar_panels")), routingContext)) return;
        }
        catch(Exception error)
        {
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.missingInformation")));
            return;
        }

        WalletBasic wallet = new WalletBasic(address, name, id, nameOwner, numberOfResidents, sizeOfHouse, isHouse, isElectricityToCharge, solarPanels);

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

        String energyCategory = routingContext.request().getParam("energy_category");
        String regionCategory = routingContext.request().getParam("region_category");

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

        if(commonDB.getProposalManager().doesTheProposalExist(nameProposal, idProvider))
        {
            ProposalFull proposal = commonDB.getProposalManager().getProposal(nameProposal, idProvider);

            routingContext.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("proposal", proposal)));
        }
        else
            routingContext.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.notHisProposal")));
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
            if(!commonDB.getNotificationManager().createNotification(id, idProvider, nameProposal, idProvider, "Contract request from " + nameClient, ean, address))
            {
                routingContext.response()
                    .setStatusCode(401)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                                .put("error", "error.eanAlreadyUse")));
                return;
            }

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

    private void getAllInvoices(final RoutingContext routingContext)
    {
        LOGGER.info("GetAllInvoices...");

        String id = null;
        if(((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        int[] slice = getSlice(routingContext);
        if(slice == null)
            return;

        Object[] res = commonDB.getInvoiceManager().getInvoices(id, slice[0], slice[1]);
        int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

        ArrayList<InvoiceBasic> invoices = (ArrayList<InvoiceBasic>) res[1];

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("invoices", invoices)
                        .put("last_page", numberOfPagesRemaining)));
    }

    private void getInvoice(final RoutingContext routingContext) {
        LOGGER.info("GetInvoice...");

        int invoiceId = Integer.parseInt(routingContext.pathParam("invoice_id"));

        InvoiceFull invoice = commonDB.getInvoiceManager().getInvoice(invoiceId);

        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(Json.encodePrettily(new JsonObject()
                        .put("invoice", invoice)));
    }

    private void deleteInvoice(final RoutingContext routingContext) {
        LOGGER.info("DeleteInvoice...");

        String invoiceId = routingContext.pathParam("invoice_id");

        commonDB.getInvoiceManager().deleteInvoice(invoiceId);
        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    private void changePaymentMethod(final RoutingContext routingContext) {
        LOGGER.info("ChangePaymentMethod...");

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String invoiceId = null;
        if(checkParam((invoiceId = body.getString("invoice_id")), routingContext)) return;

        String paymentMethod = null;
        if(checkParam((paymentMethod = body.getString("payment_method")), routingContext)) return;

        //convert paymentMethod to boolean
        if(paymentMethod.equals("true"))
            paymentMethod = "1";
        else
            paymentMethod = "0";

        commonDB.getBankManager().changePaymentMethod(invoiceId, paymentMethod);
        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    private void changeAccountInformation(final RoutingContext routingContext) {
        LOGGER.info("ChangeAccountInformation...");

        JsonObject body = null;
        if(checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String invoiceId = null;
        if(checkParam((invoiceId = body.getString("invoice_id")), routingContext)) return;

        String accountName = null;
        if(checkParam((accountName = body.getString("account_name")), routingContext)) return;

        String accountNumber = null;
        if(checkParam((accountNumber = body.getString("account_number")), routingContext)) return;

        String expirationDate = null;
        if(checkParam((expirationDate = body.getString("expiration_date")), routingContext)) return;

        commonDB.getBankManager().changeAccountInformation(invoiceId, accountName, accountNumber, expirationDate);
        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end();
    }

    private void createInvoice(final RoutingContext routingContext) {
        LOGGER.info("CreateInvoice...");

        String id = null;
        if (((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        JsonObject body = null;
        if (checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        double price = 0;
        boolean status = false;
        String contractId = null;
        double remaining = 0;
        String paymentMethod = null;
        String paymentDate = null;
        try {

            if (checkParam((price = body.getDouble("price")), routingContext)) return;

            if (checkParam((status = body.getBoolean("status")), routingContext)) return;
            if (checkParam((contractId = body.getString("contract_id")), routingContext)) return;

            if (checkParam((remaining = body.getDouble("remaining")), routingContext)) return;

            if (checkParam((paymentMethod = body.getString("payment_method")), routingContext)) return;

            if (checkParam((paymentDate = body.getString("payment_date")), routingContext)) return;

        } catch (Exception e) {
            routingContext.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.missingInformation")));
        }

        InvoiceFull invoice = new InvoiceFull(0, id, price, status);
        invoice.setMoreInformation(contractId, remaining, paymentMethod, paymentDate);

        commonDB.getInvoiceManager().createInvoice(invoice);
    }

    private void addBank(final RoutingContext routingContext) {
        LOGGER.info("AddBank...");

        JsonObject body = null;
        if (checkParam((body = routingContext.body().asJsonObject()), routingContext)) return;

        String id = null;
        if (((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        String accountName = null;
        String accountNumber = null;
        String expirationDate = null;
        String paymentMethod = null;
        try {

            if (checkParam((accountName = body.getString("account_name")), routingContext)) return;

            if (checkParam((accountNumber = body.getString("account_number")), routingContext)) return;

            if (checkParam((expirationDate = body.getString("expiration_date")), routingContext)) return;

            if (checkParam((paymentMethod = body.getString("payment_method")), routingContext)) return;

        } catch (Exception error) {
            routingContext.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end(Json.encodePrettily(new JsonObject()
                            .put("error", "error.missingInformation")));
        }

        Bank bank = new Bank(id, accountName, accountNumber, expirationDate, paymentMethod);

        commonDB.getBankManager().addBank(bank);
        routingContext.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end();
    }

    private void getBank(final RoutingContext routingContext) {
        LOGGER.info("GetBank...");

        String id = null;
        if (((id = MyApi.getDataInToken(routingContext, "id")) == null)) return;

        int[] slice = getSlice(routingContext);
        if(slice == null) return;

        Object[] res = commonDB.getBankManager().getBank(id, slice[0], slice[1]);
        int numberOfPagesRemaining = getNumberOfPagesRemaining((int) res[0], slice[1]);

        ArrayList<Bank> banks = (ArrayList<Bank>) res[1];

        routingContext.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end(Json.encodePrettily(new JsonObject()
                        .put("banks", banks)
                        .put("last_page", numberOfPagesRemaining)));
    }
}
