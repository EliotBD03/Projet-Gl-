package main.be.ac.umons.g02.data_object;

import java.util.Calendar;

public class ContractFull extends ContractBasic
{
    private ProposalFull proposal;
    private Calendar openingDate;
    private Calendar closingDate;

    public ContractFull(String id_contract, String id_client, String id_provider, String nameProvider, String nameClient, String ean)
    {
        super(id_contract, id_client, id_provider, nameProvider, nameClient, ean);
    }

    public Calendar getOpeningDate()
    {
        return openingDate;
    }

    public Calendar getClosingDate()
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

    public Calendar getStartOfPeakHours()
    {
        return proposal.getStartOfPeakHours();
    }

    public Calendar getEndOfPeakHours()
    {
        return proposal.getEndOfPeakHours();
    }

    public void setMoreInformation(ProposalFull proposal, Calendar openingDate, Calendar closingDate)
    {
        this.proposal = proposal;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
    }
}
