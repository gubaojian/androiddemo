package com.zhongpin.mvvm_android.network

object ApiServiceInstance {
    val apiService: ApiService by lazy {
        RetrofitFactory.instance.create(ApiService::class.java)
    }
}