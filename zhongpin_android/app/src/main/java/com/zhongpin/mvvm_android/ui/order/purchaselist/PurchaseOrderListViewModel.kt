package com.zhongpin.mvvm_android.ui.order.purchaselist

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.PurchaseOrderListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class PurchaseOrderListViewModel  : BaseViewModel<PurchaseOrderListRepository>() {

    val mFirstPageData: MutableLiveData<BaseResponse<PurchaseOrderListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<PurchaseOrderListResponse>> = MutableLiveData()


    fun getFirstPagePurchaseOrderList(query: HashMap<String, Any>) : MutableLiveData<BaseResponse<PurchaseOrderListResponse>> {
        initiateRequest({
            mFirstPageData.value = mRepository.getPurchaseOrderList(1, query).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mFirstPageData;
    }

    fun getPurchaseOrderListMore(pageNo:Int, query: HashMap<String, Any>) : MutableLiveData<BaseResponse<PurchaseOrderListResponse>> {
        initiateRequest({
            mMorePageData.value = mRepository.getPurchaseOrderList(pageNo, query)
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

    fun cancelPurchaseOrder(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.cancelPurchaseOrder(id);
        }, loadState)
        return mLiveData
    }

    fun payPurchaseOrder(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.payPurchaseOrder(id);
        }, loadState)
        return mLiveData
    }

}