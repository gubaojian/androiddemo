package com.zhongpin.mvvm_android.bean
import com.zhongpin.mvvm_android.network.BaseResponse


data class HomeOrderInfo(
    var waitOrderNum : String? = null,
    var waitPaperNum : String? = null,
) {

}

data class HomeBanner(
    var url : String? = null,
    var resId:Int  = 0
) {

}

data class HomePageData(
    var isLogin : Boolean = false,
    var minePrice:BaseResponse<MaterialPriceListResponse>? = null,
    var platformPrice:BaseResponse<PlatformMaterialListResponse>? = null,
    var statisticsData: BaseResponse<OrderStatisticsData>? = null
) {

}


class SwitchToMainTabEvent(val tabIndex: Int)

class SwitchToOrderSubTabEvent(val tabIndex: Int)

class HideSplashEvent(val hidden: Boolean)




class MainResumeToRefreshEvent(val refresh: Boolean)