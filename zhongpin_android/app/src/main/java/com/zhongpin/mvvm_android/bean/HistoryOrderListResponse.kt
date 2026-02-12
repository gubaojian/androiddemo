package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.network.CheckResponseEmpty

/**
 * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3221942/detail/55951944?spaceKey=space-64stfp
 * /pc/order/hisOrderList
 * */
data class HistoryOrderListResponse(
    var records: List<HistoryOrderItem>? = null,
    var total:Long? = 0,
    var size:Long? = 0,
    var current:Long? = 0,
    var pages:Long? = 0
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  records.isNullOrEmpty()
    }

}

data class SelectHistoryOrderListResponse(
    var records: List<SelectHistoryOrderItem>? = null,
    var total:Long? = 0,
    var size:Long? = 0,
    var current:Long? = 0,
    var pages:Long? = 0
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  records.isNullOrEmpty()
    }

}


data class HistoryOrderItem(
    val id: Long? = null,
    val orderNo: String? = null,
    val purId: Long? = null,
    val companyId: Long? = null,
    val companyName: String? = null,
    val platCode: String? = null,
    val entCode: String? = null,
    val lenType: String? = null,
    val length: Int? = null,
    val width: Int? = null,
    val floor: Int? = null,
    val touch: String? = null,
    val line: Int? = null,
    val lineType: String? = null,
    val num: Int? = null,
    val orderTime: String? = null,
    val demandTime: String? = null,
    val area: Double? = null,
    val price: Double? = null,
    val totalPrice:Double? = null,
    val orderStatus: Int? = null,
    val orderStatusName: String? = null,
    val size: String? = null,
    val status: Int? = null,
    val remark: String? = null,
    val totalLength: String? = null,
    val menFu:  String? = null,
    val maxCut:  String? = null,
    val urgent: String? = null,
    val menFuList: List<Int>? = null,
    val platCodeList: List<String>? = null,
    val cityId: Long? = null,
    val coordinates: String? = null
) : java.io.Serializable {
    companion object {
        private const val serialVersionUID = 20250701154400L
    }
}

data class SelectHistoryOrderItem(
    val id: Long? = null,
    val orderNo: String? = null,
    val purId: Long? = null,
    val companyId: Long? = null,
    val companyName: String? = null,
    val platCode: String? = null,
    val entCode: String? = null,
    val lenType: String? = null,
    val length: Int? = null,
    val width: Int? = null,
    val floor: Int? = null,
    val touch: String? = null,
    val touchSize: String? = null,
    val line: Int? = null,
    val lineType: String? = null,
    val num: Int? = null,
    val orderTime: String? = null,
    val demandTime: String? = null,
    val area: Double? = null,
    val price: Double? = null,
    val totalPrice:Double? = null,
    val orderStatus: Int? = null,
    val orderStatusName: String? = null,
    val size: String? = null,
    val status: Int? = null,
    val remark: String? = null,
    val totalLength: String? = null,
    val menFu: String? = null,
    val maxCut: String? = null,
    val urgent: String? = null,
    val menFuList: List<Int>? = null,
    val platCodeList: List<String>? = null,
    val cityId: Long? = null,
    val coordinates: String? = null
) : java.io.Serializable {
    companion object {
        private const val serialVersionUID = 20250701154400L
    }
}

data class SelectHistoryOrderSearchQuery(
    var platCode:String? = null,
    var layerCode:String? = null,
    var lineType:String? = null,
    var paperSize:String? = null,
    var lineDesc:String? = null,
    var lastTrimLayerCode:String = "",
    var lastTrimLineType:String = "",
) :java.io.Serializable {

    fun toQuery():String {
        val sb = StringBuilder();
        append(sb, platCode);
        append(sb, layerCode);
        append(sb, lineType);
        append(sb, paperSize);
        append(sb, lineDesc);
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length -1);
        }
        return sb.toString();
    }

    fun trimAllText() {
        if ("全部".equals(lineType)) {
            lastTrimLineType = lineType ?: ""
            lineType = null
        } else {
            lastTrimLineType = lineType ?: ""
        }
        if ("全部".equals(layerCode)) {
            lastTrimLayerCode = layerCode ?: ""
            layerCode = null
        } else {
            lastTrimLayerCode= layerCode ?: ""
        }
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

    companion object {
        private const val serialVersionUID = 202505191643701L
    }

}

class SelectHistoryOrderSearchQueryEvent(item:SelectHistoryOrderSearchQuery);

class ChooseSelectHistoryOrderItemEvent(var item:SelectHistoryOrderItem) {}
