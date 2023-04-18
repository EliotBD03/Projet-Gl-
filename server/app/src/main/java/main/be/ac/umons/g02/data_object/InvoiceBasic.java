package main.be.ac.umons.g02.data_object;

public class InvoiceBasic {
    protected String invoiceId;
    protected double price;
    protected boolean status;

    public InvoiceBasic(String invoiceId, double price, boolean status) {
        this.invoiceId = invoiceId;
        this.price = price;
        this.status = status;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public double getPrice() {
        return price;
    }

    public boolean getStatus() {
        return status;
    }

}
