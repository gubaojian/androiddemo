package com.zhongpin.mvvm_android.ui.order.delivery

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.OrderDeliveryProofListResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin


class DeliveryDetailRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getOrderDeliveryProofList(pageNo:Int, orderId: Long): BaseResponse<OrderDeliveryProofListResponse>{
        return apiService.getOrderDeliveryProofList(hashMapOf(
            "pageNo" to pageNo,
            "orderId" to orderId,
            "pageSize" to 20
        )).requireLogin()
    }
}