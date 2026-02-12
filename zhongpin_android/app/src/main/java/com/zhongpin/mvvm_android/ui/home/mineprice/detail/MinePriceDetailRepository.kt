package com.zhongpin.mvvm_android.ui.home.mineprice.detail

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.MaterialPriceDetailItem
import com.zhongpin.mvvm_android.bean.OrderDetailItem
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin


class MinePriceDetailRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getMaterialPriceDetail(id: Long): BaseResponse<MaterialPriceDetailItem> {
        val params = hashMapOf<String, Any>(
            "id" to id
        )
        return apiService.getMaterialPriceDetail(params).requireLogin()
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