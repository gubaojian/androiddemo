package com.zhongpin.mvvm_android.ui.utils

import android.icu.util.Calendar
import android.text.TextUtils
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import java.text.SimpleDateFormat
import java.util.Date

object PingDanAppUtils {
    fun getDate(year: Int, month: Int, day: Int):String {
        val date = String.format("%d-%02d-%02d", year, month, day)
        return date;
    }

    fun getDateEntity(date : String):DateEntity? {
        try {
            if (TextUtils.isEmpty(date)) {
                return null
            }
            val dateEntity = DateEntity();
            val dates = date.split("-")
            if (dates.size == 3) {
                dateEntity.year = dates[0].toInt()
                dateEntity.month = dates[1].toInt()
                dateEntity.day = dates[2].toInt()
                return dateEntity
            }
            val trim = date.trim();
            if (trim.length == 8) {
                dateEntity.year = trim.substring(0, 4).toInt()
                dateEntity.month = trim.substring(4, 4 + 2).toInt()
                dateEntity.day = trim.substring(6, 6 + 2).toInt()
                return dateEntity
            }
            return null
        } catch (_: Exception) {
            return  null
        }
    }
    //2025-07-04T00:00:00.000+00:00
    //yyyy-MM-dd HH:mm:ss
    fun getDateDay(time:String):String  {
        val format = "yyyy-MM-dd";
        if (time.length <= format.length) {
            return time;
        }
        return time.substring(0, format.length)
    }

    fun getDateDayHHMM(time:String):String  {
        val format = "yyyy-MM-dd HH:mm";
        if (time.length <= format.length) {
            return time;
        }
        return time.substring(0, format.length)
    }

    fun getOrderFilterDateFormat(year: Int, month:Int, day:Int):String  {
        val date = String.format("%d-%02d-%02d", year, month, day)
        return date;
    }

    fun priceValid(price:String): Boolean {
        val prices = price.trim().split(Regex("[.]"))
        if (prices.size == 2) {
            if (prices[1].length > 2) {
                return false;
            }
        }
        return true;
    }

    fun showAmount(price:String): String {
        val prices = price.trim().split(Regex("[.]"))
        if (prices.size == 2) {
            if (prices[1].length > 2) {
                return "${prices[0]}.${prices[1].substring(0, 2)}";
            }
        }
        return price;
    }


    fun roleNameList(roleName:String?): List<String> {
        if (roleName.isNullOrEmpty()) {
            return  emptyList();
        }
        val roles = roleName.trim().split(Regex("[,，]"))
        return roles.toList();
    }

    fun roleIdList(roleId:String?): List<String> {
        if (roleId.isNullOrEmpty()) {
            return  emptyList();
        }
        val ids = roleId.trim().split(Regex("[,，]"))
        return ids.toList();
    }


    fun getPositivePrice(price:String?): String {
        if (price.isNullOrEmpty()) {
            return  "";
        }
        var result = price.replace(Regex("[-+]"), "")
        return result;
    }

    fun getServerFormatTouch(touch:String?): String {
        if (touch.isNullOrEmpty()) {
            return  "";
        }
        val touchs  = touch.split(Regex("[-+]")).filter {
            it != "0" && it.isNotEmpty()
        }
        return touchs.joinToString(separator = "+");
    }


    fun getAdaptTypeName(typeName:String?, type:String?): String {
        if (!TextUtils.isEmpty(typeName)) {
            return typeName ?: ""
        }
        //1:面纸 2:瓦纸 3:中夹 4:底纸
        val descMap = mutableMapOf<String, String>(
            "1" to "面纸",
            "2" to "瓦纸",
            "3" to "中夹",
            "4" to "底纸"
        )
        type?.let {
            val descs = type.split(Regex("[,]")).map { descMap.get(it) ?: it }.toList()
            return descs.joinToString(separator = ",")
        }
        return ""
    }

    fun toPaperSizeWithUnitMM(paperSize:String?): String {
        if (paperSize.isNullOrEmpty()) {
            return ""
        }
        if (paperSize.contains("mm")) {
            return  paperSize;
        }
        val descs = paperSize.split(Regex("[*]")).map { "${it}mm" }.toList()
        return descs.joinToString(separator = "*")
    }

    fun getPurTimeRemain(purTime:String?): Long {
        try {
            if (purTime == null) {
                return  0;
            }
            val curTime = Date().time;
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            val purDate = format.parse(purTime)
            if (purDate == null) {
                return 0;
            }
            val expireTime = purDate.time + 30*60*1000L; //30分钟过期
            val remain = expireTime - curTime;
            if (remain >= 0) {
                return remain
            }
            return 0;
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L;
        }
    }

}