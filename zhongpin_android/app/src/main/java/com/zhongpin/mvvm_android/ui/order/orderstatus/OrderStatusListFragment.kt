package com.zhongpin.mvvm_android.ui.order.orderstatus

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.FragmentOrderStatusListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.bean.LoginEvent
import com.zhongpin.mvvm_android.bean.OrderFilterQuery
import com.zhongpin.mvvm_android.bean.OrderFilterQueryChangeEvent
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.common.autoGoFeedbackWhenDoneConfirmReceiveAction
import com.zhongpin.mvvm_android.ui.common.goOrderDetailActivity
import com.zhongpin.mvvm_android.ui.order.ShowOrderWaitConfirmRedDotEvent
import com.zhongpin.mvvm_android.ui.order.ShowPurchaseOrderWaitPayRedDotEvent
import com.zhongpin.mvvm_android.view.MultiStateView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.zhongpin.mvvm_android.base.view.BaseVMFragment as BaseVMFragment


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@EventBusRegister
class OrderStatusListFragment : BaseVMFragment<OrderStatusListViewModel>() {

    private var mStatusText: String? = null

    private lateinit var mBinding: FragmentOrderStatusListBinding;

    var orderFilterQuery: OrderFilterQuery = OrderFilterQuery()
    private var mDatas:MutableList<OrderItem> = mutableListOf()
    private lateinit var listAdapter: OrderStatusListAdapter
    private var mPageMoreNo:Int = 2

    val searchQuery = hashMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mStatusText = it.getString(STATUS_TEXT)
        }
    }

    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = FragmentOrderStatusListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }


    override fun initView() {
        super.initView()
        registerDefaultLoad(mBinding.refreshLayout, Constant.COMMON_KEY)

        if ("进行中".equals(mStatusText)) {
            val statusSet = mutableListOf<Int>();
            statusSet.add(10)
            statusSet.add(20)
            statusSet.add(30)
            statusSet.add(40)
            statusSet.add(50)
            searchQuery.put("orderStatusList", statusSet)
        }else if ("待签收".equals(mStatusText)) {
            val statusSet = mutableListOf<Int>();
            statusSet.add(50)
            searchQuery.put("orderStatusList", statusSet)
        } else if ("已完成".equals(mStatusText)) {
            val statusSet = mutableListOf<Int>();
            statusSet.add(60)
            searchQuery.put("orderStatusList", statusSet)
        } else if ("申诉".equals(mStatusText)) {
            searchQuery.put("appeal", true)
        }

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(activity))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(activity).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getFirstPageOrderList(searchQuery)
            mViewModel.getWaitPayPurchaseOrderList();
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getOrderListMore(mPageMoreNo, searchQuery)
        }

        listAdapter = OrderStatusListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
                adapter, view, position ->
            val orderListItem = mDatas.get(position)
            activity?.goOrderDetailActivity(orderListItem)
        }
        listAdapter.isStateViewEnable = true;
        val multiStateView: MultiStateView = MultiStateView(requireActivity())
        multiStateView.addStateView(R.layout.empty_view_status_order_empty)
        multiStateView.addStateView(R.layout.empty_view_status_order_no_permission_empty)
        listAdapter.stateView = multiStateView

        mBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        mBinding.recyclerView.adapter = listAdapter

    }

    override fun initData() {
        super.initData()
        mViewModel.getFirstPageOrderList(searchQuery);
        //全部
        if (mStatusText.equals("全部")) {
            mViewModel.getWaitPayPurchaseOrderList();
        }
    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mFirstPageData.observe(this) {
            if(it.success) {
                if (it.data?.noViewOrderPermission == true) {
                    (listAdapter.stateView as MultiStateView?)?.showStateView(R.layout.empty_view_status_order_no_permission_empty)
                } else {
                    (listAdapter.stateView as MultiStateView?)?.showStateView(R.layout.empty_view_status_order_empty)
                }
                mPageMoreNo = 2
                val records = it.data?.records ?: emptyList()
                if (records.isEmpty()) {
                    showSuccess(Constant.COMMON_KEY)
                    mDatas.clear()
                } else {
                    showSuccess(Constant.COMMON_KEY)
                    mDatas.clear()
                    mDatas.addAll(records)
                }
                listAdapter.notifyDataSetChanged()

                //更新待签收的红点数量
                if ("待签收".equals(mStatusText)) {
                    EventBusUtils.postEvent(ShowOrderWaitConfirmRedDotEvent(records.size))
                }
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

        mViewModel.mWaitPayOrderPageData.observe(this) {
            val waitPayNum = (it.data?.records?.size ?: 0L).toInt()
            EventBusUtils.postEvent(ShowPurchaseOrderWaitPayRedDotEvent(waitPayNum))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderFilterQueryChangeEvent(event : OrderFilterQueryChangeEvent){
        orderFilterQuery = orderFilterQuery.copy(
            startTime = event.query.startTime,
            endTime = event.query.endTime
        )
        doRequestData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderItemInfoChangeEvent(infoEvent : OrderItemInfoChangeEvent){
        if (infoEvent.change) {
           doRequestData()
            if (mStatusText.equals("全部")) {
                mViewModel.getWaitPayPurchaseOrderList();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserLoginEvent(loginEvent : LoginEvent){
        if (loginEvent.isLogin) {
            lifecycleScope.launch {
                delay(2000) //延迟2秒，等权限接口请求成功后再去请求。
                doRequestData()
            }
        }
    }

    private fun doRequestData() {
        if (!TextUtils.isEmpty(orderFilterQuery.startTime)
            && !TextUtils.isEmpty(orderFilterQuery.endTime)) {
            searchQuery.put("orderTimeStart", orderFilterQuery.startTime ?: "")
            searchQuery.put("orderTimeEnd", orderFilterQuery.endTime ?: "")
        } else {
            searchQuery.remove("orderTimeStart")
            searchQuery.remove("orderTimeEnd")
        }

        mViewModel.getFirstPageOrderList(searchQuery);
    }

    private fun doRequestWaitPay() {
        mViewModel.getWaitPayPurchaseOrderList();
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
                autoGoFeedbackWhenDoneConfirmReceiveAction?.invoke();
            } else {
                autoGoFeedbackWhenDoneConfirmReceiveAction = null;
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(requireActivity(),it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }



    companion object {
        private const val STATUS_TEXT = "statusText"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param statusText Parameter 1.
         * @return A new instance of fragment SettingFragment.
         */
        @JvmStatic
        fun newInstance(statusText: String) =
            OrderStatusListFragment().apply {
                arguments = Bundle().apply {
                    putString(STATUS_TEXT, statusText)
                }
            }
    }
}