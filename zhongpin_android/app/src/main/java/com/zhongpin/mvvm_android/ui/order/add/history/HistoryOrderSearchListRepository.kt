package com.zhongpin.mvvm_android.ui.order.add.history

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.HistoryOrderListResponse
import com.zhongpin.mvvm_android.bean.SelectHistoryOrderListResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin


class HistoryOrderSearchListRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getSelectHisOrderList(pageNo:Int, query: Map<String, Any>): BaseResponse<SelectHistoryOrderListResponse> {
        val parameters = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        parameters.putAll(query)
        return apiService.getSelectHisOrderList(parameters).requireLogin()
    }

    suspend fun getFirstSelectHisOrderList(pageNo:Int, query: Map<String, Any>): BaseResponse<SelectHistoryOrderListResponse> {
        val parameters = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        parameters.putAll(query)
        return apiService.getSelectHisOrderList(parameters).requireLogin()
    }

    suspend fun getHisOrderListMore(pageNo:Int, query: Map<String, Any>): BaseResponse<SelectHistoryOrderListResponse> {
        val parameters = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        parameters.putAll(query)
        return apiService.getSelectHisOrderList(parameters).requireLogin()
    }



}