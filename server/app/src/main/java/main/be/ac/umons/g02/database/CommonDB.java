package main.be.ac.umons.g02.database;

/**
 * Fait office d'intermédiaire entre le package api et database.
 */
public class CommonDB
{
    private static final ContractManager contractManager = new ContractManager();
    private static final LanguageManager languageManager = new LanguageManager();
    private static final NotificationManager notificationManager = new NotificationManager();
    private static final WalletManager walletManager = new WalletManager();
    private static final LogManager logManager = new LogManager();
    private static final ConsumptionManager consumptionManager = new ConsumptionManager();
    private static final ProposalManager proposalManager = new ProposalManager();
    private static final ClientManager clientManager = new ClientManager();
    private static final InvitedClientManager invitedClientManager = new InvitedClientManager(); //Extension Claire
    private static final InvitationManager invitationManager = new InvitationManager(); //Extension Claire

    public InvitedClientManager getInvitedClientManager()
    {
        return invitedClientManager;
    }

    public InvitationManager getInvitationManager()
    {
        return invitationManager;
    }

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
    public ProposalManager getProposalManager(){return proposalManager;}

    public ClientManager getClientManager()
    {
        return clientManager;
    }

}
