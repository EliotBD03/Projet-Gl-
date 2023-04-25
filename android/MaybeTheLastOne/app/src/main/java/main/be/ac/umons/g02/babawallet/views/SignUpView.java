package main.be.ac.umons.g02.babawallet.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import main.be.ac.umons.g02.babawallet.R;
import main.be.ac.umons.g02.babawallet.viewmodels.Code;
import main.be.ac.umons.g02.babawallet.viewmodels.SignUpViewModel;

/**
 * View se chargeant du layout : sign_up.xml
 */
public class SignUpView extends Fragment
{
    private main.be.ac.umons.g02.babawallet.databinding.SignUpBinding binding;
    private SignUpView signUpView;
    private String language;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        signUpView = this;
        binding = main.be.ac.umons.g02.babawallet.databinding.SignUpBinding.inflate(inflater, container, false);
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
                CallbackHandler.handleCallback(new Code(binding.emailInput.getText().toString()), signUpView,  -1);
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
    private void signUp(String name, String email, String password, String code, boolean isClient, String language)
    {
        SignUpViewModel signUpViewModel = new SignUpViewModel(name, email, password, code, isClient, language);
        CallbackHandler.handleCallback(signUpViewModel, this, R.id.action_SignUp_to_SecondFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
