package main.be.ac.umons.g02.database;


import main.be.ac.umons.g02.data_object.ContractBasic;
import main.be.ac.umons.g02.data_object.ContractFull;
import main.be.ac.umons.g02.data_object.ProposalFull;


import java.util.ArrayList;

public class ContractManager
{

    /**
     * Supprime un contrat.
     *
     * @param contractId l'identifiant du contrat
     */
    private void deleteContract(String contractId)
    {
        String[] tables = {"contract", "counter", "provider_contract", "wallet_contract"};

        for(String table: tables)
            new Query("DELETE FROM "+ table + " WHERE contract_id="+contractId).executeWithoutResult();
    }
    /**
     * Donne le contrat avec un identifiant donné.
     *
     * @param contractId l'id du contrat
     * @return le contrat sous la forme d'un objet ContractFull. Renvoie null si le contrat n'existe pas.
     */
    public ContractFull getContract(String contractId)
    {
        CommonDB commonDB = new CommonDB();

        String query = "SELECT * FROM contract WHERE contract_id="+contractId;
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult
                (
                        "contract_id","proposal_name", "ean","provider_id","address", "client_id", "opening_date", "closing_date"
                ).getTable();

        if(table == Table.EMPTY_TABLE)
            return null;

        ContractFull contract;

        String providerId = table.get(0).get(3);
        ProposalFull proposalFull = commonDB.getProposalManager().getProposal(table.get(0).get(1), providerId);
        String ean = table.get(0).get(2);
        String address = table.get(0).get(4);
        String clientId = table.get(0).get(5);
        String providerName = commonDB.getLogManager().getName(providerId);
        String clientName = commonDB.getLogManager().getName(clientId);
        String openingDate = table.get(0).get(6);
        String closingDate = table.get(0).get(7);
        contract = new ContractFull(contractId,ean,providerId,clientId,providerName,clientName);
        contract.setMoreInformation(proposalFull,openingDate,closingDate, address);
        return contract;
    }

    /**
     * Donne tous les contrats d'un client dans un intervalle : [base, base+limit].
     *
     * @param clientId l'id du client
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return le nombre de contrats du client et une arraylist contenant des objets contratBasics
     */
    public Object[] getAllContracts(String clientId, int base, int limit)
    {
        String query = "SELECT * FROM contract WHERE client_id=" + clientId +" LIMIT "+base + ", " + limit;
        if(limit < 0)
            query = "SELECT * FROM contract WHERE client_id=" + clientId +" LIMIT "+base + ", " + "18446744073709551615"; //18446744073709551615 max(BigInt) in mysql

        String clientName = new Query("SELECT name FROM user WHERE id="+clientId).executeAndGetResult("name").getStringElem(0,0);

        ArrayList<ContractBasic> contractBasics = new ArrayList<>();
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult
                (
                "contract_id", "ean", "provider_id", "client_id"
                ).getTable();

        String providerId;
        String providerName;
        String contractId;
        String ean;
        for (ArrayList<String> row : table)
        {
            providerId = row.get(2);
            DB.getInstance().executeQuery("SELECT name FROM user WHERE id=" + providerId, true);
            providerName = new Query("SELECT name FROM user WHERE id=" + providerId).executeAndGetResult("name").getStringElem(0, 0);
            contractId = row.get(0);
            ean = row.get(1);
            contractBasics.add(new ContractBasic(contractId, ean, providerId, clientId, providerName, clientName));
        }

        String numberOfContractQuery = "SELECT count(*) AS 'c' FROM contract WHERE client_id="+clientId;
        int count = new Query(numberOfContractQuery).executeAndGetResult("c").getIntElem(0,0);

        return new Object[] {count, contractBasics};
    }

    /**
     * Donne tous les contrats communs à un client et un fournisseur dans un intervalle : [base,base+limit].
     *
     * @param clientId l'id du client
     * @param providerId l'id du fournisseur
     * @param base la borne inférieure
     * @param limit le nombre d'éléments
     * @return le nombre total de contrats communs et une arraylist contenant des objets de type ContractBasic
     */
    public Object[] getCommonContracts(String clientId, String providerId, int base, int limit)
    {
        ArrayList<ContractBasic> contractBasics = (ArrayList<ContractBasic>) getAllContracts(clientId, base, limit)[1];
        ArrayList<ContractBasic> results = new ArrayList<>();

        for (ContractBasic contractBasic : contractBasics)
            if (contractBasic.getProviderId().equals(providerId))
                results.add(contractBasic);

        String query = "SELECT count(*) AS 'c' FROM contract WHERE client_id="+clientId + " AND provider_id="+providerId;
        int count = new Query(query).executeAndGetResult("c").getIntElem(0,0);

        return new Object[] {count, results};
    }

