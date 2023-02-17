package main.be.ac.umons.g02.database;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ConsumptionManager
{
    public HashMap<Calendar, Double> getConsumptions(String ean, Calendar startingDate, Calendar closingDate)
    {
        String startingDateConverted = "'" +startingDate.get(Calendar.YEAR)+ "-"+startingDate.get(Calendar.MONTH)+"-"+startingDate.get(Calendar.DAY_OF_MONTH)+"'";
        String closingDateConverted = "'"+closingDate.get(Calendar.YEAR)+"-"+closingDate.get(Calendar.MONTH)+"-"+closingDate.get(Calendar.DAY_OF_MONTH)+"'";
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

    public boolean addConsumption(String ean, ArrayList<Integer> values, ArrayList<Calendar> dates, boolean forcingChange)
    {
        //TODO
        return false;
    }

    public void changeConsumption(String ean, double value, Calendar date)
    {
        //TODO
    }
}
