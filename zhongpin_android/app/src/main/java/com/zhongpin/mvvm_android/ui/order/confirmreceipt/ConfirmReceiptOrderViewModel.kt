package com.zhongpin.mvvm_android.ui.order.confirmreceipt

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.bean.OrderDeliveryProofListResponse
import com.zhongpin.mvvm_android.bean.OrderDetailItem
import com.zhongpin.mvvm_android.bean.PreviewOrderResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateNRequest
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.mergeError
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState

class ConfirmReceiptOrderViewModel  : BaseViewModel<ConfirmReceiptOrderRepository>() {

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

    val mPreviewData: MutableLiveData<BaseResponse<PreviewOrderResponse>> = MutableLiveData()

    fun orderPreview(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<PreviewOrderResponse>> {
        initiateRequest({
            mPreviewData.value = mRepository.orderPreview(parameters)
        }, loadState)
        return mPreviewData;
    }



    fun createOrder(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<Boolean>> {
        val mCreateOrderData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mCreateOrderData.value = mRepository.createOrder(parameters)
        }, loadState)
        return mCreateOrderData;
    }

    val mDeliveryPageData: MutableLiveData<BaseResponse<OrderDeliveryProofListResponse>> = MutableLiveData()

    fun getAllOrderDeliveryProfList(orderId: Long) : MutableLiveData<BaseResponse<OrderDeliveryProofListResponse>> {
        initiateRequest({
            mDeliveryPageData.value = mRepository.getOrderDeliveryProofList(1, orderId).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mDeliveryPageData;
    }


    fun confirmOrderReceiveDone(orderId: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.confirmOrderReceiveDone(orderId);
        }, dialogLoadState)
        return mLiveData
    }

    var mOrderItemData: MutableLiveData<BaseResponse<OrderDetailItem>> = MutableLiveData()

    fun getOrderDetail(orderId: Long) {
        initiateRequest({
            mOrderItemData.value = mRepository.getOrderDetail(orderId).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
    }


    val mPageData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
    fun getPageData(orderId: Long) {
        initiateNRequest({
            mDeliveryPageData.value = mRepository.getOrderDeliveryProofList(1, orderId)
        },  {
            mOrderItemData.value = mRepository.getOrderDetail(orderId)
        }, done = {
            val response = BaseResponse<Boolean>(true, success = true);
            response.mergeError(mDeliveryPageData.value)
            response.mergeError(mOrderItemData.value)
            response.requireLogin().showLoadingState(loadState, Constant.COMMON_KEY)
            mPageData.value = response
        },  loadState = loadState)
    }


}