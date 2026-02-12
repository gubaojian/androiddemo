package com.zhongpin.mvvm_android.ui.buy.add

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class AddPublishBuyDetailViewModel  : BaseViewModel<AddPublishBuyDetailRepository>() {

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

    fun getEntReceiveAddressList(entId:Long) : MutableLiveData<BaseResponse<List<AddressListItemResponse>>> {
        initiateRequest({
            mAddressData.value = mRepository.getEntReceiveAddressList(entId)
        }, loadState)
        return mAddressData;
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



}