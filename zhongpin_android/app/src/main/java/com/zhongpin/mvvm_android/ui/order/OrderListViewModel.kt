package com.zhongpin.mvvm_android.ui.order

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.bean.PurchaseOrderListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState

class OrderListViewModel  : BaseViewModel<OrderListRepository>() {

    val mFirstPageData: MutableLiveData<BaseResponse<CompanyListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<CompanyListResponse>> = MutableLiveData()


    fun getFirstPageCompanyList() : MutableLiveData<BaseResponse<CompanyListResponse>> {
        initiateRequest({
            mFirstPageData.value = mRepository.getCompanyList(1).requireLogin().showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mFirstPageData;
    }

    fun getCompanyListMore(pageNo:Int) : MutableLiveData<BaseResponse<CompanyListResponse>> {
        initiateRequest({
            mMorePageData.value = mRepository.getCompanyList(pageNo)
        }, loadState)
        return mMorePageData;
    }


    val mWaitPayOrderPageData: MutableLiveData<BaseResponse<PurchaseOrderListResponse>> = MutableLiveData()
    fun getFirstPagePurchaseOrderList(query: HashMap<String, Any>) : MutableLiveData<BaseResponse<PurchaseOrderListResponse>> {
        initiateRequest({
            mWaitPayOrderPageData.value = mRepository.getPurchaseOrderList(1, query).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mWaitPayOrderPageData;
    }

}