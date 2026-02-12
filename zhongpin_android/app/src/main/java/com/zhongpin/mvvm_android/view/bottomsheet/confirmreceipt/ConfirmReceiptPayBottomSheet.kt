package com.zhongpin.mvvm_android.view.bottomsheet.confirmreceipt

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.text.HtmlCompat
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.SpanUtils
import com.lxj.xpopup.impl.PartShadowPopupView
import com.zhilianshidai.pindan.app.databinding.BottomSheetComfirmOrderReceiptPayBinding
import com.zhongpin.mvvm_android.base.ext.HomeNavBarConfig
import com.zhongpin.mvvm_android.bean.OrderDetailItem
import com.zhongpin.mvvm_android.bean.OrderItem


class ConfirmReceiptPayBottomSheet(val mContext: Activity,
                                   val confirmAction: OnClickListener? = null,
                                   var inFullScreenActivity: Boolean = true,
                                   var orderDetailItem: OrderDetailItem? = null,
                                   ) : PartShadowPopupView(mContext) {

    var mBinding: BottomSheetComfirmOrderReceiptPayBinding? = null;

    override fun addInnerContent() {
        val binding = BottomSheetComfirmOrderReceiptPayBinding.inflate(LayoutInflater.from(getContext()),
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
            .append(orderDetailItem?.extraPrice ?: "0.0")
            .setFontSize(36, true)
            .create()

        binding.messageTip.text = HtmlCompat.fromHtml("该订单存在额外配送情况，超额数量为 <font color='#557EF7'>${orderDetailItem?.extraNum}</font> 件，需补交差价。<br/><font color='#557EF7'>【支付并签收】</font>后将从企业账户（预付款）余额扣除对应金额。", HtmlCompat.FROM_HTML_MODE_LEGACY);


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