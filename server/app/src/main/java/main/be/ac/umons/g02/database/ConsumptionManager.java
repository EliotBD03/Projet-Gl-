package main.be.ac.umons.g02.database;

import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class ConsumptionManager
{

    private boolean isThereSomeValues(String ean, ArrayList<String> dates)
    {
        for(String date: dates)
        {
            DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM consumption WHERE ean='"+ean+"' AND date='"+date+"') AS c", true);
            if(Integer.parseInt(DB.getInstance().getResults(new String[]{"c"}).get(0).get(0)) != 0)
                return true;
        }
        return false;
    }

    public HashMap<String, Double> getConsumptionOfMonth(String ean, String startingDate, String closingDate)
    {
        return null; //TODO on doit en parler avec les autres ?
    }

    public HashMap<String, Double> getConsumptions(String ean, String startingDate, String closingDate)
    {


       //if(!isThereSomeValues(ean, new ArrayList<Calendar>(Arrays.asList(startingDate, closingDate))))
         //   throw new Exception("The table doesn't contain any consumption with the ean code: "+ ean + " within the interval : "+ startingDate + "and " + closingDate);

        DB.getInstance().executeQuery("SELECT daily_consumption FROM consumption WHERE ean ='"+ean+"' AND date BETWEEN "+ startingDate + " AND " + closingDate, true);
        HashMap<Calendar,Double> consumptions= new HashMap<>();
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"date", "daily_consumption"});

        for(int i = 0; i < results.get(0).size(); i++)
        {
            String[] date = results.get(0).get(i).split("-");
            consumptions.put(new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])), Double.parseDouble(results.get(1).get(1)));
        }

        return null; //TODO
    }

    /**
     * @throws IllegalArgumentException when the size of the two lists isn't the same
     */
    public boolean addConsumption(String ean, ArrayList<Double> values, ArrayList<String> dates, boolean forcingChange)
    {
        if(values.size() != dates.size())
            throw new IllegalArgumentException("the size of the two lists is not the same.\n"+
                                                "values : " + values.size() + "\n" +
                                                "dates  : " + dates.size());

        if(isThereSomeValues(ean, dates) && !forcingChange)
           return false;

        String previousConsumption = "SELECT daily_consumption FROM consumption WHERE date=DATE_SUB(CUR_DATE(), INTERVAL 1 DAY)";
        DB.getInstance().executeQuery(previousConsumption, true);
        double value = Double.parseDouble(DB.getInstance().getResults(new String[] {"daily_consumption"}).get(0).get(0));

        for(int i = 0; i < values.size(); i++)
            DB.getInstance().executeQuery("INSERT INTO consumption(ean, date, daily_consumption) VALUES('"+
                                            ean+"','"+dates.get(i)+"',"+(values.get(i)+value)+
                                            ") ON DUPLICATE KEY UPDATE daily_consumption="+values.get(i), false);
        return true;
    }

    public void deleteConsumption(String ean, String date)
    {
        DB.getInstance().executeQuery("DELETE FROM consumption WHERE ean='"+ean+"' AND daily_consumption="+date,false);
    }

    public void deleteAllConsumptions(String ean)
    {
        DB.getInstance().executeQuery("DELETE FROM consumption WHERE ean='"+ean+"'",false);
    }


    public void changeConsumption(String ean, double value, String date)
    {
        DB.getInstance().executeQuery("UPDATE consumption SET daily_consumption="+value+
                " WHERE date="+date+" AND ean="+ean,false);
    }

}
