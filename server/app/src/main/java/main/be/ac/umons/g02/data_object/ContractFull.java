package main.be.ac.umons.g02.data_object;

import java.util.Calendar;

public class ContractFull extends ContractBasic
{
    private ProposalFull proposal;
    private String openingDate;
    private String closingDate;

    public ContractFull(String contractId,  String ean,  String providerId, String clientId, String providerName, String clientName)
    {
        super(contractId, ean, providerId, clientId, providerName, clientName);
    }

    public String getOpeningDate()
    {
        return openingDate;
    }

    public String getClosingDate()
    {
        return closingDate;
    }

    public boolean getIsFixedDate()
    {
        return proposal.isFixedRate();
    }

    public boolean getIsSingleHourCounter()
    {
        return proposal.getIsSingleHour();
    }

    public double getBasicPrice()
    {
        return proposal.getBasicPrice();
    }

    public double getVariableDayPrice()
    {
        return proposal.getVariableDayPrice();
    }

    public double getVariableNightPrice()
    {
        return proposal.getVariableNightPrice();
    }

    public String getStartOfPeakHours()
    {
        return proposal.getStartOfPeakHours();
    }

    public String getEndOfPeakHours()
    {
        return proposal.getEndOfPeakHours();
    }

    public void setMoreInformation(ProposalFull proposal, String openingDate, String closingDate)
    {
        this.proposal = proposal;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
    }
}
