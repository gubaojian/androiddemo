package global.longbridge.libpierui.button;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
public class RoundButton extends AppCompatTextView {
    public RoundButton(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RoundButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        int radius = dpToPx(context, 18); // 18dp 圆角
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#100F0F")); // 设置背景颜色，可自定义
        drawable.setCornerRadius(radius);
        setBackground(drawable);
    }

    private int dpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
