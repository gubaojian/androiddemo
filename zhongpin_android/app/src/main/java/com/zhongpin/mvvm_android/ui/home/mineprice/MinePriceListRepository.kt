package com.zhongpin.mvvm_android.ui.home.mineprice

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.MaterialPriceListResponse
import com.zhongpin.mvvm_android.bean.PayAccountBalance
import com.zhongpin.mvvm_android.bean.PayItemListResponse
import com.zhongpin.mvvm_android.bean.PriceTipResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin


class MinePriceListRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getPayList(pageNo:Int, query: HashMap<String, Any>): BaseResponse<PayItemListResponse> {
        val params = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        params.putAll(query)
        return apiService.getPayList(params).requireLogin()
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


    suspend fun getPriceTipResponse(): BaseResponse<PriceTipResponse> {
        return apiService.getPriceTipResponse().requireLogin()
    }

    suspend fun getAllMaterialPriceList(query: HashMap<String, Any>): BaseResponse<MaterialPriceListResponse>{
        val params = hashMapOf<String, Any>(
            "pageNo" to 1,
            "pageSize" to 999
        );
        params.putAll(query)
        return apiService.getMaterialPriceList(params)
    }

}