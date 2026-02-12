package com.zhongpin.mvvm_android.ui.photo.preview

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.chrisbanes.photoview.PhotoView
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityPhotoPreviewerBinding
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.common.utils.ImageUtils
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.utils.ImageLoaderUtils


class PhonePreviewerActivity : BaseVMActivity<PhonePreviewerViewModel>() {


    private lateinit var mBinding: ActivityPhotoPreviewerBinding;

    private var imageUrls:Array<String> = emptyArray()

    private var selectPosition:Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(true).init()
        if (intent != null) {
            imageUrls = intent.getStringArrayExtra("imageUrls") ?: emptyArray()
            selectPosition = intent.getIntExtra("selectPosition", 0)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPhotoPreviewerBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.appBarContainer)
        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.photoViewPager.adapter = SamplePagerAdapter(imageUrls, this)
        mBinding.photoViewPager.setOnPageChangeListener(object :ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            @SuppressLint("DefaultLocale")
            override fun onPageSelected(position: Int) {
                formatLabel(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        formatLabel(0)
        if (selectPosition != 0) {
            mBinding.photoViewPager.setCurrentItem(selectPosition, false);
        }
    }

    fun formatLabel(position: Int) {
        val length = imageUrls.size ?: 0
        val label = String.format("%d/%d", (position + 1), length)
        mBinding.photoIndicator.text = label
    }

    class SamplePagerAdapter(val imageUrls:Array<String>, val activity: PhonePreviewerActivity) : PagerAdapter() {
        override fun getCount(): Int {
            return imageUrls.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView: PhotoView = PhotoView(container.context)
            ImageLoaderUtils.setImage(photoView, imageUrls[position])
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView,
                ViewPager.LayoutParams.MATCH_PARENT,
                ViewPager.LayoutParams.MATCH_PARENT)
            photoView.setOnClickListener {
                activity.finish()
            }
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

}