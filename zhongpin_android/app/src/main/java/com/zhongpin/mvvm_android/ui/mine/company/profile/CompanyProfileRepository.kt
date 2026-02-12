package com.zhongpin.mvvm_android.ui.mine.company.profile

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState


class CompanyProfileRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getUserInfo(): BaseResponse<UserInfoResponse> {
        return apiService.getUserInfo().requireLogin().showLoadingState(loadState, Constant.COMMON_KEY)
    }

    suspend fun deleteEntInfoAuth(id: Long): BaseResponse<Boolean> {
        return apiService.deleteEntInfoAuth(id)
    }

    suspend fun getCompanyInfo(): BaseResponse<CompanyListItemResponse> {
        return apiService.getCompanyInfo().requireLogin()
    }

}