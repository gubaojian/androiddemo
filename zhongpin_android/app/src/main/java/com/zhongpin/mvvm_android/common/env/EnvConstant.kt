package com.zhongpin.mvvm_android.common.env

object EnvConstant {

    //https://api.pinhuaxia.com/
    const val BASE_URL_ONLINE = "https://api.pinhuaxia.com/"

    //测试环境
    const val BASE_URL_TEST = "http://192.168.6.42:81/"


    // 开发环境
    const val BASE_URL_DEV = "http://192.168.6.36:81/"


    //通过修改：AppEnv来切换环境
    var appApiBaseUrl = BASE_URL_DEV

}