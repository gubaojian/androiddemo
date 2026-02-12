package com.zhongpin.mvvm_android.ui.buy.detail

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class PublishBuyDetailViewModel  : BaseViewModel<PublishBuyDetailRepository>() {

    val mFirstPageData: MutableLiveData<BaseResponse<CompanyListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<CompanyListResponse>> = MutableLiveData()


    fun getFirstPageCompanyList() : MutableLiveData<BaseResponse<CompanyListResponse>> {
        initiateRequest({
            mFirstPageData.value = mRepository.getCompanyList(1)
        }, loadState)
        return mFirstPageData;
    }

    fun getCompanyListMore(pageNo:Int) : MutableLiveData<BaseResponse<CompanyListResponse>> {
         initiateRequest({
            mMorePageData.value = mRepository.getCompanyList(pageNo)
        }, loadState)
        return mMorePageData;
    }


    fun deleteReceiveAddress(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.deleteReceiveAddress(id);
        }, loadState)
        return mLiveData
    }


}