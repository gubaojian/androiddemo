package com.zhongpin.mvvm_android.ui.order.add

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.databinding.ListAddOrderPurchaseItemBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.mvvm_android.bean.AddOrderPurchaseItem
import com.zhongpin.mvvm_android.bean.DeleteOrderPurchaseItemEvent
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.view.ext.setWaBg
import com.zhongpin.mvvm_android.ui.view.ext.setWaText
import com.zhongpin.mvvm_android.ui.view.ext.setWaTextColor

class AddOrderPurchaseItemListAdapter(val mActivity: AddOrderActivity, data: MutableList<AddOrderPurchaseItem>)
    : BaseQuickAdapter<AddOrderPurchaseItem, AddOrderPurchaseItemListAdapter.VH>(data) {


    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): AddOrderPurchaseItemListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListAddOrderPurchaseItemBinding = ListAddOrderPurchaseItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: AddOrderPurchaseItemListAdapter.VH,
        position: Int,
        item: AddOrderPurchaseItem?
    ) {
        item?.let {
            holder.binding.apply {
                waBg.setWaBg(item.platCode)
                name.setWaTextColor(item.platCode)
                fluteText.setWaTextColor(item.platCode)

                name.setWaText(item.platCode)
                fluteText.text = "${item.flute}瓦"
                paperSize.text = PingDanAppUtils.toPaperSizeWithUnitMM(item.pageSize)
                purchaseAmount.text = "${item.num}张"

                addOrderPurchaseItemDelete.setOnClickListener {
                    val dialog = ConfirmDialog(
                        mContext = mActivity,
                        title = "确认删除吗？",
                        message = HtmlCompat.fromHtml("${item.platCode}<br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
                        confirmText = "确认",
                        onConfirm = {
                            EventBusUtils.postEvent(DeleteOrderPurchaseItemEvent(item))
                        }
                    );
                    dialog.showDialog(mActivity)
                }
            }
        }
    }


}