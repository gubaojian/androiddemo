package com.zhongpin.mvvm_android.view.popup

import android.app.Activity
import android.graphics.Typeface
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.core.graphics.toColorInt
import com.lxj.xpopup.core.AttachPopupView
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ViewMinePriceSortPopupBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.bean.MaterialPriceFilterQuery
import com.zhongpin.mvvm_android.bean.MaterialPriceFilterQueryChangeEvent
import com.zhongpin.mvvm_android.bean.OrderFilterQuery
import com.zhongpin.mvvm_android.bean.OrderFilterQueryChangeEvent

val sortByBuyAmountAsc =  "订购次数由少到多";
val sortByBuyAmountDesc =  "订购次数由多到少";
val sortByBuyDateASC =  "订购时间由远到近";
val sortByBuyfDateDesc =  "订购时间由近到远";

class MinePriceSortPopup(val mActivity: Activity, var filter: MaterialPriceFilterQuery): AttachPopupView(mActivity) {


    override fun addInnerContent() {
        val binding = ViewMinePriceSortPopupBinding.inflate(LayoutInflater.from(getContext()),
            attachPopupContainer, false)

        val labels = listOf(binding.buyAmountAsc, binding.buyAmountDesc, binding.orderDateDesc, binding.orderDateAsc)
        var target = binding.buyAmountDesc
        if (TextUtils.equals(filter.sortBy, sortByBuyAmountAsc)) {
            target = binding.buyAmountAsc
        } else if (TextUtils.equals(filter.sortBy, sortByBuyAmountDesc)) {
            target = binding.buyAmountDesc
        } else if (TextUtils.equals(filter.sortBy, sortByBuyDateASC)) {
            target = binding.orderDateAsc
        } else if (TextUtils.equals(filter.sortBy, sortByBuyfDateDesc)) {
            target = binding.orderDateDesc
        }


        labels.forEach {
            if (target == it) {
                it.setBackgroundResource(R.drawable.bg_order_filter_month_item_selected)
                it.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                it.setTextColor("#557EF7".toColorInt())
            } else {
                it.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                it.setBackgroundResource(R.drawable.bg_order_filter_month_item_normal)
                it.setTextColor("#333333".toColorInt())
            }
        }

        binding.buyAmountAsc.setOnClickListener {
            filter.sortBy = sortByBuyAmountAsc
            EventBusUtils.postEvent(MaterialPriceFilterQueryChangeEvent(filter))
            dismiss()
        }

        binding.buyAmountDesc.setOnClickListener {
            filter.sortBy = sortByBuyAmountDesc
            EventBusUtils.postEvent(MaterialPriceFilterQueryChangeEvent(filter))
            dismiss()
        }

        binding.orderDateAsc.setOnClickListener {
            filter.sortBy =  sortByBuyDateASC
            EventBusUtils.postEvent(MaterialPriceFilterQueryChangeEvent(filter))
            dismiss()
        }

        binding.orderDateDesc.setOnClickListener {
            filter.sortBy =  sortByBuyfDateDesc
            EventBusUtils.postEvent(MaterialPriceFilterQueryChangeEvent(filter))
            dismiss()
        }


        val contentView = binding.root
        attachPopupContainer.addView(contentView)

    }



}