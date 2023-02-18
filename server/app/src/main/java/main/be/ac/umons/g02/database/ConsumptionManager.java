package main.be.ac.umons.g02.database;

import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class ConsumptionManager
{
    private String getDateFormat(Calendar date)
    {
        return "'" +date.get(Calendar.YEAR)+ "-"+date.get(Calendar.MONTH)+"-"+date.get(Calendar.DAY_OF_MONTH)+"'";
    }

    private boolean isThereSomeValues(String ean, ArrayList<Calendar> dates)
    {
        for(Calendar date: dates)
        {
            DB.getInstance().executeQuery("SELECT EXISTS(SELECT * FROM consumption WHERE ean='"+ean+"' AND date='"+getDateFormat(date)+"') AS c", true);
            if(Integer.parseInt(DB.getInstance().getResults(new String[]{"c"}).get(0).get(0)) != 0)
                return true;
        }
        return false;
    }

    public HashMap<Calendar, Double> getConsumptions(String ean, Calendar startingDate, Calendar closingDate) throws Exception
    {
        String startingDateConverted = getDateFormat(startingDate);
        String closingDateConverted = getDateFormat(closingDate);

        if(!isThereSomeValues(ean, new ArrayList<Calendar>(Arrays.asList(startingDate, closingDate))))
            throw new Exception("The table doesn't contain any consumption with the ean code: "+ ean + " within the interval : "+ startingDate + "and " + closingDate);

        DB.getInstance().executeQuery("SELECT daily_consumption FROM consumption WHERE ean ='"+ean+"' AND date BETWEEN "+ startingDate + " AND " + closingDate, true);
        HashMap<Calendar,Double> consumptions= new HashMap<>();
        ArrayList<ArrayList<String>> results = DB.getInstance().getResults(new String[] {"date", "daily_consumption"});

        for(int i = 0; i < results.get(0).size(); i++)
        {
            String[] date = results.get(0).get(i).split("-");
            consumptions.put(new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])), Double.parseDouble(results.get(1).get(1)));
        }

        return consumptions;
    }

    /**
     * @throws IllegalArgumentException when the size of the two lists isn't the same
     */
    public boolean addConsumption(String ean, ArrayList<Double> values, ArrayList<Calendar> dates, boolean forcingChange)
    {
        if(values.size() != dates.size())
            throw new IllegalArgumentException("the size of the two lists is not the same.\n"+
                                                "values : " + values.size() + "\n" +
                                                "dates  : " + dates.size());

        if(isThereSomeValues(ean, dates) && !forcingChange)
            return false;

        for(int i = 0; i < values.size(); i++)
            DB.getInstance().executeQuery("INSERT INTO consumption(ean, date, daily_consumption) VALUES('"+
                                            ean+"','"+getDateFormat(dates.get(i))+"',"+values.get(i)+
                                            ") ON DUPLICATE KEY UPDATE daily_consumption="+values.get(i), false);
        return true;
    }

    public void deleteAllConsumption(String ean)
    {
        DB.getInstance().executeQuery("DELETE FROM consumption WHERE ean='"+ean+"'",false);
    }

    public void deleteConsumption(String ean, Calendar date)
    {
        DB.getInstance().executeQuery("DELETE FROM consumption WHERE ean='"+ean+"' AND daily_consumption="+date,false);
    }


}
