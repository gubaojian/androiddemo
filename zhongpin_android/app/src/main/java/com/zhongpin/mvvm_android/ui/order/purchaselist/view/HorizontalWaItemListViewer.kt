package com.zhongpin.mvvm_android.ui.order.purchaselist.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.databinding.ListHorizontalWaItemListViewerItemBinding
import com.zhilianshidai.pindan.app.databinding.ViewHorizontalWaItemListViewerBinding
import com.zhongpin.lib_base.ktx.invisible
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.ui.view.ext.setWaBg
import com.zhongpin.mvvm_android.ui.view.ext.setWaText
import com.zhongpin.mvvm_android.ui.view.ext.setWaTextColor

class HorizontalWaItemListViewer @JvmOverloads constructor (context: Context,
                                                            attrs: AttributeSet? = null,
                                                            defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr)  {
    private lateinit var mBinding: ViewHorizontalWaItemListViewerBinding;
    private val mData = mutableListOf<OrderItem>();
    private lateinit var mHorizontalWaItemListAdapter:HorizontalWaItemListAdapter;

    init {
        mBinding = ViewHorizontalWaItemListViewerBinding.inflate(
            LayoutInflater.from(context), this, true)
        mHorizontalWaItemListAdapter = HorizontalWaItemListAdapter(context, mData);
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
        mBinding.horizontalRecyclerView.layoutManager = layoutManager;
        mBinding.horizontalRecyclerView.adapter = mHorizontalWaItemListAdapter;
    }

    fun setContentOnClickListener(action: View.OnClickListener) {
        mBinding.root.setOnClickListener(action)
        mBinding.horizontalRecyclerView.setOnClickListener(action)
        mHorizontalWaItemListAdapter.setOnItemClickListener { _, _,_ ->
            action.onClick(mBinding.root)
        }
    }

    fun setOrderItems(orderItems:List<OrderItem>) {
        if (orderItems.isEmpty()) {
            invisible()
            mData.clear()
            mHorizontalWaItemListAdapter.notifyDataSetChanged()
            return
        }
        visible()
        mData.clear();
        mData.addAll(orderItems);
        mHorizontalWaItemListAdapter.notifyDataSetChanged()
    }

    private fun setViewWith(view:View, width:Int) {
        val layoutParams = view.layoutParams;
        layoutParams.width = width;
        view.layoutParams = layoutParams;
    }

    class HorizontalWaItemListAdapter(val mContext: Context, data: List<OrderItem>)
        : BaseQuickAdapter<OrderItem, HorizontalWaItemListAdapter.VH>(data) {


        override fun onBindViewHolder(
            holder: HorizontalWaItemListAdapter.VH,
            position: Int,
            item: OrderItem?
        ) {
            item?.let {
                holder.binding.apply {
                    waBg.setWaBg(item.platCode)
                    name.setWaTextColor(item.platCode)
                    fluteText.setWaTextColor(item.platCode)

                    name.setWaText(item.platCode)
                    fluteText.text = "${item.lenType}瓦"
                }
            }
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): HorizontalWaItemListAdapter.VH {
            val holder =  VH(parent)
            return holder;
        }

        class VH(
            parent: ViewGroup,
            val binding: ListHorizontalWaItemListViewerItemBinding= ListHorizontalWaItemListViewerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        ) : RecyclerView.ViewHolder(binding.root)

    }
}