package main.be.ac.umons.g02.maybethelastone.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import main.be.ac.umons.g02.maybethelastone.R;
import main.be.ac.umons.g02.maybethelastone.viewmodels.Code;
import main.be.ac.umons.g02.maybethelastone.viewmodels.SignUpViewModel;
import main.be.ac.umons.g02.maybethelastone.viewmodels.api.APICallback;

public class SignUpView extends Fragment
{
    private main.be.ac.umons.g02.maybethelastone.databinding.SignUpBinding binding;

    private String language;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = main.be.ac.umons.g02.maybethelastone.databinding.SignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguageFromCheckbox();
                signUp(binding.nameInput.getText().toString(), binding.emailInput.getText().toString(), binding.passwordInput.getText().toString(), binding.codeInput.getText().toString(), binding.clientCheckBox.isChecked(), language);
            }
        });

        binding.sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Code(binding.emailInput.getText().toString()).sendCode(new APICallback() {
                    @Override
                    public void onAPIError(String errorMessage) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAPISuccess() {

                    }
                });
            }
        });

    }

    private void setLanguageFromCheckbox()
    {
        if(binding.frenchCheckBox.isChecked())
            language = "french";
        else
            language = "english";
    }
    private void signUp(String name, String email, String password, String code, boolean isCLient, String language)
    {
        new SignUpViewModel(name, email, password, code, isCLient, language).saveAccount(new APICallback() {
            @Override
            public void onAPIError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAPISuccess() {
                NavHostFragment.findNavController(SignUpView.this)
                        .navigate(R.id.action_SignUp_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

