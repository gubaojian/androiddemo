package com.zhongpin.mvvm_android.bean

data class FeedbackSearchQuery(
    var orderNo:String?,
    var zhiDaiHao:String?,
    var lengXing:String?,
    var yaXian:String?,
    var xianXing:String?,
    var zhiBanFactorName:String?,
    var zhiXiangFactorName:String?,
    var orderJiaoHuoDate:String?
) :java.io.Serializable {


    fun toQuery():String {
        val sb = StringBuilder();
        append(sb, orderNo);
        append(sb, zhiDaiHao);
        append(sb, lengXing);
        append(sb, yaXian);
        append(sb, xianXing);
        append(sb, zhiBanFactorName);
        append(sb, zhiXiangFactorName);
        append(sb, orderJiaoHuoDate);
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length -1);
        }
        return sb.toString();
    }

    private fun append(sb:StringBuilder, item:Any?) {
        if (item == null) {
            return;
        }
        if (item is String) {
            if (item.isEmpty()) {
                return;
            }
        }
        item.let {
            sb.append(it)
            sb.append("/");
        }
    }

    companion object {
        private const val serialVersionUID = 202505191643701L
    }

}