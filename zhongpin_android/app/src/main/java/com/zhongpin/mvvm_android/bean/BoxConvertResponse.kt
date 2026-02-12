package com.zhongpin.mvvm_android.bean

import java.io.Serializable

/**
 * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3223861/detail/55958049?spaceKey=space-64stfp
 * /pc/calculate/boxConvert
 * */
data class BoxConvertResponse (
    var length:Int? = 0,
    var width:Int? = 0,
    var boxHeightOne:Int? = 0,
    var boxWidth:Int? = 0,
    var boxHeightTwo:Int? = 0,
){
}

