package com.zhongpin.mvvm_android.ui.utils


import com.blankj.utilcode.util.ToastUtils
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI

object WXAPIUtil {
    var wxApi: IWXAPI? = null;
    var authAction:((String?) -> Unit)? = null;

    fun authLogin(action:(String?) -> Unit) {
        if (wxApi == null) {
            ToastUtils.showShort("微信未安装")
            return;
        }
        wxApi?.let {
            if (!it.isWXAppInstalled) {
                ToastUtils.showShort("微信未安装")
                return@let
            }
            authAction = action;

            //参考：import cn.authing.guard.social.handler.Wechat
            val req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = "wechat_sdk_oneyuan_login"
            it.sendReq(req)
        }
    }


}