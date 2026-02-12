package com.longbridge.common.uiLib.drawableview;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author 郭生生
 * on 2020/6/2
 */
public abstract class BaseCustomView extends View {
    protected Context mContext;
    protected AttributeSet mAttrs;

    public BaseCustomView(Context context) {
        this(context, null);
    }

    public BaseCustomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mAttrs = attrs;
        initProperty();
    }

    protected void initProperty() {
    }
}
