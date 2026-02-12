package com.zhongpin.lib_base.ktx

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.text.TextUtils
import android.view.View
import androidx.core.graphics.toColorInt

fun Drawable?.extSetDrawableColor(color:String?) {
    if (color == null || TextUtils.isEmpty(color)) {
        return
    }
    val bg = this;
    if (bg is ColorDrawable) {
        bg.color = color.toColorInt()
    } else if (bg is ShapeDrawable) {
        bg.paint.setColor(color.toColorInt())
    }else if (bg is GradientDrawable) {
        bg.setColor(color.toColorInt())
    }
}

fun View?.setBackgroundDrawableColor(color:String?) {
    val background =  this?.background
    background?.extSetDrawableColor(color)
}