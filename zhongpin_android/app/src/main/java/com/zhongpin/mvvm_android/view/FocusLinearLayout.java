package com.zhongpin.mvvm_android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.zhilianshidai.pindan.app.R;

public class FocusLinearLayout extends LinearLayout {
    public FocusLinearLayout(Context context) {
        super(context);
    }

    public FocusLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FocusLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findAndSetupEditText();
    }

    private void findAndSetupEditText() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof EditText) {
                final EditText editText = (EditText) child;
                editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            setBackgroundResource(R.drawable.edit_input_rect_circle_gray_bg_focused);
                        } else {
                            setBackgroundResource(R.drawable.edit_input_rect_circle_gray_bg_normal);
                        }
                    }
                });
                break;
            }
        }
    }
}
