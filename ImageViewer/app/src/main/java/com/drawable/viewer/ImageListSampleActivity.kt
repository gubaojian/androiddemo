package com.drawable.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.draggable.library.extension.ImageViewerHelper
import com.drawable.viewer.databinding.ActivityImageListSampleBinding

class ImageListSampleActivity : AppCompatActivity() {

    private val imags = ArrayList<ImageViewerHelper.ImageInfo>().apply {
        add(ImageViewerHelper.ImageInfo("https://upload-bbs.mihoyo.com/upload/2019/08/21/73766616/4d09b6b94491d3921344be906aa7971a_4136353673894269217.png"))
        add(ImageViewerHelper.ImageInfo("https://upload-bbs.mihoyo.com/upload/2019/08/12/50600998/1543e13e5cba414a1e4d396d8e6bdbb0_4959259236143228277.jpg"))
        add(ImageViewerHelper.ImageInfo("https://upload-bbs.mihoyo.com/upload/2019/02/03/74582189/ede10255b2a99cfcf33868d1afd81a92_6059341049122226062.png"))
        add(ImageViewerHelper.ImageInfo("https://upload-bbs.mihoyo.com/upload/2019/08/06/75158229/53c6eb0e1c4bb8db97cbd9c8db631423_3306524819178217982.jpg"))
        add(ImageViewerHelper.ImageInfo("https://upload-bbs.mihoyo.com/upload/2019/08/08/10982654/fe2e9243c4e6ea7e489f81ae3814ed08_3279663480817048245.jpg"))
        add(ImageViewerHelper.ImageInfo("https://upload-bbs.mihayo.com/upload/2019/03/01/73565430/82a40083d95800c553d036b8c0689323_4849126433310918291.png"))
    }

    lateinit var mBinding: ActivityImageListSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityImageListSampleBinding.inflate(LayoutInflater.from(this))

        setContentView(mBinding.root)


        loadImage(imags[0].thumbnailUrl, mBinding.mImagesIv1)
        loadImage(imags[1].thumbnailUrl, mBinding.mImagesIv2)
        loadImage(imags[2].thumbnailUrl, mBinding.mImagesIv3)

        mBinding.mImagesIv1.setOnClickListener {
            showImages(0)
        }

        mBinding.mImagesIv2.setOnClickListener {
            showImages(1)
        }

        mBinding.mImagesIv3.setOnClickListener {
            showImages(2)
        }

        mBinding.mTvShow4.setOnClickListener {
            showImages(3)
        }

        mBinding.mTvShowLast.setOnClickListener {
            showImages(imags.size - 1)
        }


    }

    private fun showImages(index: Int) {
        ImageViewerHelper.showImages(this, listOf(mBinding.mImagesIv1, mBinding.mImagesIv2, mBinding.mImagesIv3), imags, index)
    }

    private fun loadImage(url: String, iv: ImageView) {
        Glide.with(this)
            .load(url)
            .into(iv)
    }

}
