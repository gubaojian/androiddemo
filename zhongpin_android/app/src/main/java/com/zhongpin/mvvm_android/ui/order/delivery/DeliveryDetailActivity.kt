package com.zhongpin.mvvm_android.ui.order.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.databinding.ActivityOrderDeliveryDetailBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.FeedbackChangeEvent
import com.zhongpin.mvvm_android.bean.OrderDeliveryProofItem
import com.zhongpin.mvvm_android.bean.OrderDetailItem
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import com.zhongpin.mvvm_android.ui.view.ext.setWaBg
import com.zhongpin.mvvm_android.ui.view.ext.setWaText
import com.zhongpin.mvvm_android.ui.view.ext.setWaTextColor
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class DeliveryDetailActivity : BaseVMActivity<DeliveryDetailViewModel>() {


    private lateinit var mBinding: ActivityOrderDeliveryDetailBinding;

    private var mDatas:MutableList<OrderDeliveryProofItem> = mutableListOf()
    private lateinit var listAdapter: DeliveryDetailListAdapter
    private var mPageMoreNo:Int = 2

    private var orderId: Long = 0L
    private var orderDetailItem: OrderDetailItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            orderId = intent.getLongExtra("orderId", 0L);
            if (orderId >= 0L) {
                orderDetailItem = ShareParamDataUtils.getParams<OrderDetailItem>("orderDetailItem")
                if (orderDetailItem != null) {
                    ShareParamDataUtils.clearParams()
                }
            }
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityOrderDeliveryDetailBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getFirstPageOrderDeliveryProfList(orderId ?: 0)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getOrderDeliveryProfListMore(mPageMoreNo, orderId ?: 0)
        }

        listAdapter = DeliveryDetailListAdapter(this, mDatas)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = listAdapter

        registerDefaultLoad(mBinding.refreshLayout, Constant.COMMON_KEY)
    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mFirstPageData.observe(this) {
            if(it.success) {
                mPageMoreNo = 2
                val records = it.data?.records
                if (records.isNullOrEmpty()) {
                    showEmpty(Constant.COMMON_KEY)
                    mDatas.clear()
                } else {
                   showSuccess(Constant.COMMON_KEY)
                   mDatas.clear()
                   mDatas.addAll(records)
                }
                showPageData()
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
    }

    override fun initData() {
        super.initData()
        mViewModel.getFirstPageOrderDeliveryProfList(orderId ?: 0)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : FeedbackChangeEvent){
        if (infoEvent.isFeedbackSuccess) {
            mViewModel.getFirstPageOrderDeliveryProfList(orderId ?: 0)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
    }

    fun showPageData() {
        orderDetailItem?.let { item ->
            mBinding.apply {
                hasSignAmount.text = "${item.signNum}张"
                hasStoreAmount.text = "${item.receiptNum ?: 0}张"
            }
        }
    }

}