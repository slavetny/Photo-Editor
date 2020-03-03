package com.slavetny.photoeditor.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.slavetny.photoeditor.R;

public class SelectFilterDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Button redFilter, yellowFilter, blueFilter;

    private OnDialogClickListener listener;

    public SelectFilterDialog(Activity a, OnDialogClickListener listener) {
        super(a);

        this.c = a;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filter_dialog);

        redFilter = findViewById(R.id.redFilter);
        yellowFilter = findViewById(R.id.yellowFilter);
        blueFilter = findViewById(R.id.blueFilter);

        redFilter.setOnClickListener(this);
        yellowFilter.setOnClickListener(this);
        blueFilter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redFilter:
                listener.onDialogImageRunClick(Color.RED);

                dismiss();
                break;
            case R.id.yellowFilter:
                listener.onDialogImageRunClick(Color.YELLOW);

                dismiss();
                break;
            case R.id.blueFilter:
                listener.onDialogImageRunClick(Color.BLUE);

                dismiss();
                break;
            default:
                break;
        }
    }

    public interface OnDialogClickListener {
        void onDialogImageRunClick(int filterColor);
    }
}