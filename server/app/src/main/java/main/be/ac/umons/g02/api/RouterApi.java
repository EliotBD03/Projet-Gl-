package main.be.ac.umons.g02.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * Interface pour que chaque sous catégorie de requête ait une sous-route à lui
 */
public interface RouterApi
{
    /**
     * Méthode qui permet de retourner les sous-routes d'une catégorie de requête
     *
     * @param vertx - L'objet qui représente l'API
     * @see LoginApi
     * @see CommonApi
     * @see ClientApi
     * @see ProviderApi 
     */
    public Router getSubRouter(final Vertx vertx);
}
