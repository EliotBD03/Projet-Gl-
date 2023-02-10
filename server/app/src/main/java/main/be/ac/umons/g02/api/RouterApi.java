package main.be.ac.umons.g02.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public interface RouterApi
{
    public Router getSubRouter(final Vertx vertx);
}
