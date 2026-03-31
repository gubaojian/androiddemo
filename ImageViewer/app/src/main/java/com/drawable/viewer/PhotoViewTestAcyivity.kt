package com.drawable.viewer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.drawable.viewer.databinding.ActivityPhotoViewTestAcyivityBinding

class PhotoViewTestAcyivity : AppCompatActivity() {


    lateinit var mBinding: ActivityPhotoViewTestAcyivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPhotoViewTestAcyivityBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        val url  = "https://upload-bbs.mihoyo.com/upload/2019/08/08/10982654/fe2e9243c4e6ea7e489f81ae3814ed08_3279663480817048245.jpg"

        Glide.with(this)
            .load(url)
            .into(mBinding.mPhotoViewTestPV)

        findViewById<View>(android.R.id.content).postDelayed({
            Log.d("PhotoViewTestAcyivity", "width : ${mBinding.mPhotoViewTestPV.width}  height : ${mBinding.mPhotoViewTestPV.height}")
        },1000)

    }
}
