package com.zhongpin.mvvm_android.ui.mine.company.member

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.MemberListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class CompanyMemberListViewModel  : BaseViewModel<CompanyMemberListRepository>() {

    val mFirstPageData: MutableLiveData<BaseResponse<MemberListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<MemberListResponse>> = MutableLiveData()


    fun getFirstPageCompanyMemberList(query: HashMap<String, Any>) : MutableLiveData<BaseResponse<MemberListResponse>> {
        initiateRequest({
            mFirstPageData.value = mRepository.getCompanyMemberList(1, query).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mFirstPageData;
    }

    fun getCompanyMemberListMore(pageNo:Int, query: HashMap<String, Any>) : MutableLiveData<BaseResponse<MemberListResponse>> {
        initiateRequest({
            mMorePageData.value = mRepository.getCompanyMemberList(pageNo, query)
        }, loadState)
        return mMorePageData;
    }


    fun confirmOrderReceiveDone(orderId: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.confirmOrderReceiveDone(orderId);
        }, dialogLoadState)
        return mLiveData
    }



}