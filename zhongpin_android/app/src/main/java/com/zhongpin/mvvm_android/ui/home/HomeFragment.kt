package com.zhongpin.mvvm_android.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.zxing.client.android.Intents
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.header.ClassicsHeader
import com.youth.banner.indicator.CircleIndicator
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.FragmentHomeBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.HideSplashEvent
import com.zhongpin.mvvm_android.bean.HomeBanner
import com.zhongpin.mvvm_android.bean.HomePageData
import com.zhongpin.mvvm_android.bean.LoginEvent
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.TokenExpiredEvent
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.login.LoginUtils
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.buy.PublishBuyActivity
import com.zhongpin.mvvm_android.ui.common.goAddOrderPage
import com.zhongpin.mvvm_android.ui.common.goMinePriceListActivity
import com.zhongpin.mvvm_android.ui.common.goMyOrderTab
import com.zhongpin.mvvm_android.ui.common.goOrderTabSelectOrderIng
import com.zhongpin.mvvm_android.ui.common.goPayAccountDetailActivity
import com.zhongpin.mvvm_android.ui.common.goPlatformPriceListActivity
import com.zhongpin.mvvm_android.ui.scan.ScanCaptureActivity
import com.zhongpin.mvvm_android.ui.shouhuo.ConfirmShuoHuoActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.zhongpin.mvvm_android.base.view.BaseVMFragment as BaseVMFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@EventBusRegister
class HomeFragment : BaseVMFragment<HomeViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

    private lateinit var mBinding: FragmentHomeBinding;

    private var mData: MutableList<HomeItemEntity> = mutableListOf()
    private lateinit var mHomeListAdapter: HomeListAdapter

    private lateinit var startScanLauncher: ActivityResultLauncher<Void?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(false).fullScreen(true).init()
    }

    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }


    override fun initView() {
        super.initView()
        mViewModel.loadState.observe(this, {
            mBinding.refreshLayout.finishRefresh();
        })

        //StatusBarUtil.setMargin(activity, mBinding.homeTopBanner)

        val topOrderInfoItemBinding = mBinding.topOrderInfoCardItem
        topOrderInfoItemBinding.buyNow.setOnClickListener {
            requireActivity().goAddOrderPage()
        }

        topOrderInfoItemBinding.orderWaitContainer.setOnClickListener {
            requireActivity().goOrderTabSelectOrderIng()
        }


        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(getContext()))
        mBinding.refreshLayout.setOnRefreshListener {
            refreshPageData()
        }

        mHomeListAdapter = HomeListAdapter(this@HomeFragment, mData)
        mBinding.homeRecyclerView.layoutManager = LinearLayoutManager(activity)
        mBinding.homeRecyclerView.adapter = mHomeListAdapter

        startScanLauncher = registerForActivityResult(
            object  : ActivityResultContract<Void?, String?>() {
                override fun createIntent(context: Context, input: Void?): Intent {
                    val intent = Intent(mActivity, ScanCaptureActivity::class.java)
                    return  intent
                }

                override fun parseResult(resultCode: Int, intent: Intent?): String? {
                    var qr: String?  = null;
                    if (intent != null && resultCode == Activity.RESULT_OK) {
                        qr = intent.getStringExtra(Intents.Scan.RESULT)
                    }
                    return qr;
                }

            },
            ActivityResultCallback {
                if (it != null) {
                    handleScanQrCode(it);
                }
            }
        )

        configTopEntries()

        registerDefaultLoad(mBinding.homeContent, Constant.COMMON_KEY)

    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mPageData.observe(viewLifecycleOwner, Observer {
            mBinding.refreshLayout.finishRefresh();
            if (it.success) {
                showSuccess(Constant.COMMON_KEY)
            }
            configHome(it.data);
            EventBusUtils.postEvent(HideSplashEvent(true))
        })

    }

    override fun initData() {
        super.initData()
        mViewModel.getPageData()
    }

    private fun refreshPageData() {
        mViewModel.getPageData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(loginEvent : LoginEvent){
        if (loginEvent.isLogin) {
            mViewModel.getPageData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTokenExpiredEvent(tokenEvent : TokenExpiredEvent) {
        if (tokenEvent.isExpired) {
            lifecycleScope.launch {
                delay(60)
                mViewModel.getPageData()
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderItemInfoChangeEvent(infoEvent : OrderItemInfoChangeEvent){
        if (infoEvent.change) {
            mViewModel.getPageData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCompanyInfoChangeEvent(infoEvent : CompanyInfoChangeEvent){
        if (infoEvent.isChange) {
            mViewModel.getPageData()
        }
    }


    private fun configHome(homeData: HomePageData?) {
        if(homeData?.isLogin == true) {
            configHomeLogin(homeData)
        } else {
            configHomeUnLogin(homeData)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun configHomeLogin(homeData: HomePageData?) {
        val loginTipBinding = mBinding.loginTipLayout
        loginTipBinding.root.gone()
        configBanner(homeData)
        configTopOrderInfoItemLogin(homeData)
        mData.clear()

        //认证状态
        if (!UserInfoUtil.hasCompanyVerified()) {
            if (UserInfoUtil.companyInfo == null) {
                mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.COMPANY_VERIFY_NEED))
            } else {
                mData.add(HomeItemEntity(data = UserInfoUtil.companyInfo, type = HomeListAdapter.Companion.COMPANY_VERIFY_ING))
            }
        }

        //专属报价
        var hasMinePrice = false;
        homeData?.minePrice?.data?.records?.let {
            if (it.isNotEmpty()) {
                mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.MINE_PRICE_TITLE))
                it.forEach { item ->
                    mData.add(HomeItemEntity(data = item, type = HomeListAdapter.Companion.MINE_PRICE_ITEM))
                }
                hasMinePrice = true;
            }
        }
        if (!hasMinePrice) {
            mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.MINE_PRICE_TITLE))
            mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.MINE_PRICE_ITEM_EMPTY))
        }

        homeData?.platformPrice?.data?.records?.let {
           if (it.isNotEmpty()) {
               mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.PLATFORM_PRICE_TITLE))
               mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.PLATFORM_PRICE_ITEM_TITLE))
               var index = 0;
               val maxCount = 7;
               it.forEach { item ->
                   if (index % 2 == 1) {
                       item.cellItemCellColor = "#F5F6FA";
                   } else {
                       item.cellItemCellColor = "#FFFFFF";
                   }
                   index++
                   item.cellListShowIsLastItem = (item == it.last()) || index == maxCount
                   if (index <= maxCount) {
                       mData.add(HomeItemEntity(data = item, type = HomeListAdapter.Companion.PLATFORM_PRICE_ITEM_ITEM))
                   }
               }
           }
        }



       mHomeListAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun configHomeUnLogin(homeData: HomePageData?) {
         val loginTipBinding = mBinding.loginTipLayout
         loginTipBinding.root.visible()
         loginTipBinding.root.setOnClickListener {
             LoginUtils.toLogin(requireActivity())
         }

         loginTipBinding.homeTipLoginNow.setOnClickListener {
            LoginUtils.toLogin(requireActivity())
         }

         configBanner(homeData)
         configTopOrderInfoItemUnLogin()
         mData.clear()
         mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.MINE_PRICE_TITLE))
         mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.MINE_PRICE_UN_LOGIN))


        homeData?.platformPrice?.data?.records?.let {
            if (it.isNotEmpty()) {
                mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.PLATFORM_PRICE_TITLE))
                mData.add(HomeItemEntity(data = null, type = HomeListAdapter.Companion.PLATFORM_PRICE_ITEM_TITLE))
                var index = 0;
                val maxCount = 7;
                it.forEach { item ->
                    if (index % 2 == 1) {
                        item.cellItemCellColor = "#F5F6FA";
                    } else {
                        item.cellItemCellColor = "#FFFFFF";
                    }
                    index++
                    item.cellListShowIsLastItem = (item == it.last()) || index == maxCount
                    if (index <= maxCount) {
                        mData.add(HomeItemEntity(data = item, type = HomeListAdapter.Companion.PLATFORM_PRICE_ITEM_ITEM))
                    }
                }
            }
        }
        mHomeListAdapter.notifyDataSetChanged()
    }

    private fun configBanner(homeData: HomePageData?) {
        val imageUrls: MutableList<HomeBanner> = mutableListOf();
        imageUrls.add(HomeBanner(resId = R.mipmap.home_banner_one))

        mBinding.banner.setIndicator(CircleIndicator(mActivity))
        val config = mBinding.banner.indicatorConfig
        config.margins.bottomMargin = (70 * (ScreenUtils.getScreenWidth()/375.0f)).toInt()
        mBinding.banner.setIndicatorMargins(config.margins)
        mBinding.banner.setAdapter(BannerImageAdapter(imageUrls), true);
    }

    private fun configTopEntries() {
        val topEntriesBinding = mBinding.topEntriesItem
        topEntriesBinding.homeEntryMinePrice.setOnClickListener {
            requireActivity().goMinePriceListActivity()
        }
        topEntriesBinding.homeEntryMaterialPrice.setOnClickListener {
            requireActivity().goPlatformPriceListActivity()
        }

        topEntriesBinding.homeEntryMyOrder.setOnClickListener {
            requireActivity().goMyOrderTab();
        }

        topEntriesBinding.homeEntryMyPayAccount.setOnClickListener {
            requireActivity().goPayAccountDetailActivity()
        }
    }

    private fun configTopOrderInfoItemLogin(homeData: HomePageData?) {
        val params = mBinding.topCardItemContainer.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin =   (240 * (ScreenUtils.getScreenWidth()/375.0f)).toInt()
        mBinding.topCardItemContainer.layoutParams = params
        val topCardItemBinding = mBinding.topOrderInfoCardItem
        SpanUtils.with( topCardItemBinding.waitForReceiveOrderNum)
            .append(homeData?.statisticsData?.data?.orderCount ?: "0")
            .setFontSize(24, true)
            .append("笔")
            .setFontSize(14, true)
            .create()
        val goodsCount = (homeData?.statisticsData?.data?.goodsCount ?: "0").trim();
        if (goodsCount.length < 5) {//超过万件一下展示万件数
            SpanUtils.with( topCardItemBinding.waitForReceivePaperNum)
                .append(goodsCount)
                .setFontSize(24, true)
                .append("件")
                .setFontSize(14, true)
                .create()
        } else {
            val goodsCountWan = (goodsCount.toLongOrNull() ?: 0)/10000;
            SpanUtils.with( topCardItemBinding.waitForReceivePaperNum)
                .append(goodsCountWan.toString())
                .setFontSize(24, true)
                .append("万+")
                .setFontSize(14, true)
                .create()
        }

    }
    private fun configTopOrderInfoItemUnLogin() {
        val params = mBinding.topCardItemContainer.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin =   (240 * (ScreenUtils.getScreenWidth()/375.0f)).toInt()
        mBinding.topCardItemContainer.layoutParams = params
        val topCardItemBinding = mBinding.topOrderInfoCardItem
        SpanUtils.with( topCardItemBinding.waitForReceiveOrderNum)
            .append("0")
            .setFontSize(24, true)
            .append("笔")
            .setFontSize(14, true)
            .create()
        SpanUtils.with( topCardItemBinding.waitForReceivePaperNum)
            .append("0")
            .setFontSize(24, true)
            .append("件")
            .setFontSize(14, true)
            .create()
    }

    fun onClickScan() {
        LoginUtils.ensureLogin(activity) {
            startScanLauncher.launch(null);
        }
    }

    fun onClickFaBu() {
        LoginUtils.ensureLogin(activity) {
            val intent = Intent(activity, PublishBuyActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleScanQrCode(qr: String) {
        Toast.makeText(requireActivity().applicationContext,"扫描结果," + qr, Toast.LENGTH_LONG).show()
        val intent = Intent(activity, ConfirmShuoHuoActivity::class.java)
        startActivity(intent)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}