    /**
     * Supprime un contrat avec un identifiant donné et envoie une notification de suppression.
     *
     * @param contractId l'identifiant du contrat
     */

    public void deleteContractAndNotify(String contractId, String senderId)
    {
        String query = "SELECT CASE " +
                "WHEN client_id <> "+senderId+" THEN client_id ELSE provider_id " +
                "END AS 'receiver' " +
                "FROM contract " +
                "WHERE contract_id="+contractId;

        String receiverId = new Query(query).executeAndGetResult("receiver").getStringElem(0,0);

        new NotificationManager().createNotification(senderId, receiverId, contractId, "Your contract has been deleted by :" + new LogManager().getName(senderId));
        deleteContract(contractId);
    }

    /**
     * Donne tous les clients ayant pris une certaine proposition pour leur contrat.
     *
     * @param proposalName le nom de la proposition
     * @param providerId l'id du fournisseur
     * @return une arraylist contenant les identifiants des clients.
     */
    public ArrayList<String> getAllClientsOfContract(String proposalName, String providerId)
    {
        String query = "SELECT client_id FROM contract WHERE proposal_name='"+proposalName
                +"' AND provider_id="+providerId;

        return new Query(query).executeAndGetResult("client_id").getColumn(0);
    }

    /**
     * Crée un contrat
     *
     * @param proposalName le nom de la proposition
     * @param ean le code ean du compteur
     * @param providerId l'id du fournisseur
     * @param address l'adresse de la maison
     * @param clientId l'id du client
     * @return vrai si le contrat a été créé, faux sinon
     */

    public boolean createContract(String proposalName, String ean, String providerId, String address, String clientId)
    {
        if(!new ProposalManager().doesTheProposalExist(proposalName, providerId) || !new WalletManager().doesTheWalletExists(address))
            return false;

        new Query("INSERT INTO wallet_contract(address) VALUES('"+address+"')").executeWithoutResult();
        new Query("INSERT INTO provider_contract(provider_id) VALUES("+providerId+")").executeWithoutResult();

        String contractId = new Query("SELECT max(contract_id) AS m FROM provider_contract").executeAndGetResult("m").getStringElem(0,0);
        String openingDate = "CURDATE()";
        String closingDate = "DATE_ADD(CURDATE(), INTERVAL "+new ProposalManager().getProposal(proposalName, providerId).getDuration()+" MONTH)";

        new ConsumptionManager().createCounterOrReplace(ean, contractId);

        new Query("INSERT INTO contract(" +
                " contract_id," +
                " proposal_name," +
                " ean," +
                " provider_id," +
                " address," +
                " client_id," +
                " opening_date," +
                " closing_date)" +
                " VALUES("
                + contractId + ", '" +
                proposalName+"','" +
                ean + "'," +
                providerId + ",'" +
                address + "'," +
                clientId + "," +
                openingDate + "," +
                closingDate + ")").executeWithoutResult();
        return true;
    }

    /**
     * Donne le type d'énergie du compteur associé à un contrat.
     *
     *  @param address l'adresse de la maison
     * @return un enum représentant le type d'énergie
     * @see WalletManager.energyType
     */
    public WalletManager.energyType getTypeOfEnergy(String address)
    {
        String query = "SELECT water, gas, electricity "
                +"FROM proposal "
                +"WHERE (proposal_name,provider_id) "
                +"IN (SELECT proposal_name, provider_id FROM contract WHERE address='"+address + "')";
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult("water", "gas", "electricity").getTable();

        for (ArrayList<String> row : table)
        {
            for (int j = 0; j < row.size(); j++)
                if (row.get(j).equals("1"))
                    return WalletManager.energyType.values()[j];
        }
        return null;
    }

    /**
     * Supprime tous les contrats expirés.
     */
    public void deleteExpiredContracts()
    {
        String query = "SELECT contract_id FROM contract WHERE closing_date <= CURDATE()";
        Table table = new Query(query).executeAndGetResult("contract_id");

        if (table.getTable().size() == 0)
            return;

        ArrayList<String> contractIds = table.getColumn(0);
        for (String contractId : contractIds)
        {
            ContractFull contractFull = getContract(contractId);
            new NotificationManager().createNotification("0",contractFull.getProviderId(), contractId, "Your contract has expired.");
            new NotificationManager().createNotification("0",contractFull.getClientId(), contractId, "Your contract has expired.");
            deleteContract(contractId);
        }

    }

    /**
     * Retourne le type d'énergie d'un contrat en fonction de son identifiant.
     *
     * @param contractId l'identifiant du contrat
     * @return le type d'énergie : electricity||gas||water
     */
    public String getTypeOfEnergyFromContract(String contractId)
    {
        return getContract(contractId).getProposal().getTypeOfEnergy();
    }


}
