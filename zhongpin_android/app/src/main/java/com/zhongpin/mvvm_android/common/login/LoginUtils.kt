package com.zhongpin.mvvm_android.common.login

import android.app.Activity
import android.content.Intent
import com.blankj.utilcode.util.SPUtils
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.bean.TokenExpiredEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.ui.login.LoginActivity

/**
 * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/code/-1?branchID=&projectVersionID=&spaceKey=space-64stfp
 * session中的token过期。
 * */
fun  <T> sessionFilter(data: BaseResponse<T>):BaseResponse<T> {
    if (data.code == 403) {
        EventBusUtils.postEvent(TokenExpiredEvent(true))
    }
    return data;
}

object LoginUtils {

    fun hasLogin(): Boolean {
        val token = SPUtils.getInstance().getString(Constant.TOKEN_KEY,"")
        return token.isNotEmpty()
    }

    /**
     * 保证登录，未登录唤起登录界面
     * */
    fun ensureLogin(activity: Activity?, onSureLogin: () -> Unit) {
        if (hasLogin()) {
            onSureLogin()
            return
        }
        val intent = Intent(activity, LoginActivity::class.java)
        activity?.startActivity(intent)
    }

    fun token(): String {
        val token = SPUtils.getInstance().getString(Constant.TOKEN_KEY,"")
        return token
    }

    fun clearToken() {
        SPUtils.getInstance().remove(Constant.TOKEN_KEY)
    }

    fun toLogin(activity: Activity?) {
        val intent = Intent(activity, LoginActivity::class.java)
        activity?.startActivity(intent)
    }
}