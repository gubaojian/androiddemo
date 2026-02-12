package com.zhongpin.mvvm_android.ui.pay

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.OrderListResponse
import com.zhongpin.mvvm_android.bean.PayAccountBalance
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.PayItemListResponse
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState


class PayAccountDetailRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getPayList(pageNo:Int, query: HashMap<String, Any>): BaseResponse<PayItemListResponse> {
        val params = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        params.putAll(query)
        return apiService.getPayList(params).requireLogin()
    }

    suspend fun getWaitPayList(pageNo:Int, query: HashMap<String, Any>): BaseResponse<List<PayItem>> {
        val params = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        params.putAll(query)
        return apiService.getWaitPayList().requireLogin()
    }

    suspend fun confirmOrderReceiveDone(orderId: Long): BaseResponse<Boolean> {
        val params = hashMapOf<String, Any>(
            "id" to orderId
        )
        return apiService.confirmOrderReceiveDone(params).requireLogin()
    }

    suspend fun getPayAccountBalance(): BaseResponse<PayAccountBalance> {
        return apiService.getPayAccountBalance().requireLogin()
    }

}