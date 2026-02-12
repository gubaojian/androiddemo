package com.zhongpin.mvvm_android.ui.mine.company.edit

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.EntInfoResponse
import com.zhongpin.mvvm_android.bean.LatLntResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.dataConvert
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditCompanyVerifyRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun loadBannerCo(): List<UserInfoResponse>? {
        return apiService.getUserInfoCo().dataConvert(loadState)
    }

    suspend fun sendVerifyCode(mobile:String): BaseResponse<Boolean> {
        return apiService.sendVerifyCo(hashMapOf(
            "mobile" to mobile
        ))
    }

    suspend fun editEntInfoAuth(parameters:HashMap<String,Any>): BaseResponse<Boolean> {
        return apiService.editEntInfoAuth(parameters)
    }

    suspend fun getLntLngInfo(address: String): BaseResponse<LatLntResponse> {
        return apiService.getLntLngInfo(address)
    }

    suspend fun uploadImage(filePath:String): BaseResponse<String> {
        val file = File(filePath)
        val requestFile =
            RequestBody.create(MultipartBody.FORM, file)
        // MultipartBody.Part is used to send also the actual file name
        // https://stackoverflow.com/questions/39953457/how-to-upload-an-image-file-in-retrofit-2
        val filePart: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        return apiService.uploadImage(filePart)
    }

    suspend fun identifyEntInfo(filePath:String): BaseResponse<EntInfoResponse> {
        val file = File(filePath)
        val requestFile =
            RequestBody.create(MultipartBody.FORM, file)
        // MultipartBody.Part is used to send also the actual file name
        // https://stackoverflow.com/questions/39953457/how-to-upload-an-image-file-in-retrofit-2
        val filePart: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        return apiService.identifyEntCard(filePart)
    }

}