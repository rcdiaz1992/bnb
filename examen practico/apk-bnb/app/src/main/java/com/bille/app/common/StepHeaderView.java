package com.bille.app.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bille.app.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class StepHeaderView extends LinearLayout {

    private static final int TOTAL_STEPS = 6;

    private MaterialToolbar toolbar;
    private ImageView stepIcon;
    private TextView stepLabel;
    private TextView stepName;
    private LinearProgressIndicator stepProgress;

    public StepHeaderView(Context context) {
        super(context);
        init();
    }

    public StepHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StepHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.view_step_header, this);
        toolbar = findViewById(R.id.toolbar);
        stepIcon = findViewById(R.id.stepIcon);
        stepLabel = findViewById(R.id.stepLabel);
        stepName = findViewById(R.id.stepName);
        stepProgress = findViewById(R.id.stepProgress);
        stepProgress.setMax(TOTAL_STEPS);
        ViewCompat.setOnApplyWindowInsetsListener(this, (view, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), view.getPaddingBottom());
            return insets;
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewCompat.requestApplyInsets(this);
    }

    public void render(String title, int step, String name, int iconRes) {
        toolbar.setTitle(title);
        stepLabel.setText(getResources().getString(R.string.step_counter, step, TOTAL_STEPS));
        stepName.setText(name);
        stepIcon.setImageResource(iconRes);
        stepProgress.setProgressCompat(step, false);
    }

    public void setOnBackClick(OnClickListener listener) {
        toolbar.setNavigationOnClickListener(listener);
    }
}
