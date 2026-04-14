package com.longbridge.android.mdtrade.export.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;

import com.longbridge.core.uitls.DimenUtils;


/**
 * @author zhaofei
 */
public class AutoAdaptSizeWithMaxWidthEditText extends EditText {
    /**
     * 默认文字字体大小最小值(单位：像素)
     */
    private static final float DEFAULT_TEXT_SIZE_MIN = DimenUtils.sp2px(6f);

    /**
     * 默认文字字体大小最大值(单位：像素)(貌似用不上)
     */
    @SuppressWarnings("unused")
    private static final float DEFAULT_TEXT_SIZE_MAX = DimenUtils.sp2px(14f);

    /**
     * XML 中设置的 maxWidth 值（单位：像素）
     */
    private int maxWidthInPx = 0;

    private Runnable autoAdaptTask = new Runnable() {
        @Override
        public void run() {
            autoAdaptTextSize(AutoAdaptSizeWithMaxWidthEditText.this);
        }
    };

    /**
     * 文字字体大小最小值
     */
    private float minTextSize = 0;

    /**
     * 文字字体大小最大值
     */
    private float maxTextSize = 0;

    /**
     * 判断输入文本字体是否变小过
     */
    private boolean hasScaleSmall = false;

    public AutoAdaptSizeWithMaxWidthEditText(Context context) {
        super(context);
    }

    public AutoAdaptSizeWithMaxWidthEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoAdaptSizeWithMaxWidthEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public AutoAdaptSizeWithMaxWidthEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        //未设置字体最小值,则使用默认最小值
        if (0 == minTextSize) {
            minTextSize = DEFAULT_TEXT_SIZE_MIN;
        }
        //未设置字体最大值,则使用当前字体大小作为最大值
        if (0 == maxTextSize) {
            maxTextSize = this.getTextSize();
        }
        //如果设置的值不正确（例如minTextSize>maxTextSize）,则互换
        if (minTextSize > maxTextSize) {
            float minSize = maxTextSize;
            maxTextSize = minTextSize;
            minTextSize = minSize;
        }
        if (attrs != null) {
            int[] attrsArray = new int[]{android.R.attr.maxWidth};
            TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
            maxWidthInPx = ta.getDimensionPixelSize(0, 0);
            ta.recycle();
            // LogUtils.i(TAG, "init: maxWidth from XML = " + maxWidthInPx + "px (" + DimenUtils.px2dp(maxWidthInPx) + "dp)");
        }

    }

    public void setMinTextSize(int minSp) {
        minTextSize = DimenUtils.sp2px(minSp);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        // 根据需要调整字体大小, 延迟50和千分位格式化错开，千分为不会触发onTextChanged,只会改span
        removeCallbacks(autoAdaptTask);
        postDelayed(autoAdaptTask, 30);
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // 对比前后输入前后字体大小
        if (w != oldw) {
            //延迟50和千分位格式化错开
            removeCallbacks(autoAdaptTask);
            postDelayed(autoAdaptTask, 30);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (maxWidthInPx <= 0) {
            return;
        }
        CharSequence text = getText();
        if (TextUtils.isEmpty(text)) {
            text = getHint();
        }
        if (text == null) {
            text = "";
        }
        float textWidth = getPaint().measureText(text, 0 , text.length());
        int hitWidth = 0;
        if (getHint() != null) {
            hitWidth = (int)getPaint().measureText(getHint(), 0, getHint().length());
        }
        // 计算需要的总宽度 = 文本宽度 + padding + 光标预留空间
        float cursorReservedWidth = DimenUtils.dp2px(12f);
        int totalWidth = (int) (textWidth + getPaddingLeft() + getPaddingRight() + cursorReservedWidth);
        totalWidth = Math.min(totalWidth, maxWidthInPx);
        totalWidth = Math.max(totalWidth, hitWidth);
        setMeasuredDimension(totalWidth, getMeasuredHeight());
    }

    /**
     * 调整文本的显示
     */
    private void autoAdaptTextSize(TextView textView) {
        if (null == textView) {
            //参数错误，不与处理
            return;
        }
        //已输入文本
        CharSequence text = textView.getText();
        //已输入文本长度
        int textWidth = textView.getWidth();
        if (textWidth <= 0) {
            return;
        }
        // 如果文本为空，恢复到最大字体大小并重置标记
        if (TextUtils.isEmpty(text)) {
            if (hasScaleSmall) {
                hasScaleSmall = false;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, maxTextSize);
            }
            return;
        }
        //获取输入框总的可输入的文本长度
        float maxInputWidth = maxWidthInPx;
        //获取当前文本字体大小
        float currentTextSize = textView.getTextSize();
        float savedTextSize = getPaint().getTextSize();
        TextPaint paint = getPaint();
        Log.d("AutoScaleSizeEditText", "currentTextSize=" + String.valueOf(currentTextSize));
        //设置画笔的字体大小
        paint.setTextSize(currentTextSize);
        /*
         * 循环减小字体大小，条件如下
         * 1、文本字体小于最大值
         * 2、可输入文本长度小于已输入文本长度
         */
        while ((currentTextSize > minTextSize) && (paint.measureText(text, 0, text.length()) > (maxInputWidth - currentTextSize))) {
            Log.e("tag", "paint.measureText(text)=" + paint.measureText(text, 0, text.length()) + "maxInputWidth:" + maxInputWidth);
            hasScaleSmall = true;
            --currentTextSize;
            if (currentTextSize < minTextSize) {
                currentTextSize = minTextSize;
                break;
            }
            //设置画笔字体大小
            paint.setTextSize(currentTextSize);
        }
        /*
         * 循环增大字体大小，条件如下
         * 1、文本字体小于默认值
         * 2、可输入文本长度大于已输入文本长度
         */
        while (hasScaleSmall && (currentTextSize < maxTextSize) && ((maxInputWidth - currentTextSize) > paint.measureText(text, 0, text.length()))) {
            ++currentTextSize;
            if (currentTextSize > maxTextSize) {
                currentTextSize = maxTextSize;
                break;
            }
            //设置画笔字体大小
            paint.setTextSize(currentTextSize);
        }

        //设置文本字体(单位为像素px)
        paint.setTextSize(savedTextSize);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
    }

}
