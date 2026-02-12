package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.network.CheckResponseEmpty

class PurchaseOrderDetail(
    val id: Long? = null,
    val purNo: String? = null,
    val companyId: String? = null,
    val companyName: String? = null,
    val purTime: String? = null,
    val demandTime: String? = null,
    val totalNum: String? = null,
    val totalArea: String? = null,
    val purName: String? = "",
    val purMobile: String? = "",
    val receiveName: String? = "",
    val receiveMobile: String? = "",
    val address: String? = "",
    val addressId: Long? = 0,
    val status: Int? = null, // (0待支付,1已支付,2已取消)
    val creator: String? = null,
    val modifier: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
    val remark: String? = null,
    val totalAmount: String? = null,
    val materialAmount:String? = null,
    val stepAmount: String? = null,
    val cutAmount: String? = null,
    val orders:List<OrderItem>? = null,
    var viewRemainTime: Long = 0,
)  {


    fun statusDesc():String {
        if (status == 0) {
            return  "待支付"
        }
        if (status == 1) {
            return  "已支付"
        }
        if (status == 2) {
            return  "已取消"
        }
        return ""
    }
}


data class PurchaseOrderListResponse(
    var  records: List<PurchaseOrderDetail> = emptyList(),
    var total:Long? = 0,
    var size:Long? = 0,
    var current:Long? = 0,
    var pages:Long? = 0
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  records.isEmpty()
    }

}


