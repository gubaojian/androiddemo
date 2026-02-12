package com.zhongpin.mvvm_android.bean

import com.zhongpin.mvvm_android.network.CheckResponseEmpty

/**
 * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3148316/detail/55762714?spaceKey=space-64stfp
 * */
data class CompanyListResponse(
    var  records: List<CompanyListItemResponse> = emptyList<CompanyListItemResponse>()
) : CheckResponseEmpty {

    override fun isDataEmpty(): Boolean {
        return  records.isNullOrEmpty()
    }

}