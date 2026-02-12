package com.zhongpin.mvvm_android.ui.order.add.item.add.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.lxj.xpopup.impl.PartShadowPopupView
import com.sum.glide.setUrl
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.BottomSheetChooseWaTypeBinding
import com.zhilianshidai.pindan.app.databinding.ListWaTypeGridItemBinding
import com.zhongpin.mvvm_android.bean.WaLenTypeItem
import com.zhongpin.mvvm_android.ui.common.throttleToast

fun interface OnChooseWaLenTypeAction {
    fun onChooseWaType(selectItem:  WaLenTypeItem?)
}

class ChooseWaTypeBottomSheet(val mContext: Activity,
                              var selectWaCode: String? = null,
                              val items: List<WaLenTypeItem> = emptyList<WaLenTypeItem>(),
                              val chooseAction: OnChooseWaLenTypeAction? = null,
) : PartShadowPopupView(mContext)  {

    var selectItem: WaLenTypeItem? = null;

    val adapter =  WaTypeItemListAdapter(
        bottomSheet = this,
        mItems = items.toMutableList()
    );


    @SuppressLint("NotifyDataSetChanged")
    override fun addInnerContent() {
        val binding = BottomSheetChooseWaTypeBinding.inflate(LayoutInflater.from(getContext()),
            attachPopupContainer, false)

        binding.closePopup.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        selectItem = items.find {  it.type == selectWaCode }

        if (selectItem == null) {
            selectItem = items.firstOrNull();
        }

        binding.btnSubmit.setOnClickListener {
            if (selectItem == null) {
                mContext.throttleToast("请选择瓦型")
                return@setOnClickListener
            }
            dismiss()
            chooseAction?.onChooseWaType(selectItem)
        }
        binding.gridView.layoutManager = GridLayoutManager(mContext, 3)
        adapter.setOnItemClickListener { _, view, position ->
            selectItem = items[position]
            adapter.notifyDataSetChanged()
        }
        binding.gridView.adapter = adapter

        val contentView = binding.root
        attachPopupContainer.addView(contentView)
    }

    class WaTypeItemListAdapter(val bottomSheet: ChooseWaTypeBottomSheet, val mItems: MutableList<WaLenTypeItem>)
        : BaseQuickAdapter<WaLenTypeItem, WaTypeItemListAdapter.ItemVH>(mItems) {


        override fun onBindViewHolder(
            holder: WaTypeItemListAdapter.ItemVH,
            position: Int,
            item: WaLenTypeItem?
        ) {
            item?.let {
                holder.binding.apply {
                    if (item.type == bottomSheet.selectItem?.type) {
                        waItemContainer.setBackgroundResource(R.drawable.bg_wa_len_type_item_selected)
                        labelText.setTextColor("#557EF7".toColorInt())
                    } else {
                        waItemContainer.setBackgroundResource(R.drawable.bg_wa_len_type_item_normal)
                        labelText.setTextColor("#333333".toColorInt())
                    }
                    labelText.text = item.type
                    labelImage.setUrl(item.image)
                }
            }
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): WaTypeItemListAdapter.ItemVH {
            return ItemVH(parent)
        }

        class ItemVH(
            parent: ViewGroup,
            val binding: ListWaTypeGridItemBinding = ListWaTypeGridItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        ) : RecyclerView.ViewHolder(binding.root)

    }

}