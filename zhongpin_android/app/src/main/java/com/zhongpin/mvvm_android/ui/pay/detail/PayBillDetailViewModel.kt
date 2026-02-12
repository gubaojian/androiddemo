package com.zhongpin.mvvm_android.ui.pay.detail

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class PayBillDetailViewModel  : BaseViewModel<PayBillDetailRepository>() {

    var mUserAuthInfoData: MutableLiveData<BaseResponse<UserInfoAuthResponse>> = MutableLiveData()

    fun getUserAuthInfoData() {
        initiateRequest({
            mUserAuthInfoData.value = mRepository.getUserAuthInfo()
        }, loadState)
    }


    fun deleteEntInfoAuth(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.deleteEntInfoAuth(id);
        }, loadState)
        return mLiveData
    }

    var mPurchaseOrderDetailData: MutableLiveData<BaseResponse<PurchaseOrderDetail>> = MutableLiveData()
    fun getPurchaseOrderDetail(purId: Long) {
        initiateRequest({
            mPurchaseOrderDetailData.value = mRepository.getPurchaseOrderDetail(purId)
        }, loadState)
    }
}