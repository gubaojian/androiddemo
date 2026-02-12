package com.zhongpin.mvvm_android.ui.utils

import android.content.Intent
import android.os.Build
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.mvvm_android.common.login.LoginUtils
import java.io.Serializable

object IntentUtils {

    fun <T : Serializable?> getSerializableExtra(
        intent: Intent?,
        name: String?,
        clazz: Class<T>
    ): T? {
        try  {
            if (intent == null) {
                return null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return  intent.getSerializableExtra(name, clazz);
            }
            return intent.getSerializableExtra(name) as T?;
        } catch (e:Exception) {
            LogUtils.e("IntentUtils", "IntentUtils getSerializableExtra error " + e.message)
            return null
        }
    }
}