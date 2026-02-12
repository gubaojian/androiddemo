package com.zhongpin.mvvm_android.ui.me

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.MinePageData
import com.zhongpin.mvvm_android.bean.PayAccountBalance
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateNRequest
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.mergeError
import com.zhongpin.mvvm_android.network.showLoadingState

class MineViewModel  : BaseViewModel<MineRepository>() {


    var mUserInfoData: MutableLiveData<BaseResponse<UserInfoResponse>> = MutableLiveData()
    fun getUserInfo(): MutableLiveData<BaseResponse<UserInfoResponse>> {
        initiateRequest({
            val response = mRepository.getUserInfo().showLoadingState(loadState, Constant.COMMON_KEY);
            if (response.success) {
                UserInfoUtil.userInfo = response.data
            }
            mUserInfoData.value = response;
        }, loadState, mUserInfoData)
        return  mUserInfoData
    }

    fun getCompanyInfo() : MutableLiveData<BaseResponse<CompanyListItemResponse>> {
        val mLiveData: MutableLiveData<BaseResponse<CompanyListItemResponse>> = MutableLiveData()
        initiateRequest({
            val response = mRepository.getCompanyInfo()
            UserInfoUtil.companyInfo = response.data;
            mLiveData.value = response;
        }, loadState)
        return mLiveData
    }

    var mPayAccountBalanceData: MutableLiveData<BaseResponse<PayAccountBalance>> = MutableLiveData()
    fun getPayAccountBalance() {
        initiateRequest({
            mPayAccountBalanceData.value = mRepository.getPayAccountBalance()
        }, dialogLoadState)
    }

    val mPageData: MutableLiveData<BaseResponse<MinePageData>> = MutableLiveData()
    fun getPageData() {
        val pageData = MinePageData();
        initiateNRequest(
            {
                 pageData.statisticsData = mRepository.getOrderStatisticsData()
            },
            {
                pageData.payAccountInfo = mRepository.getPayAccountBalance()
            },
            {
                val response = mRepository.getUserInfo();
                if (response.success) {
                    UserInfoUtil.userInfo = response.data
                }
                pageData.userInfo = response
            },
            {
                val response = mRepository.getCompanyInfo()
                if (response.success) {
                    UserInfoUtil.companyInfo = response.data;
                }
                pageData.companyInfo = response
            },
            done = {
                val response = BaseResponse<MinePageData>(pageData, success = true);
                response.mergeError(pageData.companyInfo)
                response.mergeError(pageData.userInfo)
                response.mergeError(pageData.payAccountInfo)
                response.mergeError(pageData.payAccountInfo)
                response.showLoadingState(loadState, Constant.COMMON_KEY)
                mPageData.value = response
            }, loadState = loadState
        )
    }

}