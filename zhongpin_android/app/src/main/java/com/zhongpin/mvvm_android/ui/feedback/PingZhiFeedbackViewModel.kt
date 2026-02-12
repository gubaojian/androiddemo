package com.zhongpin.mvvm_android.ui.feedback

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class PingZhiFeedbackViewModel  : BaseViewModel<PingZhiFeedbackRepository>() {

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