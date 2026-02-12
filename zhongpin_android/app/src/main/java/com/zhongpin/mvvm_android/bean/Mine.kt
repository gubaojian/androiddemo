package com.zhongpin.mvvm_android.bean
import com.zhongpin.mvvm_android.network.BaseResponse


data class MinePageData(
    var companyInfo: BaseResponse<CompanyListItemResponse>? = null,
    var userInfo:BaseResponse<UserInfoResponse>? = null,
    var payAccountInfo: BaseResponse<PayAccountBalance>? = null,
    var statisticsData: BaseResponse<OrderStatisticsData>? = null
) {

}