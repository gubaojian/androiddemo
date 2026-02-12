package com.zhongpin.mvvm_android.ui.home.mineprice

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.MaterialPriceListResponse
import com.zhongpin.mvvm_android.bean.PayAccountBalance
import com.zhongpin.mvvm_android.bean.PayItemListResponse
import com.zhongpin.mvvm_android.bean.PriceTipResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class MinePriceListViewModel  : BaseViewModel<MinePriceListRepository>() {

    val mFirstPageData: MutableLiveData<BaseResponse<PayItemListResponse>> = MutableLiveData()
    val mMorePageData: MutableLiveData<BaseResponse<PayItemListResponse>> = MutableLiveData()


    fun getFirstPagePayList(query: HashMap<String, Any>) : MutableLiveData<BaseResponse<PayItemListResponse>> {
        initiateRequest({
            mFirstPageData.value = mRepository.getPayList(1, query).showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mFirstPageData;
    }

    fun getPayListMore(pageNo:Int, query: HashMap<String, Any>) : MutableLiveData<BaseResponse<PayItemListResponse>> {
        initiateRequest({
            mMorePageData.value = mRepository.getPayList(pageNo, query)
        }, loadState)
        return mMorePageData;
    }


    var mPayAccountBalanceData: MutableLiveData<BaseResponse<PayAccountBalance>> = MutableLiveData()
    fun getPayAccountBalance() {
        initiateRequest({
            mPayAccountBalanceData.value = mRepository.getPayAccountBalance()
        }, dialogLoadState)
    }

    var mPriceTipData: MutableLiveData<BaseResponse<PriceTipResponse>> = MutableLiveData()
    fun getPriceTipResponse() {
        initiateRequest({
            mPriceTipData.value = mRepository.getPriceTipResponse()
        }, dialogLoadState)
    }

    val mAllMaterialPriceListData: MutableLiveData<BaseResponse<MaterialPriceListResponse>> = MutableLiveData()
    fun getAllMaterialPriceList(query: HashMap<String, Any>) : MutableLiveData<BaseResponse<MaterialPriceListResponse>> {
        initiateRequest({
            mAllMaterialPriceListData.value = mRepository.getAllMaterialPriceList(query).showLoadingState(loadState,
                Constant.COMMON_KEY)
        }, loadState)
        return mAllMaterialPriceListData;
    }

}