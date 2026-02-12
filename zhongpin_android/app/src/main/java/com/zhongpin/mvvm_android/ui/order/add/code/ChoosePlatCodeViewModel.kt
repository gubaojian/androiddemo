package com.zhongpin.mvvm_android.ui.order.add.code

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.bean.MaterialPriceListResponse
import com.zhongpin.mvvm_android.bean.PlatformMaterialListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiate2Request
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class ChoosePlatCodeViewModel  : BaseViewModel<ChoosePlatCodeRepository>() {

    val mAllMaterialPriceListData: MutableLiveData<BaseResponse<MaterialPriceListResponse>> = MutableLiveData()
    fun getAllMaterialPriceList(floor: Int) : MutableLiveData<BaseResponse<MaterialPriceListResponse>> {
        initiateRequest({
            mAllMaterialPriceListData.value = mRepository.getAllMaterialPriceList(floor)
        }, loadState)
        return mAllMaterialPriceListData;
    }


    val mAllPlatformMaterialListData: MutableLiveData<BaseResponse<PlatformMaterialListResponse>> = MutableLiveData()
    fun getAllPlatformMaterialList() : MutableLiveData<BaseResponse<PlatformMaterialListResponse>> {
        initiateRequest({
            mAllPlatformMaterialListData.value = mRepository.getAllPlatformMaterialList()
        }, loadState)
        return mAllPlatformMaterialListData;
    }

    val mPageData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
    fun getPageData(floor: Int) {
        initiate2Request({
            mAllMaterialPriceListData.value = mRepository.getAllMaterialPriceList(floor)
        }, {
            mAllPlatformMaterialListData.value = mRepository.getAllPlatformMaterialList()
        }, {
            val response = BaseResponse<Boolean>(true, success = true);
            if (false == mAllMaterialPriceListData.value?.success) {
                response.data = false;
                response.success = false;
                response.code = mAllMaterialPriceListData.value?.code ?: 0
                response.msg = mAllMaterialPriceListData.value?.msg ?: ""
            }
            if (false == mAllPlatformMaterialListData.value?.success) {
                response.data = false;
                response.success = false;
                response.code = mAllPlatformMaterialListData.value?.code ?: 0
                response.msg = mAllPlatformMaterialListData.value?.msg ?: ""
            }
            response.showLoadingState(loadState, Constant.COMMON_KEY)
            mPageData.value = response
        }, loadState)
    }


    val mAddressData: MutableLiveData<BaseResponse<List<AddressListItemResponse>>> = MutableLiveData()

    fun getReceiveAddressList() : MutableLiveData<BaseResponse<List<AddressListItemResponse>>> {
        initiateRequest({
            mAddressData.value = mRepository.getReceiveAddressList()
        }, loadState)
        return mAddressData;
    }





}