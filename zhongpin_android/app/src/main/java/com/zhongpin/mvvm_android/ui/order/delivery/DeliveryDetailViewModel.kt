package com.zhongpin.mvvm_android.ui.order.delivery

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.OrderDeliveryProofListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class DeliveryDetailViewModel  : BaseViewModel<DeliveryDetailRepository>() {

    val mFirstPageData: MutableLiveData<BaseResponse<OrderDeliveryProofListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<OrderDeliveryProofListResponse>> = MutableLiveData()


    fun getFirstPageOrderDeliveryProfList(orderId: Long) : MutableLiveData<BaseResponse<OrderDeliveryProofListResponse>> {
        initiateRequest({
            mFirstPageData.value = mRepository.getOrderDeliveryProofList(1, orderId).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mFirstPageData;
    }

    fun getOrderDeliveryProfListMore(pageNo:Int, orderId: Long) : MutableLiveData<BaseResponse<OrderDeliveryProofListResponse>> {
        initiateRequest({
            mMorePageData.value = mRepository.getOrderDeliveryProofList(pageNo, orderId)
        }, loadState)
        return mMorePageData;
    }
}