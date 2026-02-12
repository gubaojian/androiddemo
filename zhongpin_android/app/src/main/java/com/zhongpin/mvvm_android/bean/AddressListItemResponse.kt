package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.ui.utils.AreaUtil

/**
 * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3181096/detail/55805752?spaceKey=space-64stfp
 * */
data class AddressListItemResponse(var id : Long? = 0,
                                   var companyId:Long? = 0,
                                   var name:String? = null,
                                   var mobile:String? = null,
                                   var province:String? = null,
                                   var city:String?  = null,
                                   var region:String? = null,
                                   var address:String? = null,
                                   var status: Int = 0,

                                   //not exist field
                                   var entId : Long? = 0,
                                   var abbr:String? = null,
                                   var longitude:String? = null,
                                   var latitude:String? = null,
                                   ):java.io.Serializable {

    fun toShouHuoAddress(): String {
        val area = AreaUtil.toArea(province ?: "", city ?:"", region ?:"");
        return  area + (address ?: "")
    }

    companion object {
        private const val serialVersionUID = 20250440102700L
    }
}