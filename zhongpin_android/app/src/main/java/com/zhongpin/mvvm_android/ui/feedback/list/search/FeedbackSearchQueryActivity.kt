package com.zhongpin.mvvm_android.ui.feedback.list.search

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityFeedbackSearchQueryBinding
import com.zhilianshidai.pindan.app.databinding.ActivityOrderSearchQueryBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.FeedbackSearchQuery
import com.zhongpin.mvvm_android.bean.OrderSearchQuery
import com.zhongpin.mvvm_android.ui.feedback.list.FeedbackListActivity
import com.zhongpin.mvvm_android.ui.order.search.list.OrderSearchListActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class FeedbackSearchQueryActivity : BaseVMActivity<FeedbackSearchQueryViewModel>() {


    private lateinit var mBinding: ActivityFeedbackSearchQueryBinding;

    private var searchQuery: FeedbackSearchQuery? = null;
    private var mSelectDate:String? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query", FeedbackSearchQuery::class.java)
        }
        if (searchQuery != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
            }
        }
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityFeedbackSearchQueryBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.submitButton.setOnClickListener {
            searchOrder();
        }

        mBinding.chooseDateAreaText.setOnClickListener {
            val picker: DatePicker = DatePicker(this@FeedbackSearchQueryActivity)
            picker.setOnDatePickedListener { year, month, day ->
                val date = String.format("%d%02d%02d", year, month, day)
                mSelectDate = date;
                mBinding.chooseDateAreaText.setText(date)
            }
            picker.show();
        }

        searchQuery?.let {
            mBinding.orderNo.setText(it.orderNo ?: "");
            mBinding.zhiDaiHao.setText(it.zhiDaiHao ?: "");
            mBinding.lengXing.setText(it.lengXing ?: "");
            mBinding.yaXian.setText(it.yaXian ?: "");
            mBinding.xianXing.setText(it.xianXing ?: "");
            mBinding.zhibanFactoryName.setText(it.zhiBanFactorName ?: "");
            mBinding.zhiXiangFactoryName.setText(it.zhiXiangFactorName ?: "");
            mBinding.chooseDateAreaText.setText(it.orderJiaoHuoDate ?: "")
        }

    }

    fun searchOrder() {
        var orderNo:String? = mBinding.orderNo.text.trim().toString();
        var zhiDaiHao:String? = mBinding.zhiDaiHao.text.trim().toString();
        var lengXing:String? = mBinding.lengXing.text.trim().toString();
        var yaXian:String? = mBinding.yaXian.text.trim().toString();
        var xianXing:String? = mBinding.xianXing.text.trim().toString();
        var zhiBanFactorName:String? = mBinding.zhibanFactoryName.text.trim().toString();
        var zhiXiangFactorName:String? = mBinding.zhiXiangFactoryName.text.trim().toString();

        val query = FeedbackSearchQuery(
            orderNo = orderNo,
            zhiDaiHao = zhiDaiHao,
            lengXing = lengXing,
            yaXian = yaXian,
            xianXing = xianXing,
            zhiBanFactorName = zhiBanFactorName,
            zhiXiangFactorName = zhiXiangFactorName,
            orderJiaoHuoDate = mSelectDate
        );

        val intent = Intent();
        intent.putExtra("query", query);
        setResult(RESULT_OK, intent);
        finish()
    }

    override fun initDataObserver() {
        super.initDataObserver()
    }

    override fun initData() {
        super.initData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : CompanyInfoChangeEvent){

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}