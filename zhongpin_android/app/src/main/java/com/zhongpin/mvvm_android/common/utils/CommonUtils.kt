package com.zhongpin.mvvm_android.common.utils

import java.lang.reflect.ParameterizedType

object CommonUtils{

    fun <T> getClass(t:Any):Class<T>{
        return (t.javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<T>
    }
}