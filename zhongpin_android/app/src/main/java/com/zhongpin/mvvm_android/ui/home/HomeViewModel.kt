package com.zhongpin.mvvm_android.ui.home

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson2.JSON
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ResourceUtils
import com.tencent.mmkv.MMKV
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.HomePageData
import com.zhongpin.mvvm_android.bean.MaterialPriceListResponse
import com.zhongpin.mvvm_android.bean.PlatformMaterialListResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.login.LoginUtils
import com.zhongpin.mvvm_android.common.env.AppEnv
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiate2Request
import com.zhongpin.mvvm_android.network.initiateNRequest
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.isTokenExpired
import com.zhongpin.mvvm_android.network.mergeError
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.setValueSafe
import com.zhongpin.mvvm_android.network.showLoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel<HomeRepository>() {

    var mUserInfoData: MutableLiveData<BaseResponse<UserInfoResponse>> = MutableLiveData()

    fun getUserInfo() {
        initiateRequest({
            val response = mRepository.getUserInfo().showLoadingState(loadState);
            mUserInfoData.value = response;
            if (response.success) {
                UserInfoUtil.userInfo = response.data
            }
        }, loadState)
    }

    fun getCompanyInfo(): MutableLiveData<BaseResponse<CompanyListItemResponse>> {
        val mLiveData: MutableLiveData<BaseResponse<CompanyListItemResponse>> = MutableLiveData()
        initiateRequest({
            val response = mRepository.getCompanyInfo()
            if (response.success) {
                UserInfoUtil.companyInfo = response.data;
            }
            mLiveData.value = response;
        }, loadState)
        return mLiveData
    }

    val mAllPlatformMaterialListData: MutableLiveData<BaseResponse<PlatformMaterialListResponse>> =
        MutableLiveData()

    fun getAllPlatformMaterialList(): MutableLiveData<BaseResponse<PlatformMaterialListResponse>> {
        initiateRequest({
            mAllPlatformMaterialListData.value = mRepository.getAllPlatformMaterialList()
        }, loadState)
        return mAllPlatformMaterialListData;
    }

    val mAllMaterialPriceListData: MutableLiveData<BaseResponse<MaterialPriceListResponse>> = MutableLiveData()
    fun getAllMaterialPriceList() : MutableLiveData<BaseResponse<MaterialPriceListResponse>> {
        initiateRequest({
            mAllMaterialPriceListData.value = mRepository.getAllMaterialPriceList()
        }, loadState)
        return mAllMaterialPriceListData;
    }

    val mPageData: MutableLiveData<BaseResponse<HomePageData>> = MutableLiveData()
    fun getPageData() {
        if (mPageData.value == null) {
            getPageDataCache();
        }
        val homePageData = HomePageData();
        if (LoginUtils.hasLogin()) {
            initiateNRequest(
                {
                    homePageData.minePrice = mRepository.getAllMaterialPriceList();
                },
                {
                    homePageData.platformPrice = mRepository.getAllPlatformMaterialList()
                },
                {
                    homePageData.statisticsData = mRepository.getOrderStatisticsData()
                }, {
                    val response = mRepository.getUserInfo();
                    mUserInfoData.value = response;
                    if (response.success) {
                        UserInfoUtil.userInfo = response.data
                    }
                    if (response.isTokenExpired()) {
                        LoginUtils.clearToken()
                    }
                }, {
                    val response = mRepository.getCompanyInfo()
                    if (response.success) {
                        UserInfoUtil.companyInfo = response.data;
                    }
                },
                done = {
                    homePageData.isLogin = LoginUtils.hasLogin()
                    val response = BaseResponse<HomePageData>(homePageData, success = true);
                    response.mergeError(homePageData.platformPrice)
                    response.mergeError(homePageData.minePrice)
                    response.mergeError(homePageData.statisticsData)
                    if (response.isTokenExpired()) {
                        response.success = true
                    }
                    response.showLoadingState(loadState, Constant.COMMON_KEY)
                    mPageData.value = response
                    if (response.success) {
                        savePageCache(item = homePageData)
                    }
                }, loadState = loadState
            )
        } else {
            initiateNRequest({
                homePageData.platformPrice = mRepository.getAllPlatformMaterialList()
            }, done = {
                homePageData.isLogin = LoginUtils.hasLogin()
                val response = BaseResponse<HomePageData>(homePageData, success = true);
                response.mergeError(homePageData.platformPrice)
                if (response.isTokenExpired()) {
                    response.success = true
                }
                response.showLoadingState(loadState, Constant.COMMON_KEY)
                mPageData.value = response
                if (response.success) {
                    savePageCache(item = homePageData)
                }
            }, loadState = loadState)
        }

    }

    fun getPageDataCache() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                var json = mmkv.getString(homeDataCacheKey, "")
                if (TextUtils.isEmpty(json)) {
                     json = ResourceUtils.readAssets2String("home_asset_cache.json", "UTF-8")
                }
                if (TextUtils.isEmpty(json)) {
                    return@runCatching
                }

                val homePageData = JSON.parseObject(json, HomePageData::class.java);
                val response = BaseResponse<HomePageData>(homePageData, success = true);
                if (BuildConfig.DEBUG) {
                    LogUtils.d("HomePageData", "get HomePageData from cache success")
                }
                viewModelScope.launch (Dispatchers.Main) {
                    if (mPageData.value == null) { //一定要在主线程判断
                        mPageData.setValueSafe(response)
                    }
                }
            }
        }
    }
    
    fun savePageCache(item: HomePageData) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val json = JSON.toJSONString(item)
                if (BuildConfig.DEBUG) {
                    val fileName = PathUtils.getExternalAppDataPath() + "/home_asset_cache.json"
                    LogUtils.d("HomePageData", "HomePageData cache file $fileName")
                    FileIOUtils.writeFileFromString(fileName, json)
                }
                mmkv.putString(homeDataCacheKey, json)
            }
        }
    }
    private val homeDataCacheKey = "homeDataCacheKey_${AppEnv.env}";
    private val mmkv = MMKV.defaultMMKV()

}