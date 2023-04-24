package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Bank;

import java.util.ArrayList;

public class BankManager {
    
    public boolean doesBankExist(String clientId) {
        return new Query("SELECT EXISTS(SELECT * FROM bank WHERE client_id='"+clientId+"') AS 'c'").executeAndGetResult("c").getIntElem(0,0) == 1;
    }
    
    public void addBank(Bank bank) {
        if(doesBankExist(bank.getClientId())) {
            String query = "DELETE FROM bank WHERE client_id='"+bank.getClientId()+"'";
            DB.getInstance().executeQuery(query,false);
        }

        String query = "INSERT INTO bank VALUES ('"+bank.getClientId()+"','"+bank.getPaymentMethod()+"','"+bank.getAccountName()+"','"+bank.getAccountNumber()+"','"+bank.getExpirationDate()+"')";
        DB.getInstance().executeQuery(query,false);
    }
    
    public void changePaymentMethod(String clientId, String paymentMethod) {
        if(!doesBankExist(clientId))
            addBank(new Bank(clientId,"","","",paymentMethod));
        
        new Query("UPDATE bank SET payment_method='"+paymentMethod+"' WHERE client_id='"+clientId+"'").executeWithoutResult();
        new Query("UPDATE invoice SET payment_method='"+paymentMethod+"' WHERE client_id='"+clientId+"'").executeWithoutResult();
    }
    
    public Bank getBank(String clientId) {
        if(!doesBankExist(clientId))
            return null;

        String query = "SELECT * FROM bank WHERE client_id='"+clientId+"'";
        DB.getInstance().executeQuery(query,true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult(
                "client_id",
                "account_name",
                "account_number",
                "expiration_date",
                "payment_method"
        ).getTable();

        ArrayList<String> row = table.get(0);
        return new Bank(row.get(0),row.get(1),row.get(2),row.get(3),row.get(4));
    }

    public void changeAccountInformation(String clientId, String accountName, String accountNumber, String expirationDate, String paymentMethod) {
        if(!doesBankExist(clientId)) {
            System.out.println("Bank doesn't exist");
            addBank(new Bank(clientId,accountName,accountNumber,expirationDate,paymentMethod)); }


        System.out.println("UPDATE bank SET account_name='"+accountName+"', account_number='"+accountNumber+"', expiration_date='"+expirationDate+"', payment_method='"+paymentMethod+"' WHERE client_id='"+clientId+"'");
        new Query("UPDATE bank SET account_name='"+accountName+"', account_number='"+accountNumber+"', expiration_date='"+expirationDate+"', payment_method='"+paymentMethod+"' WHERE client_id='"+clientId+"'").executeWithoutResult();

    }
}
