package com.zhongpin.mvvm_android.ui.order

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.bean.PurchaseOrderListResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState

class OrderListRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getCompanyList(pageNo:Int): BaseResponse<CompanyListResponse> {
        return apiService.getCompanyList(hashMapOf(
            "pageNo" to pageNo,
            "pageSize" to 20
        )).requireLogin().showLoadingState(loadState)
    }


    suspend fun getPurchaseOrderList(pageNo:Int, query: HashMap<String, Any>): BaseResponse<PurchaseOrderListResponse> {
        val params = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        params.putAll(query)
        return apiService.getPurchaseOrderList(params).requireLogin()
    }


}