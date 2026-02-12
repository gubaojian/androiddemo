package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.network.CheckResponseEmpty
import java.io.Serializable

/**
 * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3223861/detail/55958049?spaceKey=space-64stfp
 * /pc/material/priceList
 * */
data class MaterialPriceListResponse (
    var total:Int? = 0,
    var size:Int? =0,
    var records:List<MaterialPriceItem>? = null
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  records.isNullOrEmpty()
    }

}

data class MaterialPriceItem (var id:Long? = 0,
                              var companyId:Long? = 0,
                              var platCode:String? = null,
                              var name:String? = null,
                              var lenType:String? = null,
                              var detail:String? = null,
                              var price:String? = null,
                              var prePrice:String? = null,
                              var limitPrice:String? = null,
                              var specialPrice:String? = null,
                              var type:String? = null,
                              var typeName:String? = null,
                              var orderCount:Long? = 0,
                              var orderTime:String? = null,
                              val orderAmount: String? = null,
                              val orderArea: String? = null,
                              var priceUpdateTime:String? = null,
                              var status:Int? = 0,
                              var floor:String? = null,
                              var line:Int? = 0,
                              var num:Int? = 0,
                              val creator: String? = null,
                              val modifier: String? = null,
                              val createTime: String? = null,
                              val updateTime: String? = null,
                              var demandTime:String? = null,
                              var size:String? = null): Serializable {
    companion object {
        private const val serialVersionUID = 20250701164400L
    }
}

class ChooseMaterialPriceItemEvent(var item:MaterialPriceItem) {

}


data class MaterialPriceFilterQuery(
    var sortBy:String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 20250804154400L
    }

    fun buildSearchDesc():String {
        val sb = StringBuilder();
        append(sb, sortBy)
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

class MaterialPriceFilterQueryChangeEvent(val query: MaterialPriceFilterQuery)



data class MaterialPriceDetailItem (var id:Long? = 0,
                              var companyId:Long? = 0,
                              var platCode:String? = null,
                              var name:String? = null,
                              var lenType:String? = null,
                              var detail:String? = null,
                              var price:String? = null,
                              var prePrice:String? = null,
                              var limitPrice:String? = null,
                              var specialPrice:String? = null,
                              var type:String? = null,
                              var typeName:String? = null,
                              var orderCount:Long? = 0,
                              var orderTime:String? = null,
                              val orderAmount: String? = null,
                              val orderArea: String? = null,
                              var status:Int? = 0,
                              var floor:String? = null,
                              var line:Int? = 0,
                              var num:Int? = 0,
                              val creator: String? = null,
                              val modifier: String? = null,
                              val createTime: String? = null,
                              val updateTime: String? = null,
                              val materialInfoList: List<RelateMaterialInfoItem>? = null,
                              var demandTime:String? = null,
                              var size:String? = null)

data class RelateMaterialInfoItem(
    val id: Long? = null,
    val companyId: String? = null,
    val platCode: String? = null,
    val entCode: String? = null,
    val name: String? = null,
    val pressure: String? = null,
    val crush: String? = null,
    val fold: String? = null,
    val type: String? = null,
    var typeName:String? = null,
    val quality: String? = null,
    val weight: String? = null,
    val price: String? = null,
    val prePrice: String? = null,
    val status: Int? = null,
    val creator: String? = null,
    val modifier: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
    val remark: String? = null,
    var cellListShowIsLastItem: Boolean = false,
    var cellItemCellColor:String? = null,
)