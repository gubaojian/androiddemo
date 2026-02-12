package com.zhongpin.mvvm_android.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.github.gzuliyujiang.wheelpicker.contract.OnDatePickedListener
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.DialogDatePickerBinding

class DatePickerDialog @JvmOverloads constructor(
    val mContext: Context,
    val confirmText: String? = null,
    val cancelText: String? = null,
    val startDate: DateEntity? = null,
    val endDate: DateEntity? = null,
    val selectDate: DateEntity? = null,
    val onCancel: View.OnClickListener? = null,
    val onPicker: OnDatePickedListener? = null,
    val canceledOnTouchOutside: Boolean = true,
) : Dialog(
    mContext,
    R.style.ConfirmDialog
) {

    private lateinit  var mBinding: DialogDatePickerBinding;


    init {
        mBinding = DialogDatePickerBinding.inflate(LayoutInflater.from(context))
        setContentView(mBinding.root)
        setCanceledOnTouchOutside(canceledOnTouchOutside)

        if (!TextUtils.isEmpty(confirmText)) {
            mBinding.confirmText.text = confirmText;
        }
        if (!TextUtils.isEmpty(cancelText)) {
            mBinding.cancelText.text = cancelText
        }

        mBinding.cancelButtonContainer.setOnClickListener {
            dismissDialog()
            onCancel?.onClick(it)
        }
        if (startDate != null && endDate != null) {
            mBinding.datePicker.setRange(startDate, endDate)
        }
        if (selectDate != null) {
            mBinding.datePicker.setDefaultValue(selectDate)
        }
        mBinding.confirmButtonContainer.setOnClickListener {
            val wheelLayout = mBinding.datePicker;
            val year: Int = wheelLayout.getSelectedYear()
            val month: Int = wheelLayout.getSelectedMonth()
            val day: Int = wheelLayout.getSelectedDay()
            dismissDialog()
            onPicker?.onDatePicked(year, month, day)
        }
    }

    fun showDialog(context: Context) {
        if (context is Activity) {
            if (context.isFinishing) {
                return
            }
        }
        try {
            show()
        } catch (_:Exception) {}
    }

    fun dismissDialog() {
        try {
            dismiss()
        } catch (_:Exception) {}
    }

    override fun show() {
        try {
            super.show()
        } catch (_:Exception) {}
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (_:Exception) {}
    }
}