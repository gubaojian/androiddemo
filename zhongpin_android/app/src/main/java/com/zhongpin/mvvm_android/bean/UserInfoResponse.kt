package com.zhongpin.mvvm_android.bean

data class UserInfoResponse (
    var id : Long? = 0,
    var mobile : String? = null,
    var headImage : String? = null,
    var nickName:String? = null,
    var companyName:String? = null,
    var roleName:String? = null,
    var admin:Int? = null,
    var menuList:List<MenuPermissionItem>? = null
)


data class MenuPermissionItem(
    val id: String? = null,
    val menuName: String? = null,
    val parentId: String? = null,
    val menuType: Long? = null,
    val permission: String? = null,
    val url: String? = null,
    val component: String? = null,
    val icon: String? = null,
    val sort: Int? = null,
    val visible: Int? = null,
    val deviceType: Int? = null,
    val type: Int? = null,
    val status: Int? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
    val children: List<MenuPermissionItem>? = null,
    val userId: String? = null
)


class UserInfoChangeEvent(val isChange: Boolean = true)

class UserNoPermissionOperationEvent(val isChange: Boolean = true)