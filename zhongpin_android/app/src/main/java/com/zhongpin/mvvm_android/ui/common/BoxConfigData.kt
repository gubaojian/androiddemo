package com.zhongpin.mvvm_android.ui.common

import android.text.TextUtils
import com.zhongpin.mvvm_android.bean.LenTypeConfigItem
import com.zhongpin.mvvm_android.bean.WaLenTypeItem

data class FeedbackLabelItem(
    val code: Long = 0,
    val label: String = ""
)

object BoxConfigData {
    val companyTypes = arrayOf("造纸厂", "纸板厂", "纸箱厂", "耗材厂商", "纸箱使用单位")
    val feedbackTypes = arrayOf("让步接收", "申请折扣", "申请退货", "申请退货+补偿", "申请挑货")

    fun getCompanyType(entType:Int):String {
        if(entType < companyTypes.size
            && entType >= 0) {
            return companyTypes.get(entType)
        }
        return  ""
    }

    val layerMap = mutableMapOf<String, List<WaLenTypeItem>>();

    val feedbackMap = mutableMapOf<FeedbackLabelItem, List<FeedbackLabelItem>>(
        FeedbackLabelItem(
            code = 0,
            label = "质量问题",
        ) to listOf(
            FeedbackLabelItem(
                code = 10,
                label = "退货并补发",
            ),
            FeedbackLabelItem(
                code = 11,
                label = "退货并退款",
            ),
            FeedbackLabelItem(
                code = 12,
                label = "仅退款",
            )
        ),
        FeedbackLabelItem(
            code = 1,
            label = "数量不足",
        ) to listOf(
            FeedbackLabelItem(
                code = 20,
                label = "补发",
            ),
            FeedbackLabelItem(
                code = 21,
                label = "退款",
            )
        ),
        FeedbackLabelItem(
            code = 2,
            label = "数量超出",
        ) to listOf(
            FeedbackLabelItem(
                code = 30,
                label = "退货",
            ),
            FeedbackLabelItem(
                code = 31,
                label = "补款",
            ),
            FeedbackLabelItem(
                code = 32,
                label = "不退货且不补款",
            )
        ),
        FeedbackLabelItem(
            code = 3,
            label = "其他问题",
        ) to listOf(
            FeedbackLabelItem(
                code = 40,
                label = "其他",
            )
        )
    );

    val allFeedbackCodes = mutableSetOf<Long>()
    init {
        feedbackMap.forEach {
            allFeedbackCodes.add(it.key.code)
            it.value.forEach { innerIt ->
                allFeedbackCodes.add(innerIt.code)
            }
        }
    }

    fun isFeedbackCodeValid(code:Int?): Boolean {
        if (code == null) {
            return false;
        }
        return allFeedbackCodes.contains(code.toLong())
    }




    val hasLineDesc  = "明线"
    val noneLineDesc  = "无需压线"

    val lines = mapOf<String, String>(
        "1" to hasLineDesc ,
        "0" to noneLineDesc
    )

    fun getAllLayerCode():List<String> {
        val codes = mutableSetOf<String>();
        layerMap.values.forEach { valueIt ->
            valueIt.forEach { itemIt ->
                itemIt.type?.let {
                    codes.add(it)
                }
            }
        }
        return codes.toList()
    }
    fun getFloorLayerCodes(floor:String):List<String> {
        val  items = layerMap[floor] ?: emptyList()
        return  items.map { it.type ?: "" }.toList()
    }
    fun splitTouch(touchOne:String?,  touchTwo: String?): List<String> {
        var touch = touchOne ?: "";
        if (TextUtils.isEmpty(touchOne)) {
            touch = touchTwo ?: ""
        }
        val results = touch.trim().split(Regex("[-x*+]")).toMutableList()
        for (i in results.indices) {
            results[i].trim().let { results[i] = it }
        }
        if (results.size == 2) {
            results.add("0")
        }
        return results
    }

    fun splitSize(size: String): List<String> {
        val results = size.trim().split(Regex("[-x*+]")).toMutableList()
        for (i in results.indices) {
            results[i].trim().let { results[i] = it }
        }
        return results
    }

    fun updateLenConfig(items: List<LenTypeConfigItem>?) {
        if (items.isNullOrEmpty()) {
            return
        }
        val records = items;
        records.forEach {
            val floor = it.floor;
            val codes = it.lenType;
            if (floor != null && codes != null) {
                layerMap[floor.toString()] = codes
            }
        }
    }

}