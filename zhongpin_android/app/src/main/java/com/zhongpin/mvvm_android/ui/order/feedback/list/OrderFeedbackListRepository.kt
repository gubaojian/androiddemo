package com.zhongpin.mvvm_android.ui.order.feedback.list

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.bean.OrderFeedbackListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState


class OrderFeedbackListRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun getOrderFeedbackList(pageNo:Int, query: HashMap<String, Any>): BaseResponse<OrderFeedbackListResponse>{
        val params = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        params.putAll(query)
        return apiService.getOrderFeedbackList(params).requireLogin()
    }

    suspend fun cancelOrderFeedback(orderId: Long): BaseResponse<Boolean> {
        val params = hashMapOf<String, Any>(
            "id" to orderId
        )
        return apiService.cancelOrderFeedback(params).requireLogin()
    }


}