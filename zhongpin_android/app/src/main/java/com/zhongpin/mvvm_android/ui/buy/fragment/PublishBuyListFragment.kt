package com.zhongpin.mvvm_android.ui.buy.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar.*
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.R.*
import com.zhilianshidai.pindan.app.databinding.FragmentPublishBuyListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.NewNotifyEvent
import com.zhongpin.mvvm_android.bean.PublishBuyEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.buy.detail.PublishBuyDetailActivity
import com.zhongpin.mvvm_android.ui.buy.edit.EditPublishBuyDetailActivity
import com.zhongpin.mvvm_android.ui.notify.setting.NotifySettingActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.zhongpin.mvvm_android.base.view.BaseVMFragment as BaseVMFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PAGE_TYPE = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@EventBusRegister
class PublishBuyListFragment : BaseVMFragment<PublishBuyListViewModel>() {
    // TODO: Rename and change types of parameters
    private var mPageType: String? = null

    private lateinit var mBinding: FragmentPublishBuyListBinding;

    private var mDatas:MutableList<CompanyListItemResponse> = mutableListOf()
    private lateinit var listAdapter: PublishBuyListAdapter
    private var mPageMoreNo:Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageType = it.getString(PAGE_TYPE)
        }
    }

    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = FragmentPublishBuyListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }


    override fun initView() {
        super.initView()
        registerDefaultLoad(mBinding.refreshLayout, Constant.COMMON_KEY)

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

        listAdapter = PublishBuyListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
                adapter, view, position ->
            val companyListItem = mDatas.get(position)
            if ("0".equals(mPageType)) {
                val intent = Intent(activity, PublishBuyDetailActivity::class.java)
                intent.putExtra("buyStatus", 0)
                intent.putExtra("buyOrderItem", companyListItem)
                startActivity(intent)
            } else {
                val intent = Intent(activity, PublishBuyDetailActivity::class.java)
                intent.putExtra("buyOrderItem", companyListItem)
                startActivity(intent)
            }
        }
        mBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        mBinding.recyclerView.adapter = listAdapter

    }

    override fun initData() {
        super.initData()
        mViewModel.getFirstPageCompanyList();
    }

    override fun initDataObserver() {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(publishBuyEvent : PublishBuyEvent){
        if (publishBuyEvent.isPublishSuccess) {
            mViewModel.getFirstPageCompanyList();
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param pageType Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(pageType: String) =
            PublishBuyListFragment().apply {
                arguments = Bundle().apply {
                    putString(PAGE_TYPE, pageType)
                }
            }
    }
}