package com.zhongpin.mvvm_android.ui.order.add.item.add.view

import android.app.Activity
import android.graphics.Typeface
import android.view.LayoutInflater
import androidx.core.graphics.toColorInt
import com.android.common.oss.jpgMaxWith
import com.lxj.xpopup.impl.PartShadowPopupView
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.BottomSheetChooseBoxTypeBinding
import com.zhilianshidai.pindan.app.databinding.ViewBoxTypeLabelItemBinding
import com.zhongpin.mvvm_android.bean.BoxTypeConfigItem
import com.zhongpin.mvvm_android.ui.common.setImageUrlAndEnablePreviewByXPopup
import com.zhongpin.mvvm_android.ui.common.throttleToast

fun interface OnChooseBoxTypeAction {
    fun onChooseBoxType(selectItem:  BoxTypeConfigItem?)
}

class ChooseBoxTypeBottomSheet(val mContext: Activity,
                               val selectItemCode:  String? = null,
                               val items: List<BoxTypeConfigItem> = emptyList<BoxTypeConfigItem>(),
                               val chooseAction: OnChooseBoxTypeAction? = null,
) : PartShadowPopupView(mContext)  {

    var selectItem:  BoxTypeConfigItem? = null

    override fun addInnerContent() {
        val binding = BottomSheetChooseBoxTypeBinding.inflate(LayoutInflater.from(getContext()),
            attachPopupContainer, false)

        binding.closePopup.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        selectItem = items.find { it.typeName == selectItemCode }

        if (selectItem == null) {
            selectItem = items.firstOrNull();
        }

        binding.btnSubmit.setOnClickListener {
            if (selectItem == null) {
                mContext.throttleToast("请选择箱型")
                return@setOnClickListener
            }
            dismiss()
            chooseAction?.onChooseBoxType(selectItem)
        }

        setLabelLayout(binding)

        val contentView = binding.root
        attachPopupContainer.addView(contentView)
    }

    fun setLabelLayout(binding: BottomSheetChooseBoxTypeBinding) {
        binding.flowLayout.removeAllViews();
        items.forEach {
            val itemBinding = ViewBoxTypeLabelItemBinding.inflate(LayoutInflater.from(mContext), binding.flowLayout, false);
            if (selectItem == it) {
                itemBinding.boxLabel.setBackgroundResource(R.drawable.bg_box_label_button_item_selected)
                itemBinding.boxLabel.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                itemBinding.boxLabel.setTextColor("#557EF7".toColorInt())
            } else {
                itemBinding.boxLabel.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                itemBinding.boxLabel.setBackgroundResource(R.drawable.bg_box_label_button_item_normal)
                itemBinding.boxLabel.setTextColor("#333333".toColorInt())
            }
            itemBinding.boxLabel.text = it.typeName;
            val itemIt = it;
            itemBinding.boxLabel.setOnClickListener {
                selectItem = itemIt;
                setLabelLayout(binding)
            }
            binding.flowLayout.addView(itemBinding.root)
        }
        binding.chooseBoxTypeImage.setImageUrlAndEnablePreviewByXPopup(selectItem?.image.jpgMaxWith(640))
    }

}