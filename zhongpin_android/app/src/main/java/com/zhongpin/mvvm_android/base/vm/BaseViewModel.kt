package com.zhongpin.mvvm_android.base.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.common.utils.CommonUtils

open class BaseViewModel<R: BaseRepository>: ViewModel() {

    val loadState by lazy {
        MutableLiveData<State>()
    }

    val dialogLoadState by lazy {
        MutableLiveData<State>()
    }


    val mRepository by lazy {
        CommonUtils.getClass<R>(this)
            .getDeclaredConstructor(MutableLiveData::class.java)
            .newInstance(loadState)
    }

    override fun onCleared() {
        super.onCleared()
    }
}