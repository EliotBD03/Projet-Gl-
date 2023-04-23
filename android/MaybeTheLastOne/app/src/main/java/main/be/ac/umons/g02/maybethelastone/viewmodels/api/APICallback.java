package main.be.ac.umons.g02.maybethelastone.viewmodels.api;

public interface APICallback
{
    /**
     * à utiliser dans le cas d'une erreur issus d'une requête
     * @param errorMessage le message d'erreur à envoyer
     */
    void onAPIError(String errorMessage);

    /**
     * à utiliser dans le cas d'un succès d'une requête
     */
    void onAPISuccess();
}
