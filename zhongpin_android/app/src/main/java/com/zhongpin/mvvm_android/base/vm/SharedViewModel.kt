package com.zhongpin.mvvm_android.base.vm

import androidx.lifecycle.ViewModel

class SharedViewModel :ViewModel(){

    val windowFocusChangedListener: UnPeekLiveData<Boolean> = UnPeekLiveData()
}