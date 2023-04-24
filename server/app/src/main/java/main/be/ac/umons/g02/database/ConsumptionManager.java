package main.be.ac.umons.g02.database;

import java.util.*;

public class ConsumptionManager
{

    /**
     * Vérifie s'il existe des consommations pour une date et un ean donné.
     *
     * @param ean le code ean du compteur à inspecter
     * @param dates les dates à vérifier (YYYY-MM-DD)
     * @return vrai s'il existe une consommation pour une des dates et le code ean, faux sinon
     */
    private boolean isThereSomeValues(String ean, ArrayList<String> dates)
    {
        for(String date: dates)
        {
            String query = "SELECT EXISTS(SELECT * FROM consumption WHERE ean='"+ean+"' AND date_recorded='"+date+"') AS c";
            int count = new Query(query).executeAndGetResult("c").getIntElem(0,0);

            if(count != 0)
                return true;
        }
        return false;
    }

    /**
     * Donne la consommation sur un mois pour une date donnée.
     *
     * @param ean le code ean
     * @param month le mois (MM)
     * @param year l'année (YYYY)
     * @return la consommation sous forme d'une HashMap contenant les jours en clé et les consommations associée à ce jour en valeur.
     */
    public HashMap<String, Double> getConsumptionOfMonth(String ean, String month, String year)
    {
        String query = null;
        String lowerBoundDate = new Query("SELECT DATE(assignment_date) as 'd' FROM counter WHERE ean="+ean).executeAndGetResult("d").getStringElem(0,0);
        if(month != null && year != null)
        {
            String openingDate = year + "-" + month + "-01";
            String closingDate = year + "-" + month + "-31";
            query = "SELECT daily_consumption, date_recorded FROM consumption WHERE ean ='"+ean+"' AND date_recorded BETWEEN '"+ openingDate + "' AND '" + closingDate + "'";
        }
        else
            query = "SELECT daily_consumption, date_recorded FROM consumption WHERE ean = '" + ean + "' " +
                    "AND YEAR(date_recorded) = YEAR((SELECT MAX(date_recorded) FROM consumption WHERE ean = '" + ean + "')) " +
                    "AND MONTH(date_recorded) = MONTH((SELECT MAX(date_recorded) FROM consumption WHERE ean = '" + ean + "')) ";

        query += " AND date_recorded >= '"+lowerBoundDate+"'";
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult("date_recorded", "daily_consumption").getTable();
        HashMap<String,Double> consumptions= new HashMap<>();

        for (ArrayList<String> row : table) consumptions.put(row.get(0), Double.parseDouble(row.get(1)));

        return consumptions;
    }

    /**
     * Donne les consommations dans un intervalle de dates donné en plus d'un code ean.
     *
     *  @param ean le code ean
     * @param date la première date (YYYY-MM-DD). Si null, la date la plus récente prise
     * @param isAfter mis à faux si on veut des dates antérieures à date
     * @return une hashmap contenant la date en clé et la consommation en valeur
     */
    public HashMap<String, Double> getConsumptions(String ean, String date, boolean isAfter)
    {
        String lowerBoundDate = new Query("SELECT DATE(assignment_date) as 'd' FROM counter WHERE ean="+ean).executeAndGetResult("d").getStringElem(0,0);
        String inequality = isAfter ? ">" : "<";
        String order = isAfter ? "ASC" : "DESC";
        String query = "SELECT daily_consumption, date_recorded FROM consumption WHERE ean ='" + ean + "' AND date_recorded " + inequality + "'" + date + "' AND date_recorded >= "+lowerBoundDate+" ORDER BY date_recorded " + order + " LIMIT 0, 10;";

        if(date == null)
        {
            query = "SELECT c.daily_consumption, c.date_recorded FROM consumption c INNER JOIN " +
                    "( SELECT date_recorded FROM consumption WHERE ean = '" + ean + "' AND date_recorded <= (SELECT MAX(date_recorded) FROM consumption) " +
                    "ORDER BY date_recorded DESC LIMIT 0, 10) t ON c.date_recorded = t.date_recorded WHERE c.ean = '" + ean + "' AND c.date_recorded >="+lowerBoundDate+" ORDER BY c.date_recorded ASC;";

        }
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult("date_recorded", "daily_consumption").getTable();
        HashMap<String,Double> consumptions= new HashMap<>();

        for (ArrayList<String> row : table) consumptions.put(row.get(0), Double.parseDouble(row.get(1)));

        return consumptions;
    }

