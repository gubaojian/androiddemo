package com.zhongpin.mvvm_android.ui.feedback.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.databinding.ActivityFeedbackListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.FeedbackSearchQuery
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.feedback.detail.FeedbackDetailActivity
import com.zhongpin.mvvm_android.ui.feedback.list.search.FeedbackSearchQueryActivity
import com.zhongpin.mvvm_android.ui.mine.company.detail.CompanyDetailActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class FeedbackListActivity : BaseVMActivity<FeedbackListViewModel>() {


    private lateinit var mBinding: ActivityFeedbackListBinding;

    private var searchQuery: FeedbackSearchQuery? = null;

    private var mDatas:MutableList<CompanyListItemResponse> = mutableListOf()
    private lateinit var listAdapter: FeedbackListListAdapter
    private var mPageMoreNo:Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query", FeedbackSearchQuery::class.java)
        }
        StatusBarUtil.immersive(this)
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityFeedbackListBinding.inflate(layoutInflater, container, false)
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
        showSearchQuery()
        val startSearchQueryLauncher: ActivityResultLauncher<FeedbackSearchQuery?> = registerForActivityResult(
            object  : ActivityResultContract<FeedbackSearchQuery?,  FeedbackSearchQuery?>() {
                override fun createIntent(context: Context, input: FeedbackSearchQuery?): Intent {
                    val intent = Intent(this@FeedbackListActivity, FeedbackSearchQueryActivity::class.java)
                    intent.putExtra("query", searchQuery)
                    return  intent
                }

                override fun parseResult(resultCode: Int, intent: Intent?): FeedbackSearchQuery? {
                    var query: FeedbackSearchQuery?  = null;
                    if (intent != null && resultCode == Activity.RESULT_OK) {
                        query = IntentUtils.getSerializableExtra(intent,"query", FeedbackSearchQuery::class.java);
                    }
                    return  query;
                }

            },
            ActivityResultCallback {
                if (it != null) {
                    searchQuery = it;
                    searchOrderByQuery();
                }
            }
        )

        mBinding.searchBar.setOnClickListener {
            startSearchQueryLauncher.launch(searchQuery)
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

        listAdapter = FeedbackListListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
            adapter, view, position ->
            val feedbackListItem = mDatas.get(position)
            val intent = Intent(this@FeedbackListActivity, FeedbackDetailActivity::class.java)
            intent.putExtra("feedbackListItem", feedbackListItem)
            startActivity(intent)
        }
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

    fun showSearchQuery() {
        val query = searchQuery?.toQuery();
        if (query != null && query.isNotEmpty()) {
            mBinding.searchQuery.text = query
        } else {
            mBinding.searchQuery.text = "查询记录"
        }
    }

    fun searchOrderByQuery() {
        showSearchQuery()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}