package com.zhongpin.mvvm_android.ui.order.preview.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.SpanUtils
import com.lxj.xpopup.impl.PartShadowPopupView
import com.zhongpin.lib_base.utils.LogUtils.e
import com.zhongpin.mvvm_android.bean.PreviewOrderResponse
import com.zhilianshidai.pindan.app.R
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils

@SuppressLint("ViewConstructor")
class PriceDiscountDetailPopupView(
    context: Context,
    private val mPreviewOrderResponse: PreviewOrderResponse?
) : PartShadowPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.preview_order_price_discount_detail
    }

    override fun onCreate() {
        super.onCreate()
        try {
            val close = findViewById<View?>(R.id.closePopup)
            close?.setOnClickListener(object : OnClickListener {
                override fun onClick(v: View?) {
                    dismiss()
                }
            })
            val normalPriceView = findViewById<TextView?>(R.id.normalTotalPrice)
            val cutTotalPriceView = findViewById<TextView?>(R.id.cutTotalPrice)
            val discountTotalPriceView = findViewById<TextView?>(R.id.discountTotalPrice)
            val allTotalPriceView = findViewById<TextView?>(R.id.allTotalPrice)
            if (mPreviewOrderResponse == null) {
                return
            }
            SpanUtils.with(normalPriceView)
                .append("￥")
                .setFontSize(14, true)
                .append(mPreviewOrderResponse.materialAmount ?: "")
                .setFontSize(18, true)
                .create()

            SpanUtils.with(cutTotalPriceView)
                .append("￥")
                .setFontSize(14, true)
                .append(mPreviewOrderResponse.cutAmount ?: "")
                .setFontSize(18, true)
                .create()
            val stepPrice = mPreviewOrderResponse.stepAmount?.toDoubleOrNull() ?: 0.toDouble();
            if (stepPrice > 0) {
                SpanUtils.with(discountTotalPriceView)
                    .append("加￥")
                    .setFontSize(14, true)
                    .append(PingDanAppUtils.getPositivePrice(mPreviewOrderResponse.stepAmount))
                    .setFontSize(18, true)
                    .create()
            } else {
                SpanUtils.with(discountTotalPriceView)
                    .append("减￥")
                    .setFontSize(14, true)
                    .append(PingDanAppUtils.getPositivePrice(mPreviewOrderResponse.stepAmount))
                    .setFontSize(18, true)
                    .create()
            }

            SpanUtils.with(allTotalPriceView)
                .append("￥")
                .setFontSize(14, true)
                .append(mPreviewOrderResponse.totalAmount ?: "")
                .setFontSize(18, true)
                .create()
        } catch (e: Exception) {
            e("PriceDiscountDetailPopupView", e.toString())
        }
    }
}