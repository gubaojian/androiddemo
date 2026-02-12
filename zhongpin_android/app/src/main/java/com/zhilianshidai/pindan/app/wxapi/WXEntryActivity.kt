package com.zhilianshidai.pindan.app.wxapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.mvvm_android.ui.utils.WXAPIUtil

class WXEntryActivity :  AppCompatActivity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).transparentBar().fullScreen(true).init()
        WXAPIUtil.wxApi?.handleIntent(intent, this)
    }

    override fun onReq(baseReq: BaseReq?) {
        finish()
    }

    //参考： cn.authing.guard.social.callback.wechat.WXCallbackActivity
    // https://github.com/elbbbird/ESSocialSDK/blob/master/socialsdk/src/main/java/com/elbbbird/android/socialsdk/sso/wechat/WXCallbackActivity.java
    override fun onResp(resp: BaseResp?) {
        if (resp == null) {
            return;
        }
        when (resp.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                LogUtils.e("WXEntryActivity", "Auth success")
                handlerWechat(resp)
            }
            else -> {}
        }

        finish()
    }


    private fun handlerWechat(resp: BaseResp?) {
        val sendAuthResp = resp as SendAuth.Resp
        val code = sendAuthResp.code
        LogUtils.e("WXEntryActivity",  "Got wechat code: " + code)
        WXAPIUtil.authAction?.invoke(code)
    }
}