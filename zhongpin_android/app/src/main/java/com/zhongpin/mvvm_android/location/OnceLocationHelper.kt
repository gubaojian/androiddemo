package com.zhongpin.mvvm_android.location

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol
import com.amap.api.location.AMapLocationListener
import com.blankj.utilcode.util.GsonUtils
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhongpin.lib_base.utils.LogUtils

fun interface OnLocationCallback {
    fun onLocation(location: AMapLocation?);
}

class OnceLocationHelper : Runnable {

    private var callbackAction: OnLocationCallback? = null;
    private val timeoutHandler = Handler(Looper.getMainLooper())


    fun setOnLocationCallback(callback: OnLocationCallback) {
        this.callbackAction = callback
        this.timeoutHandler.removeCallbacks(this)
    }
    fun getLocation(activity: Activity) {
        if ((System.currentTimeMillis() - lastCacheLocation.time) <= locationCacheTime) {
            onLocation(lastCacheLocation)
            return
        }

        this.timeoutHandler.removeCallbacks(this)

        AMapLocationClient.setApiKey("9b9c7700a59396797881de3663f0f3d4")
        AMapLocationClient.updatePrivacyShow(activity.applicationContext, true, true);
        AMapLocationClient.updatePrivacyAgree(activity.applicationContext, true);
        AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTPS)

        val locationOption = AMapLocationClientOption()
        locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy) //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        locationOption.setNeedAddress(true) //可选，设置是否返回逆地理地址信息。默认是true
        locationOption.setOnceLocation(true) //可选，设置是否单次定位。默认是false
        locationOption.setLocationCacheEnable(true) //可选，设置是否使用缓存定位，默认为true
        locationOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.ZH)
        locationOption.setMockEnable(true)

        val locationClient = AMapLocationClient(activity.getApplicationContext())

        //设置定位参数
        locationClient.setLocationOption(locationOption)

        locationClient.setReGeoLocationCallback {
            val location = it;
            if (BuildConfig.DEBUG) {
                LogUtils.d("LocationUtil", "Location address ${location?.latitude} ${location?.longitude} ${location?.address} ${locationClient.lastKnownLocation?.address}")
            }
            LogUtils.d("Location", "Location ${GsonUtils.toJson(it)}")
            onLocation(location)
            locationClient.setReGeoLocationCallback {}
            locationClient.onDestroy()
        }

        // 设置定位监听
        locationClient.setLocationListener(object : AMapLocationListener {
            override fun onLocationChanged(location: AMapLocation?) {
                locationClient.unRegisterLocationListener(this)
                locationClient.stopLocation()
                if (mockLocation) {
                    mockAddressLocation(location)
                    locationClient.getReGeoLocation(location);
                    return
                }
                if (BuildConfig.DEBUG) {
                    LogUtils.d("LocationUtil", "Location address ${location?.latitude} ${location?.longitude} ${location?.address} ${locationClient.lastKnownLocation?.address}")
                }
                location?.let {
                    if (it.errorCode == 0 && !TextUtils.isEmpty(it.city)) {
                       lastCacheLocation = location;
                       lastCacheLocation.time = System.currentTimeMillis()
                    }
                }
                onLocation(location)
                locationClient.onDestroy()
            }
        })
        locationClient.startLocation()
        timeoutHandler.postDelayed(this, 2000); //2秒超时时间
    }

    fun mockAddressLocation(location: AMapLocation?) {
        //上海
        location?.longitude = 121.51
        location?.latitude = 31.24
        //杭州
        //location?.longitude = 120.14
        //location?.latitude = 30.31
    }

    override fun run() {
        onLocation(lastCacheLocation)
    }

    fun onLocation(location: AMapLocation?) {
        timeoutHandler.removeCallbacks(this)
        callbackAction?.onLocation(location)
        callbackAction = null
    }

    companion object {
        var lastCacheLocation = createErrorLocation();

        val locationCacheTime = 120*1000;

        val mockLocation = false;

        fun createErrorLocation(): AMapLocation {
            val location: AMapLocation = AMapLocation("mock_error")
            location.errorCode = 100;
            return location
        }

    }
}