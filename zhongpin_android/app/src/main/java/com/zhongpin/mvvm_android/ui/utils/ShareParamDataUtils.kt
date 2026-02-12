package com.zhongpin.mvvm_android.ui.utils

import android.util.LruCache
import com.zhongpin.mvvm_android.bean.MaterialPriceItem
import com.zhongpin.mvvm_android.bean.MemberItem
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.PlatformMaterialItem

object ShareParamDataUtils {
    var orderItem: OrderItem? = null;

    var payItem:PayItem? = null;

    var memberItem: MemberItem? = null;


    var materialPriceItem: MaterialPriceItem? = null;

    var platformMaterialPriceItem: PlatformMaterialItem? = null;


    var params: HashMap<String, Any?>  = hashMapOf<String, Any?>();

    fun clearParams() {
        params.clear()
    }

    fun putParams(key:String, value: Any?) {
        params.put(key, value)
    }

    fun <T> getParams(key:String ):T? {
        try {
            return params.get(key) as T?
        } catch (e: Exception) {
            e.printStackTrace()
            return  null;
        }
    }

}