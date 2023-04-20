package main.be.ac.umons.g02.maybethelastone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import main.be.ac.umons.g02.maybethelastone.data.Account;
import main.be.ac.umons.g02.maybethelastone.databinding.FragmentFirstBinding;

public class LogIn extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(logIn(binding.emailInput.toString(), binding.passordInput.toString()))
                    NavHostFragment.findNavController(LogIn.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        binding.forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LogIn.this)
                        .navigate(R.id.forgotPassword);
            }
        });
    }

    public boolean logIn(String email, String password)
    {
        return Account.checkAccount(email, password);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}