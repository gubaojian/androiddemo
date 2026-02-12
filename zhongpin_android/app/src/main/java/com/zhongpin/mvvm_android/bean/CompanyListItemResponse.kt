package com.zhongpin.mvvm_android.bean

/**
 * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3217641/detail/55946225?spaceKey=space-64stfp
 * */
data class CompanyListItemResponse(var id : Long = 0,
                                   var userId : Long = 0,
                                   var companyName:String?,
                                   var license:String?,
                                   var creditCode:String?,
                                   var province:String? = null,
                                   var city:String? = null,
                                   var region:String? = null,
                                   var registerAddress:String? = null,
                                   var leftCard:String? = null,
                                   var rightCard:String? = null,
                                   var legal:String? = null,
                                   var idCard:String? = null,
                                   var contract:List<String>? = null,
                                   var contactPeople:String? = null,
                                   var contactTel:String? = null,
                                   var regionManager:String? = null,
                                   var regionMobile:String? = null,
                                   var mark:String? = null,
                                   var rejectReason:String? = null,
                                   var payMethod:String? = null,
                                   var credit:String? = null,
                                   var methodType:Int? = 0,
                                   var currentCycle:String? = null,
                                   var lastCycle:String? = null,
                                   var creditCycle:String? = null,
                                   var type:Int? = 0,
                                   var status:Int = 0, //审核状态(0:新申请,1:待审核,2:审核成功,3:审核拒绝)
                                   var createTime:String? = null,
                                   var updateTime:String? = null,

                                   //not exist unused field
                                   var identify:String? = null,
                                   var mobile:String?  = null,
                                   var bankAccount:String? = null,
                                   var bankName:String? = null,
                                   var describe:String? = null,
                                   var entType:Int = 0,
                                   var handStatus:Int = 0):java.io.Serializable {
    companion object {
        private const val serialVersionUID = 20180617104400L
    }
}

class CompanyInfoChangeEvent(val isChange: Boolean = true)

class CompanySignInfoChangeEvent(var isChange:Boolean)