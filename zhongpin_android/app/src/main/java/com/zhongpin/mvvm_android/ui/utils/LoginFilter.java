package com.zhongpin.mvvm_android.ui.utils;
import com.zhongpin.mvvm_android.bean.TokenExpiredEvent;
import com.zhongpin.lib_base.utils.EventBusUtils;
import com.zhongpin.mvvm_android.network.BaseResponse;

public class LoginFilter {
    public static  <T> BaseResponse<T> requireLogin(BaseResponse<T> response) {
        if (response != null
                && response.getCode() == 403) {
            EventBusUtils.postEvent(new TokenExpiredEvent(true, false));
        }
        return response;
    }

    public static <T> BaseResponse<T> filterTokenExpire(BaseResponse<T> response) {
        return requireLogin(response);
    }
}
