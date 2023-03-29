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
            DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM consumption WHERE ean='"+ean+"' AND date_recorded='"+date+"') AS c", true);
            if(Integer.parseInt(DB.getInstance().getResults("c").get(0).get(0)) != 0)
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
        String openingDate = year + "-" + month + "-01";
        String closingDate = year + "-" + month + "-LAST_DAY("+month+")";
        return getConsumptions(ean, openingDate, closingDate);
    }

    /**
     * Donne les consommations dans un intervalle de dates donné en plus d'un code ean.
     *
     *  @param ean le code ean
     * @param startingDate la première date (YYYY-MM-DD)
     * @param closingDate la dernière date (YYYY-MM-DD)
     * @return une hashmap contenant la date en clé et la consommation en valeur
     */
    public HashMap<String, Double> getConsumptions(String ean, String startingDate, String closingDate)
    {


       //if(!isThereSomeValues(ean, new ArrayList<Calendar>(Arrays.asList(startingDate, closingDate))))
         //   throw new Exception("The table doesn't contain any consumption with the ean code: "+ ean + " within the interval : "+ startingDate + "and " + closingDate);

        String query = "SELECT daily_consumption, date_recorded FROM consumption WHERE ean ='"+ean+"' AND date_recorded BETWEEN '"+ startingDate + "' AND '" + closingDate + "'";
        DB.getInstance().executeQuery(query, true);
        HashMap<String,Double> consumptions= new HashMap<>();
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults("date_recorded", "daily_consumption");
        for(int i = 0; i < results.get(0).size(); i++)
            consumptions.put(results.get(0).get(i), Double.parseDouble(results.get(1).get(i)));

        return consumptions;
    }

    /**
     * Ajoute les consommations pour un code ean.
     *
     * @param ean le code ean du compteur
     * @param values les différentes valeurs
     * @param dates les différentes dates (format d'une date : YYYY-MM-DD)
     * @param forcingChange réécrit sur les valeurs déjà existantes si le booléen est mis à vrai
     * @throws IllegalArgumentException quand la taille des listes n'est pas la même
     * @return vrai dans le cas où une écriture dans la base de données a été faite, faux sinon
     */
    public boolean addConsumption(String ean, ArrayList<Double> values, ArrayList<String> dates, boolean forcingChange)
    {
        if(values.size() != dates.size())
            throw new IllegalArgumentException("the size of the two lists is not the same.\n"+
                                                "values : " + values.size() + "\n" +
                                                "dates  : " + dates.size());

        if(isThereSomeValues(ean, dates) && !forcingChange)
           return false;

        String previousConsumption = "SELECT CASE daily_consumption"+
                " WHEN MONTH(DATE_SUB(daily_consumption, INTERVAL 1 DAY)) = MONTH(MAX(date_recorded)) THEN daily_consumption ELSE 0.0 END "+
                " AS daily_consumption"+
                " FROM consumption"+
                " WHERE date_recorded IN (SELECT max(date_recorded) FROM consumption) AND ean='"+ean+"'";

        double value = 0.0;
        for(int i = 0; i < values.size(); i++)
        {
            DB.getInstance().executeQuery(previousConsumption, true);
            ArrayList<ArrayList<String>> consumptions = DB.getInstance().getResults("daily_consumption");

            if(consumptions.get(0).size() != 0)
                value = Double.parseDouble(consumptions.get(0).get(0));

            DB.getInstance().executeQuery("INSERT INTO consumption(ean, date_recorded, daily_consumption) VALUES('"+
                    ean+"','"+dates.get(i)+"',"+(values.get(i)+value)+
                    ") ON DUPLICATE KEY UPDATE daily_consumption="+values.get(i), false);
            value = 0.0;
        }

        DB.getInstance().executeQuery("SELECT daily_consumption" +
                " FROM consumption " +
                "WHERE date_recorded IN " +
                "(SELECT max(date_recorded) FROM consumption) " +
                "AND " +
                "ean = '"+ean+"'",true);

        double maxVal = Double.parseDouble(DB.getInstance().getResults("daily_consumption").get(0).get(0));
        DB.getInstance().executeQuery("SELECT address FROM " +
                "wallet_contract WHERE " +
                "contract_id IN " +
                "(SELECT contract_id FROM counter WHERE ean='"+ean+"')",true);
        String address = DB.getInstance().getResults("address").get(0).get(0); //we suppose there is only one contract for one counter
        new WalletManager().addLastConsumption(address, maxVal, new ContractManager().getTypeOfEnergy(address));


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
        DB.getInstance().executeQuery("DELETE FROM consumption WHERE ean='"+ean+"' AND date_recorded='"+date+"'",false);
    }

    /**
     * Supprime toutes les consommations pour un ean donné.
     *
     * @param ean le code ean
     */
    public void deleteAllConsumptions(String ean)
    {
        DB.getInstance().executeQuery("DELETE FROM consumption WHERE ean='"+ean+"'",false);
    }


    /**
     * Crée un compteur avec un code ean et le premier contrat associé.
     *
     * @param ean le code ean
     * @param contractId l'id du contrat
     */
    public void createCounterOrReplace(String ean, String contractId)
    {
        DB.getInstance().executeQuery("INSERT INTO counter(ean,contract_id) VALUES('"+ean+"',"+contractId+") ON DUPLICATE KEY UPDATE contract_id="+contractId,false);
    }

}
