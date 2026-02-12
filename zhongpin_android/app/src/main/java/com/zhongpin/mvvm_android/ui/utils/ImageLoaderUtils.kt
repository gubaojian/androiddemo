package com.zhongpin.mvvm_android.ui.utils

import android.app.Activity
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.zhilianshidai.pindan.app.R

object ImageLoaderUtils {

    fun setImage(fragment: Fragment, imageView: ImageView, url:String?) {
        if (imageView.drawable != null) {
            Glide.with(fragment)
                .load(url)
                .placeholder(imageView.drawable)
                .into(imageView)
        } else {
            Glide.with(fragment)
                .load(url)
                .placeholder(R.mipmap.dummy_placeholder_400)
                .into(imageView)
        }
    }

    fun setImage(activity: Activity, imageView: ImageView, url:String?) {
        if (imageView.drawable != null) {
            Glide.with(activity)
                .load(url)
                .placeholder(imageView.drawable)
                .into(imageView)
        } else {
            Glide.with(activity)
                .load(url)
                .placeholder(R.mipmap.dummy_placeholder_400)
                .into(imageView)
        }
    }

    fun setImage(imageView: ImageView, url:String?) {
        if (imageView.drawable != null) {
            Glide.with(imageView)
                .load(url)
                .placeholder(imageView.drawable)
                .into(imageView)
        } else {
            Glide.with(imageView)
                .load(url)
                .placeholder(R.mipmap.dummy_placeholder_400)
                .into(imageView)
        }
    }
}