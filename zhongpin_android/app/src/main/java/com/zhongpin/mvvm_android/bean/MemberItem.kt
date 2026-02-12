package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.network.CheckResponseEmpty
import java.io.Serializable


data class MemberListResponse(
    var  records: List<MemberItem> = emptyList(),
    var total:Long? = 0,
    var size:Long? = 0,
    var current:Long? = 0,
    var pages:Long? = 0
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  records.isEmpty()
    }

}



data class MemberItem(
    val id: Long? = null,
    val nickName: String? = null,
    val mobile: String? = null,
    val password: String? = null,
    val headImage: String? = null,
    val deptId: Int? = null,
    val companyName: String? = null,
    val status: Int? = null,
    val primary:Int? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
    val roleName: String? = null,
    val roleId: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 20250731154400L
    }

    fun isMasterAccount(): Boolean {
        return primary == 1;
    }
}
data class MemberItemSearchQuery(
    var inputText:String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 20250731154401L
    }

    fun toQuery():String {
        val sb = StringBuilder();
        append(sb, inputText);
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length -1);
        }
        return sb.toString();
    }

    private fun append(sb:StringBuilder, item:Any?) {
        if (item == null) {
            return;
        }
        if (item is String) {
            if (item.isEmpty()) {
                return;
            }
        }
        item.let {
            sb.append(it)
            sb.append("/");
        }
    }
}

class MemberItemSearchQueryChangeEvent(val query: MemberItemSearchQuery)

class MemberInfoChangeEvent(val isChange: Boolean = true)
