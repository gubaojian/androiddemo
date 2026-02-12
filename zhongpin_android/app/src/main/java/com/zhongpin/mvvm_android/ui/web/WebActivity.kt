package com.zhongpin.mvvm_android.ui.web

import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityWebBinding
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil

class WebActivity : BaseVMActivity<WebViewModel>() {


    private lateinit var mBinding: ActivityWebBinding;
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var countDownTimer: CountDownTimer

    private var title :String = "";
    private var url:String  = "";


    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            title = intent.getStringExtra("title") ?: "";
            url = intent.getStringExtra("url") ?: "";
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityWebBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }


    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mLoadingDialog = LoadingDialog(this, false)
        mBinding.ivBack.setOnClickListener { finish() }
        if (!TextUtils.isEmpty(title)) {
            mBinding.ivTitle.text = title
        }
        initWebViewSetting()
        mBinding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    if (it.startsWith("http")) {
                        view?.loadUrl(it)
                    }
                }
                return true
            }
        }
        mBinding.webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (!TextUtils.isEmpty(title) && TextUtils.isEmpty(mBinding.ivTitle.text)) {
                    mBinding.ivTitle.text = title
                }
            }
        }
        mBinding.webView.loadUrl(url)
    }

    fun initWebViewSetting() {
        val wvSettings = mBinding.webView.settings
        // 是否阻止网络图像
        wvSettings.blockNetworkImage = false
        // 是否阻止网络请求
        wvSettings.blockNetworkLoads = false
        // 是否加载JS
        wvSettings.javaScriptEnabled = true
        wvSettings.javaScriptCanOpenWindowsAutomatically = true
        //覆盖方式启动缓存
        wvSettings.cacheMode = WebSettings.LOAD_DEFAULT
        // 使用广泛视窗
        wvSettings.useWideViewPort = true
        wvSettings.loadWithOverviewMode = true
        wvSettings.domStorageEnabled = true
        //是否支持缩放
        wvSettings.builtInZoomControls = false
        wvSettings.setSupportZoom(false)
        //不显示缩放按钮
        wvSettings.displayZoomControls = false
        wvSettings.allowFileAccess = true
        wvSettings.databaseEnabled = true
        mBinding.webView.setVerticalScrollbarOverlay(false) //不出现指定的垂直滚动条有叠加样式
        wvSettings.useWideViewPort = true //设定支持viewport
        wvSettings.builtInZoomControls = true //设置出现缩放工具
        wvSettings.displayZoomControls = false //设置缩放工具隐藏
        wvSettings.setSupportZoom(true) //设定支持缩放
        //缓存相关
        //wvSettings.setAppCacheEnabled(true);
        wvSettings.domStorageEnabled = true
        wvSettings.databaseEnabled = true
    }

    override fun onResume() {
        mBinding.webView.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.webView.onPause()
    }

    override fun onDestroy() {
        mBinding.webView.destroy()
        super.onDestroy()
    }

    /**
     * show 加载中
     */
    fun showLoadingDialog() {
        mLoadingDialog.showDialog(this, false)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissLoadingDialog() {
        mLoadingDialog.dismissDialog()
    }

}