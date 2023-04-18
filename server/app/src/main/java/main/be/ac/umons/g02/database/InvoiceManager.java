package main.be.ac.umons.g02.database;

import main.be.ac.umons.g02.data_object.InvoiceBasic;
import main.be.ac.umons.g02.data_object.InvoiceFull;

import java.util.ArrayList;

public class InvoiceManager {

    public boolean doesInvoiceExist(String invoiceId) {
        // TODO
        return false;
    }

    private void deleteInvoice(String invoiceId) {
        // TODO
    }

    public InvoiceFull getInvoice(String invoiceId) {
        // TODO
        return null;
    }

    public Object[] getInvoices(String clientId, int base, int limit) {
        // TODO
        return null;
    }

    public boolean createInvoice(InvoiceBasic invoice) {
        // TODO
        return false;
    }

    public ArrayList<InvoiceBasic> seeHistory(String clientId) {
        // TODO
        return null;
    }

    public boolean isInvoicePaid(String invoiceId) {
        // TODO
        return false;
    }

    public boolean changePaymentMethod(String invoiceId, String paymentMethod) {
        // TODO
        return false;
    }

    public boolean changeAccountInformation(String invoiceId, String accountName, String accountNumber, String expirationDate) {
        // TODO
        return false;
    }
}
