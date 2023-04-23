package main.be.ac.umons.g02.maybethelastone.views;

import main.be.ac.umons.g02.maybethelastone.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import main.be.ac.umons.g02.maybethelastone.databinding.HomeBinding;

/**
 * View se chargeant du layout : home.xml
 */
public class HomeView extends Fragment {

    private HomeBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = HomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(this).asGif().load(R.drawable.catcoding).into(binding.gifImageView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}