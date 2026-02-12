package com.zhongpin.mvvm_android.ui.home

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.MaterialPriceListResponse
import com.zhongpin.mvvm_android.bean.OrderStatisticsData
import com.zhongpin.mvvm_android.bean.PlatformMaterialListResponse
import com.zhongpin.mvvm_android.bean.PriceTipResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.dataConvert
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState

class HomeRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getUserInfo(): BaseResponse<UserInfoResponse> {
        return apiService.getUserInfo()
    }

    suspend fun getCompanyInfo(): BaseResponse<CompanyListItemResponse> {
        return apiService.getCompanyInfo()
    }

    suspend fun getAllPlatformMaterialList(): BaseResponse<PlatformMaterialListResponse>{
        return apiService.getPlatformMaterialList(hashMapOf(
            "pageNo" to 1,
            "pageSize" to 999
        ))
    }

    suspend fun getAllMaterialPriceList(): BaseResponse<MaterialPriceListResponse>{
        return apiService.getMaterialPriceList(hashMapOf(
            "pageNo" to 1,
            "pageSize" to 3
        ))
    }

    suspend fun getOrderStatisticsData(): BaseResponse<OrderStatisticsData> {
        return apiService.getOrderStatisticsData(hashMapOf())
    }

}