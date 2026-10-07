package com.bille.app.authentication;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bille.app.R;
import com.bille.app.databinding.FragmentAuthenticationBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthenticationFragment extends Fragment {

    private FragmentAuthenticationBinding binding;
    private TextToSpeech textToSpeech;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.stepHeader.render(
                getString(R.string.step_authentication),
                2,
                getString(R.string.step_authentication),
                R.drawable.ic_step_auth
        );
        binding.stepHeader.setOnBackClick(v -> NavHostFragment.findNavController(this).navigateUp());
        binding.buttonNext.setOnClickListener(v ->
                Snackbar.make(binding.getRoot(), R.string.auth_ready, Snackbar.LENGTH_LONG).show()
        );
        binding.buttonSpeak.setOnClickListener(v -> speakRecommendation());
        textToSpeech = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS && textToSpeech != null) {
                textToSpeech.setLanguage(new Locale("es", "BO"));
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentPanel, (v, insets) -> {
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottom);
            return insets;
        });
    }

    @Override
    public void onDestroyView() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        binding = null;
        super.onDestroyView();
    }

    private void speakRecommendation() {
        if (textToSpeech == null || binding == null) {
            return;
        }
        String message = getString(R.string.auth_recommendation) + " " + getString(R.string.auth_lighting);
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "auth-tip");
    }
}
