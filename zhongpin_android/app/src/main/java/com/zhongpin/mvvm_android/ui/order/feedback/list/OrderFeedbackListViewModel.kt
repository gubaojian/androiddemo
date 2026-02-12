package com.zhongpin.mvvm_android.ui.order.feedback.list

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.OrderFeedbackListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class OrderFeedbackListViewModel  : BaseViewModel<OrderFeedbackListRepository>() {




    val mFirstPageData: MutableLiveData<BaseResponse<OrderFeedbackListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<OrderFeedbackListResponse>> = MutableLiveData()

    val query = hashMapOf<String, Any>()

    fun getFirstPageOrderFeedbackList() : MutableLiveData<BaseResponse<OrderFeedbackListResponse>> {
        initiateRequest({
            mFirstPageData.value = mRepository.getOrderFeedbackList(1, query).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mFirstPageData;
    }

    fun getOrderFeedbackListMore(pageNo:Int) : MutableLiveData<BaseResponse<OrderFeedbackListResponse>> {
         initiateRequest({
            mMorePageData.value = mRepository.getOrderFeedbackList(pageNo, query)
        }, loadState)
        return mMorePageData;
    }

    fun cancelOrderFeedback(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.cancelOrderFeedback(id);
        }, dialogLoadState)
        return mLiveData
    }



}