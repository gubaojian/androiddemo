package com.example.composelearn

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
import razerdp.basepopup.BasePopupWindow

class TestPopup2(context: Context) : RelativePopupWindow() {
    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.test_popup, null)
        setContentView(contentView)
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
    }
}