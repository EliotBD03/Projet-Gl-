package main.be.ac.umons.g02.data_object;

import java.util.ArrayList;

public class InvoiceFull extends InvoiceBasic{

    private String contractId;
    private ArrayList<Double> accounts;
    private double remaining;

    private String paymentMethod;
    private String accountName;
    private String accountNumber;
    private String expirationDate;
    private String paymentDate;

    public InvoiceFull(int invoiceId, String client_id, double price, boolean status) {
        super(invoiceId, client_id, price, status);
    }

    public String getContractId() {
        return contractId;
    }


    public double getRemaining() {
        return remaining;
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
