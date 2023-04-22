package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.Bank;

import java.util.ArrayList;

public class BankManager {
    
    public boolean doesBankExist(String clientId) {
        return new Query("SELECT * FROM bank WHERE client_id='"+clientId+"'").executeAndGetResult("client_id").getIntElem(0,0) >= 1;
    }
    
    public boolean addBank(Bank bank) {
        if(doesBankExist(bank.getClientId())) {
            String query = "DELETE FROM bank WHERE client_id='"+bank.getClientId()+"'";
            DB.getInstance().executeQuery(query,true);
        }
        
        String query = "INSERT INTO bank VALUES ('"+bank.getClientId()+"','"+bank.getAccountName()+"','"+bank.getAccountNumber()+"','"+bank.getExpirationDate()+"','"+bank.getPaymentMethod()+"')";
        DB.getInstance().executeQuery(query,true);
        return true;
    }
    
    public boolean changePaymentMethod(String clientId, String paymentMethod) {
        if(!doesBankExist(clientId))
            return false;
        
        String query1 = "UPDATE bank SET payment_method='"+paymentMethod+"' WHERE client_id='"+clientId+"'";
        DB.getInstance().executeQuery(query1,true);
        String query2 = "UPDATE invoice SET payment_method='"+paymentMethod+"' WHERE client_id='"+clientId+"'";
        DB.getInstance().executeQuery(query2,true);
        return true;
    }
    
    public Object[] getBank(String clientId, int base, int limit) {
        if(!doesBankExist(clientId))
            return null;

        String query = "SELECT * FROM bank WHERE client_id='"+clientId+"' LIMIT "+base+","+limit;
        DB.getInstance().executeQuery(query,true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult(
                "client_id",
                "account_name",
                "account_number",
                "expiration_date",
                "payment_method"
        ).getTable();

        ArrayList<Bank> banks = new ArrayList<>();
        for(ArrayList<String> row : table) {
            banks.add(new Bank(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)));
        }

        int count = new Query("SELECT COUNT(*) FROM bank WHERE client_id='"+clientId+"'").executeAndGetResult("COUNT(*)").getIntElem(0,0);

        return new Object[]{banks, count};
    }

    public void changeAccountInformation(String clientId, String accountName, String accountNumber, String expirationDate) {
        String query = "UPDATE bank SET account_name='"+accountName+"', account_number='"+accountNumber+"', expiration_date='"+expirationDate+"' WHERE client_id='"+clientId+"'";
        DB.getInstance().executeQuery(query,true);
    }
}
