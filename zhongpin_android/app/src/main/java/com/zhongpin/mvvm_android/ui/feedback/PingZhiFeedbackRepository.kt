package com.zhongpin.mvvm_android.ui.feedback

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class PingZhiFeedbackRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getUserAuthInfo(): BaseResponse<UserInfoAuthResponse> {
        return apiService.getUserAuthInfo().requireLogin().showLoadingState(loadState)
    }

    suspend fun deleteEntInfoAuth(id: Long): BaseResponse<Boolean> {
        return apiService.deleteEntInfoAuth(id)
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