package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.InvoiceBasic;
import main.be.ac.umons.g02.data_object.InvoiceFull;

import java.util.ArrayList;

public class InvoiceManager {

    public boolean doesInvoiceExist(String invoiceId) {
        return new Query("SELECT EXISTS(SELECT * FROM invoice WHERE invoice_id='"+invoiceId+"') AS 'c'").executeAndGetResult("c").getIntElem(0,0) == 1;
    }

    public void deleteInvoice(String invoiceId) {
        new Query("DELETE FROM invoice WHERE invoice_id='"+invoiceId+"'").executeWithoutResult();
    }

    public InvoiceFull getInvoice(String invoiceId) {
        if(!doesInvoiceExist(invoiceId))
            return null;

        String query = "SELECT * FROM invoice WHERE invoice_id='"+invoiceId+"'";
        System.out.println(query);
        DB.getInstance().executeQuery(query,true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult(
                "invoice_id",
                "client_id",
                "price",
                "proposal",
                "contract_id",
                "status",
                "payment_method",
                "already_paid",
                "payment_date"
        ).getTable();

        ArrayList<String> row = table.get(0);
        double remaining = Double.parseDouble(row.get(2)) - Double.parseDouble(row.get(7));
        String contractName = getProposalName(row.get(4));

        InvoiceFull invoicefull = new InvoiceFull(invoiceId, row.get(1), Double.parseDouble(row.get(2)), Double.parseDouble(row.get(3)), row.get(5).equals("1"));
        invoicefull.setMoreInformation(
                contractName,
                remaining,
                row.get(6),
                row.get(7)
        );
        return invoicefull;
    }

    public Object[] getInvoices(String clientId, int base, int limit) {
        String query = "SELECT * FROM invoice WHERE client_id='"+clientId+"' LIMIT "+base+","+limit;
        DB.getInstance().executeQuery(query,true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult(
                "invoice_id",
                "client_id",
                "price",
                "proposal",
                "contract_id",
                "status",
                "payment_method",
                "already_paid",
                "payment_date"
        ).getTable();

        ArrayList<InvoiceBasic> invoiceBasics = new ArrayList<>();
        for(ArrayList<String> row : table) {
            invoiceBasics.add(new InvoiceBasic(row.get(0), clientId, Double.parseDouble(row.get(2)), Double.parseDouble(row.get(3)), row.get(5).equals("1")));
        }

        int count = new Query("SELECT COUNT(*) as 'c' FROM invoice WHERE client_id='"+clientId+"'").executeAndGetResult("c").getIntElem(0,0);

        return new Object[]{count, invoiceBasics};
    }

    public String getProposalName(String contract_id){
        String query = "SELECT proposal_name FROM contract WHERE contract_id='"+contract_id+"'";
        DB.getInstance().executeQuery(query,true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult(
                "proposal_name"
        ).getTable();

        return table.get(0).get(0);
    }

    public boolean createInvoice(InvoiceFull invoice) {
        if(doesInvoiceExist(invoice.getContractId())) {
            String query = "UPDATE invoice set client_id='"+invoice.getClientId()+"', price='"+invoice.getPrice()+"', status='"+(invoice.isPaid() ? 1 : 0)+"', payment_method='"+invoice.getPaymentMethod()+"', already_paid='"+invoice.getAlreadyPaid()+"', payment_date='"+invoice.getPaymentDate()+"' WHERE contract_id='"+invoice.getContractId()+"'";
            DB.getInstance().executeQuery(query,false);
            return true;
        } else {
            String query = "INSERT INTO invoice (client_id, price, proposal, contract_id, status, payment_method, already_paid, payment_date) VALUES ('"+invoice.getClientId()+"', '"+invoice.getPrice()+"', '"+invoice.getProposal()+"', '"+invoice.getContractId()+"', '"+(invoice.isPaid() ? 1 : 0)+"', '"+invoice.getPaymentMethod()+"', '"+invoice.getAlreadyPaid()+"', '"+invoice.getPaymentDate()+"')";
            DB.getInstance().executeQuery(query,false);
            return true;
        }
    }

    public void changeProposal(String invoiceId, double proposal) {
        String query = "UPDATE invoice set proposal='"+proposal+"' WHERE invoice_id='"+invoiceId+"'";
        DB.getInstance().executeQuery(query,false);
    }

    public void changePrice(String invoiceId, double price) {
        String actualPrice = "SELECT price FROM invoice WHERE invoice_id='"+invoiceId+"'";
        DB.getInstance().executeQuery(actualPrice,true);
        ArrayList<ArrayList<String>> table = new Query(actualPrice).executeAndGetResult(
                "price"
        ).getTable();
        double actualPriceDouble = Double.parseDouble(table.get(0).get(0));
        price += actualPriceDouble;
        String query = "UPDATE invoice set price='"+price+"' WHERE invoice_id='"+invoiceId+"'";
        DB.getInstance().executeQuery(query,false);

        changeProposal(invoiceId, Math.floor(price/12));

        if (price == 0) {
            String query2 = "UPDATE invoice set status='1' WHERE invoice_id='"+invoiceId+"'";
            DB.getInstance().executeQuery(query2,false);
        }
        else {
            String query2 = "UPDATE invoice set status='0' WHERE invoice_id='"+invoiceId+"'";
            DB.getInstance().executeQuery(query2,false);
        }
    }

    public void changeAlreadyPaid(String invoiceId, double alreadyPaid) {

        String query = "SELECT already_paid FROM invoice WHERE invoice_id='"+invoiceId+"'";
        DB.getInstance().executeQuery(query, true);
        ArrayList<ArrayList<String>> table1 = new Query(query).executeAndGetResult(
                "already_paid"
        ).getTable();
        double already_paid = Double.parseDouble(table1.get(0).get(0));
        alreadyPaid += already_paid;

        String query1 = "UPDATE invoice set already_paid='"+alreadyPaid+"' WHERE invoice_id='"+invoiceId+"'";
        DB.getInstance().executeQuery(query1,false);

        String query2 = "SELECT price FROM invoice WHERE invoice_id='"+invoiceId+"'";
        DB.getInstance().executeQuery(query2,true);
        ArrayList<ArrayList<String>> table = new Query(query2).executeAndGetResult(
                "price"
        ).getTable();
        double price = Double.parseDouble(table.get(0).get(0));

        double newPrice = price - alreadyPaid;
        String query3 = "UPDATE invoice set price='"+newPrice+"' WHERE invoice_id='"+invoiceId+"'";
        DB.getInstance().executeQuery(query3,false);


        changeProposal(invoiceId, Math.floor(newPrice/12));

        if (newPrice == 0) {
            String query4 = "UPDATE invoice set status='1' WHERE invoice_id='"+invoiceId+"'";
            DB.getInstance().executeQuery(query4,false);
        }
        else {
            String query5 = "UPDATE invoice set status='0' WHERE invoice_id='"+invoiceId+"'";
            DB.getInstance().executeQuery(query5,false);
        }
    }
}
