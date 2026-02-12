package com.zhongpin.mvvm_android.ui.mine.company

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.bean.IdCardInfoResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.dataConvert
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CompanyListRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun getCompanyList(pageNo:Int): BaseResponse<CompanyListResponse>{
        return apiService.getCompanyList(hashMapOf(
            "pageNo" to pageNo,
            "pageSize" to 20
        )).requireLogin().showLoadingState(loadState, Constant.COMMON_KEY)
    }



}