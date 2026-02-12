package com.zhongpin.mvvm_android.ui.mine.company.result

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.EntInfoResponse
import com.zhongpin.mvvm_android.bean.IdCardInfoResponse
import com.zhongpin.mvvm_android.bean.LatLntResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class CompanySignResultViewModel  : BaseViewModel<CompanySignResultRepository>() {

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

    fun submitEntInfoAuth(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<Long>> {
        val mLiveData: MutableLiveData<BaseResponse<Long   >> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.submitEntInfoAuth(parameters);
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

    fun identifyIdCardInfo(filePath:String) : MutableLiveData<BaseResponse<IdCardInfoResponse>> {
        val mLiveData: MutableLiveData<BaseResponse<IdCardInfoResponse>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.identifyIdCardInfo(filePath);
        }, loadState)
        return mLiveData
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

    fun editEntInfoAuth(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.editEntInfoAuth(parameters);
        }, loadState)
        return mLiveData
    }

}