package com.zhongpin.mvvm_android.ui.pay.chargeinput

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState


class ChargeInputRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getUserAuthInfo(): BaseResponse<UserInfoAuthResponse> {
        return apiService.getUserAuthInfo().requireLogin().showLoadingState(loadState)
    }

    suspend fun deleteEntInfoAuth(id: Long): BaseResponse<Boolean> {
        return apiService.deleteEntInfoAuth(id)
    }

    suspend fun addChargeInput(parameters:HashMap<String,Any>): BaseResponse<PayItem> {
        return apiService.addChargeInput(parameters["amount"].toString()).requireLogin()
    }

    suspend fun getWaitPayList(pageNo:Int, query: HashMap<String, Any>): BaseResponse<List<PayItem>> {
        val params = hashMapOf<String, Any>(
            "pageNo" to pageNo,
            "pageSize" to 20
        )
        params.putAll(query)
        return apiService.getWaitPayList().requireLogin()
    }

}