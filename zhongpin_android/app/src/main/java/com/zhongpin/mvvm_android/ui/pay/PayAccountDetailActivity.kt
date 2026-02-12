package com.zhongpin.mvvm_android.ui.pay

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityPayAccountDetailBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.OrderFilterQueryChangeEvent
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.PayItemFilterQuery
import com.zhongpin.mvvm_android.bean.PayItemFilterQueryChangeEvent
import com.zhongpin.mvvm_android.bean.PayItemInfoChangeEvent
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goChargeInputActivity
import com.zhongpin.mvvm_android.ui.common.goChargePayActivity
import com.zhongpin.mvvm_android.ui.common.goPayRecordDetailActivity
import com.zhongpin.mvvm_android.ui.common.showOrderFilterDialog
import com.zhongpin.mvvm_android.ui.common.showPayItemFilterDialog
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class PayAccountDetailActivity : BaseVMActivity<PayAccountDetailViewModel>() {


    private var mDatas:MutableList<PayItem> = mutableListOf()
    private lateinit var listAdapter: PayItemListAdapter
    private var mPageMoreNo:Int = 2

    val filterQuery = hashMapOf<String, Any>()
    private lateinit var mBinding: ActivityPayAccountDetailBinding;

    private var payItemFilterQuery = PayItemFilterQuery();

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPayAccountDetailBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.payAccountDetail.setOnClickListener {
            val payItem = mViewModel.mWaitPayData.value?.data?.firstOrNull()
            if (payItem != null) {
                doPayTip()
            }else {
                goChargeInputActivity()
            }
        }

        mBinding.waitForPayContainer.setOnClickListener {
            val payItem = mViewModel.mWaitPayData.value?.data?.firstOrNull()
            payItem?.let {
                payItem.expireTime = mBinding.waitForTime.getRemainTime()
                goChargePayActivity(payItem)
            }
        }

        mBinding.orderFilter.setOnClickListener {
            showPayItemFilterDialog(payItemFilterQuery)
        }

        mBinding.orderFilterQuery.setOnClickListener {
            showPayItemFilterDialog(payItemFilterQuery)
        }

        showQueryFilter(payItemFilterQuery)

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
             doRequestData();
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getPayListMore(mPageMoreNo, filterQuery)
        }

        listAdapter = PayItemListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
                adapter, view, position ->
            val payItem = mDatas.get(position)
            goPayRecordDetailActivity(payItem)
        }
        listAdapter.isStateViewEnable = true;
        listAdapter.setStateViewLayout(this@PayAccountDetailActivity, R.layout.empty_view_search_order_empty)

        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = listAdapter

        registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)


    }

    override fun initDataObserver() {
        super.initDataObserver()

        mViewModel.mFirstPageData.observe(this) {
            if(it.success) {
                mPageMoreNo = 2
                val records = it.data?.records
                if (records.isNullOrEmpty()) {
                    showSuccess(Constant.COMMON_KEY)
                    mDatas.clear()
                } else {
                    showSuccess(Constant.COMMON_KEY)
                    mDatas.clear()
                    mDatas.addAll(records)
                }
                listAdapter.notifyDataSetChanged()
            }
            mBinding.refreshLayout.finishRefresh()
        }
        mViewModel.mMorePageData.observe(this) {
            if(it.success) {
                mPageMoreNo++
                val records = it.data?.records
                if (!records.isNullOrEmpty()) {
                    mDatas.addAll(records)
                    mBinding.refreshLayout.finishLoadMore()
                    listAdapter.notifyDataSetChanged()
                } else {
                    mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
                }
            } else {
                mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
            }
        }

        mViewModel.mPayAccountBalanceData.observe(this) {
            if (it.success) {
                mBinding.payAccountDetail.visible()
                mBinding.remainAmount.text = PingDanAppUtils.showAmount(it?.data?.amount ?: "0.00")
                mBinding.companyName.text = UserInfoUtil.companyInfo?.companyName ?: ""
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }

        mViewModel.mWaitPayData.observe(this) {
            if (it.success) {
                if (it.data.isNullOrEmpty()) {
                    mBinding.waitForPayContainer.gone()
                } else {
                    mBinding.waitForPayContainer.visible()
                    val payItem = it.data?.firstOrNull()
                    payItem?.let {
                        mBinding.waitForTime.setPrefix("剩余时间：")
                        mBinding.waitForTime.setRemainTime(payItem.expireTime ?: 0L, {
                            EventBusUtils.postEvent(PayItemInfoChangeEvent(true));
                        })
                        SpanUtils.with(mBinding.waitPayAmount)
                            .append("￥")
                            .append(payItem.amount ?: "")
                            .create()
                    }
                }
            } else {
                mBinding.waitForPayContainer.gone()
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    override fun initData() {
        super.initData()
        doRequestData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPayItemInfoChangeEvent(event : PayItemInfoChangeEvent){
        if (event.change) {
            doRequestData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPayItemFilterQueryChangeEvent(event : PayItemFilterQueryChangeEvent){
        payItemFilterQuery = event.query
        showQueryFilter(event.query);
        doRequestData()
    }


    private var mLoadingDialog: LoadingDialog? = null
    /**
     * show 加载中
     */
    fun showLoadingDialog() {
        dismissLoadingDialog()
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this, false)
        }
        mLoadingDialog?.showDialogV2(this)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissLoadingDialog() {
        mLoadingDialog?.dismissDialogV2()
        mLoadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun showQueryFilter(query: PayItemFilterQuery) {
        if (TextUtils.isEmpty(query.buildSearchDesc())) {
            mBinding.orderFilter.visibility = View.VISIBLE
            mBinding.orderFilterQuery.visibility = View.GONE
        } else {
            mBinding.orderFilter.visibility = View.GONE
            mBinding.orderFilterQuery.visibility = View.VISIBLE
            mBinding.orderFilterText.text = query.buildSearchDesc();
        }
    }

    private fun doRequestData() {
        if (!TextUtils.isEmpty(payItemFilterQuery.startTime)
            && !TextUtils.isEmpty(payItemFilterQuery.endTime)) {
            filterQuery.put("startTime", payItemFilterQuery.startTime ?: "")
            filterQuery.put("endTime", payItemFilterQuery.endTime ?: "")
        } else {
            filterQuery.remove("startTime")
            filterQuery.remove("endTime")
        }
        //receiveType 收支类型(0:收入,1:支出)
        //tranType 交易类型(0:预付款充值,1:订单支付,2:取消订单退款)
        if ("充值".equals(payItemFilterQuery.payType)) {
            filterQuery.remove("receiveType")
            filterQuery.put("tranType", 0);
        } else if ("退款".equals(payItemFilterQuery.payType)) {
            filterQuery.remove("receiveType")
            filterQuery.put("tranType", 2);
        } else if ("支出".equals(payItemFilterQuery.payType)) {
            filterQuery.put("receiveType", 1)
            filterQuery.remove("tranType")
        } else {
            filterQuery.remove("receiveType")
            filterQuery.remove("tranType")
        }


        mViewModel.getFirstPagePayList(filterQuery)
        mViewModel.getPayAccountBalance()
        mViewModel.getWaitPayList()
    }


    private fun doPayTip() {
        val dialog = ConfirmDialog(
            mContext = this,
            title = "充值提示",
            message = HtmlCompat.fromHtml("<br/>当前账户存在未完成支付的充值记录，请先完成支付或取消支付后再进行充值<br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
            cancelText = "取消",
            confirmText = "去支付",
            onConfirm = {
                val payItem = mViewModel.mWaitPayData.value?.data?.firstOrNull()
                payItem?.let {
                    goChargePayActivity(payItem);
                }
            }
        );
        dialog.showDialog(this)
    }

}