package com.example.composelearn

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.labo.kaji.relativepopupwindow.RelativePopupWindow


class TestPopup2(context: Context) : RelativePopupWindow() {
    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.test_popup, null)
        setContentView(contentView)
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        setFocusable(false)
        setTouchable(true)
        setOutsideTouchable(true)
    }
}