package com.zhongpin.mvvm_android.ui.order.view

import android.app.Activity
import android.graphics.Typeface
import android.text.TextUtils
import android.widget.Toast
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.BottomSheetOrderFilterBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.bean.OrderFilterQuery
import com.zhongpin.mvvm_android.bean.OrderFilterQueryChangeEvent
import androidx.core.graphics.toColorInt
import com.lxj.xpopup.impl.PartShadowPopupView
import com.zhongpin.mvvm_android.ui.common.showOrderFilterDatePickerDialog
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import java.text.SimpleDateFormat
import java.util.Calendar

class OrderFilterBottomSheet(val mContext: Activity, var filter: OrderFilterQuery) : PartShadowPopupView(mContext) {


    override fun getImplLayoutId(): Int {
        return R.layout.bottom_sheet_order_filter
    }

    override fun initPopupContent() {
        super.initPopupContent()
        val binding: BottomSheetOrderFilterBinding = BottomSheetOrderFilterBinding.bind(attachPopupContainer.getChildAt(0))

        binding.closePopup.setOnClickListener {
            dismiss()
        }

        binding.labelAll.setOnClickListener {
            filter = OrderFilterQuery()
            showFilter(filter, binding)
        }

        binding.labelOneWeek.setOnClickListener {
            filter = OrderFilterQuery()
            filter.label = binding.labelOneWeek.text.toString()
            val format = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA)
            val end = Calendar.getInstance()
            end.add(Calendar.DAY_OF_YEAR, 1)

            val start = Calendar.getInstance()
            start.add(Calendar.DAY_OF_YEAR, -7)

            format.format(start.time)

            filter.startTime = format.format(start.time)
            filter.endTime = format.format(end.time)

            showFilter(filter, binding)
        }


        binding.labelOneMonth.setOnClickListener {
            filter = OrderFilterQuery()
            filter.label = binding.labelOneMonth.text.toString()
            val format = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA)
            val end = Calendar.getInstance()
            end.add(Calendar.DAY_OF_YEAR, 1)

            val start = Calendar.getInstance()
            start.add(Calendar.MONTH, -1)

            format.format(start.time)

            filter.startTime = format.format(start.time)
            filter.endTime = format.format(end.time)

            showFilter(filter, binding)
        }

        binding.labelSixMonth.setOnClickListener {
            filter = OrderFilterQuery()
            filter.label = binding.labelSixMonth.text.toString()
            val format = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA)
            val end = Calendar.getInstance()
            end.add(Calendar.DAY_OF_YEAR, 1)

            val start = Calendar.getInstance()
            start.add(Calendar.MONTH, -6)

            format.format(start.time)

            filter.startTime = format.format(start.time)
            filter.endTime = format.format(end.time)

            showFilter(filter, binding)
        }

        binding.startDate.setOnClickListener {
             mContext.showOrderFilterDatePickerDialog(
                 selectDate = PingDanAppUtils.getDateEntity(binding.startDate.text.toString()),
                 onPicker = { year, month, day ->
                     val date = PingDanAppUtils.getOrderFilterDateFormat(year, month, day)
                     binding.startDate.text = date
                     onSetDate(binding);
                 }
             )
        }

        binding.endDate.setOnClickListener {
            mContext.showOrderFilterDatePickerDialog(
                selectDate = PingDanAppUtils.getDateEntity(binding.endDate.text.toString()),
                onPicker = { year, month, day ->
                    val date = PingDanAppUtils.getOrderFilterDateFormat(year, month, day)
                    binding.endDate.text = date
                    onSetDate(binding);
                }
            )
        }

        binding.btnCancel.setOnClickListener {
            EventBusUtils.postEvent(OrderFilterQueryChangeEvent(OrderFilterQuery()))
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
            EventBusUtils.postEvent(OrderFilterQueryChangeEvent(filter))
            dismiss()
        }
        showFilter(filter, binding)
    }

    private fun onSetDate(binding: BottomSheetOrderFilterBinding) {
        if (!TextUtils.isEmpty(binding.startDate.text)
            && !TextUtils.isEmpty(binding.endDate.text)) {
            filter.label = "自定义"
            filter.startTime =  binding.startDate.text.toString()
            filter.endTime = binding.endDate.text.toString()
            showFilter(filter, binding)
        }
    }

    fun showFilter(filter: OrderFilterQuery, binding: BottomSheetOrderFilterBinding) {
        val labels = listOf(binding.labelAll, binding.labelOneMonth, binding.labelOneWeek, binding.labelSixMonth)
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
}