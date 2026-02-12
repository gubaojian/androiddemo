package com.zhongpin.mvvm_android.view;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

public class NoSelectLinkMovementMethod extends LinkMovementMethod {


    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            return true; //禁用选中效果
        }
        return  super.onTouchEvent(widget, buffer, event);
    }
}
