package com.zhongpin.mvvm_android.common.env

import com.zhongpin.mvvm_android.common.utils.Constant

object AppEnv {
    val ONELINE:Int  = 0;

    val DEV:Int  = 5;

    val TEST:Int  = 10;

    val env:Int = DEV;

    fun isOnline(): Boolean {
        return env == ONELINE;
    }

    fun isDev(): Boolean {
        return env == DEV;
    }

    fun isTest(): Boolean {
        return env == TEST;
    }

    fun initAppEnv() {
        if (isDev()) {
            EnvConstant.appApiBaseUrl = EnvConstant.BASE_URL_DEV
        } else if (isTest()) {
            EnvConstant.appApiBaseUrl = EnvConstant.BASE_URL_TEST
        } else  {
            EnvConstant.appApiBaseUrl = EnvConstant.BASE_URL_ONLINE
        }
    }

}