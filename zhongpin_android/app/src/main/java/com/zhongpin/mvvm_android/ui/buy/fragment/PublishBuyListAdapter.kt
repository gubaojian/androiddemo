package com.zhongpin.mvvm_android.ui.buy.fragment

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.databinding.ListPublichBuyHorizontalImageItemBinding
import com.zhilianshidai.pindan.app.databinding.ListPublishBuyItemBinding
import com.zhongpin.lib_base.utils.BannerUtils
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import java.io.File

class PublishBuyListAdapter(val mFragment: PublishBuyListFragment, data: MutableList<CompanyListItemResponse>)
    : BaseQuickAdapter<CompanyListItemResponse, PublishBuyListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: PublishBuyListAdapter.VH,
        position: Int,
        item: CompanyListItemResponse?
    ) {
        item?.let {
            holder.binding.apply {
                /**
                name.text = it?.name ?: ""
                address.text = it?.address ?: ""
                legal.text = it?.legal ?: ""
                if (it.status == 0) {
                    statusText.text = "待审核"
                    statusText.setTextColor(Color.parseColor("#FFA826"))
                    statusText.setBackgroundResource(drawable.bg_company_verify_status_wait)
                } else if (it.status == 1) {
                    statusText.text = "已认证"
                    statusText.setTextColor(Color.parseColor("#57C248"))
                    statusText.setBackgroundResource(drawable.bg_company_verify_status_ok)
                } else if (it.status == 2) {
                    statusText.text = "认证失败"
                    statusText.setTextColor(Color.parseColor("#D34545"))
                    statusText.setBackgroundResource(drawable.bg_company_verify_status_failed)
                } else {
                    statusText.text = "已认证"
                    statusText.setTextColor(Color.parseColor("#57C248"))
                    statusText.setBackgroundResource(drawable.bg_company_verify_status_ok)
                }*/
            }
            val imageUrls = mutableListOf<String>();
            imageUrls.add("https://img.alicdn.com/bao/uploaded/i2/79426796/O1CN01LVCxOE204cgONl79q_!!79426796.jpg_460x460q90.jpg_.webp");
            imageUrls.add("https://img.alicdn.com/bao/uploaded/i2/79426796/O1CN01LVCxOE204cgONl79q_!!79426796.jpg_460x460q90.jpg_.webp");
            imageUrls.add("https://img.alicdn.com/bao/uploaded/i2/79426796/O1CN01LVCxOE204cgONl79q_!!79426796.jpg_460x460q90.jpg_.webp");
            imageUrls.add("https://img.alicdn.com/bao/uploaded/i2/79426796/O1CN01LVCxOE204cgONl79q_!!79426796.jpg_460x460q90.jpg_.webp");
            imageUrls.add("https://img.alicdn.com/bao/uploaded/i2/79426796/O1CN01LVCxOE204cgONl79q_!!79426796.jpg_460x460q90.jpg_.webp");
            imageUrls.add("https://img.alicdn.com/bao/uploaded/i2/79426796/O1CN01LVCxOE204cgONl79q_!!79426796.jpg_460x460q90.jpg_.webp");
            imageUrls.add("https://img.alicdn.com/bao/uploaded/i2/79426796/O1CN01LVCxOE204cgONl79q_!!79426796.jpg_460x460q90.jpg_.webp");
            imageUrls.add("https://img.alicdn.com/bao/uploaded/i2/79426796/O1CN01LVCxOE204cgONl79q_!!79426796.jpg_460x460q90.jpg_.webp");

            val layoutManager = LinearLayoutManager(mFragment.requireActivity(), LinearLayoutManager.HORIZONTAL, false);
            val adapter = PublishBuyHorizontalListAdapter(mFragment, imageUrls)
            adapter.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener<String> {
                override fun onClick(
                    adapter: BaseQuickAdapter<String, *>,
                    innerView: View,
                    innerPosition: Int
                ) {
                    if (getOnItemClickListener() != null) {
                        getOnItemClickListener()?.onClick(this@PublishBuyListAdapter, holder.binding.horizontalRecyclerView, position)
                    }
                }

            });
            holder.binding.horizontalRecyclerView.layoutManager = layoutManager;
            holder.binding.horizontalRecyclerView.adapter = adapter;
            holder.binding.horizontalRecyclerView.setOnClickListener {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener()?.onClick(this@PublishBuyListAdapter, holder.binding.horizontalRecyclerView, position)
                }
            }

        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): PublishBuyListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListPublishBuyItemBinding = ListPublishBuyItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


    class PublishBuyHorizontalListAdapter(val mFragment: PublishBuyListFragment, data: MutableList<String>)
        : BaseQuickAdapter<String, PublishBuyHorizontalListAdapter.ImageVH>(data) {
        override fun onBindViewHolder(
            holder: PublishBuyHorizontalListAdapter.ImageVH,
            position: Int,
            imageUrl: String?
        ) {
            if (imageUrl == null) {
                return
            }
            BannerUtils.setBannerRound(holder.binding.image, SizeUtils.dp2px(4.0f).toFloat())
            Glide.with(context)
                .load(imageUrl)
                .placeholder(holder.binding.image.drawable)
                .into(holder.binding.image)
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): PublishBuyHorizontalListAdapter.ImageVH {
            return ImageVH(parent);
        }

        class ImageVH(
            parent: ViewGroup,
            val binding: ListPublichBuyHorizontalImageItemBinding = ListPublichBuyHorizontalImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        ) : RecyclerView.ViewHolder(binding.root)

    }

}