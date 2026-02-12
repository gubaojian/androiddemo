package com.zhongpin.mvvm_android

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.alipay.sdk.app.AlipayApi
import com.alipay.sdk.app.EnvUtils
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol
import com.king.pay.wxpay.WXAPI
import com.kingja.loadsir.core.LoadSir
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.mmkv.MMKV
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhongpin.lib_base.app.ActivityLifecycleCallbacksImpl
import com.zhongpin.lib_base.app.LoadModuleProxy
import com.zhongpin.lib_base.utils.AutoRestartApp
import com.zhongpin.mvvm_android.app.AppLifecycleListener
import com.zhongpin.mvvm_android.common.callback.EmptyCallBack
import com.zhongpin.mvvm_android.common.callback.ErrorCallBack
import com.zhongpin.mvvm_android.common.callback.LoadingCallBack
import com.zhongpin.mvvm_android.common.env.AppEnv
import com.zhongpin.mvvm_android.ui.utils.WXAPIUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis


class MyApplication: Application(), ViewModelStoreOwner {


    lateinit var mAppViewModelStore : ViewModelStore

    private var mFactory: ViewModelProvider.Factory? = null

    private val mCoroutineScope by lazy(mode = LazyThreadSafetyMode.NONE) { MainScope() }

    private val mLoadModuleProxy by lazy(mode = LazyThreadSafetyMode.NONE) { LoadModuleProxy() }


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var INSTANCE: MyApplication

        // 全局Context
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var application: MyApplication
    }

    override val viewModelStore: ViewModelStore
        get() = mAppViewModelStore


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        AppEnv.initAppEnv();
        MyApplication.context = base
        MyApplication.application = this
        mLoadModuleProxy.onAttachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        AppEnv.initAppEnv();
        INSTANCE = this
        mAppViewModelStore = ViewModelStore()

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())
        // 全局监听 Activity 生命周期
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksImpl())

        //config loadSir
        configLoadSir()

        configBugly()

        configMMKV()

        registerWeChat()

        configDepSdk()

        // 策略初始化第三方依赖
        initDepends()
    }

    /**
     * 初始化第三方依赖
     */
    private fun initDepends() {
        // 开启一个 Default Coroutine 进行初始化不会立即使用的第三方
        mCoroutineScope.launch(Dispatchers.Default) {
            mLoadModuleProxy.initByBackstage()
        }
        // 前台初始化
        val allTimeMillis = measureTimeMillis {
            val depends = mLoadModuleProxy.initByFrontDesk()
            var dependInfo: String
            depends.forEach {
                val dependTimeMillis = measureTimeMillis { dependInfo = it() }
                Log.d("BaseApplication", "initDepends: $dependInfo : $dependTimeMillis ms")
            }
        }
        Log.d("BaseApplication", "初始化完成 $allTimeMillis ms")
    }


    override fun onTerminate() {
        super.onTerminate()
        mLoadModuleProxy.onTerminate(this)
        mCoroutineScope.cancel()
    }


    fun getAppViewModelProvider(activity: Activity): ViewModelProvider {
        return ViewModelProvider(
            activity.applicationContext as MyApplication,
            (activity.applicationContext as MyApplication).getAppFactory(activity)
        )
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application = checkApplication(activity)
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
        return mFactory as ViewModelProvider.Factory
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException("Your activity/fragment is not yet attached to " + "Application. You can't request ViewModel before onCreate call.")
    }

    private fun configLoadSir() {
        LoadSir.beginBuilder()
            .addCallback(LoadingCallBack())
            .addCallback(ErrorCallBack())
            .addCallback(EmptyCallBack())
            .commit()
    }

    private fun configMMKV() {
        MMKV.initialize(this);
    }

    private fun configBugly() {
        val strategy = UserStrategy(applicationContext)
        strategy.deviceModel = Build.MODEL
        CrashReport.initCrashReport(applicationContext, "21bd213551", BuildConfig.DEBUG, strategy);
        Thread.setDefaultUncaughtExceptionHandler(AutoRestartApp(applicationContext))
    }


    private fun registerWeChat() {
        val APP_ID = "wxc64e5ce272062557";
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        val api = WXAPIFactory.createWXAPI(this, APP_ID, true)
        // 将应用的appId注册到微信
        api.registerApp(APP_ID)
        WXAPI.getInstance(application)
            .registerApp(APP_ID)
        WXAPIUtil.wxApi = api;
        try {
            //建议动态监听微信启动广播进行注册到微信
            val intentFilter = IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP);
            ContextCompat.registerReceiver(this,
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        // 将该app注册到微信
                        api.registerApp(APP_ID)
                        WXAPI.getInstance(application)
                            .registerApp(APP_ID)
                    }
                }, intentFilter, ContextCompat.RECEIVER_EXPORTED
            )
        } catch (e: Exception) {
            e.printStackTrace()
            e.printStackTrace()
        }
    }

    private fun configDepSdk() {
        if (AppEnv.isDev() || AppEnv.isTest()) {
            AlipayApi.registerApp(this,"9021000150681498");
            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX)
        } else {
            val appId = "2021005175610319";
            AlipayApi.registerApp(this,appId);
            EnvUtils.setEnv(EnvUtils.EnvEnum.ONLINE)
        }

        //高德地图设置
        AMapLocationClient.setApiKey("9b9c7700a59396797881de3663f0f3d4")
        AMapLocationClient.updatePrivacyShow(applicationContext, true, true);
        AMapLocationClient.updatePrivacyAgree(applicationContext, true);
        AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTPS) //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
    }

}