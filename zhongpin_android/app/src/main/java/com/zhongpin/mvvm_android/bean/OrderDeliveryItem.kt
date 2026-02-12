package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.network.CheckResponseEmpty
import java.io.Serializable


data class OrderDeliveryProofListResponse(
    var  records: List<OrderDeliveryProofItem> = emptyList<OrderDeliveryProofItem>(),
    var total:Long? = 0,
    var size:Long? = 0,
    var current:Long? = 0,
    var pages:Long? = 0
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  records.isEmpty()
    }

}

data class OrderDeliveryProofItem(
    val id: Long? = null,
    val orderId: Long? = null,
    val num: Int? = null,
    val bizType: Int? = null, // 业务类型 1送货单 2收货单
    val bizTypeName: String? = null,
    val type: Int? = null, // 1 部分送达， 2全部送达
    val typeName: String? = null,
    val imageList: List<String>? = null,
    val status: Long? = null,
    var signer: String? = null,
    var signTime: String? = null,
    var signStatus: Int? = null, // 签收状态(0未签收 1已签收)
    var signStatusName: String? = null,
    var stockUser: String? = null,
    var stockTime: String? = null,
    var stockStatus: Int? = null, // 入库状态(0未入库 1已入库)
    var stockStatusName: String? = null,
    val creator: String? = null,
    val creatorName: String? = null,
    val modifier: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
    val remark: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 20250724154400L
    }
}

