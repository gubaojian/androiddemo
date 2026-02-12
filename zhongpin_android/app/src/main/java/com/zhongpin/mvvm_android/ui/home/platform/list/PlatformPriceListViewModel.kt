package com.zhongpin.mvvm_android.ui.home.platform.list

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.MemberListResponse
import com.zhongpin.mvvm_android.bean.PlatformMaterialListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class PlatformPriceListViewModel  : BaseViewModel<PlatformPriceListRepository>() {



    val mAllPlatformMaterialListData: MutableLiveData<BaseResponse<PlatformMaterialListResponse>> =
        MutableLiveData()

    fun getAllPlatformMaterialList(query: HashMap<String, String>): MutableLiveData<BaseResponse<PlatformMaterialListResponse>> {
        initiateRequest({
            val response = mRepository.getAllPlatformMaterialList();
            val keyword = query.get("keyword") ?: "";
            val records = response.data?.records?: emptyList()
            val searchResults = records.filter {
                it.matchKeyword(keyword)
            }.toMutableList()
            response.data?.records = searchResults;
            mAllPlatformMaterialListData.value = response
        }, loadState)
        return mAllPlatformMaterialListData;
    }


}