package com.zhongpin.mvvm_android.ui.order

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.SizeUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.OrderFragmentCustomTabBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.PayItemFilterQuery
import com.zhongpin.mvvm_android.ui.pay.view.PayItemFilterBottomSheet




class ShowOrderWaitConfirmRedDotEvent(val num:Int = 0)

fun OrderFragmentCustomTabBinding?.showRedDot(num:Int) {
    this?.let {
        if (!"待签收".equals(textTitle.text.trim().toString())) {
            textTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = 0;
                bottomMargin = 0;
                leftMargin  = 0;
                rightMargin = 0;
            }
            redDot.gone()
            return;
        }
        if (num <= 0) {
            textTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = SizeUtils.dp2px(4.0f);
                bottomMargin = SizeUtils.dp2px(4.0f);
                leftMargin  = SizeUtils.dp2px(12.0f);
                rightMargin = SizeUtils.dp2px(12.0f);
            }
            redDot.gone()
        } else {
            textTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = SizeUtils.dp2px(4.0f);
                bottomMargin = SizeUtils.dp2px(4.0f);
                leftMargin  = SizeUtils.dp2px(12.0f);
                rightMargin = SizeUtils.dp2px(12.0f);
            }
            if (num > 10) {
                redDot.text = ""
            } else {
                redDot.text = num.toString()
            }
            redDot.visible()
        }

    }
}



class ShowPurchaseOrderWaitPayRedDotEvent(val num:Int = 0)
fun purchaseOrderShowRedDot(num:Int, title: TextView, redDot: TextView) {
    if (num <= 0) {
        title.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            rightMargin = SizeUtils.dp2px(0.0f);
        }
        redDot.gone()
    } else {
        title.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            rightMargin = SizeUtils.dp2px(14.0f);
        }
        if (num > 10) {
            redDot.text = ""
        } else {
            redDot.text = num.toString()
        }
        redDot.visible()
    }
}