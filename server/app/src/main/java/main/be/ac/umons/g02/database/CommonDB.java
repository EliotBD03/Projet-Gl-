package main.be.ac.umons.g02.database;

import java.util.ArrayList;

public class CommonDB
{
    private static final ContractManager contractManager = new ContractManager();
    private static final LanguageManager languageManager = new LanguageManager();
    private static final NotificationManager notificationManager = new NotificationManager();
    private static final WalletManager walletManager = new WalletManager();
    private static final LogManager logManager = new LogManager();
    private static final ConsumptionManager consumptionManager = new ConsumptionManager();

    public ConsumptionManager getConsumptionManager()
    {
        return consumptionManager;
    }
    public ContractManager getContractManager()
    {
        return contractManager;
    }
    public LanguageManager getLanguageManager()
    {
        return languageManager;
    }
    public NotificationManager getNotificationManager()
    {
        return notificationManager;
    }
    public LogManager getLogManager()
    {
        return logManager;
    }
    public WalletManager getWalletManager()
    {
        return walletManager;
    }

}