package com.zhongpin.mvvm_android.base.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.MyApplication
import com.zhongpin.mvvm_android.base.vm.SharedViewModel
import kotlin.system.measureTimeMillis

abstract class BaseFragment: Fragment() {

    protected lateinit var mActivity: AppCompatActivity
    protected lateinit var mSharedViewModel: SharedViewModel
    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null
    lateinit var mContext: Context


    open fun initView(){}

    open fun initData(){}

    open fun reLoad() = initData()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        mContext = this.requireActivity().baseContext
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedViewModel = getAppViewModelProvider().get(SharedViewModel::class.java)
    }


    abstract fun setContentView(inflater: LayoutInflater,container: ViewGroup?):View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setContentView(inflater,container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val eventBusTimes = measureTimeMillis {
            // 注册EventBus
            if (javaClass.isAnnotationPresent(EventBusRegister::class.java)) EventBusUtils.register(this)
        }
        Log.d("$javaClass==fragment", "init: EventBus : $eventBusTimes ms")
        initView()
        initData()
    }

    private fun getAppViewModelProvider(): ViewModelProvider {
        return (mActivity.applicationContext as MyApplication).getAppViewModelProvider(mActivity)
    }

    protected fun getFragmentViewModelProvider(fragment: Fragment): ViewModelProvider {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(fragment)
        }
        return mFragmentProvider as ViewModelProvider
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