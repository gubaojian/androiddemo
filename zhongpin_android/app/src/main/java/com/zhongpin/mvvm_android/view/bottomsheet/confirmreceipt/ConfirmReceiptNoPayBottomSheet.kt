package com.zhongpin.mvvm_android.view.bottomsheet.confirmreceipt

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.SpanUtils
import com.lxj.xpopup.impl.PartShadowPopupView
import com.zhilianshidai.pindan.app.databinding.BottomSheetComfirmOrderReceiptNoPayBinding
import com.zhilianshidai.pindan.app.databinding.BottomSheetPurchaseOrderConfirmPayBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.base.ext.HomeNavBarConfig
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils


class ConfirmReceiptNoPayBottomSheet(val mContext: Activity,
                                     val confirmAction: OnClickListener? = null,
                                     var inFullScreenActivity: Boolean = true
                                     ) : PartShadowPopupView(mContext) {

    var mBinding: BottomSheetComfirmOrderReceiptNoPayBinding? = null;

    override fun addInnerContent() {
        val binding = BottomSheetComfirmOrderReceiptNoPayBinding.inflate(LayoutInflater.from(getContext()),
            attachPopupContainer, false)
        mBinding = binding

        binding.closePopup.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            dismiss()
            confirmAction?.onClick(it)
        }

        if (inFullScreenActivity) {
            binding.bottomBarSpace.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = HomeNavBarConfig.bottomMargin
            }
        }

        val contentView = binding.root
        attachPopupContainer.addView(contentView)
    }

    override fun dismiss() {
        super.dismiss()
    }
}