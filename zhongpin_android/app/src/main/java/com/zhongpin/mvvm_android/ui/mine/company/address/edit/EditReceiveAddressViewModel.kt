package com.zhongpin.mvvm_android.ui.mine.company.address.edit

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.EntInfoResponse
import com.zhongpin.mvvm_android.bean.LatLntResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class EditReceiveAddressViewModel  : BaseViewModel<EditReceiveAddressRepository>() {

    var mBannerData: MutableLiveData<List<UserInfoResponse>> = MutableLiveData()

    fun loadBannerCo() {
        initiateRequest({
            mBannerData.value = mRepository.loadBannerCo()
        }, loadState)
    }


    fun sendVerifyCode(mobile:String) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.sendVerifyCode(mobile);
        }, loadState)
        return mLiveData
    }

    fun editReceiveAddress(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.editReceiveAddress(parameters);
        }, loadState)
        return mLiveData
    }

    fun getLntLngInfo(address:String) : MutableLiveData<BaseResponse<LatLntResponse>> {
        val mLiveData: MutableLiveData<BaseResponse<LatLntResponse>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.getLntLngInfo(address);
        }, loadState)
        return mLiveData
    }

    fun uploadImage(filePath:String) : MutableLiveData<BaseResponse<String>> {
        val mLiveData: MutableLiveData<BaseResponse<String>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.uploadImage(filePath);
        }, loadState)
        return mLiveData
    }

    fun identifyEntInfo(filePath:String) : MutableLiveData<BaseResponse<EntInfoResponse>> {
        val mLiveData: MutableLiveData<BaseResponse<EntInfoResponse>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.identifyEntInfo(filePath);
        }, loadState)
        return mLiveData
    }

}