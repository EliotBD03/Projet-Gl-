package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Bank;
import main.be.ac.umons.g02.data_object.InvoiceFull;

import java.util.ArrayList;

public class BankManager {
    
    public boolean doesBankExist(String clientId) {
        return new Query("SELECT * FROM bank WHERE client_id='"+clientId+"'").executeAndGetResult("client_id").getIntElem(0,0) >= 1;
    }
    
    public boolean addBank(Bank bank) {
        if(doesBankExist(bank.getClientId()))
            return false;
        
        String query = "INSERT INTO bank VALUES ('"+bank.getClientId()+"','"+bank.getAccountName()+"','"+bank.getAccountNumber()+"','"+bank.getExpirationDate()+"','"+(bank.getPaymentMethod() ? 1 : 0)+"')";
        DB.getInstance().executeQuery(query,true);
        return true;
    }
    
    public boolean changePaymentMethod(String clientId, String paymentMethod) {
        if(!doesBankExist(clientId))
            return false;
        
        String query = "UPDATE bank SET payment_method='"+paymentMethod+"' WHERE client_id='"+clientId+"'";
        DB.getInstance().executeQuery(query,true);
        return true;
    }
    
    public Bank getBank(String clientId) {
        if(!doesBankExist(clientId))
            return null;
        
        String query = "SELECT * from bank WHERE client_id='"+clientId+"'";
        DB.getInstance().executeQuery(query,true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult(
                "client_id",
                "account_name",
                "account_number",
                "expiration_date",
                "payment_method"
        ).getTable();
        
        ArrayList<String> row = table.get(0);
        
        Bank bank = new Bank(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4));
        return bank;
    }

    public void changeAccountInformation(String invoiceId, String accountName, String accountNumber, String expirationDate) {
        String query = "UPDATE bank SET account_name='"+accountName+"', account_number='"+accountNumber+"', expiration_date='"+expirationDate+"' WHERE client_id='"+invoiceId+"'";
        DB.getInstance().executeQuery(query,true);
    }
}
