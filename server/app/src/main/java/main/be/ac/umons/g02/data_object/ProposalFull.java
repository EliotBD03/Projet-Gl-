package main.be.ac.umons.g02.data_object;

import java.util.Calendar;

public class ProposalFull extends ProposalBasic
{
    private double basicPrice;
    private double variableDayPrice;
    private double variableNightPrice;
    private boolean isFixedRate;
    private boolean isSingleHourCounter;
    private Calendar startOffPeakHours;
    private Calendar endOfPeakHours;

    public ProposalFull(String providerId, String nameProvider, TypeEnergy typeEnergy, Location[] location, String nameProposal)
    {
        super(nameProposal,providerId,nameProvider,typeEnergy,location);
    }

    public double getBasicPrice()
    {
        return basicPrice;
    }

    public double getVariableDayPrice()
    {
        return variableDayPrice;
    }

    public double getVariableNightPrice()
    {
        return variableNightPrice;
    }

    public boolean isFixedRate()
    {
        return isFixedRate;
    }

    public boolean getIsSingleHour()
    {
        return isSingleHourCounter;
    }

    public Calendar getStartOfPeakHours()
    {
        return startOffPeakHours;
    }

    public Calendar getEndOfPeakHours()
    {
        return endOfPeakHours;
    }

    public void setMoreInformation(double basicPrice, double variableDayPrice, double variableNightPrice, boolean isFixedRate, boolean isSingleHourCounter, Calendar startOffPeakHours, Calendar endOfPeakHours)
    {
        this.basicPrice = basicPrice;
        this.variableDayPrice = variableDayPrice;
        this.variableNightPrice = variableNightPrice;
        this.isFixedRate = isFixedRate;
        this.isSingleHourCounter = isSingleHourCounter;
        this.startOffPeakHours = startOffPeakHours;
        this.endOfPeakHours = endOfPeakHours;
    }
}
