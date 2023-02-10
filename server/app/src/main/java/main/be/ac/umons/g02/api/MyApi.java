package main.be.ac.umons.g02.api;

import io.vertx.core.AbstractVerticle;

public class MyApi extends AbstractVerticle
{
    private final LogApi logApi = new LogApi();
    private final ClientApi clientApi = new ClientApi();
    private final ProviderApi providerApi = new ProviderApi();
    private final CommonApi commonApi = new CommonApi();

    @Override
    public void start() throws Exception
    {
    }

    @Override
    public void stop() throws Exception
    {
    }
}
