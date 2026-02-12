package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.network.CheckResponseEmpty


data class OrderListResponse(
    var  records: List<OrderItem> = emptyList<OrderItem>(),
    var total:Long? = 0,
    var size:Long? = 0,
    var current:Long? = 0,
    var pages:Long? = 0,
    var noViewOrderPermission: Boolean = false
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        if (noViewOrderPermission) {
            return  false
        }
        return  records.isEmpty()
    }

}

object OrderStatus {
    val orderStatusMap = mapOf<Int, String>(
        0 to "未发布",
        10 to "待收货",
        20 to "已拼单",
        30 to "已接单",
        40 to "部分送达",
        50 to "全部送达",
        60 to "已完成",
        80 to "已取消",
    )

    fun getOrderStatusText(item:OrderItem):String {
        return orderStatusMap[item.status ?: 0] ?: (item.orderStatusName ?: "")
    }

}


data class OrderItem(
    val id: Long? = null,
    val orderNo: String? = null,
    val purId: Long? = null,
    val companyId: Long? = null,
    val companyName: String? = null,
    val floor: String? = null,
    val platCode: String? = null,
    val entCode: String? = null,
    val lenType: String? = null,
    val length: Int? = null,
    val width: Int? = null,
    val touch: String? = null,
    val touchSize:String? = null,
    val line: Int? = null,
    val lineType: String? = null,
    val num: Int? = null,
    val orderTime: String? = null,
    val demandTime: String? = null,
    val area: String? = null,
    val price: String? = null,
    val totalPrice:String? = null,
    val orderStatus: Int? = null,
    val orderStatusName: String? = null,
    val size: String? = null,
    val status: Int? = null,
    val orderType: Int? = null, // (1纸板订单 2补发订单)
    val signStatus: Int? = null, // (0 无签收 1待签收 2已签收 3签收异常)
    val receiptStatus: Int? = null, //(0 未入库 1部分入库 2已入库)
    var unSignNum: Long? = null,
    val remark: String? = null,
    val totalLength: String? = null,
    val menFu:  String? = null,
    val maxCut:  String? = null,
    val urgent: String? = null,
    val menFuList: List<Int>? = null,
    val platCodeList: List<String>? = null,
    val cityId: Long? = null,
    val coordinates: String? = null,
    val orderAppeal: Boolean? = null,
    val signNum: Long? = null,
    val signTime: String? = null,
) : java.io.Serializable {
    companion object {
        private const val serialVersionUID = 20250701154400L
    }
}


data class OrderDetailItem(
    val id: Long? = null,
    val orderNo: String? = null,
    val purId: Long? = null,
    val companyId: Long? = null,
    val companyName: String? = null,
    val platCode: String? = null,
    val preCode: String? = null,
    val entCode: String? = null,
    val lenType: String? = null,
    val length: Int? = null,
    val width: Int? = null,
    val touch: String? = null,
    val touchSize: String? = null,
    val line: Int? = null,
    val lineType: String? = null,
    val num: Int? = null,
    val orderTime: String? = null,
    val demandTime: String? = null,
    val area: String? = null,
    val price: String? = null,
    val totalPrice:String? = null,
    val orderStatus: Int? = null,
    val orderStatusName: String? = null,
    val size: String? = null,
    val status: Int? = null,
    val orderType: Int? = null, // (1纸板订单 2补发订单)
    val signStatus: Int? = null, // (0 无签收 1待签收 2已签收 3签收异常)
    val receiptStatus: Int? = null, //(0 未入库 1部分入库 2已入库)
    var receiptStatusName: String? = null,
    val remark: String? = null,
    val totalLength: String? = null,
    val menFu: String? = null,
    val maxCut: String? = null,
    val urgent: String? = null,
    val menFuList: List<Int>? = null,
    val platCodeList: List<String>? = null,
    val cityId: Long? = null,
    val coordinates: String? = null,

    //
    val purchId: Long? = null,
    val signNum: Long? = null,
    val signTime: String? = null,
    var unSignNum: Long? = null,
    var receiptNum: String? = null,
    var receiptTime: String? = null,
    val materialPrice: String? = null,
    val materialTotalPrice: String? = null,
    val cutPrice: String? = null,
    val cutTotalPrice: String? = null,
    val stepPrice: String? = null,
    val stepTotalPrice: String? = null,
    val areaPrice: String? = null,
    val extraNum: Long? = null,
    val extraPrice: String? = null,
    val creator: String? = null,
    val modifier: String? = null,
    val createTime: String? = null,
    val updateTime: Any? = null,
    val floor: String? = null,
    val platformName: String? = null,
    val providerName: String? = null,
    val providerId: Long? = null,
    val receiveName: String? = null,
    val receiveMobile: String? = null,
    val tradeNo: String? = null,
    val address: String? = null,
    val purName: String? = null,
    val purMobile: String? = null,
    val cancelOrder: Boolean? = null,
    val orderAppeal: Boolean? = null,
    val deliverCount: Long? = null,
    val appealCount: Long? = null

) : java.io.Serializable {
    companion object {
        private const val serialVersionUID = 202507251154400L
    }
}

class OrderItemInfoChangeEvent(var change:Boolean)


data class OrderFilterQuery(
    var label:String? = null,
    var startTime:String? = null,
    var endTime:String? = null,
) : java.io.Serializable {
    companion object {
        private const val serialVersionUID = 20250724154400L
    }
}

class OrderFilterQueryChangeEvent(val query:OrderFilterQuery)


data class OrderStatisticsData(
    val orderTotalCount: String? = null,
    val purchaseCount: String? = null,
    val totalArea: String? = null,
    val orderCount: String? = null,
    val goodsCount:String? = null
): java.io.Serializable {
    companion object {
        private const val serialVersionUID = 20250724154400L
    }
}
