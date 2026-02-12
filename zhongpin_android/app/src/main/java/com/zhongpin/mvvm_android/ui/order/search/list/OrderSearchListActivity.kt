package com.zhongpin.mvvm_android.ui.order.search.list

import android.app.Activity
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
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityOrderSearchListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.OrderSearchQuery
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.common.autoGoFeedbackWhenDoneConfirmReceiveAction
import com.zhongpin.mvvm_android.ui.common.goOrderDetailActivity
import com.zhongpin.mvvm_android.ui.order.search.OrderSearchQueryActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class OrderSearchListActivity : BaseVMActivity<OrderSearchListViewModel>() {


    private lateinit var mBinding: ActivityOrderSearchListBinding;

    private var searchQuery: OrderSearchQuery? = null;

    private var mDatas:MutableList<OrderItem> = mutableListOf()
    private lateinit var listAdapter: OrderSearchListAdapter
    private var mPageMoreNo:Int = 2

    val filterQuery = hashMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query", OrderSearchQuery::class.java)
        }
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityOrderSearchListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        registerDefaultLoad(mBinding.refreshLayout, Constant.COMMON_KEY)
        mBinding.ivBack.setOnClickListener {
            finish()
        }
        showSearchQuery()
        val startSearchQueryLauncher: ActivityResultLauncher<OrderSearchQuery?> = registerForActivityResult(
            object  : ActivityResultContract<OrderSearchQuery?,  OrderSearchQuery?>() {
                override fun createIntent(context: Context, input: OrderSearchQuery?): Intent {
                    val intent = Intent(this@OrderSearchListActivity, OrderSearchQueryActivity::class.java)
                    intent.putExtra("query", searchQuery)
                    return  intent
                }

                override fun parseResult(resultCode: Int, intent: Intent?): OrderSearchQuery? {
                    var query: OrderSearchQuery?  = null;
                    if (intent != null && resultCode == Activity.RESULT_OK) {
                        query = IntentUtils.getSerializableExtra(intent,"query", OrderSearchQuery::class.java);
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

        mBinding.searchBarContainer.setOnClickListener {
            startSearchQueryLauncher.launch(searchQuery)
        }

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getFirstPageOrderList(filterQuery)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getOrderListMore(mPageMoreNo, filterQuery)
        }

        listAdapter = OrderSearchListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
            adapter, view, position ->
            val orderListItem = mDatas.get(position)
            goOrderDetailActivity(orderListItem)
        }
        listAdapter.isStateViewEnable = true;
        listAdapter.setStateViewLayout(this@OrderSearchListActivity, R.layout.empty_view_search_order_empty)


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
    }

    override fun initData() {
        super.initData()
        mViewModel.getFirstPageOrderList(filterQuery)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderItemInfoChangeEvent(event : OrderItemInfoChangeEvent){
        if (event.change) {
            mViewModel.getFirstPageOrderList(filterQuery)
        }
    }

    fun showSearchQuery() {
        if(!TextUtils.isEmpty(searchQuery?.inputText)) {
            searchQuery?.inputText?.let {
                filterQuery.put("queryString", it)
            }
        } else {
            filterQuery.remove("queryString")
        }
        val query = searchQuery?.toQuery();
        if (query != null && query.isNotEmpty()) {
            mBinding.searchQuery.text = query
        } else {
            mBinding.searchQuery.text = "查询全部订单"
        }
    }

    fun searchOrderByQuery() {
        showSearchQuery()
        mViewModel.getFirstPageOrderList(filterQuery)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    /**
     * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3240503/detail/55987259?spaceKey=space-64stfp
     * */
    fun confirmReceiptOrder(item: OrderItem){
        showLoadingDialogV2()
        val id  = item.id ?: 0
        mViewModel.confirmOrderReceiveDone(id).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                autoGoFeedbackWhenDoneConfirmReceiveAction?.invoke()
            } else {
                autoGoFeedbackWhenDoneConfirmReceiveAction = null;
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }


}