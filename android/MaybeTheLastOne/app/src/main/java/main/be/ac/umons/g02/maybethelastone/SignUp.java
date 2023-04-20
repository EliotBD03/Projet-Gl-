package main.be.ac.umons.g02.maybethelastone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import main.be.ac.umons.g02.maybethelastone.data.Account;

public class SignUp extends Fragment
{
    private main.be.ac.umons.g02.maybethelastone.databinding.ForgottenPasswordBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = main.be.ac.umons.g02.maybethelastone.databinding.ForgottenPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signIn(binding.emailInput2.toString(), binding.passordInput2.toString(), binding.editTextNumberPassword.toString()))
                    NavHostFragment.findNavController(SignUp.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                else
                    System.out.println("couldn't create account"); //TODO
            }
        });

        binding.sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account.sendCode(binding.emailInput2.toString());
            }
        });

    }

    public boolean signIn(String email, String password, String code)
    {
        return Account.checkAccount(email, password);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

