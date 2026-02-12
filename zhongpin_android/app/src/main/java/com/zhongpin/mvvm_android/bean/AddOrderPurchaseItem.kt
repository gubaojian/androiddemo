package com.zhongpin.mvvm_android.bean

import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import java.util.UUID


data class AddOrderPurchaseItem(var uuid : String? = UUID.randomUUID().toString(),
                                var floor:String? = null,
                                var platCode:String? = null,
                                var flute:String? = null,
                                var paperLength:Int? = null,
                                var paperWidth:Int? = null,
                                var pageSize:String? = null,
                                var line:String? = null,
                                var touch:String? = null,
                                var boxLength:Int? = null,
                                var boxWidth:Int? = null,
                                var boxHeight:Int? = null,
                                var lineLength:Int? = null,
                                var lineWidth:Int? = null,
                                var lineHeight:Int? = null,
                                var num:Int = 0,
                                var boxTypeName:String? = null,
                                var boxTypeNum:Long? = null,
                                var useBoxTypeSwitch: Boolean = false, //默认关闭
                                var demandTime:String? = null,
                                var inputRemark:String? = null
    ):java.io.Serializable {
    companion object {
        private const val serialVersionUID = 20250630104400L
    }

    fun isStrangeBox(): Boolean {
        val boxTypeName = this.boxTypeName  ?: "";
        return boxTypeName.contains("异形箱");
    }

    fun isYouGaiWuDiBox(): Boolean {
        val boxTypeName = this.boxTypeName  ?: "";
        return boxTypeName.contains("有盖无底");
    }

    fun isYouDiWuGaiBox(): Boolean {
        val boxTypeName = this.boxTypeName  ?: "";
        return boxTypeName.contains("有底无盖");
    }

}

class AddOrderPurchaseItemEvent(var item:AddOrderPurchaseItem)

class DeleteOrderPurchaseItemEvent(var item:AddOrderPurchaseItem)

class EditOrderPurchaseItemEvent(var item:AddOrderPurchaseItem)