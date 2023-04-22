package main.be.ac.umons.g02.data_object;

public class InvoiceBasic {
    protected String client_id;
    protected int invoiceId;
    protected double price;
    protected boolean status;

    public InvoiceBasic(int invoiceId, String client_id, double price, boolean status) {
        this.invoiceId = invoiceId;
        this.client_id = client_id;
        this.price = price;
        this.status = status;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public double getPrice() {
        return price;
    }

    public boolean getStatus() {
        return status;
    }

    public String getClientId() {
        return client_id;
    }

}