    /**
     * Ajoute les consommations pour un code ean.
     *
     * @param ean le code ean du compteur
     * @param values les différentes valeurs
     * @param dates les différentes dates (format d'une date : YYYY-MM-DD)
     * @param forcingChange réécrit sur les valeurs déjà existantes si le booléen est mis à vrai
     * @param isClient vrai si l'utilisateur qui ajout/change les consommations est un client
     * @throws IllegalArgumentException quand la taille des listes n'est pas la même
     * @return vrai dans le cas où une écriture dans la base de données a été faite, faux sinon
     */
    public boolean addConsumption(String ean, ArrayList<Double> values, ArrayList<String> dates, boolean forcingChange, boolean isClient) {
        double electricityPrice = 0.7;
        double gasPrice = 6.5;
        double waterPrice = 2.8;
        if (values.size() != dates.size())
            throw new IllegalArgumentException("the size of the two lists is not the same.\n" +
                    "values : " + values.size() + "\n" +
                    "dates  : " + dates.size());

        if (isThereSomeValues(ean, dates) && !forcingChange)
            return false;

        String previousConsumption = "SELECT CASE daily_consumption" +
                " WHEN MONTH(DATE_SUB(daily_consumption, INTERVAL 1 DAY)) = MONTH(MAX(date_recorded)) THEN daily_consumption ELSE 0.0 END " +
                " AS daily_consumption" +
                " FROM consumption" +
                " WHERE date_recorded IN (SELECT max(date_recorded) FROM consumption) AND ean='" + ean + "'";

        double value = 0.0;
        boolean isConsumptionChanged = false;
        for (int i = 0; i < values.size(); i++) {
            ArrayList<ArrayList<String>> consumptions = new Query(previousConsumption).executeAndGetResult("daily_consumption").getTable();

            if (consumptions.size() != 0) {
                value = Double.parseDouble(consumptions.get(0).get(0));
                isConsumptionChanged = true;
            }

            String query = "INSERT INTO consumption(ean, date_recorded, daily_consumption) VALUES('" +
                    ean + "','" + dates.get(i) + "'," + (values.get(i) + value) +
                    ") ON DUPLICATE KEY UPDATE daily_consumption=" + values.get(i);

            new Query(query).executeWithoutResult();

            if (isConsumptionChanged) {
                String getUsefulColumnsQuery = "SELECT contract_id, provider_id, client_id FROM contract WHERE ean=" + ean;
                Table table = new Query(getUsefulColumnsQuery).executeAndGetResult("contract_id", "provider_id", "client_id");

                String senderId = isClient ? table.getStringElem(0, 2) : table.getStringElem(0, 1);
                String receiverId = isClient ? table.getStringElem(0, 1) : table.getStringElem(0, 2);
                String contractId = table.getStringElem(0, 0);

                new NotificationManager().createNotification(senderId, receiverId, contractId, "the daily consumption in the " + dates.get(i) + " has changed to " + values.get(i) + " for this ean code : " + ean);
                WalletManager.energyType typeOfEnergy = new ContractManager().getTypeOfEnergy(new Query("SELECT address FROM wallet_contract WHERE contract_id IN (SELECT contract_id FROM counter WHERE ean='" + ean + "')").executeAndGetResult("address").getStringElem(0, 0));
                if (typeOfEnergy == WalletManager.energyType.GAS)
                    new InvoiceManager().changePrice(contractId, gasPrice * values.get(i));
                else if (typeOfEnergy == WalletManager.energyType.WATER)
                    new InvoiceManager().changePrice(contractId, waterPrice * values.get(i));
                else
                    new InvoiceManager().changePrice(contractId, electricityPrice * values.get(i));

            }

            value = 0.0;
        }

        String getMaximumValueQuery = "SELECT daily_consumption" +
                " FROM consumption " +
                "WHERE date_recorded IN " +
                "(SELECT max(date_recorded) FROM consumption WHERE ean='" + ean + "')";

        double maxVal = new Query(getMaximumValueQuery).executeAndGetResult("daily_consumption").getDoubleElem(0, 0);

        String getAddressQuery = "SELECT address FROM " +
                "wallet_contract WHERE " +
                "contract_id IN " +
                "(SELECT contract_id FROM counter WHERE ean='" + ean + "')";

        String getClientIdQuery = "SELECT client_id FROM contract WHERE contract_id IN (SELECT contract_id FROM counter WHERE ean='" + ean + "')";

        String address = new Query(getAddressQuery).executeAndGetResult("address").getStringElem(0, 0); //on suppose qu'il y a un seul contrat pour même compteur
        String clientId = new Query(getClientIdQuery).executeAndGetResult("client_id").getStringElem(0, 0);

        new WalletManager().addLastConsumption(address, clientId, maxVal, new ContractManager().getTypeOfEnergy(address));
        return true;
    }

    /**
     * Supprime une consommation pour une date et un ean donné
     *
     * @param ean le code ean
     * @param date la date (YYYY-MM-DD)
     */
    public void deleteConsumption(String ean, String date)
    {
        new Query("DELETE FROM consumption WHERE ean='"+ean+"' AND date_recorded='"+date+"'").executeWithoutResult();
    }

    /**
     * Supprime toutes les consommations pour un ean donné.
     *
     * @param ean le code ean
     */
    public void deleteAllConsumptions(String ean)
    {
        new Query("DELETE FROM consumption WHERE ean='"+ean+"'").executeWithoutResult();
    }


    /**
     * Crée un compteur avec un code ean et le premier contrat associé.
     *
     * @param ean le code ean
     * @param contractId l'id du contrat
     */
    public void createCounterOrReplace(String ean, String contractId)
    {
        new Query("INSERT INTO counter(ean,contract_id) VALUES('"+ean+"',"+contractId+") ON DUPLICATE KEY UPDATE contract_id="+contractId).executeWithoutResult();
    }

}
