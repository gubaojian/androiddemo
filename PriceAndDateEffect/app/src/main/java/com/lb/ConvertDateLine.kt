package com.lb

import android.text.SpannableStringBuilder
import com.lb.price.one.R
import com.lb.util.DateTimeUtils

import com.longbridge.common.global.entity.OptionChainDataListV4
import com.longbridge.common.global.entity.OptionChainDataListV4.ExpireDate
import com.longbridge.common.global.entity.StrikePriceInfo
import com.longbridge.common.kotlin.extends.updateList

import com.longbridge.common.quoteCenter.entity.ReportItem
import com.longbridge.core.comm.FApp
import com.longbridge.core.multilingual.LanguageUtils


import java.util.TimeZone

class ConvertDateLine {


    private val mCounterId: String = ""
    private var mOptionChainDataListV4: OptionChainDataListV4? = null
    private var fetchChainStandardAttr = ""
    private val mDatas = arrayListOf<OptionDate>()


    // 查询期权链的日期。
    private var fetchChainDate = ""
    private var mDefaultDate: String = ""

    fun getFetchChainDate(): String {
        return fetchChainDate
    }


    companion object {
        private val easternTimeZone: TimeZone = DateTimeUtils.usaTimeZone
        private val monthMap = mapOf(
            "1" to "Jan",
            "2" to "Feb",
            "3" to "Mar",
            "4" to "Apr",
            "5" to "May",
            "6" to "Jun",
            "7" to "Jul",
            "8" to "Aug",
            "9" to "Sep",
            "10" to "Oct",
            "11" to "Nov",
            "12" to "Dec"
        )

        @JvmStatic
        fun convertMonthNumberToAbbreviation(month: String): String {
            val normalizedMonth = month.trimStart('0')
            return monthMap[normalizedMonth] ?: month
        }
    }

    fun getOptionDate(): String {
        val tempKey = mCounterId + "_date"
        return ""
    }

    private fun getOptionDateStandardAttr(): String {
        val tempKey = mCounterId + "_standard_attr"
        return ""
    }

    data class OptionDate(var expireDate: ExpireDate? = null, val report: ReportItem? = null) {
        val expireDiffDate
            get() = run {
                val builder = SpannableStringBuilder()
                val daysUntilExpiration = DateTimeUtils.getDaysUntilSpecZoneFromCache(
                    expireDate?.expire_date, easternTimeZone
                )
                val dayStr = if (daysUntilExpiration >= 0) {
                    "$daysUntilExpiration${FApp.get().getString(R.string.common_simple_day_number)}"
                } else {
                    // common_order_status_expired_status2
                    FApp.get().getString(R.string.common_order_status_expired_status2)
                }

                val expirationStatus = if (expireDate?.standard_attr == "N") {
                    "$dayStr Old"
                } else {
                    dayStr
                }
                builder.append(expirationStatus)
                builder.toString()
            }

        val expireDateContent
            get() = run {
                val builder = SpannableStringBuilder()
                val year = expireDate?.expire_date?.substring(0, 4)
                val simplifiedYear = expireDate?.expire_date?.substring(2, 4)
                val month = expireDate?.expire_date?.substring(4, 6)
                val day = expireDate?.expire_date?.substring(6, 8)

                val isFutureYear = DateTimeUtils.isFeatureYear(year, easternTimeZone)

                val formattedDate = if (LanguageUtils.isEN()) {
                    val engMonth = convertMonthNumberToAbbreviation(month ?: "")
                    if (isFutureYear) {
                        "$engMonth $day, $simplifiedYear"
                    } else {
                        "$engMonth $day"
                    }
                } else {
                    if (isFutureYear) {
                        "$simplifiedYear/$month/$day"
                    } else {
                        "$month/$day"
                    }
                }
                builder.append(formattedDate)
                builder.toString()
            }
    }


    fun setTimelineData(data: OptionChainDataListV4?): List<OptionDate> {
        if (data == null) {
            return mDatas
        }
        mOptionChainDataListV4 = data
        fetchChainDate = getOptionDate()
        fetchChainStandardAttr = getOptionDateStandardAttr()
        mDefaultDate =
            if (fetchChainDate.isNotEmpty() && mOptionChainDataListV4?.expire_date_list?.filter { it.expire_date == fetchChainDate && it.standard_attr == fetchChainStandardAttr } != null) {
                fetchChainDate
            } else {
                mOptionChainDataListV4?.default_expire_date?.expire_date?.takeIf { it.isNotEmpty() }
                    ?: ""
            }
        fetchChainDate = mDefaultDate

        mDatas.clear()

        mDatas.updateList(data.expire_date_list?.map {
            OptionDate(it)
        })

        return mDatas

        // mRvDates?.notifyStatus()
    }

