package main.be.ac.umons.g02.data_object;


public class ProposalFull extends ProposalBasic
{
    private double basicPrice;
    private double variableDayPrice;
    private double variableNightPrice;
    private boolean isFixedRate;
    private boolean isSingleHourCounter;
    private String startOffPeakHours;
    private String endOfPeakHours;
    private int duration; //duration se compte en mois

    public ProposalFull(String providerId, String nameProvider, String typeEnergy, String location, String nameProposal)
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

    public String getStartOfPeakHours()
    {
        return startOffPeakHours;
    }

    public String getEndOfPeakHours()
    {
        return endOfPeakHours;
    }
    public int getDuration(){return duration;}

    public void setMoreInformation(double basicPrice, double variableDayPrice, double variableNightPrice, boolean isFixedRate, boolean isSingleHourCounter, String startOffPeakHours, String endOfPeakHours, int duration)
    {
        this.basicPrice = basicPrice;
        this.variableDayPrice = variableDayPrice;
        this.variableNightPrice = variableNightPrice;
        this.isFixedRate = isFixedRate;
        this.isSingleHourCounter = isSingleHourCounter;
        this.startOffPeakHours = startOffPeakHours;
        this.endOfPeakHours = endOfPeakHours;
        this.duration = duration;
    }
}
