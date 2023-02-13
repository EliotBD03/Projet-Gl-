package main.be.ac.umons.g02.database;

import java.util.ArrayList;

public class CommonDB
{
   //TODO private static final ConsumptionManager consumptionManager = new ConsumptionManager();
    private static final ContractManager contractManager = new ContractManager();
    private static final LanguageManager languageManager = new LanguageManager();
  //TODO  private static final NotificationManager notificationManager = new NotificationManager();
    private static final WalletManager walletManager = new WalletManager();
/*
    public ConsumptionManager getConsumptionManager()
    {
        return consumptionManager;
    }
   TODO
*/
    public ContractManager getContractManager()
    {
        return contractManager;
    }

    public LanguageManager getLanguageManager()
    {
        return languageManager;
    }
/*
    public NotificationManager getNotificationManager()
    {
        return notificationManager;
    }
TODO
*/
    public WalletManager getWalletManager()
    {
        return walletManager;
    }

}
