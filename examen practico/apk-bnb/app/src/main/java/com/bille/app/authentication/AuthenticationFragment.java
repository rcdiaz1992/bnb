package com.bille.app.authentication;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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

    private static final GuidePage[] PAGES = {
            new GuidePage(2, R.string.step_authentication, R.string.auth_recommendation, R.string.auth_lighting, R.drawable.guide_lighting),
            new GuidePage(3, R.string.guide_good_light, R.string.guide_good_light, R.string.guide_good_light_body, R.drawable.guide_good_light),
            new GuidePage(4, R.string.guide_no_cap, R.string.guide_no_cap, R.string.guide_no_cap_body, R.drawable.guide_no_cap),
            new GuidePage(5, R.string.guide_no_glasses, R.string.guide_no_glasses, R.string.guide_no_glasses_body, R.drawable.guide_no_glasses),
            new GuidePage(6, R.string.guide_ready, R.string.guide_ready, R.string.guide_ready_body, R.drawable.guide_ready)
    };

    private FragmentAuthenticationBinding binding;
    private TextToSpeech textToSpeech;
    private int pageIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            pageIndex = savedInstanceState.getInt("pageIndex");
        }
        binding.buttonSpeak.setOnClickListener(v -> speakRecommendation());
        binding.buttonNext.setOnClickListener(v -> onNext());
        binding.stepHeader.setOnBackClick(v -> goBack());
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
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBack();
            }
        });
        showPage(pageIndex);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageIndex", pageIndex);
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

    private void onNext() {
        if (pageIndex < PAGES.length - 1) {
            showPage(pageIndex + 1);
            return;
        }
        Snackbar.make(binding.getRoot(), R.string.auth_ready, Snackbar.LENGTH_LONG).show();
    }

    private void goBack() {
        if (pageIndex > 0) {
            showPage(pageIndex - 1);
            return;
        }
        NavHostFragment.findNavController(this).navigateUp();
    }

    private void showPage(int index) {
        pageIndex = index;
        GuidePage page = PAGES[index];
        binding.stepHeader.render(
                getString(R.string.step_authentication),
                page.step,
                getString(page.stepName),
                R.drawable.ic_step_auth
        );
        binding.guideKicker.setText(page.kicker);
        binding.guideBody.setText(page.body);
        binding.guidePhoto.setImageResource(page.image);
        binding.guidePhoto.setContentDescription(getString(page.body));
        binding.buttonNext.setText(index == PAGES.length - 1 ? R.string.action_finish : R.string.action_next);
        renderDots(index);
    }

    private void renderDots(int selected) {
        binding.guideDots.removeAllViews();
        int size = dp(8);
        int gap = dp(8);
        for (int index = 0; index < PAGES.length; index++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            if (index > 0) {
                params.setMarginStart(gap);
            }
            dot.setLayoutParams(params);
            dot.setBackgroundResource(index == selected ? R.drawable.bg_dot_selected : R.drawable.bg_dot);
            binding.guideDots.addView(dot);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void speakRecommendation() {
        if (textToSpeech == null || binding == null) {
            return;
        }
        GuidePage page = PAGES[pageIndex];
        String message = getString(page.kicker) + ". " + getString(page.body);
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "auth-tip");
    }

    private static final class GuidePage {
        final int step;
        @StringRes final int stepName;
        @StringRes final int kicker;
        @StringRes final int body;
        @DrawableRes final int image;

        private GuidePage(int step, @StringRes int stepName, @StringRes int kicker, @StringRes int body, @DrawableRes int image) {
            this.step = step;
            this.stepName = stepName;
            this.kicker = kicker;
            this.body = body;
            this.image = image;
        }
    }
}
