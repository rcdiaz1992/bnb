package com.bille.app.information;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bille.app.R;
import com.bille.app.databinding.BottomSheetEnableLocationBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EnableLocationBottomSheet extends BottomSheetDialogFragment {

    interface Listener {
        void onContinueWithLocation();
    }

    private Listener listener;

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_Bille_BottomSheet;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if (parent instanceof Listener) {
            listener = (Listener) parent;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_enable_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetEnableLocationBinding binding = BottomSheetEnableLocationBinding.bind(view);
        binding.buttonContinue.setOnClickListener(v -> {
            Listener current = listener;
            dismiss();
            if (current != null) {
                current.onContinueWithLocation();
            }
        });
    }
}
