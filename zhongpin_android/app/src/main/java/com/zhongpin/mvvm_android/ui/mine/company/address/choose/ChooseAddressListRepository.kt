package com.zhongpin.mvvm_android.ui.mine.company.address.choose

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState


class ChooseAddressListRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun getReceiveAddressList(): BaseResponse<List<AddressListItemResponse>>{
        return apiService.getReceiveAddressList().requireLogin().showLoadingState(loadState)
    }

    suspend fun editReceiveAddress(parameters:HashMap<String,Any>): BaseResponse<Boolean> {
        return apiService.updateReceiveAddress(parameters)
    }

    suspend fun deleteReceiveAddress(id: Long): BaseResponse<Boolean> {
        val parameters:HashMap<String,Any> = hashMapOf();
        parameters.put("id", id)
        return apiService.deleteReceiveAddress(parameters)
    }


}