package com.zhongpin.mvvm_android.bean


data class UserInfoAuthResponse(var id : Long,
                                var userId : Long,
                                var name:String,
                                var idCardLeft:String,
                                var idCardRight:String,
                                var idCard:String,
                                var address:String,
                                var describe:String?,
                                var entType : Int,
                                var status:Int):java.io.Serializable {
    companion object {
        private const val serialVersionUID = 202506171044013L
    }
}


