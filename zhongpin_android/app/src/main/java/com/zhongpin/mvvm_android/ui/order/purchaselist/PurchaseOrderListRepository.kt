package com.zhongpin.mvvm_android.ui.order.purchaselist

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.MemberListResponse
import com.zhongpin.mvvm_android.bean.PurchaseOrderListResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin


class PurchaseOrderListRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun getPurchaseOrderList(pageNo:Int, query: HashMap<String, Any>): BaseResponse<PurchaseOrderListResponse> {
        val params = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        params.putAll(query)
        return apiService.getPurchaseOrderList(params).requireLogin()
    }

    suspend fun confirmOrderReceiveDone(orderId: Long): BaseResponse<Boolean> {
        val params = hashMapOf<String, Any>(
            "id" to orderId
        )
        return apiService.confirmOrderReceiveDone(params).requireLogin()
    }

    suspend fun cancelPurchaseOrder(id: Long): BaseResponse<Boolean> {
        val parameters:HashMap<String,Any> = hashMapOf();
        parameters.put("id", id);
        return apiService.cancelPurchaseOrder(parameters)
    }

    suspend fun payPurchaseOrder(id: Long): BaseResponse<Boolean> {
        val parameters:HashMap<String,Any> = hashMapOf();
        parameters.put("id", id);
        return apiService.payPurchaseOrder(parameters)
    }


}