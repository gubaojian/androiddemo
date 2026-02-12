package com.zhongpin.mvvm_android.ui.buy.edit

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.CompanyListResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class EditPublishBuyDetailRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun getCompanyList(pageNo:Int): BaseResponse<CompanyListResponse>{
        return apiService.getCompanyList(hashMapOf(
            "pageNo" to pageNo,
            "pageSize" to 96
        )).requireLogin().showLoadingState(loadState, Constant.COMMON_KEY)
    }


    suspend fun getEntReceiveAddressList(entId:Long): BaseResponse<List<AddressListItemResponse>>{
        return apiService.getEntReceiveAddressList(entId).requireLogin().showLoadingState(loadState)
    }


    suspend fun uploadImage(filePath:String): BaseResponse<String> {
        val file = File(filePath)
        val requestFile =
            RequestBody.create(MultipartBody.FORM, file)
        // MultipartBody.Part is used to send also the actual file name
        // https://stackoverflow.com/questions/39953457/how-to-upload-an-image-file-in-retrofit-2
        val filePart: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        return apiService.uploadImage(filePart).requireLogin()
    }


}