package com.zhongpin.mvvm_android.ui.home.platform.detail

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.OrderDetailItem
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin


class PlatformPriceDetailRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getOrderDetail(orderId: Long): BaseResponse<OrderDetailItem> {
        val params = hashMapOf<String, Any>(
            "id" to orderId
        )
        return apiService.getOrderDetail(params).requireLogin()
    }

    suspend fun confirmOrderReceiveDone(orderId: Long): BaseResponse<Boolean> {
        val params = hashMapOf<String, Any>(
            "id" to orderId
        )
        return apiService.confirmOrderReceiveDone(params).requireLogin()
    }

    suspend fun cancelOrder(orderId: Long): BaseResponse<Boolean> {
        val params = hashMapOf<String, Any>(
            "id" to orderId
        )
        return apiService.cancelOrder(params).requireLogin()
    }
}