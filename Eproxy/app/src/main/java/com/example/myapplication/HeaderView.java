package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.TextProp;
import com.example.myapplication.databinding.HeaderViewContentBinding;

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class HeaderView extends LinearLayout {

    private HeaderViewContentBinding binding;

    public HeaderView(Context context) {
        super(context);
        init();
    }

    private void init() {
        binding = HeaderViewContentBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @TextProp
    public void setTitle2(CharSequence text) {
        binding.headerViewContent.setText(text);
    }
}