    /**
     * fake日期date
     */
    fun getFakeTimelineData(): List<ConvertDateLine.OptionDate> {
        val result = ArrayList<ConvertDateLine.OptionDate>()
        // 1:
        val o1 = OptionDate()
        val e1 = ExpireDate()
        e1.expire_date = "20251121"
        e1.standard_attr = "N"
        o1.expireDate = e1
        result.add(o1)
        // 2
        val o2 = OptionDate()
        val e2 = ExpireDate()
        e2.expire_date = "20251128"
        e2.standard_attr = "S"
        o2.expireDate = e2
        result.add(o2)

        // 3
        val o3 = OptionDate()
        val e3 = ExpireDate()
        e3.expire_date = "20251205"
        e3.standard_attr = "S"
        o3.expireDate = e3
        result.add(o3)


        // 3
        val o4 = OptionDate()
        val e4 = ExpireDate()
        e4.expire_date = "20251212"
        e4.standard_attr = "S"
        e4.type = "W"
        o4.expireDate = e4
        result.add(o4)

        // 5
        val o5 = OptionDate()
        val e5 = ExpireDate()
        e5.expire_date = "20251219"
        e5.standard_attr = "S"
        o5.expireDate = e5
        result.add(o5)


        // 6
        val o6 = OptionDate()
        val e6 = ExpireDate()
        e6.expire_date = "20251128"
        e6.standard_attr = "S"
        o6.expireDate = e6
        result.add(o6)


        // 7
        val o7 = OptionDate()
        val e7 = ExpireDate()
        e7.expire_date = "20251226"
        e7.standard_attr = "S"
        o7.expireDate = e7
        result.add(o7)

        return result

        // mRvDates?.notifyStatus()
    }

    fun getFakePriceData(): List<StrikePriceInfo> {
        val result = ArrayList<StrikePriceInfo>()
        val prices0 = listOf(
            98.6f, 99.9f, 100.5f, 101.2f, 102.0f, 102.8f, 103.6f, 104.1f, 105.0f,
            106.3f,
            107.5f,
            109.2f,
            109.1f,
        )
         val prices = listOf(
             41f,
             42f,
             43f,
             44f,
             45f,
             46f,
             47f,
             48f,
             49f,
            50f,
            53f,
            54f,
            55f,
            61f,
            65f,
            66f,
            67f,
            69f,
            71f,
            72f,
            73f,
            74f,
            75f,
            76f,
            77f,
            78f,
            79f,
             81f,
             82f,
             83f,
             84f,
             85f,
             86f,
             87f,
             88f,
             89f,
             91f,
             92f,
             93f,
             94f,
             95f,
             96f,
             97f,
             98f,
             99f,

        )

        // isSameYear = (idx % 3 == 0)
        prices.forEachIndexed { index, f ->
            val info = StrikePriceInfo()
            info.price = f.toString()
            info.isSameYear =  (index % 3 == 0)
            result.add(info)
        }
        return result


    }

    /**
     * 从 priceDataList 中找到离 currPrice 最近（或等于）的价格下标。
     * - 若有多个同距，优先选择价格小于等于 currPrice 的那个（更靠左），再退而选较小下标
     */
    fun getNearIndex(currPrice: Double, priceDataList: List<StrikePriceInfo>): Int {
        if (priceDataList.isEmpty()) return 0
        var bestIdx = 0
        var bestDiff = Double.MAX_VALUE
        priceDataList.forEachIndexed { idx, info ->
            val tempPrice = info.price.toDoubleOrNull() ?: return@forEachIndexed
            val diff = kotlin.math.abs(tempPrice - currPrice)
            when {
                diff < bestDiff -> {
                    bestDiff = diff
                    bestIdx = idx
                }
                // 同距时，优先选择价格不大于当前价（更靠左的点）
                diff == bestDiff -> {
                    val bestPrice = priceDataList[bestIdx].price.toDoubleOrNull() ?: tempPrice
                    if (tempPrice <= currPrice && bestPrice > currPrice) {
                        bestIdx = idx
                    } else if ((tempPrice <= currPrice) == (bestPrice <= currPrice)) {
                        // 都在同一侧，取下标较小的（更靠左）
                        if (idx < bestIdx) bestIdx = idx
                    }
                }
            }
        }
        return bestIdx
    }
}