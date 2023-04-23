package main.be.ac.umons.g02.maybethelastone.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import main.be.ac.umons.g02.maybethelastone.R;
import main.be.ac.umons.g02.maybethelastone.databinding.ForgottenPasswordBinding;
import main.be.ac.umons.g02.maybethelastone.viewmodels.Code;
import main.be.ac.umons.g02.maybethelastone.viewmodels.ForgottenPasswordViewModel;
import main.be.ac.umons.g02.maybethelastone.viewmodels.api.APICallback;

/**
 * View se chargeant du layout : forgotten_password.xml
 */
public class ForgottenPasswordView extends Fragment
{
    private ForgottenPasswordBinding binding;
    private ForgottenPasswordView forgottenPasswordView;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        forgottenPasswordView = this;
       binding = ForgottenPasswordBinding.inflate(inflater, container, false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.sendCodeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                CallbackHandler.handleCallback(new Code(binding.emailInput.getText().toString()), forgottenPasswordView,  -1);
            }
        });

        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinitializePassword(binding.emailInput.getText().toString(), binding.codeInput.getText().toString(), binding.newPasswordInput.getText().toString());
            }
        });
    }

    private void reinitializePassword(String email, String code, String newPassword)
    {
        CallbackHandler.handleCallback(new ForgottenPasswordViewModel(email,code,newPassword),
                forgottenPasswordView, R.id.action_FirstFragment_to_SecondFragment);
    }
}
