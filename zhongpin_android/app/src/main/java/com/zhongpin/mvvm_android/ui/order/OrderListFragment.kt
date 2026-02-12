package com.zhongpin.mvvm_android.ui.order

import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar.*
import com.zhilianshidai.pindan.app.databinding.FragmentOrderListBinding
import com.zhilianshidai.pindan.app.databinding.OrderFragmentCustomTabBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.LoginEvent
import com.zhongpin.mvvm_android.bean.NewNotifyEvent
import com.zhongpin.mvvm_android.bean.OrderFilterQuery
import com.zhongpin.mvvm_android.bean.OrderFilterQueryChangeEvent
import com.zhongpin.mvvm_android.bean.SwitchToOrderSubTabEvent
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goAddOrderPage
import com.zhongpin.mvvm_android.ui.common.goOrderSearchQueryActivity
import com.zhongpin.mvvm_android.ui.common.goPurchaseOrderListActivity
import com.zhongpin.mvvm_android.ui.common.showOrderFilterDialog
import com.zhongpin.mvvm_android.ui.order.orderstatus.OrderStatusListFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.zhongpin.mvvm_android.base.view.BaseVMFragment as BaseVMFragment



/**
 * A simple [Fragment] subclass.
 * Use the [OrderListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@EventBusRegister
class OrderListFragment : BaseVMFragment<OrderListViewModel>() {

    private var param1: String? = null

    private lateinit var mBinding: FragmentOrderListBinding;

    private var mDatas:MutableList<CompanyListItemResponse> = mutableListOf()
    private lateinit var listAdapter: OrderListAdapter
    private var mPageMoreNo:Int = 2

    private var mTitles = arrayOf("全部", "待签收", "进行中", "已完成", "申诉")
    val mFragments: MutableMap<Int, Fragment> = mutableMapOf()

    var orderFilterQuery: OrderFilterQuery = OrderFilterQuery();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }

        with(this).transparentBar().statusBarDarkFont(true).fullScreen(true).init()
    }

    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = FragmentOrderListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }




    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(activity, mBinding.content)
        //registerDefaultLoad(mBinding.refreshLayout, Constant.COMMON_KEY)

        mBinding.goSubmitOrder.setOnClickListener {
            activity?.goAddOrderPage();
        }

        mBinding.orderFilter.setOnClickListener {
            requireActivity().showOrderFilterDialog(orderFilterQuery)
        }

        mBinding.orderFilterQuery.setOnClickListener {
            requireActivity().showOrderFilterDialog(orderFilterQuery)
        }

        showQueryFilter(orderFilterQuery);

        mFragments.clear();
        mBinding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mTitles.size;
            }

            override fun createFragment(position: Int): Fragment {
                var fragment: Fragment? = mFragments.get(position)
                if (fragment == null) {
                    fragment = OrderStatusListFragment.newInstance(mTitles.get(position));
                }
                return fragment;
            }
        }

        mBinding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabBinding : OrderFragmentCustomTabBinding? =
                    tab?.customView?.tag as OrderFragmentCustomTabBinding?;
                tabBinding?.let {
                    tabBinding.textTitle.typeface =  Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val tabBinding : OrderFragmentCustomTabBinding? =
                    tab?.customView?.tag as OrderFragmentCustomTabBinding?;
                tabBinding?.let {
                    tabBinding.textTitle.typeface =  Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2, object : TabLayoutMediator.TabConfigurationStrategy {
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                val tabBinding = OrderFragmentCustomTabBinding.inflate(LayoutInflater.from(requireActivity()), mBinding.tabLayout, false);
                 tabBinding.textTitle.text = mTitles[position]
                tabBinding.showRedDot(0);
                tab.customView = tabBinding.root
                tab.customView?.tag = tabBinding;
            }

        }).attach();

        //待签收tab要加载，要显示红点提示
        mBinding.viewPager2.offscreenPageLimit = 1;


        /**
        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(activity))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(activity))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getFirstPageCompanyList()
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getCompanyListMore(mPageMoreNo)
        }

        listAdapter = OrderListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
                adapter, view, position ->
            val orderListItem = mDatas.get(position)
            val intent = Intent(activity, OrderDetailActivity::class.java)
            intent.putExtra("orderListItem", orderListItem)
            startActivity(intent)
        }
        mBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        mBinding.recyclerView.adapter = listAdapter*/

        mBinding.searchBar.setOnClickListener {
            requireActivity().goOrderSearchQueryActivity()
        }

        mBinding.purchaseOrderContainer.setOnClickListener {
            requireActivity().goPurchaseOrderListActivity()
        }
        purchaseOrderShowRedDot(0, mBinding.purchaseOrderTitle, mBinding.purchaseOrderRedDot)
    }

    override fun initData() {
        super.initData()
    }

    override fun initDataObserver() {
        super.initDataObserver()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderFilterQueryChangeEvent(event : OrderFilterQueryChangeEvent){
          orderFilterQuery = event.query
         showQueryFilter(event.query);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowOrderWaitConfirmRedDotEvent(event : ShowOrderWaitConfirmRedDotEvent) {
        val itemCount = mBinding.tabLayout.tabCount
        if (itemCount >= 2) {
            val tab = mBinding.tabLayout.getTabAt(1);
            val tabBinding : OrderFragmentCustomTabBinding? =
                tab?.customView?.tag as OrderFragmentCustomTabBinding?;
            tabBinding?.showRedDot(event.num);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowPurchaseOrderWaitPayRedDotEvent (event : ShowPurchaseOrderWaitPayRedDotEvent ) {
        purchaseOrderShowRedDot(event.num, mBinding.purchaseOrderTitle, mBinding.purchaseOrderRedDot);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSwitchToOrderTabEvent(event : SwitchToOrderSubTabEvent) {
        val itemCount = mBinding.viewPager2.adapter?.itemCount ?: 0;
        if (event.tabIndex < itemCount) {
            if (mBinding.viewPager2.currentItem != event.tabIndex) {
                mBinding.viewPager2.setCurrentItem(event.tabIndex, false)
            }
        }
    }

    private fun showQueryFilter(query: OrderFilterQuery) {
        if (TextUtils.isEmpty(query.label)) {
            mBinding.orderFilter.visibility = View.VISIBLE
            mBinding.orderFilterQuery.visibility = View.GONE
        } else {
            mBinding.orderFilter.visibility = View.GONE
            mBinding.orderFilterQuery.visibility = View.VISIBLE
            mBinding.orderFilterText.text = query.label;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewNotifyEvent(newNotifyEvent : NewNotifyEvent){
        if (newNotifyEvent.isNew) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(loginEvent : LoginEvent){
        if (loginEvent.isLogin) {
            //refresh page
        }
    }




    companion object {
        private const val ARG_PARAM1 = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            OrderListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}