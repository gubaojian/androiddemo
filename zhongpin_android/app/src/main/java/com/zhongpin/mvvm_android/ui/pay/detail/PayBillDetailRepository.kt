package com.zhongpin.mvvm_android.ui.pay.detail

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState


class PayBillDetailRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getUserAuthInfo(): BaseResponse<UserInfoAuthResponse> {
        return apiService.getUserAuthInfo().requireLogin().showLoadingState(loadState)
    }

    suspend fun deleteEntInfoAuth(id: Long): BaseResponse<Boolean> {
        return apiService.deleteEntInfoAuth(id)
    }

    suspend fun getPurchaseOrderDetail(purId: Long): BaseResponse<PurchaseOrderDetail> {
        val params = hashMapOf<String, Any>(
            "id" to purId
        )
        return apiService.getPurchaseOrderDetail(params).requireLogin()
    }
}