package main.be.ac.umons.g02.data_object;

public class InvoiceFull extends InvoiceBasic{

    private String contractId;
    private double remaining;

    private String paymentMethod;
    private String paymentDate;

    public InvoiceFull(String invoiceId, String client_id, double price, double proposal, boolean status) {
        super(invoiceId, client_id, price, proposal, status);
    }

    public String getContractId() {
        return contractId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentDate() {
        return paymentDate;
    }


    public void setMoreInformation(String contractId, double remaining, String paymentMethod, String paymentDate) {
        this.contractId = contractId;
        this.remaining = remaining;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }
    public boolean isPaid() {
        return remaining == 0;
    }

    public String getAlreadyPaid() {
        return String.valueOf(price - remaining);
    }
}
