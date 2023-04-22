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
        System.out.println(invoiceId);
        if(!doesInvoiceExist(invoiceId))
            return null;

        String query = "SELECT * FROM invoice WHERE invoice_id='"+invoiceId+"'";
        System.out.println(query);
        DB.getInstance().executeQuery(query,true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult(
                "invoice_id",
                "client_id",
                "price",
                "contract_id",
                "status",
                "payment_method",
                "already_paid",
                "payment_date"
        ).getTable();

        ArrayList<String> row = table.get(0);

        InvoiceFull invoicefull = new InvoiceFull(row.get(0), row.get(1), Double.parseDouble(row.get(2)), row.get(5).equals("1"));
        invoicefull.setMoreInformation(
                row.get(2),
                Double.parseDouble(row.get(5)),
                row.get(4),
                row.get(6)
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
                "contract_id",
                "status",
                "payment_method",
                "already_paid",
                "payment_date"
        ).getTable();

        ArrayList<InvoiceBasic> invoiceBasics = new ArrayList<>();
        for(ArrayList<String> row : table) {
            invoiceBasics.add(new InvoiceBasic(row.get(0), clientId, Double.parseDouble(row.get(1)), row.get(4).equals("1")));
        }

        int count = new Query("SELECT COUNT(*) as 'c' FROM invoice WHERE client_id='"+clientId+"'").executeAndGetResult("c").getIntElem(0,0);

        return new Object[]{count, invoiceBasics};
    }

    public boolean createInvoice(InvoiceFull invoice) {
        if(doesInvoiceExist(invoice.getInvoiceId()))
            return false;

        new Query("INSERT INTO invoice (client_id,price, contract_id, status, payment_method, already_paid, payment_date) VALUES ('"+
                invoice.getPrice()+",'"+
                invoice.getContractId()+"',"+
                (invoice.isPaid() ? 1 : 0)+",'"+
                invoice.getPaymentMethod()+"',"+
                invoice.getAlreadyPaid()+",'"+
                invoice.getPaymentDate()+"')").executeWithoutResult();

        return true;
    }

    /*** public ArrayList<InvoiceBasic> getHistory(String clientId) {
        //Return all invoiceBasic that are paid
        String query = "SELECT * FROM invoice WHERE contract_id IN (SELECT contract_id FROM contract WHERE client_id='"+clientId+"') AND status=1";
        DB.getInstance().executeQuery(query,true);
        ArrayList<ArrayList<String>> table = new Query(query).executeAndGetResult(
                "invoice_id",
                "price",
                "contract_id",
                "status",
                "payment_method",
                "already_paid",
                "payment_date"
        ).getTable();

        ArrayList<InvoiceBasic> invoiceBasics = new ArrayList<>();
        for(ArrayList<String> row : table) {
            invoiceBasics.add(new InvoiceBasic(row.get(0), Double.parseDouble(row.get(1)), row.get(4).equals("1")));
        }
        return invoiceBasics;
    }***/
}
