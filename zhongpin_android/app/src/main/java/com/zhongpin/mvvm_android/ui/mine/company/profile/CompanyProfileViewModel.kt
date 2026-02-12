package com.zhongpin.mvvm_android.ui.mine.company.profile

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class CompanyProfileViewModel  : BaseViewModel<CompanyProfileRepository>() {

    var mUserInfoData: MutableLiveData<BaseResponse<UserInfoResponse>> = MutableLiveData()
    fun getUserInfo(): MutableLiveData<BaseResponse<UserInfoResponse>> {
        initiateRequest({
            val response = mRepository.getUserInfo();
            if (response.success) {
                UserInfoUtil.userInfo = response.data
            }
            mUserInfoData.value = response;
        }, loadState)
        return  mUserInfoData
    }


    fun deleteEntInfoAuth(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.deleteEntInfoAuth(id);
        }, loadState)
        return mLiveData
    }


    val mCompanyLiveData: MutableLiveData<BaseResponse<CompanyListItemResponse>> = MutableLiveData()
    fun getCompanyInfo() : MutableLiveData<BaseResponse<CompanyListItemResponse>> {
        initiateRequest({
            val response = mRepository.getCompanyInfo()
            UserInfoUtil.companyInfo = response.data;
            mCompanyLiveData.value = response.showLoadingState(loadState);
        }, loadState)
        return mCompanyLiveData
    }

}