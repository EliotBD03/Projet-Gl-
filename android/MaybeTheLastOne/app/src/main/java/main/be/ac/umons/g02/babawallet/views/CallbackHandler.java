package main.be.ac.umons.g02.babawallet.views;


import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import main.be.ac.umons.g02.babawallet.viewmodels.ViewModel;
import main.be.ac.umons.g02.babawallet.viewmodels.api.APICallback;

public class CallbackHandler
{
    /**
     * appelle la méthode : doYourStuff et gère le retour avec un changement de view si succès
     * ou l'affichage d'un message d'erreur dans le cas d'un non-succès.
     * @param viewModel instance implémentant doYourStuff
     * @param view la view qui appelle la méthode
     * @param action l'action (le changement de view) à réaliser
     */
    public static void handleCallback(ViewModel viewModel, Fragment view, @IdRes int action)
    {
        viewModel.doYourStuff(new APICallback() {
            @Override
            public void onAPIError(String errorMessage) {
                Toast.makeText(view.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAPISuccess() {
                NavHostFragment.findNavController(view).navigate(action);
            }
        });
    }
}
