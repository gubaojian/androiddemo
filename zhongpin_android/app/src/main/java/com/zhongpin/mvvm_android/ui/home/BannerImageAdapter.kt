package com.zhongpin.mvvm_android.ui.home

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter
import com.zhilianshidai.pindan.app.R.*
import com.zhongpin.lib_base.utils.BannerUtils
import com.zhongpin.mvvm_android.bean.HomeBanner

class BannerImageAdapter (val images: List<HomeBanner>) : BannerAdapter<HomeBanner, BannerImageAdapter.ImageHolder>(images)  {

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageHolder {
        val imageView = ImageView(parent!!.context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        //通过裁剪实现圆角
        //BannerUtils.setBannerRound(imageView, SizeUtils.dp2px(16.0f).toFloat())
        return ImageHolder(imageView)
    }

    override fun onBindView(holder: ImageHolder?, item: HomeBanner?, position: Int, size: Int) {
        item?.let {
            holder?.let {
                if (TextUtils.isEmpty(item.url)) {
                    Glide.with(holder.imageView)
                        .load(item.resId)
                        .placeholder(mipmap.dummy_placeholder_400)
                        .error(mipmap.dummy_placeholder_400)
                        .into(holder.imageView)
                } else {
                    Glide.with(holder.imageView)
                        .load(item.url)
                        .placeholder(mipmap.dummy_placeholder_400)
                        .error(mipmap.dummy_placeholder_400)
                        .into(holder.imageView)
                }
            }
        }
    }


    class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view as ImageView
    }
}