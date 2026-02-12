package com.zhongpin.mvvm_android.common.utils

import com.zhongpin.mvvm_android.common.env.AppEnv

/**
 * Created with Android Studio.
 * Description:
 * @date: 2020/02/24
 * Time: 17:36
 */
object Constant {



    const val PRIVATE_TERM_URL = "https://pinhuaxia.com/user/box/agreementPage?type=privacy";
    const val SERVICE_TERM_URL = "https://pinhuaxia.com/user/box/agreementPage?type=service";
    const val USER_TERM_URL = "https://pinhuaxia.com/user/box/agreementPage?type=service";


    const val CONTRACT_KEFU_PHONE_NUM = "4008699789";
    const val CONTRACT_KEFU_PHONE_TEL_URI = "tel:4008699789";


    const val HOME = 0
    const val ORDER = 1

    //const val NOTIFY = 2 //通知本期暂时不开发

    const val MINE = 2


    const val SEARCH_RECORD = 0
    const val SEARCH_GOODS = 1
    const val SEARCH_SIMILAR = 2

    const val SUCCESS = 0

    const val COMMON_KEY = "url_common_key"

    //跟随环境
    val TOKEN_KEY = "LOGIN_TOKEN_KEY_${AppEnv.env}"
    val LOGIN_MOBILE_KEY = "LOGIN_MOBILE_KEY_${AppEnv.env}"
}
