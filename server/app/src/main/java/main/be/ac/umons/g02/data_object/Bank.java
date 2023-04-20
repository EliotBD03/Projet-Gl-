package main.be.ac.umons.g02.data_object;

public class Bank {
    
    private String clientId;
    
    private String accountName;
    private String accountNumber;
    private String expirationDate;
    
    private String paymentMethod;
    
    public Bank(String clientId, String accountName, String accountNumber, String expirationDate, String paymentMethod) {
        this.clientId = clientId;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.expirationDate = expirationDate;
        this.paymentMethod = paymentMethod;
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
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getClientId() {
        return clientId;
    }
}
