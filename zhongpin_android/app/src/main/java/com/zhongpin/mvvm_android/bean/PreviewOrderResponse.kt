package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.network.CheckResponseEmpty

/**
 *https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3221942/detail/55958144?spaceKey=space-64stfp
 * //mini/purchase/orderScan
 * */
data class PreviewOrderResponse (
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
    val orders:List<PreviewPurchaseOrderItem>? = null,
    var viewRemainTime: Long = 0,
)  : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  orders.isNullOrEmpty()
    }

}

data class PreviewPurchaseOrderItem(
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
    val area: String? = null,
    val price: String? = null,
    val totalPrice:String? = null,
    val materialPrice: String? = null,
    val materialTotalPrice: String? = null,
    val stepPrice:String? = null,
    val stepTotalPrice: String? = null,
    val cutPrice: String? = null,
    val cutTotalPrice: String? = null,
    val areaPrice: String? = null,
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
        private const val serialVersionUID = 20250702154400L
    }
}



class SubmitBuyOrderDoneEvent(var isDone:Boolean)