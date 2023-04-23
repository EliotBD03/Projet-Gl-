package main.be.ac.umons.g02.babawallet.viewmodels;

import main.be.ac.umons.g02.babawallet.viewmodels.api.APICallback;

public interface ViewModel
{
    /**
     * effectuer des requêtes tout en ayant un retour sur celle-ci.
     * @param callback instance permettant d'effectuer un retour de la requête
     */
    void doYourStuff(APICallback callback);
}
