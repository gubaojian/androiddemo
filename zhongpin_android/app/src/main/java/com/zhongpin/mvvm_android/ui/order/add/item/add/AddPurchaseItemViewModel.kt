package com.zhongpin.mvvm_android.ui.order.add.item.add

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.BoxConvertResponse
import com.zhongpin.mvvm_android.bean.BoxTypeConfigItem
import com.zhongpin.mvvm_android.bean.EntInfoResponse
import com.zhongpin.mvvm_android.bean.LatLntResponse
import com.zhongpin.mvvm_android.bean.LenTypeConfigItem
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiate2Request
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.mergeError
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState

class AddPurchaseItemViewModel  : BaseViewModel<AddPurchaseItemRepository>() {

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

    fun addReceiveAddress(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.addReceiveAddress(parameters);
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

    fun calculateBoxConvert(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<BoxConvertResponse>> {
        val mLiveData: MutableLiveData<BaseResponse<BoxConvertResponse>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.calculateBoxConvert(parameters)
        }, loadState)
        return mLiveData
    }

    fun checkMaterial(platCode:String) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.checkMaterial(platCode)
        }, loadState)
        return mLiveData
    }

    val mLenTypeConfig: MutableLiveData<BaseResponse<List<LenTypeConfigItem>>> = MutableLiveData()
    fun getLenTypeConfig() : MutableLiveData<BaseResponse<List<LenTypeConfigItem>>> {
        initiateRequest({
            mLenTypeConfig.value = mRepository.getLenTypeConfig().requireLogin()
        }, loadState)
        return mLenTypeConfig;
    }

    val mBoxTypeConfig: MutableLiveData<BaseResponse<List<BoxTypeConfigItem>>> = MutableLiveData()
    fun getBoxTypeConfig() : MutableLiveData<BaseResponse<List<BoxTypeConfigItem>>> {
        initiateRequest({
            mBoxTypeConfig.value = mRepository.getBoxTypeConfig().requireLogin()
        }, loadState)
        return mBoxTypeConfig;
    }

    val mPageData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
    fun getPageData() {
        initiate2Request({
            mBoxTypeConfig.value = mRepository.getBoxTypeConfig()
        }, {
            mLenTypeConfig.value = mRepository.getLenTypeConfig()
        }, {
            val response = BaseResponse<Boolean>(true, success = true);
            response.mergeError(mBoxTypeConfig.value)
            response.mergeError(mLenTypeConfig.value)
            response.requireLogin().showLoadingState(loadState, Constant.COMMON_KEY)
            mPageData.value = response
        }, loadState)
    }


}