package com.zhongpin.mvvm_android.base.repository

import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.network.ApiServiceInstance

open class BaseRepository {

    protected val apiService: ApiService = ApiServiceInstance.apiService
}