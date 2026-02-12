package com.zhongpin.mvvm_android.ui.pay.view

import android.app.Activity
import android.graphics.Typeface
import android.text.TextUtils
import android.widget.Toast
import com.zhilianshidai.pindan.app.R
import com.zhongpin.lib_base.utils.EventBusUtils
import androidx.core.graphics.toColorInt
import com.lxj.xpopup.impl.PartShadowPopupView
import com.zhilianshidai.pindan.app.databinding.BottomSheetPayItemFilterBinding
import com.zhongpin.mvvm_android.bean.PayItemFilterQuery
import com.zhongpin.mvvm_android.bean.PayItemFilterQueryChangeEvent
import com.zhongpin.mvvm_android.ui.common.showPayItemFilterDatePickerDialog
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PayItemFilterBottomSheet(val mContext: Activity, var filter: PayItemFilterQuery) : PartShadowPopupView(mContext) {

    override fun getImplLayoutId(): Int {
        return R.layout.bottom_sheet_pay_item_filter
    }

    override fun initPopupContent() {
        super.initPopupContent()
        val binding: BottomSheetPayItemFilterBinding = BottomSheetPayItemFilterBinding.bind(attachPopupContainer.getChildAt(0))

        binding.closePopup.setOnClickListener {
            dismiss()
        }


        binding.payTypeAll.setOnClickListener {
            filter.payType = binding.payTypeAll.text.trim().toString();
            showPayType(filter, binding)
        }

        binding.payTypeIn.setOnClickListener {
            filter.payType = binding.payTypeIn.text.trim().toString();
            showPayType(filter, binding)
        }

        binding.payTypeReFund.setOnClickListener {
            filter.payType = binding.payTypeReFund.text.trim().toString();
            showPayType(filter, binding)
        }

        binding.payTypeOut.setOnClickListener {
            filter.payType = binding.payTypeOut.text.trim().toString();
            showPayType(filter, binding)
        }


        binding.labelAll.setOnClickListener {
            filter = PayItemFilterQuery(
                    payType = filter.payType
            )
            showFilter(filter, binding)
        }

        binding.labelCurMonth.setOnClickListener {
            filter = PayItemFilterQuery(
                payType = filter.payType
            )
            filter.label = binding.labelCurMonth.text.toString()
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            val end = Calendar.getInstance()
            end.add(Calendar.DAY_OF_YEAR, 1)

            val start = Calendar.getInstance()
            start.set(Calendar.DAY_OF_MONTH, 1)

            format.format(start.time)

            filter.startTime = format.format(start.time)
            filter.endTime = format.format(end.time)

            showFilter(filter, binding)
        }


        binding.labelPreMonth.setOnClickListener {
            filter = PayItemFilterQuery(
                payType = filter.payType
            )
            filter.label = binding.labelPreMonth.text.toString()
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            val end = Calendar.getInstance()
            end.set(Calendar.DAY_OF_MONTH, 1)
            end.add(Calendar.DAY_OF_MONTH, -1)

            val start = Calendar.getInstance()
            start.add(Calendar.MONTH, -1)
            start.set(Calendar.DAY_OF_MONTH, 1)

            format.format(start.time)

            filter.startTime = format.format(start.time)
            filter.endTime = format.format(end.time)

            showFilter(filter, binding)
        }

        binding.labelPrePreMonth.setOnClickListener {
            filter = PayItemFilterQuery(
                payType = filter.payType
            )
            filter.label = binding.labelPrePreMonth.text.toString()
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            val end = Calendar.getInstance()
            end.add(Calendar.MONTH, -1)
            end.set(Calendar.DAY_OF_MONTH, 1)
            end.add(Calendar.DAY_OF_MONTH, -1)

            val start = Calendar.getInstance()
            start.add(Calendar.MONTH, -2)
            start.set(Calendar.DAY_OF_MONTH, 1)

            format.format(start.time)

            filter.startTime = format.format(start.time)
            filter.endTime = format.format(end.time)

            showFilter(filter, binding)
        }

        binding.startDate.setOnClickListener {
             mContext.showPayItemFilterDatePickerDialog(
                 selectDate = PingDanAppUtils.getDateEntity(binding.startDate.text.toString()),
                 onPicker = { year, month, day ->
                     val date = PingDanAppUtils.getOrderFilterDateFormat(year, month, day)
                     binding.startDate.text = date
                     onSetDate(binding);
                 }
             )
        }

        binding.endDate.setOnClickListener {
            mContext.showPayItemFilterDatePickerDialog(
                selectDate = PingDanAppUtils.getDateEntity(binding.endDate.text.toString()),
                onPicker = { year, month, day ->
                    val date = PingDanAppUtils.getOrderFilterDateFormat(year, month, day)
                    binding.endDate.text = date
                    onSetDate(binding);
                }
            )
        }

        binding.btnCancel.setOnClickListener {
            EventBusUtils.postEvent(PayItemFilterQueryChangeEvent(PayItemFilterQuery()))
            dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            if ("自定义".equals(filter.label)) {
                val start = PingDanAppUtils.getDateEntity(binding.startDate.text.toString())?.toTimeInMillis() ?: 0
                val end = PingDanAppUtils.getDateEntity(binding.endDate.text.toString())?.toTimeInMillis() ?: 0
                if (end < start) {
                    Toast.makeText(mContext, "请选择合理的开始和结束时间", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            EventBusUtils.postEvent(PayItemFilterQueryChangeEvent(filter))
            dismiss()
        }
        showFilter(filter, binding)
        showPayType(filter, binding)
    }

    private fun onSetDate(binding: BottomSheetPayItemFilterBinding) {
        if (!TextUtils.isEmpty(binding.startDate.text)
            && !TextUtils.isEmpty(binding.endDate.text)) {
            filter.label = "自定义"
            filter.startTime =  binding.startDate.text.toString()
            filter.endTime = binding.endDate.text.toString()
            showFilter(filter, binding)
        }
    }

    fun showFilter(filter: PayItemFilterQuery, binding: BottomSheetPayItemFilterBinding) {
        val labels = listOf(binding.labelAll, binding.labelCurMonth, binding.labelPreMonth, binding.labelPrePreMonth)
        var found = false;
        labels.forEach {
            if (TextUtils.equals(filter.label ?: "全部", it.text.toString())) {
                it.setBackgroundResource(R.drawable.bg_order_filter_month_item_selected)
                it.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                it.setTextColor("#557EF7".toColorInt())
                found = true;
            } else {
                it.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                it.setBackgroundResource(R.drawable.bg_order_filter_month_item_normal)
                it.setTextColor("#333333".toColorInt())
            }
        }
        if (found) {
            binding.startDate.text = "";
            binding.endDate.text = "";
        } else {
            binding.startDate.text = filter.startTime ?: "";
            binding.endDate.text =filter.endTime ?: "";
        }
    }

    fun showPayType(filter: PayItemFilterQuery, binding: BottomSheetPayItemFilterBinding) {
        val labels = listOf(binding.payTypeAll, binding.payTypeIn, binding.payTypeReFund, binding.payTypeOut)
        var found = false;
        labels.forEach {
            if (TextUtils.equals(filter.payType ?: "全部", it.text.toString())) {
                it.setBackgroundResource(R.drawable.bg_order_filter_month_item_selected)
                it.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                it.setTextColor("#557EF7".toColorInt())
                found = true;
            } else {
                it.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                it.setBackgroundResource(R.drawable.bg_order_filter_month_item_normal)
                it.setTextColor("#333333".toColorInt())
            }
        }
        if (!found) {
            binding.payTypeAll.let {
                it.setBackgroundResource(R.drawable.bg_order_filter_month_item_selected)
                it.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                it.setTextColor("#557EF7".toColorInt())
                found = true;
            }
        }
    }


}