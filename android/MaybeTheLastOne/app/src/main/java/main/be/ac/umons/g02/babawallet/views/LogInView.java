package main.be.ac.umons.g02.babawallet.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import main.be.ac.umons.g02.babawallet.R;
import main.be.ac.umons.g02.babawallet.databinding.LogInBinding;
import main.be.ac.umons.g02.babawallet.viewmodels.LoginViewModel;

/**
 * View se chargeant du layout : log_in.xml
 */
public class LogInView extends Fragment {

    private LogInBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = LogInBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn(binding.emailInput.getText().toString(), binding.passordInput.getText().toString());
            }
        });

        binding.forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LogInView.this)
                        .navigate(R.id.forgotPassword);
            }
        });

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LogInView.this)
                        .navigate(R.id.action_FirstFragment_to_SignUp);
            }
        });
    }

    public void logIn(String email, String password)
    {
        CallbackHandler.handleCallback(new LoginViewModel(email, password), this, R.id.action_FirstFragment_to_SecondFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}