package com.zhongpin.mvvm_android.ui.order.preview

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.bean.PreviewOrderResponse
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class PreviewAddOrderViewModel  : BaseViewModel<PreviewAddOrderRepository>() {

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

    fun createOrderV6(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<PurchaseOrderDetail>> {
        val mCreateOrderData: MutableLiveData<BaseResponse<PurchaseOrderDetail>> = MutableLiveData()
        initiateRequest({
            mCreateOrderData.value = mRepository.createOrderV6(parameters)
        }, loadState)
        return mCreateOrderData;
    }

}