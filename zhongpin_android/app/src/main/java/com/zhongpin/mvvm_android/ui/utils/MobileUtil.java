package com.zhongpin.mvvm_android.ui.utils;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author pengzhengfa
 */
public class MobileUtil {


    /**
     * 校验手机号是否为大陆手机号
     *
     * @param mobile 要校验的手机号
     * @return 如果是大陆手机号返回 true,否则返回 false
     */
    public static boolean checkMobile(String mobile) {
        String regex = "^1[3-9]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }

}
