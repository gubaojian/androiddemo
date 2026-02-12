package com.zhongpin.mvvm_android.ui.order.add.history

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.HistoryOrderListResponse
import com.zhongpin.mvvm_android.bean.SelectHistoryOrderListResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class HistoryOrderSearchListViewModel  : BaseViewModel<HistoryOrderSearchListRepository>() {

    val mFirstPageData: MutableLiveData<BaseResponse<SelectHistoryOrderListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<SelectHistoryOrderListResponse>> = MutableLiveData()


    fun getFirstPageCompanyList(query: Map<String, Any>) : MutableLiveData<BaseResponse<SelectHistoryOrderListResponse>> {
        initiateRequest({
            mFirstPageData.value = mRepository.getFirstSelectHisOrderList(1, query)
        }, loadState)
        return mFirstPageData;
    }

    fun getCompanyListMore(pageNo:Int, query: Map<String, Any>) : MutableLiveData<BaseResponse<SelectHistoryOrderListResponse>> {
         initiateRequest({
            mMorePageData.value = mRepository.getHisOrderListMore(pageNo, query)
        }, loadState)
        return mMorePageData;
    }




}