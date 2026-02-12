package com.zhongpin.mvvm_android.ui.order.add

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.bean.LenTypeConfigItem
import com.zhongpin.mvvm_android.bean.PreviewOrderResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiate2Request
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.mergeError
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState

class AddOrderViewModel  : BaseViewModel<AddOrderRepository>() {

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

    val mAddressData: MutableLiveData<BaseResponse<List<AddressListItemResponse>>> = MutableLiveData()

    fun getReceiveAddressList() : MutableLiveData<BaseResponse<List<AddressListItemResponse>>> {
        initiateRequest({
            mAddressData.value = mRepository.getReceiveAddressList()
        }, loadState)
        return mAddressData;
    }

    val mLenTypeConfig: MutableLiveData<BaseResponse<List<LenTypeConfigItem>>> = MutableLiveData()
    fun getLenTypeConfig() : MutableLiveData<BaseResponse<List<LenTypeConfigItem>>> {
        initiateRequest({
            mLenTypeConfig.value = mRepository.getLenTypeConfig()
        }, loadState)
        return mLenTypeConfig;
    }

    val mPageData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
    fun getPageData() {
        initiate2Request({
            mAddressData.value = mRepository.getReceiveAddressList()
        }, {
            mLenTypeConfig.value = mRepository.getLenTypeConfig()
        }, {
            val response = BaseResponse<Boolean>(true, success = true);
            response.mergeError(mAddressData.value)
            response.mergeError(mLenTypeConfig.value)
            response.requireLogin().showLoadingState(loadState, Constant.COMMON_KEY)
            mPageData.value = response
        }, loadState)
    }


    fun uploadImageList(filePaths:List<String>) : MutableLiveData<List<BaseResponse<String>>> {
        val mLiveData: MutableLiveData<List<BaseResponse<String>>> = MutableLiveData()
        initiateRequest({
            val responses:MutableList<BaseResponse<String>> = mutableListOf();
            for(filePath in filePaths) {
                responses.add(mRepository.uploadImage(filePath))
            }
            mLiveData.value = responses;
        }, loadState)
        return mLiveData
    }



    fun orderPreview(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<PreviewOrderResponse>> {
        val mPreviewData: MutableLiveData<BaseResponse<PreviewOrderResponse>> = MutableLiveData()
        initiateRequest({
            mPreviewData.value = mRepository.orderPreview(parameters)
        }, dialogLoadState)
        return mPreviewData;
    }



}