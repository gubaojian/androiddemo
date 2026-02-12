package com.zhongpin.mvvm_android.bean

import java.io.Serializable


data class OrderFeedbackListResponse(
    var  records: List<OrderFeedbackItem> = emptyList<OrderFeedbackItem>(),
    var total:Long? = 0,
    var size:Long? = 0,
    var current:Long? = 0,
    var pages:Long? = 0
)

/**
 *
 * // 纸箱厂申诉状态
 * const APPEAL_STATUS = {
 *   0: '待处理',
 *   1: '处理中',
 *   2: '已完成',
 *   3: '已取消',
 *   enums: [{ value: 0, label: '待处理' }, { value: 1, label: '处理中' }, { value: 2, label: '已完成' }, { value: 3, label: '已取消' }]
 * };
 *
 * */
data class OrderFeedbackItem(
    val id: Long? = null,
    val orderId: Long? = null,
    val appealNo: String? = null,
    val orderNo: String? = null,
    val platCode: String? = null,
    val orderType: Int? = null,
    val appealType: Int? = null,
    val appealTypeName: String? = null,
    val handleType: Int? = null,
    val handleTypeName: String? = null,
    val num: Int? = null,
    val totalPrice: String? = null,
    val price: String? = null,
    val description: String? = null,
    val imageList: List<String>? = null,
    val deliverImageList : List<String>? = null,
    val resolution: String? = null,
    val appealStatus: Int? = null,
    val status: Int? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
    val remark: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 2025072511154400L
    }
}

class FeedbackChangeEvent(var isFeedbackSuccess:Boolean)
