package main.be.ac.umons.g02.data_object;

public class InvoiceBasic {
    protected String client_id;
    protected String invoiceId;
    protected double price;
    protected double proposal;
    protected boolean status;

    public InvoiceBasic(String invoiceId, String client_id, double price, double proposal, boolean status) {
        this.invoiceId = invoiceId;
        this.client_id = client_id;
        this.price = price;
        this.proposal = proposal;
        this.status = status;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public double getPrice() {
        return price;
    }

    public double getProposal() {
        return proposal;
    }

    public boolean getStatus() {
        return status;
    }

    public String getClientId() {
        return client_id;
    }

}
