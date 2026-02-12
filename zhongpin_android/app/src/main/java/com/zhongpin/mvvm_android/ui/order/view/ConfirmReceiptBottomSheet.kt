package com.zhongpin.mvvm_android.ui.order.view

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import com.lxj.xpopup.core.BottomPopupView
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.BottomSheetConfirmOrderReceiptBinding
import com.zhongpin.mvvm_android.base.ext.forceSetNavBarMarin
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.ui.view.ext.setLineTypeText
import com.zhongpin.mvvm_android.ui.view.ext.setPaperSizeText

class ConfirmReceiptBottomSheet(context: Context,
                                val item: OrderItem,
                                val dialogPaddingBottom :Int =  0,
                                val block: (()->Unit)? = null,
) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.bottom_sheet_confirm_order_receipt
    }

    override fun initPopupContent() {
        super.initPopupContent()
        val binding: BottomSheetConfirmOrderReceiptBinding = BottomSheetConfirmOrderReceiptBinding.bind(bottomPopupContainer)

        binding.closePopup.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            dismiss()
            block?.invoke()
        }
        binding.apply {
            materialCode.text = item.platCode
            waCode.text = item.lenType
            purchaseAmount.text = (item.num ?: 0).toString()
            paperSize.setPaperSizeText(item)
            lineDesc.setLineTypeText(item)
        }

        binding.bottomSpace.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = dialogPaddingBottom
        }
    }
}