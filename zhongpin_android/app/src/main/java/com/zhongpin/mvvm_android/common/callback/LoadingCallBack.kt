package com.zhongpin.mvvm_android.common.callback
import com.kingja.loadsir.callback.Callback
import com.zhilianshidai.pindan.app.R

class LoadingCallBack : Callback() {
    override fun onCreateView(): Int = R.layout.layout_loading
}