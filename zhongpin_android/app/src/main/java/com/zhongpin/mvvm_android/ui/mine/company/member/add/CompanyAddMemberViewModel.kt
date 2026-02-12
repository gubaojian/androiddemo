package com.zhongpin.mvvm_android.ui.mine.company.member.add

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.RoleItem
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class CompanyAddMemberViewModel  : BaseViewModel<CompanyAddMemberRepository>() {

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

    fun updateEntContract(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.updateEntContract(parameters);
        }, loadState)
        return mLiveData
    }


    val mRoleData: MutableLiveData<BaseResponse<List<RoleItem>>> = MutableLiveData()

    fun getCompanyRoleList() : MutableLiveData<BaseResponse<List<RoleItem>>> {
        initiateRequest({
            mRoleData.value = mRepository.getCompanyRoleList().showLoadingState(loadState, Constant.COMMON_KEY)
        }, loadState)
        return mRoleData;
    }

    fun deleteCompanyMember(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.deleteCompanyMember(id);
        }, dialogLoadState)
        return mLiveData
    }


    fun addCompanyMember(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.addCompanyMember(parameters);
        }, dialogLoadState)
        return mLiveData
    }

    fun editCompanyMember(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.editCompanyMember(parameters);
        }, dialogLoadState)
        return mLiveData
    }



}