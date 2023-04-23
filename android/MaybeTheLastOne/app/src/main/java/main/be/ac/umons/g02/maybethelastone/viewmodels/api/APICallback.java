package main.be.ac.umons.g02.maybethelastone.viewmodels.api;

public interface APICallback
{
    void onAPIError(String errorMessage);
    void onAPISuccess();
}
