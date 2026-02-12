package com.zhongpin.mvvm_android.biz.utils;


import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author pengzhengfa
 */
public class MobileUtil {

    /**
     * 生成6位验证码
     *
     * @return
     */
    public String selectCode() {
        Random random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    /**
     * 校验手机号是否为大陆手机号
     *
     * @param mobile 要校验的手机号
     * @return 如果是大陆手机号返回 true,否则返回 false
     */
    public boolean checkMobile(String mobile) {
        String regex = "^1[3-9]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }

}
