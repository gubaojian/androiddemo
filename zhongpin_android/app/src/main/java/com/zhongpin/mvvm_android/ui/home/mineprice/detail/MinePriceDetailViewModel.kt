package com.zhongpin.mvvm_android.ui.home.mineprice.detail

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.MaterialPriceDetailItem
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class MinePriceDetailViewModel  : BaseViewModel<MinePriceDetailRepository>() {

    var mDetailItemData: MutableLiveData<BaseResponse<MaterialPriceDetailItem>> = MutableLiveData()

    fun getMaterialPriceDetail(id: Long) {
        initiateRequest({
            mDetailItemData.value = mRepository.getMaterialPriceDetail(id).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
    }


    fun confirmOrderReceiveDone(orderId: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.confirmOrderReceiveDone(orderId);
        }, dialogLoadState)
        return mLiveData
    }

    fun cancelOrder(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.cancelOrder(id);
        }, dialogLoadState)
        return mLiveData
    }
}