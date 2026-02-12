package com.zhongpin.mvvm_android.base.view

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zhongpin.mvvm_android.MyApplication
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.vm.SharedViewModel

abstract class BaseActivity : AppCompatActivity() {

    lateinit var mSharedViewModel: SharedViewModel
    private var mActivityProvider: ViewModelProvider? = null


    open fun initView() {}

    open fun initData() {}

    open fun reLoad() = initData()

    abstract fun setContentView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView()
        // 注册EventBus
        if (javaClass.isAnnotationPresent(EventBusRegister::class.java)) EventBusUtils.register(this)

        mSharedViewModel = getAppViewModelProvider().get(SharedViewModel::class.java)
        initView()
        initData()
    }
    private fun getAppViewModelProvider(): ViewModelProvider {
        return (applicationContext as MyApplication).getAppViewModelProvider(this)
    }

    protected fun getActivityViewModelProvider(activity: AppCompatActivity): ViewModelProvider {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(activity)
        }
        return mActivityProvider as ViewModelProvider
    }


    override fun onDestroy() {
        if (javaClass.isAnnotationPresent(EventBusRegister::class.java)) EventBusUtils.unRegister(
            this
        )
        super.onDestroy()
    }
}
