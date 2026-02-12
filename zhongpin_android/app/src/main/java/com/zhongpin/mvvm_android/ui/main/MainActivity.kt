package com.zhongpin.mvvm_android.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.NotificationUtils
import com.blankj.utilcode.util.SPUtils
import com.gyf.immersionbar.ImmersionBar
import com.kongzue.tabbar.Tab
import com.kongzue.tabbar.interfaces.OnTabChangeListener
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityMainBinding
import com.zhongpin.lib_base.ktx.fadeOut
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.isVisible
import com.zhongpin.lib_base.utils.ActivityStackManager
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.base.ext.setHomeNavBarMarin
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.HideSplashEvent
import com.zhongpin.mvvm_android.bean.LoginEvent
import com.zhongpin.mvvm_android.bean.SwitchToMainTabEvent
import com.zhongpin.mvvm_android.bean.TokenExpiredEvent
import com.zhongpin.mvvm_android.bean.UserNoPermissionOperationEvent
import com.zhongpin.mvvm_android.common.login.LoginUtils
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.common.hasShowAppUseProtocolDialog
import com.zhongpin.mvvm_android.ui.common.showAppForgeUpdateDialog
import com.zhongpin.mvvm_android.ui.common.showAppNormalUpdateDialog
import com.zhongpin.mvvm_android.ui.common.showAppUseProtocolDialogIfNeed
import com.zhongpin.mvvm_android.ui.home.HomeFragment
import com.zhongpin.mvvm_android.ui.login.LoginActivity
import com.zhongpin.mvvm_android.ui.me.MineFragment
import com.zhongpin.mvvm_android.ui.order.OrderListFragment
import com.zhongpin.mvvm_android.ui.test.SettingFragment
import com.zhongpin.mvvm_android.ui.utils.VersionCompareUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@EventBusRegister
class MainActivity  : BaseVMActivity<MainViewModel>() {
    private var mLastIndex: Int = -1
    private val mFragmentSparseArray = SparseArray<Fragment>()
    // 当前显示的 fragment
    private var mCurrentFragment: Fragment? = null
    private var mLastFragment: Fragment? = null
    private lateinit var binding: ActivityMainBinding;

    private var selectTab = Constant.HOME;

