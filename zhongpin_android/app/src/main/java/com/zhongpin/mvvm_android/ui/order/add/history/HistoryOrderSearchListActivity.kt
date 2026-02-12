package com.zhongpin.mvvm_android.ui.order.add.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import com.zhilianshidai.pindan.app.databinding.ActivityHistoryOrderSearchListBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.ChooseSelectHistoryOrderItemEvent
import com.zhongpin.mvvm_android.bean.SelectHistoryOrderItem
import com.zhongpin.mvvm_android.bean.SelectHistoryOrderSearchQuery
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.common.BoxConfigData
import com.zhongpin.mvvm_android.ui.order.add.history.search.HistoryOrderSearchQueryActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils


class HistoryOrderSearchListActivity : BaseVMActivity<HistoryOrderSearchListViewModel>() {


    private lateinit var mBinding: ActivityHistoryOrderSearchListBinding;

    private var searchQuery: SelectHistoryOrderSearchQuery? = null;

    private var mDatas:MutableList<SelectHistoryOrderItem> = mutableListOf()
    private lateinit var mListAdapter: HistoryOrderSearchListAdapter
    private var mPageMoreNo:Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query", SelectHistoryOrderSearchQuery::class.java)
        }
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityHistoryOrderSearchListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        registerDefaultLoad(mBinding.body, Constant.COMMON_KEY)
        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit();
        }

        showSearchQuery()
        val startSearchQueryLauncher: ActivityResultLauncher<SelectHistoryOrderSearchQuery?> = registerForActivityResult(
            object  : ActivityResultContract<SelectHistoryOrderSearchQuery?,  SelectHistoryOrderSearchQuery?>() {
                override fun createIntent(context: Context, input: SelectHistoryOrderSearchQuery?): Intent {
                    val intent = Intent(this@HistoryOrderSearchListActivity, HistoryOrderSearchQueryActivity::class.java)
                    intent.putExtra("query", searchQuery)
                    return  intent
                }

                override fun parseResult(resultCode: Int, intent: Intent?): SelectHistoryOrderSearchQuery? {
                    var query: SelectHistoryOrderSearchQuery?  = null;
                    if (intent != null && resultCode == RESULT_OK) {
                        query = IntentUtils.getSerializableExtra(intent,"query", SelectHistoryOrderSearchQuery::class.java);
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
            mViewModel.getFirstPageCompanyList(buildQuery())
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getCompanyListMore(mPageMoreNo, buildQuery())
        }

        mListAdapter = HistoryOrderSearchListAdapter(this, mDatas)
        mListAdapter.selectPosition = 0
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = mListAdapter
    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mFirstPageData.observe(this) {
            if(it.success) {
                mPageMoreNo = 2
                mDatas.clear()
                val records = it.data?.records
                if (records.isNullOrEmpty()) {
                    showEmpty(Constant.COMMON_KEY)
                } else {
                   showSuccess(Constant.COMMON_KEY)
                   mDatas.addAll(records)
                }
                mListAdapter.notifyDataSetChanged()
            } else {
                showError(it.msg, Constant.COMMON_KEY)
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
                    mListAdapter.notifyDataSetChanged()
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
        mViewModel.getFirstPageCompanyList(buildQuery())
    }


    fun showSearchQuery() {
        val query = searchQuery?.toQuery();
        if (query != null && query.isNotEmpty()) {
            mBinding.searchQuery.text = query
        } else {
            mBinding.searchQuery.text = "查询订单"
        }
    }

    fun buildQuery():Map<String, Any> {
        val parameters = hashMapOf<String, Any>()
        if (!TextUtils.isEmpty(searchQuery?.platCode)) {
            parameters["platCode"] = searchQuery?.platCode ?: ""
        }
        if (!TextUtils.isEmpty(searchQuery?.layerCode)) {
            parameters["lenType"] = searchQuery?.layerCode ?: ""
        }
        if (!TextUtils.isEmpty(searchQuery?.lineType)) {
            if (BoxConfigData.noneLineDesc.equals(searchQuery?.lineType)) {
                parameters["line"] = 0
            } else {
                parameters["line"] = 1
            }
        }
        if (!TextUtils.isEmpty(searchQuery?.paperSize)) {
            val paperSize = searchQuery?.paperSize ?: ""
            parameters["size"] = paperSize
        }
        if (!TextUtils.isEmpty(searchQuery?.lineType)) {
            parameters["lineType"] = searchQuery?.lineType ?: ""
        }
        if (!TextUtils.isEmpty(searchQuery?.lineDesc)) {
            parameters["touch"] = searchQuery?.lineDesc ?: "";
        }
        return  parameters
    }

    fun searchOrderByQuery() {
        showSearchQuery()
        showLoading(Constant.COMMON_KEY)
        mListAdapter.selectPosition = 0
        mViewModel.getFirstPageCompanyList(buildQuery())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun checkAndSubmit() {
        if (mListAdapter.selectPosition < 0) {
            Toast.makeText(applicationContext, "请选择历史订单", Toast.LENGTH_LONG).show()
            return
        }
        if (mDatas.isEmpty()) {
            Toast.makeText(applicationContext, "请手动填写材质代码", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        val historyOrderItem = mDatas[mListAdapter.selectPosition]
        EventBusUtils.postEvent(ChooseSelectHistoryOrderItemEvent(historyOrderItem))
        val intent = Intent();
        intent.putExtra("item", historyOrderItem);
        setResult(RESULT_OK, intent);
        finish()
    }

}