package com.zhongpin.lib_base.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.DialogConfirmBinding
import com.zhongpin.lib_base.ktx.gone

class ConfirmDialog @JvmOverloads constructor(
    val mContext: Context,
    val title: String? = null,
    val message: CharSequence? = null,
    val confirmText: String? = null,
    val cancelText: String? = null,
    val onCancel: OnClickListener? = null,
    val onConfirm: OnClickListener? = null,
    val canceledOnTouchOutside: Boolean = true,
    val showCancelButton: Boolean = true,
) : Dialog(
    mContext,
    R.style.ConfirmDialog
) {

    private lateinit  var mBinding: DialogConfirmBinding;


    init {
        mBinding = DialogConfirmBinding.inflate(LayoutInflater.from(context))
        setContentView(mBinding.root)
        setCanceledOnTouchOutside(canceledOnTouchOutside)
        mBinding.title.text = title;
        mBinding.message.text = message;
        if (!TextUtils.isEmpty(confirmText)) {
            mBinding.confirmText.text = confirmText;
        }
        if (!TextUtils.isEmpty(cancelText)) {
            mBinding.cancelText.text = cancelText
        }

        if (TextUtils.isEmpty(title)) {
            mBinding.title.visibility = View.GONE
        }
        if (TextUtils.isEmpty(message)) {
            mBinding.message.visibility = View.GONE
        }
        mBinding.cancelButtonContainer.setOnClickListener {
            dismissDialog()
            onCancel?.onClick(it)
        }
        mBinding.confirmButtonContainer.setOnClickListener {
            dismissDialog()
            onConfirm?.onClick(it)
        }
        if (!showCancelButton) {
            mBinding.cancelButtonContainer.gone()
            mBinding.bottomButtonLine.gone()
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