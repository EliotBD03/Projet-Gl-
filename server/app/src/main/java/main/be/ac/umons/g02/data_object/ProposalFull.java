package main.be.ac.umons.g02.data_object;


public class ProposalFull extends ProposalBasic
{
    private double variableDayPrice;
    private double variableNightPrice;
    private boolean isFixedRate;
    private String startOffPeakHours;
    private String endOfPeakHours;
    private int duration; //duration se compte en heure

    public ProposalFull(String providerId, String nameProvider, String typeEnergy, String location, String nameProposal)
    {
        super(nameProposal,providerId,nameProvider,typeEnergy,location);
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

    public String getStartOfPeakHours()
    {
        return startOffPeakHours;
    }

    public String getEndOfPeakHours()
    {
        return endOfPeakHours;
    }
    public int getDuration(){return duration;}

    public void setMoreInformation(double variableDayPrice, double variableNightPrice, boolean isFixedRate, String startOffPeakHours, String endOfPeakHours, int duration)
    {
        this.variableDayPrice = variableDayPrice;
        this.variableNightPrice = variableNightPrice;
        this.isFixedRate = isFixedRate;
        this.startOffPeakHours = startOffPeakHours;
        this.endOfPeakHours = endOfPeakHours;
        this.duration = duration;
    }
}
