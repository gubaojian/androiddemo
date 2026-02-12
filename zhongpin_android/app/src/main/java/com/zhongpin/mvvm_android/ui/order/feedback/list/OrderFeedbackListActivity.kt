package com.zhongpin.mvvm_android.ui.order.feedback.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.databinding.ActivityOrderFeedbackListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.FeedbackChangeEvent
import com.zhongpin.mvvm_android.bean.OrderFeedbackItem
import com.zhongpin.mvvm_android.bean.FeedbackSearchQuery
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.feedback.detail.FeedbackDetailActivity
import com.zhongpin.mvvm_android.ui.feedback.list.search.FeedbackSearchQueryActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class OrderFeedbackListActivity : BaseVMActivity<OrderFeedbackListViewModel>() {


    private lateinit var mBinding: ActivityOrderFeedbackListBinding;

    private var searchQuery: FeedbackSearchQuery? = null;

    private var mDatas:MutableList<OrderFeedbackItem> = mutableListOf()
    private lateinit var listAdapter: OrderFeedbackListListAdapter
    private var mPageMoreNo:Int = 2

    private var orderId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query", FeedbackSearchQuery::class.java)
            orderId = intent.getLongExtra("orderId", 0);
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityOrderFeedbackListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        StatusBarUtil.setMargin(this, mBinding.content)
        registerDefaultLoad(mBinding.refreshLayout, Constant.COMMON_KEY)
        mBinding.ivBack.setOnClickListener {
            finish()
        }


        mViewModel.query.put("orderId", orderId ?: 0)

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getFirstPageOrderFeedbackList()
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getOrderFeedbackListMore(mPageMoreNo)
        }

        listAdapter = OrderFeedbackListListAdapter(this, mDatas)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = listAdapter
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
        mViewModel.getFirstPageOrderFeedbackList()
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
    fun onFeedbackChangeEvent(event : FeedbackChangeEvent){
        if (event.isFeedbackSuccess) {
            mViewModel.getFirstPageOrderFeedbackList()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    fun cancelFeedback(item: OrderFeedbackItem) {
        showLoadingDialogV2()
        val id  = item.id ?: 0
        mViewModel.cancelOrderFeedback(id).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                EventBusUtils.postEvent(FeedbackChangeEvent(true))
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }

}