    private var autoHideSplashJob:Job? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Home)
        ImmersionBar.with(this).transparentBar().fullScreen(true).init()
        if (intent != null) {
            selectTab = intent.getIntExtra("selectTab", Constant.HOME)
        }
        super.onCreate(savedInstanceState)
    }

    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        return view
    }


    override fun initView() {
        super.initView()
        showAppUseProtocolDialogIfNeed()
        setHomeNavBarMarin(binding.mainBody)
        switchFragment(Constant.HOME)
        initBottomNavigation()
        initTabs()
        if (selectTab != Constant.HOME
            && selectTab >= 0
            && selectTab <= Constant.MINE) {
            mSharedViewModel.viewModelScope.launch {
                delay(300)
                val child =  binding.tabs.getChild(selectTab);
                child?.performClick()
            }
        }
    }

    override fun initDataObserver() {
        super.initDataObserver()
    }

    override fun initData() {
        super.initData()
        autoHideSplashJob = mViewModel.viewModelScope.launch {
            delay(1200);
            if (!hasShowAppUseProtocolDialog()) {
                return@launch
            }
            binding.splashImage.gone()
        }
        checkForAppUpdate()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHideSplashEvent(event : HideSplashEvent) {
       if (!hasShowAppUseProtocolDialog()) {
          return
       }
       if (event.hidden) {
           if (binding.splashImage.tag != null) {
               return
           }
           binding.splashImage.tag  = "onHideSplashEventTag"
           if (binding.splashImage.isVisible) {
               autoHideSplashJob?.cancel();
               binding.splashImage.fadeOut( duration = 150, {
                   binding.splashImage.gone()
               })
           }
       }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSwitchToMainTabEvent(event : SwitchToMainTabEvent) {
        val child =  binding.tabs.getChild( event.tabIndex);
        child?.performClick()
    }

    var lastTokenExpireTime: Long = 0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTokenExpiredEvent(tokenEvent : TokenExpiredEvent) {
        if (tokenEvent.isExpired) {
            //多次接口请求，可能触发多次事件，2秒内按同一次处理
            if (System.currentTimeMillis() - lastTokenExpireTime < 2000) {
                return
            }
            lastTokenExpireTime = System.currentTimeMillis();

            if (!tokenEvent.isManual) { //非手动退出，自动调用登出接口
                val token = LoginUtils.token();
                if (token.isNotEmpty() && !tokenEvent.isManual) {
                    mViewModel.loginOut(token)
                }
            }

            LoginUtils.clearToken()
            if (!ActivityStackManager.isCurrentActivity(LoginActivity::class.java)) {
                ActivityStackManager.backToSpecifyActivity(MainActivity::class.java)
                launchLoginJob?.cancel()
                launchLoginJob = lifecycleScope.launch {
                    if (selectTab != Constant.HOME) {
                        val homeTab = binding.tabs.getChild(0);
                        homeTab?.performClick()
                    }
                    LoginUtils.toLogin(this@MainActivity)
                }

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserNoPermissionOperationEvent(event : UserNoPermissionOperationEvent) {
        if(event.isChange) {
            mViewModel.getUserInfoWhenNoPermission()
        }
    }
    private var launchLoginJob: Job? = null;
    override fun onDestroy() {
        launchLoginJob?.cancel()
        autoHideSplashJob?.cancel()
        super.onDestroy()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginSuccess(loginEvent: LoginEvent) {
        if (loginEvent.isLogin) {
            if (!NotificationUtils.areNotificationsEnabled()) {
                //requestNotificationPermission()
            }
        }
    }


    private fun initTabs() {
        val tabs:ArrayList<Tab>  = ArrayList();
        tabs.add(Tab(this@MainActivity, "首页", R.mipmap.tab_home).setFocusIcon(this, R.mipmap.tab_home_current));            //使用 setFocusIcon(bitmap/drawable/resId) 来添加选中时的第二套图标
        tabs.add(Tab(this@MainActivity, "订单", R.mipmap.tab_order).setFocusIcon(this, R.mipmap.tab_order_current));            //使用 setFocusIcon(bitmap/drawable/resId) 来添加选中时的第二套图标
        //tabs.add(Tab(this@MainActivity, "通知", R.mipmap.tab_notify).setFocusIcon(this, R.mipmap.tab_notify_current));            //使用 setFocusIcon(bitmap/drawable/resId) 来添加选中时的第二套图标
        tabs.add(Tab(this@MainActivity, "我的", R.mipmap.tab_my).setFocusIcon(this, R.mipmap.tab_my_current));            //使用 setFocusIcon(bitmap/drawable/resId) 来添加选中时的第二套图标
        binding.tabs.setTab(tabs)


        binding.tabs.setOnTabChangeListener(
            object: OnTabChangeListener {
                override fun onTabChanged(v: View?, index: Int): Boolean {
                    if ( //Constant.NOTIFY == index
                        Constant.ORDER == index
                        || Constant.MINE == index) {
                        if (!LoginUtils.hasLogin()) {
                            LoginUtils.toLogin(this@MainActivity);
                            return true;
                        }
                    }
                    selectTab = index;
                    switchFragment(index)
                   return false;
                }
            }
        )
    }

    private fun initBottomNavigation() {
        binding.navView.itemIconTintList = null
        binding.navView.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    switchFragment(Constant.HOME)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_classify -> {
                    switchFragment(Constant.ORDER)
                    return@setOnNavigationItemSelectedListener true
                }
               // R.id.menu_cart -> {
               //     switchFragment(Constant.NOTIFY)
               //     return@setOnNavigationItemSelectedListener true
               // }
                R.id.menu_mine -> {
                    switchFragment(Constant.MINE)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    private fun switchFragment(index: Int) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        // 将当前显示的fragment和上一个需要隐藏的fragment分别加上tag, 并获取出来
        // 给fragment添加tag,这样可以通过findFragmentByTag找到存在的fragment，不会出现重复添加
        mCurrentFragment = fragmentManager.findFragmentByTag(index.toString())
        mLastFragment = fragmentManager.findFragmentByTag(mLastIndex.toString())
        // 如果位置不同
        if (index != mLastIndex) {
            if (mLastFragment != null) {
                transaction.hide(mLastFragment!!)
            }
            if (mCurrentFragment == null) {
                mCurrentFragment = getFragment(index)
                transaction.add(R.id.content, mCurrentFragment!!, index.toString())
            } else {
                transaction.show(mCurrentFragment!!)
            }
        }

        // 如果位置相同或者新启动的应用
        if (index == mLastIndex) {
            if (mCurrentFragment == null) {
                mCurrentFragment = getFragment(index)
                transaction.add(R.id.content, mCurrentFragment!!, index.toString())
            }
        }
        transaction.commitAllowingStateLoss()
        mLastIndex = index
    }

    private fun getFragment(index: Int): Fragment {
        var fragment: Fragment? = mFragmentSparseArray.get(index)
        if (fragment == null) {
            when (index) {
                Constant.HOME -> fragment = HomeFragment.Companion.newInstance("home")
                Constant.ORDER -> fragment = OrderListFragment.Companion.newInstance("order")
                //Constant.NOTIFY -> fragment = NotifyListFragment.Companion.newInstance("notify")
                Constant.MINE -> fragment = MineFragment.Companion.newInstance("mine")
            }

            if (fragment == null) {
                fragment = SettingFragment.Companion.newInstance("1")
            }

            mFragmentSparseArray.put(index, fragment)
        }
        return fragment!!
    }

    fun checkForAppUpdate() {
        mViewModel.getAppUpdateInfo().observe(this) {
            if (it.success) {
                val versionName = AppUtils.getAppVersionName();
                val minVersion = it.data?.minVersion;
                val maxVersion = it.data?.maxVersion;
                if (VersionCompareUtil.isGreatThan(minVersion, versionName)) {
                    showAppForgeUpdateDialog(it.data)
                } else if (VersionCompareUtil.isGreatThan( maxVersion, versionName)) {
                    val ignoreTip = SPUtils.getInstance().getString("ignore_app_update_version", "");
                    if (!TextUtils.equals(ignoreTip,  maxVersion)) {
                        showAppNormalUpdateDialog(it.data)
                    }
                }
            }
        }
    }


}