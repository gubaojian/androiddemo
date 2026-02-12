package com.zhongpin.mvvm_android.biz.utils

import android.app.Activity
import android.text.TextUtils
import com.github.gzuliyujiang.wheelpicker.entity.ProvinceEntity
import com.github.gzuliyujiang.wheelpicker.utility.AddressJsonParser
import com.zhongpin.mvvm_android.bugfix.AssetAddressLoaderV2
import com.zhongpin.mvvm_android.ui.utils.AreaUtil

object ConfigAddress {

    val chinaAddressJsonFile = "china_address.json"

    var addressData:List<ProvinceEntity>? = null;


    fun loadAddress(activity: Activity) {
        val jsonParser = AddressJsonParser();
        val loader = AssetAddressLoaderV2(activity.applicationContext, chinaAddressJsonFile);
        loader.loadJson({
            addressData = it
        }, jsonParser)
    }
    fun releaseAddress() {
        addressData = null;
    }


    fun isAddressValid(mProvince:String?, mCity: String?, mCounty: String?): Boolean {
        if (addressData == null) {
            return false;
        }
        if (mProvince == null || mCity == null || mCounty == null) {
            return false;
        }
        addressData?.forEach {
            pIt ->
            if (mProvince.equals(pIt.name)) {
                pIt.cityList.forEach {
                    cIt ->
                    if (mCity.equals(cIt.name)) {
                        cIt.countyList.forEach { countyIt ->
                            if (mCounty.equals(countyIt.name)) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return  false
    }

    fun getSubAddress(detailAddress:String?, province: String?, city: String?, area: String?): String{
        if (TextUtils.isEmpty(detailAddress) || detailAddress == null) {
            return ""
        }
        val fullPrefix = "${province}${city}${area}"
        val addressPrefix = AreaUtil.toArea(
            province,
            city,
            area
        )
        if (detailAddress.startsWith(fullPrefix) && !TextUtils.isEmpty(fullPrefix)) {
            return detailAddress.substring(fullPrefix.length, detailAddress.length)
        }

        if (detailAddress.startsWith(addressPrefix) && !TextUtils.isEmpty(addressPrefix)) {
            return detailAddress.substring(addressPrefix.length, detailAddress.length)
        }
        return "";
    }
}