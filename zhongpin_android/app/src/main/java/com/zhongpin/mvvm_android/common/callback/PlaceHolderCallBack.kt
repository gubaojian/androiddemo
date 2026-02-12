package com.zhongpin.mvvm_android.common.callback

import android.content.Context
import android.view.View
import com.kingja.loadsir.callback.Callback

class PlaceHolderCallBack(var layoutID:Int) :Callback(){

    override fun onCreateView(): Int = layoutID

    override fun onReloadEvent(context: Context?, view: View?): Boolean = true


}