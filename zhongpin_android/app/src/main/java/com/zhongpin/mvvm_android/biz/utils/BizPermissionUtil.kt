package com.zhongpin.mvvm_android.biz.utils

import com.blankj.utilcode.util.ToastUtils
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.bean.MenuPermissionItem
import com.zhongpin.mvvm_android.bean.UserNoPermissionOperationEvent

object BizPermissionUtil {

    fun ensureViewOrderPermission(hasAction: () -> Unit) {
        if (hasBizPermission("app_order")) {
            hasAction.invoke()
        } else {
            EventBusUtils.postEvent(UserNoPermissionOperationEvent(true))
            ToastUtils.showShort("此账号没有订单权限，请联系管理员开通权限");
        }
    }

    fun hasViewOrderPermission(): Boolean {
        return hasBizPermission("app_order")
    }

    fun ensureSubmitOrderPermission(hasAction: () -> Unit) {
        if (hasBizPermission("app_place_order_allow")) {
            hasAction.invoke()
        } else {
            EventBusUtils.postEvent(UserNoPermissionOperationEvent(true))
            ToastUtils.showShort("此账号没有下单权限，请联系管理员开通权限");
        }
    }

    //订单管理权
    fun ensureOrderManagePermission(hasAction: () -> Unit) {
        if (hasBizPermission("app_order_manage")) {
            hasAction.invoke()
        } else {
            EventBusUtils.postEvent(UserNoPermissionOperationEvent(true))
            ToastUtils.showShort("此账号没有订单管理权限，请联系管理员开通权限");
        }
    }




    //支付权限
    fun ensurePayManagePermission(hasAction: () -> Unit): Boolean {
        if (hasBizPermission("app_pay_manage")) {
            hasAction.invoke()
        } else {
            EventBusUtils.postEvent(UserNoPermissionOperationEvent(true))
            ToastUtils.showShort("此账号没有支付权限，请联系管理员开通权限");
        }
        return hasBizPermission("app_pay_manage")
    }

    fun hasPayManagePermission(): Boolean {
        return hasBizPermission("app_pay_manage")
    }

    //公司管理
    fun ensureCompanyManagePermission(hasAction: () -> Unit) {
        if (hasBizPermission("app_company_manage")) {
            hasAction.invoke()
        } else {
            EventBusUtils.postEvent(UserNoPermissionOperationEvent(true))
            ToastUtils.showShort("此账号没有公司管理权限，请联系管理员开通权限");
        }
    }

    private val enablePermission:Boolean = false;

    private fun hasBizPermission(permission: String):Boolean {
        if (enablePermission) {
            val rootMenuList = UserInfoUtil.userInfo?.menuList;
            return hasBizPermission(rootMenuList, permission)
        }
        return true
    }


    private fun hasBizPermission(menuList:List<MenuPermissionItem>?, permission: String):Boolean {
        if (menuList.isNullOrEmpty()) {
            return false;
        }
        var hasPermission = false;
        menuList.forEach {
            if (it.permission == permission) {
                hasPermission = true;
            }
            if (hasBizPermission(it.children, permission)) {
                hasPermission = true;
            }
            if (hasPermission) {
                return true;
            }
        }
        return false;
    }
}