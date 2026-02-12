package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.network.CheckResponseEmpty
import java.io.Serializable


data class PayItemListResponse(
    var  records: List<PayItem> = emptyList<PayItem>(),
    var total:Long? = 0,
    var size:Long? = 0,
    var current:Long? = 0,
    var pages:Long? = 0
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  records.isEmpty()
    }

}

data class PayItem(
    val id: Long? = null,
    val userOrderId:Long? = null,
    val purId: Long? = null,
    val companyId: String? = null,
    val orderId: String? = null,
    val waterId: String? = null,
    val receiveType: Int? = null, // 收支类型(0:收入,1:支出)
    val payType: Int? = null, //支付类型(0:支付宝,1:微信,2:预付款)
    val amount: String? = null,
    val describe:String? = null,
    val tranType: Int? = null,
    val make: Int? = null,
    val status: Int? = null, //交易状态(0:待支付,1:支付超时,2:取消支付,3:支付成功)
    var expireTime: Long? = null,
    val createTime: String? = null,
    val updateTime: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 20250729154400L
    }

    fun payTypeDesc():String {
        if (tranType == 2) {
            return "返回预付款余额";
        }
        if (payType == 0) {
            return "支付宝";
        }
        if (payType == 1) {
            return "微信";
        }
        if (payType == 2) {
            return "预付款";
        }
        return "账户余额";
    }

    fun payStatusDesc():String {
        if (status == 0) {
            return "待支付"
        } else if (status == 1) {
            return "支付超时"
        } else if (status == 2) {
            return "取消支付"
        } else if (status == 3) {
            val desc = describe;
            if (desc != null && desc.contains("退款")) {
                return "退款成功"
            }
            return "支付成功"
        } else {
            return "支付状态"
        }
    }
}

data class PayUrlItem(
    val payUrl: String? = null,
    //微信支付使用
    var timeStamp: String? = null,
    var packageValue: String? = null,
    var appId: String? = null,
    var sign: String? = null,
    var prepayId: String? = null,
    var partnerId: String? = null,
    var nonceStr: String? = null,
) : Serializable {
    companion object {
        private const val serialVersionUID = 202500806154400L
    }
}

data class PayOrderStatus(
    val orderId: String? = null,
    val waterId: String? = null,
    val status: Int? = null // 支付状态((0:待支付,1:支付超时,2:取消支付,3:支付成功))
)

class PayItemInfoChangeEvent(var change:Boolean)


data class PayItemFilterQuery(
    var label:String? = null,
    var startTime:String? = null,
    var endTime:String? = null,
    var payType:String? = null,
) : Serializable {
    companion object {
        private const val serialVersionUID = 20250729154400L
    }

    fun buildSearchDesc():String {
        val sb = StringBuilder();
        append(sb, payType)
        append(sb, label)
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
        if (item.equals("全部")) {
            return
        }
        item.let {
            sb.append(it)
            sb.append("/");
        }
    }
}

class PayItemFilterQueryChangeEvent(val query: PayItemFilterQuery)


data class PayAccountBalance(
    val id: Long? = null,
    val companyId: String? = null,
    val amount: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null
)
