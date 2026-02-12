package com.zhongpin.mvvm_android.ui.mine.company.profile.change

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class ChangeCompanyContractViewModel  : BaseViewModel<ChangeCompanyContractRepository>() {

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

    fun uploadImage(filePath:String) : MutableLiveData<BaseResponse<String>> {
        val mLiveData: MutableLiveData<BaseResponse<String>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.uploadImage(filePath);
        }, loadState)
        return mLiveData
    }

    fun updateEntContract(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.updateEntContract(parameters);
        }, loadState)
        return mLiveData
    }

}