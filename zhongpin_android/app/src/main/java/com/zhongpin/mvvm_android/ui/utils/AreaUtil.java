package com.zhongpin.mvvm_android.ui.utils;

import android.text.TextUtils;

public class AreaUtil {

    public static String toArea(String provice, String city, String area){
        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(area)) {
            return "";
        }
        if (TextUtils.isEmpty(provice)) {
            return String.format(
                    "%s%s",
                    city,
                    area
            );
        }
        if (TextUtils.equals(provice, city)) { //直辖市情况，比如北京市，上海市
            return String.format(
                    "%s%s",
                    city,
                    area
            );
        }
        return String.format(
                "%s%s%s",
                provice,
                city,
                area
        );
    }
}
