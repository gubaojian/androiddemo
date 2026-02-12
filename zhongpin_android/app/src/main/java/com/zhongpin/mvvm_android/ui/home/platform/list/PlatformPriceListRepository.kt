package com.zhongpin.mvvm_android.ui.home.platform.list

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.MemberListResponse
import com.zhongpin.mvvm_android.bean.PlatformMaterialListResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin


class PlatformPriceListRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun getCompanyMemberList(pageNo:Int, query: HashMap<String, Any>): BaseResponse<MemberListResponse> {
        val params = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        params.putAll(query)
        return apiService.getCompanyMemberList(params).requireLogin()
    }



    suspend fun confirmOrderReceiveDone(orderId: Long): BaseResponse<Boolean> {
        val params = hashMapOf<String, Any>(
            "id" to orderId
        )
        return apiService.confirmOrderReceiveDone(params).requireLogin()
    }

    suspend fun getAllPlatformMaterialList(): BaseResponse<PlatformMaterialListResponse>{
        return apiService.getPlatformMaterialList(hashMapOf(
            "pageNo" to 1,
            "pageSize" to 999
        )).requireLogin()
    }


}