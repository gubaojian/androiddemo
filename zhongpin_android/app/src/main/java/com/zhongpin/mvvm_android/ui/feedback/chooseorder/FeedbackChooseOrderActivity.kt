package com.zhongpin.mvvm_android.ui.feedback.chooseorder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.databinding.ActivityFeedbackChooseOrderBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.FeedbackChangeEvent
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.feedback.PingZhiFeedbackActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class FeedbackChooseOrderActivity : BaseVMActivity<FeedbackChooseOrderViewModel>() {


    private lateinit var mBinding: ActivityFeedbackChooseOrderBinding;


    private var mDatas:MutableList<CompanyListItemResponse> = mutableListOf()
    private lateinit var listAdapter: FeedbackChooseOrderListAdapter
    private var mPageMoreNo:Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityFeedbackChooseOrderBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        registerDefaultLoad(mBinding.refreshLayout, ApiService.COMPANY_LIST)
        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.chooseOrderFeedback.setOnClickListener {
            if (listAdapter.selectPostion < 0) {
                Toast.makeText(applicationContext,"请选择订单", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val intent = Intent(this@FeedbackChooseOrderActivity, PingZhiFeedbackActivity::class.java)
            startActivity(intent)
        }

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getFirstPageCompanyList()
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getCompanyListMore(mPageMoreNo)
        }

        listAdapter = FeedbackChooseOrderListAdapter(this, mDatas)
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
                    showEmpty(ApiService.COMPANY_LIST)
                    mDatas.clear()
                } else {
                   showSuccess(ApiService.COMPANY_LIST)
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
        mViewModel.getFirstPageCompanyList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : CompanyInfoChangeEvent){
        if (infoEvent.isChange) {
            mViewModel.getFirstPageCompanyList()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFeedback(feedbackChangeEvent: FeedbackChangeEvent) {
        finish();
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}