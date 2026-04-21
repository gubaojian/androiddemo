package com.example.composelearn

import android.content.Context
import razerdp.basepopup.BasePopupWindow

class TestPopup(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.test_popup)
    }
}