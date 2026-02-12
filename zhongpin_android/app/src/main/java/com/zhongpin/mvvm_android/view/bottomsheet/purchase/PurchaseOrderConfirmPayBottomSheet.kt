package com.zhongpin.mvvm_android.view.bottomsheet.purchase

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.SpanUtils
import com.lxj.xpopup.impl.PartShadowPopupView
import com.zhilianshidai.pindan.app.databinding.BottomSheetPurchaseOrderConfirmPayBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.base.ext.HomeNavBarConfig
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils


class PurchaseOrderConfirmPayBottomSheet(val mContext: Activity,
                                         val confirmAction: View.OnClickListener? = null,
                                         var inFullScreenActivity: Boolean = true,
                                         var purchaseOrderDetail: PurchaseOrderDetail? = null,

) : PartShadowPopupView(mContext) {

    var mBinding: BottomSheetPurchaseOrderConfirmPayBinding? = null;

    override fun addInnerContent() {
        val binding = BottomSheetPurchaseOrderConfirmPayBinding.inflate(LayoutInflater.from(getContext()),
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

        SpanUtils.with(binding.payAmount)
            .append("￥")
            .setFontSize(16, true)
            .append(purchaseOrderDetail?.totalAmount ?: "0.0")
            .setFontSize(36, true)
            .create()



        val remainTime = PingDanAppUtils.getPurTimeRemain(purchaseOrderDetail?.purTime)
        if (remainTime > 0) {
            binding.countDownTextContainer.visible()
            binding.countDownText.setSuffix("后取消订单，请及时支付")
            binding.countDownText.setRemainTime(remainTime, {
                dismiss()
            })
        } else {
            binding.countDownTextContainer.gone()
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
        mBinding?.countDownText?.stop()
        super.dismiss()
    }
}