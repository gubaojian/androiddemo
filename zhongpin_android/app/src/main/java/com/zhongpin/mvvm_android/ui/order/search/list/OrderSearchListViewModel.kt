package com.zhongpin.mvvm_android.ui.order.search.list

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.OrderListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class OrderSearchListViewModel  : BaseViewModel<OrderSearchListRepository>() {

    val mFirstPageData: MutableLiveData<BaseResponse<OrderListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<OrderListResponse>> = MutableLiveData()


    fun getFirstPageOrderList(query: HashMap<String, Any>) : MutableLiveData<BaseResponse<OrderListResponse>> {
        initiateRequest({
            mFirstPageData.value = mRepository.getOrderList(1, query).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mFirstPageData;
    }

    fun getOrderListMore(pageNo:Int, query: HashMap<String, Any>) : MutableLiveData<BaseResponse<OrderListResponse>> {
        initiateRequest({
            mMorePageData.value = mRepository.getOrderList(pageNo, query)
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