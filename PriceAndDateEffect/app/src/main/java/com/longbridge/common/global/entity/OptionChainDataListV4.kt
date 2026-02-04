package com.longbridge.common.global.entity

class OptionChainDataListV4 {
    var expire_date_list: List<ExpireDate>? = null
    var default_expire_date: DefaultExpireDate? = null
    var strike_price: List<StrikePriceInfo>? = null

    class DefaultExpireDate {
        var expire_date: String? = null
        var type: String? = null
        var standard_attr: String? = null
    }

    class ExpireDate {
        var expire_date: String? = null
        var type: String? = null
        var standard_attr: String? = null
    }

    fun getAssignDatePosition(assignDate: String): Int {
        val list = expire_date_list ?: return 0
        if (list.isEmpty()) {
            return 0
        }

        // 遍历列表，查找匹配的日期
        for ((index, date) in list.withIndex()) {
            if (date.expire_date.equals(assignDate, ignoreCase = true)) {
                return index
            }
        }
        return 0
    }
}