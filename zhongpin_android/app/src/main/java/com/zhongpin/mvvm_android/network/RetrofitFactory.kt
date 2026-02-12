package com.zhongpin.mvvm_android.network
import android.os.Build
import androidx.compose.runtime.key
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.env.EnvConstant
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.SPreference
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import java.util.concurrent.TimeUnit


/**
 * Created with Android Studio.
 * Description:
 * @date: 2020/02/24
 * Time: 16:56
 */

class RetrofitFactory private constructor() {
    private val retrofit : Retrofit

    fun <T> create(clazz: Class<T>) : T {
        return retrofit.create(clazz)
    }

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(EnvConstant.appApiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(initOkHttpClient())
            .build()
    }

    companion object {
        val instance by lazy {
            RetrofitFactory()
        }
    }

    private fun initOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder();
        builder.readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(initLoggingIntercept())
        }
        builder.addInterceptor(initCookieIntercept())
            .addInterceptor(initLoginIntercept())
            .addInterceptor(initCommonInterceptor())
        return builder.build()
    }
    private fun initLoggingIntercept(): Interceptor {
        return HttpLoggingInterceptor { message ->
            try {
                if (message.length < 1024*4) {
                    LogUtils.e("OKHttp-----", message)
                } else if (message[0] == '{') {
                    LogUtils.e("OKHttp-----", message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtils.e("OKHttp-----", message)
            }
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun initCookieIntercept(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            response
        }
    }

    private fun initLoginIntercept(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val builder = request.newBuilder()
            val token = SPUtils.getInstance().getString(Constant.TOKEN_KEY,"")
            if(token != null && token.isNotEmpty()){
                builder.addHeader("token", token)
                LogUtils.e("OKHttp", "request token $token")
            }else {
                LogUtils.e("OKHttp", "request token token is empty")
            }
            val response = chain.proceed(builder.build())
            val refreshToken = response.headers["token"]
            refreshToken?.let {
                if (refreshToken.isNotEmpty()) {
                    SPUtils.getInstance().put(Constant.TOKEN_KEY, refreshToken)
                    LogUtils.e("OKHttp", "refreshToken token $refreshToken")
                }
            }
            response
        }
    }

    private fun userAgent():String {
        val deviceNameStr = URLEncoder.encode(DeviceUtils.getManufacturer().plus("_")
            .plus(DeviceUtils.getModel()), "UTF-8")
        val systemVersion = Build.VERSION.RELEASE // 系统版本（如 "13"）
        val version = AppUtils.getAppVersionName()
        val customUserAgent = "PinDan/${version} (Android-$systemVersion; $deviceNameStr)"
        return customUserAgent;
    }

    private fun initCommonInterceptor(): Interceptor {
        return Interceptor { chain ->
            val deviceNameStr = "${DeviceUtils.getManufacturer()}_${Build.BRAND}_${Build.MODEL}";
            val commonHeader = mutableMapOf<String, String>()
            commonHeader.put("User-Agent", userAgent())
            commonHeader.put("Content-Type", "application/json")
            commonHeader.put("charset", "UTF-8")
            commonHeader.put("device-type", "android")
            commonHeader.put("app-version", AppUtils.getAppVersionName())
            commonHeader.put("device-os-version", DeviceUtils.getSDKVersionName().toString())
            commonHeader.put("device-name",  URLEncoder.encode(deviceNameStr, "UTF-8"))
            addHeaderWhenUserAgreeProtocol(commonHeader)
            val  builder = chain.request()
                .newBuilder()
            commonHeader.keys.forEach { key ->
                val value = commonHeader[key] ?: "";
                builder.addHeader(key, value)
                if (BuildConfig.DEBUG) {
                    LogUtils.e("OKHttp", "${key}: ${value}")
                }
            }
            val request = builder.build()
            chain.proceed(request)
        }
    }

    private fun addHeaderWhenUserAgreeProtocol(request: MutableMap<String, String>){
        if( UserInfoUtil.isUserAgreeProtocol()) {
            //request.addHeader("device-id", DeviceUtils.getUniqueDeviceId())
        }
    }

    private fun parseCookie(it: List<String>): String {
        if(it.isEmpty()){
            return ""
        }

        val stringBuilder = StringBuilder()

        it.forEach { cookie ->
            stringBuilder.append(cookie).append(";")
        }

        if(stringBuilder.isEmpty()){
            return ""
        }
        //末尾的";"去掉
        return stringBuilder.deleteCharAt(stringBuilder.length - 1).toString()
    }

    private fun saveCookie(domain: String?, parseCookie: String) {
        domain?.let {
            var resutl :String by SPreference("cookie", parseCookie)
            resutl = parseCookie
        }
    }
}