package com.zhongpin.mvvm_android.ui.utils

/**
 * 版本号比较工具类
 */
object VersionCompareUtil {


    /**
     * 检查target是否大于current
     */
    fun isGreatThan(target: String?, current: String?) = compare(target, current) > 0

    /**
     * 检查target是否小于current
     */
    fun isLessThan(target: String?, current: String?) = compare(target, current) < 0

    /**
     * 比较两个版本号
     * @param one 第一个版本号
     * @param two 第二个版本号
     * @return 1: version1 > version2; -1: version1 < version2; 0: 相等
     * @throws IllegalArgumentException 如果版本号格式无效
     */
    private fun compare(one: String?, two: String?): Int {
       when {
            one == null && two == null -> return 0
            one == null -> return -1
            two == null -> return 1
        }
        // 解析版本号为数字段和预发布标识
        val info1 = parseVersion(one)
        val info2 = parseVersion(two)

        // 比较数字段
        val maxLength = maxOf(info1.versions.size, info2.versions.size)
        for (i in 0 until maxLength) {
            val num1 = if (i < info1.versions.size) info1.versions[i] else 0
            val num2 = if (i < info2.versions.size) info2.versions[i] else 0

            when {
                num1 > num2 -> return 1
                num1 < num2 -> return -1
            }
        }
        return 0;
    }

    private fun formateVersion(version:String?): String {
        if (version == null) {
            return "";
        }
        val sb = StringBuilder();
        version.forEach {
            if (Character.isDigit(it) || it == '.') {
                sb.append(it);
            }
        }
        return sb.toString();
    }

    private fun parseVersion(version: String?): VersionInfo {
        val formatVersion = formateVersion(version)
        val versions = formatVersion.split(Regex("[.]"))
        val segments = versions.map {
            it.toIntOrNull() ?: 0
        }.toList()
        return VersionInfo(segments)
    }

    /**
     * 版本信息数据类，存储数字段和预发布标识
     */
    private data class VersionInfo(
        val versions: List<Int>,
    )
}
