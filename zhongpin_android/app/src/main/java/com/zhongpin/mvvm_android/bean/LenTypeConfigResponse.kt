package com.zhongpin.mvvm_android.bean

import java.io.Serializable

data class LenTypeConfigItem(
    val id: Long? = null,
    val floor: Int? = null,
    val lenType: List<WaLenTypeItem>? = null,
    val createTime: String? = null,
    val updateTime: String? = null
)

data class WaLenTypeItem(
    val type: String? = null,
    val image: String? = null,
): Serializable {
    companion object {
        private const val serialVersionUID = 202508081154400L
    }
}


data class BoxTypeConfigItem(
    val id: Long? = null,
    val type: Long? = null,
    val typeName: String? = null,
    val image: String? = null,
    val order: Long? = null,
    val createTime: String? = null,
    val updateTime: String? = null
): Serializable {
    companion object {
        private const val serialVersionUID = 202508091154400L
    }
}
