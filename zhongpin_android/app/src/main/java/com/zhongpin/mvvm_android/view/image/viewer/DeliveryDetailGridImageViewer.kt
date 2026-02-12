package com.zhongpin.mvvm_android.view.image.viewer

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.sum.glide.setUrl
import com.zhilianshidai.pindan.app.databinding.ListDeliveryGridImageViewerItemBinding
import com.zhilianshidai.pindan.app.databinding.ViewDeliveryGridImageViewerBinding
import com.zhongpin.lib_base.utils.BannerUtils
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity

class DeliveryDetailGridImageViewer @JvmOverloads constructor (context: Context,
                                                               attrs: AttributeSet? = null,
                                                               defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr)  {
    private lateinit var binding: ViewDeliveryGridImageViewerBinding;
    private val mData = mutableListOf<String>();
    private lateinit var mImageListAdapter:GridImageListAdapter;

    init {
        binding = ViewDeliveryGridImageViewerBinding.inflate(
            LayoutInflater.from(context), this, true)
        mImageListAdapter = GridImageListAdapter(context, mData);
        val layoutManager = GridLayoutManager(context, 3);
        binding.recyclerView.layoutManager = layoutManager;
        binding.recyclerView.adapter = mImageListAdapter;
    }

    fun setImageUrls(imageUrls:List<String>) {
        if (imageUrls.isEmpty()) {
            visibility = GONE;
            mData.clear()
            mImageListAdapter.notifyDataSetChanged()
            return
        }
        visibility = VISIBLE;
        var viewWith = 0;
        var viewHeight = 0;
        var spanCount = 3;
        val itemSize = 80;
        if (imageUrls.size <= 3) {
            spanCount = imageUrls.size.coerceAtLeast(1)
            viewWith = SizeUtils.dp2px((itemSize + 10).toFloat()) * spanCount;
            viewHeight = SizeUtils.dp2px((itemSize + 10).toFloat())
        } else if (imageUrls.size <= 6) { //最多6张
            viewWith = SizeUtils.dp2px((itemSize + 10).toFloat()) * 3;
            viewHeight = SizeUtils.dp2px((itemSize + 10).toFloat()) * 2
        } else if (imageUrls.size <= 9) { //最多6张
            viewWith = SizeUtils.dp2px((itemSize + 10).toFloat()) * 3;
            viewHeight = SizeUtils.dp2px((itemSize + 10).toFloat()) * 4
        } else  { //最多6张
            viewWith = SizeUtils.dp2px((itemSize + 10).toFloat()) * 3;
            viewHeight = SizeUtils.dp2px((itemSize + 10).toFloat()) * 5
        }
        setViewWith(this@DeliveryDetailGridImageViewer, viewWith, viewHeight);
        setViewWith(binding.root, viewWith, viewHeight);
        setViewWith(binding.recyclerView, viewWith, viewHeight);
        mData.clear();
        mData.addAll(imageUrls);

        val layoutManager = binding.recyclerView.layoutManager as GridLayoutManager
        if (layoutManager.spanCount != spanCount) {
            layoutManager.spanCount = spanCount;
        }
        mImageListAdapter.notifyDataSetChanged()
    }

    private fun setViewWith(view: View, width:Int, height:Int) {
        val layoutParams = view.layoutParams;
        layoutParams.width = width;
        layoutParams.height = height;
        view.layoutParams = layoutParams;
    }


    class GridImageListAdapter(val mContext: Context, val imageListData: List<String>)
        : BaseQuickAdapter<String, GridImageListAdapter.VH>(imageListData) {


        override fun onBindViewHolder(
            holder: GridImageListAdapter.VH,
            position: Int,
            item: String?
        ) {
            item?.let {
                holder.binding.apply {
                    image.setUrl(item ?: "")
                    image.setOnClickListener {
                        val intent = Intent(mContext, PhonePreviewerActivity::class.java)
                        val imageUrls: Array<String> = imageListData.toTypedArray<String>();
                        intent.putExtra("imageUrls", imageUrls)
                        intent.putExtra("selectPosition", position)
                        mContext.startActivity(intent)
                    }
                }
            }
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): GridImageListAdapter.VH {
            val holder =  VH(parent)
            BannerUtils.setBannerRound(holder.binding.image, SizeUtils.dp2px(8.0f).toFloat())
            return holder;
        }

        class VH(
            parent: ViewGroup,
            val binding: ListDeliveryGridImageViewerItemBinding = ListDeliveryGridImageViewerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        ) : RecyclerView.ViewHolder(binding.root)

    }
}