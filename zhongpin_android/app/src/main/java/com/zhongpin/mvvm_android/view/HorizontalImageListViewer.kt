package com.zhongpin.mvvm_android.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.sum.glide.setUrl
import com.zhilianshidai.pindan.app.databinding.ListHorizontalImageListViewerItemBinding
import com.zhilianshidai.pindan.app.databinding.ViewHorizontalImageListViewerBinding
import com.zhongpin.lib_base.utils.BannerUtils
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity

class HorizontalImageListViewer @JvmOverloads constructor (context: Context,
                                                           attrs: AttributeSet? = null,
                                                           defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr)  {
    private lateinit var horizontalImageListViewerBinding: ViewHorizontalImageListViewerBinding;
    private val mData = mutableListOf<String>();
    private lateinit var mHorizontalImageListAdapter:HorizontalImageListAdapter;

    init {
        horizontalImageListViewerBinding = ViewHorizontalImageListViewerBinding.inflate(
            LayoutInflater.from(context), this, true)
        mHorizontalImageListAdapter = HorizontalImageListAdapter(context, mData);
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
        horizontalImageListViewerBinding.horizontalRecyclerView.layoutManager = layoutManager;
        horizontalImageListViewerBinding.horizontalRecyclerView.adapter = mHorizontalImageListAdapter;
    }

    fun setImageUrls(imageUrls:List<String>) {
        if (imageUrls.isEmpty()) {
            visibility = View.GONE;
            mData.clear()
            mHorizontalImageListAdapter.notifyDataSetChanged()
            return
        }
        visibility = View.VISIBLE;
        var viewWith = 0;
        if (imageUrls.size <= 4) {
            viewWith = SizeUtils.dp2px((50 + 10).toFloat())*imageUrls.size;
        } else {
            viewWith = SizeUtils.dp2px((50 + 10).toFloat())*4;
        }
        setViewWith(this@HorizontalImageListViewer, viewWith);
        setViewWith(horizontalImageListViewerBinding.root, viewWith);
        setViewWith(horizontalImageListViewerBinding.horizontalRecyclerView, viewWith);
        mData.clear();
        mData.addAll(imageUrls);
        mHorizontalImageListAdapter.notifyDataSetChanged()
    }

    private fun setViewWith(view:View, width:Int) {
        val layoutParams = view.layoutParams;
        layoutParams.width = width;
        view.layoutParams = layoutParams;
    }


    class HorizontalImageListAdapter(val mContext: Context, data: List<String>)
        : BaseQuickAdapter<String, HorizontalImageListAdapter.VH>(data) {


        override fun onBindViewHolder(
            holder: HorizontalImageListAdapter.VH,
            position: Int,
            item: String?
        ) {
            item?.let {
                holder.binding.apply {
                     image.setUrl(item ?: "")
                    image.setOnClickListener {
                        val intent = Intent(mContext, PhonePreviewerActivity::class.java)
                        intent.putExtra("imageUrls", arrayOf<String>(item ?: ""))
                        mContext.startActivity(intent)
                    }
                }
            }
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): HorizontalImageListAdapter.VH {
            val holder =  VH(parent)
            BannerUtils.setBannerRound(holder.binding.image, SizeUtils.dp2px(8.0f).toFloat())
            return holder;
        }

        class VH(
            parent: ViewGroup,
            val binding: ListHorizontalImageListViewerItemBinding = ListHorizontalImageListViewerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        ) : RecyclerView.ViewHolder(binding.root)

    }
}