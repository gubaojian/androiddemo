package com.zhongpin.mvvm_android.bean

data class AppUpdateInfo(
    val id: Long? = null,
    val minVersion: String? = null,
    val maxVersion: String? = null,
    var address: String? = null,
    val describe: String? = null,
    val appType: Int? = null,
    val createTime: String? = null,
    val updateTime: String? = null
)