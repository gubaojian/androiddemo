package com.zhongpin.mvvm_android.bean

import java.io.Serializable

data class RoleItem(
    val id: Long? = null,
    val companyId: Long? = null,
    val roleName: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 20250801154400L
    }
}