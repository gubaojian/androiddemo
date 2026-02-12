package com.zhongpin.mvvm_android.bean

import android.text.TextUtils
import com.zhongpin.mvvm_android.network.CheckResponseEmpty
import java.io.Serializable

/**
 * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3223861/detail/55958049?spaceKey=space-64stfp
 * /pc/material/priceList
 * */
data class PlatformMaterialListResponse (
    var total:Int? = 0,
    var size:Int? =0,
    var records:List<PlatformMaterialItem>? = null
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  records.isNullOrEmpty()
    }

}

data class PlatformMaterialItem (var id:Long? = 0,
                                 var companyId:Long? = 0,
                                 var platCode:String? = null,
                                 var entCode:String? = null,
                                 var name:String? = null,
                                 var pressure:String? = null, //环压
                                 var crush:String? = null, // 耐破
                                 var fold:String? = null, // 耐折
                                 val quality:String? = null,
                                 var type:String? = null,
                                 var typeName:String? = null,
                                 var weight:String? = null,
                                 var price: String? = null,
                                 val prePrice: String? = null,
                                 var status:Int? = 0,
                                 val createTime: String? = null,
                                 val updateTime: String? = null,
                                 var remark:String? = null,
                                 var cellListShowIsLastItem: Boolean = false,
                                 var cellItemCellColor:String? = null,
                                 ): Serializable {
    companion object {
        private const val serialVersionUID = 20250701094400L
    }

    fun matchKeyword(input:String): Boolean {
        val keyword = input.replace("g", "", true)
        if (TextUtils.isEmpty(keyword)) {
            return true;
        }
        val data = "${name}${platCode}${weight}${weight}g";
        return data.lowercase().contains(keyword.lowercase())
    }

    fun weightWithUnit():String {
        try {
            val std = weight?.replace("g", "", true)
            std?.let {
                return "${std}g";
            }
            return  "";
        } catch (e: Exception) {
            return weight ?:"";
        }
    }

}


data class PlatformMaterialItemSearchQuery(
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

class PlatformMaterialItemSearchQueryChangeEvent(val query: PlatformMaterialItemSearchQuery)
