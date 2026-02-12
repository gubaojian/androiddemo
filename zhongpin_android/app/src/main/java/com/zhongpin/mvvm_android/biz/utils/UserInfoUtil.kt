package com.zhongpin.mvvm_android.biz.utils

import com.blankj.utilcode.util.SPUtils
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse

object UserInfoUtil {
    var userInfo: UserInfoResponse? = null
    var companyInfo: CompanyListItemResponse? = null

    fun maskPhone(phone:String?):String {
        if (phone == null || phone.length < 11) {
            return phone ?: ""
        }
        val sb = StringBuilder(phone);
        sb.replace(3, 7, "****")
        return sb.toString();
    }

    fun getPhone():String? {
        return userInfo?.mobile;
    }

    fun hasCompanyVerified(): Boolean {
        return companyInfo?.status == 2;
    }

    fun isUserAgreeProtocol(): Boolean {
        return SPUtils.getInstance().getBoolean("userAgreeProtocolFlag", false)
    }

    fun setUserAgreeProtocol() {
        SPUtils.getInstance().put("userAgreeProtocolFlag", true)
    }

}