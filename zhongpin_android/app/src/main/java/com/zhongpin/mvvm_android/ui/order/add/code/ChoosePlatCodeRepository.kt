package com.zhongpin.mvvm_android.ui.order.add.code

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.bean.MaterialPriceListResponse
import com.zhongpin.mvvm_android.bean.PlatformMaterialListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class ChoosePlatCodeRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun getMaterialPriceList(pageNo:Int, floor: Int): BaseResponse<MaterialPriceListResponse>{
        return apiService.getMaterialPriceList(hashMapOf(
            "floor" to floor,
            "pageNo" to pageNo,
            "pageSize" to 20
        )).requireLogin().showLoadingState(loadState, Constant.COMMON_KEY)
    }

    suspend fun getAllMaterialPriceList(floor: Int): BaseResponse<MaterialPriceListResponse>{
        return apiService.getMaterialPriceList(hashMapOf(
            "floor" to floor,
            "pageNo" to 1,
            "pageSize" to 999
        )).requireLogin()
    }

    suspend fun getAllPlatformMaterialList(): BaseResponse<PlatformMaterialListResponse>{
        return apiService.getPlatformMaterialList(hashMapOf(
            "pageNo" to 1,
            "pageSize" to 999
        )).requireLogin()
    }


    suspend fun getReceiveAddressList(): BaseResponse<List<AddressListItemResponse>>{
        return apiService.getReceiveAddressList().requireLogin().showLoadingState(loadState)
    }


}