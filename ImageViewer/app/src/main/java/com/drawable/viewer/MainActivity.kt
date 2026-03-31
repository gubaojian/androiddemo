package com.drawable.viewer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.drawable.viewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_STORAGE =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)


    lateinit var mBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        mBinding.mTvSimpleImage.setOnClickListener {
            startActivity(Intent(this, SimpleImageSampleActivity::class.java))
        }

        mBinding.mTvMutilImage.setOnClickListener {
            startActivity(Intent(this, ImageListSampleActivity::class.java))
        }

        mBinding.mTvTest. setOnClickListener {
            startActivity(Intent(this, PhotoViewTestAcyivity::class.java))
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
            }
        }
    }

}
