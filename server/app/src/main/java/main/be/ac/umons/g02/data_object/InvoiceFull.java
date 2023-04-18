package main.be.ac.umons.g02.data_object;

import java.util.ArrayList;

public class InvoiceFull extends InvoiceBasic{

    private ContractBasic contract;
    private ArrayList<Double> accounts;
    private double remaining;

    private String paymentMethod;
    private String accountName;
    private String accountNumber;
    private String expirationDate;
    private String paymentDate;

    public InvoiceFull(String invoiceId, double price, boolean status) {
        super(invoiceId, price, status);
    }

    public ContractBasic getContract() {
        return contract;
    }

    public ArrayList<Double> getAccounts() {
        return accounts;
    }

    public double getRemaining() {
        return remaining;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setMoreInformation(ContractBasic contract, ArrayList<Double> accounts, double remaining, String paymentMethod, String accountName, String accountNumber, String expirationDate, String paymentDate) {
        this.contract = contract;
        this.accounts = accounts;
        this.remaining = remaining;
        this.paymentMethod = paymentMethod;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.expirationDate = expirationDate;
        this.paymentDate = paymentDate;
    }
}
