package com.zhongpin.mvvm_android.ui.notify

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NotificationUtils
import com.gyf.immersionbar.ImmersionBar.*
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.R.*
import com.zhilianshidai.pindan.app.databinding.FragmentNotifyListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.LoginEvent
import com.zhongpin.mvvm_android.bean.NewNotifyEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.notify.setting.NotifySettingActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.zhongpin.mvvm_android.base.view.BaseVMFragment as BaseVMFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@EventBusRegister
class NotifyListFragment : BaseVMFragment<NotifyListViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

    private lateinit var mBinding: FragmentNotifyListBinding;

    private var mDatas:MutableList<CompanyListItemResponse> = mutableListOf()
    private lateinit var listAdapter: NotifyListAdapter
    private var mPageMoreNo:Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
    }

    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = FragmentNotifyListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }


    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(activity, mBinding.content)
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

        listAdapter = NotifyListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
                adapter, view, position ->
            val companyListItem = mDatas.get(position)
            val intent = Intent(activity, NotifySettingActivity::class.java)
            intent.putExtra("companyListItem", companyListItem)
            startActivity(intent)
        }
        mBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        mBinding.recyclerView.adapter = listAdapter

        if (!NotificationUtils.areNotificationsEnabled()) {
            showNotificationDialog();
        }
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
    fun onNewNotifyEvent(newNotifyEvent : NewNotifyEvent){
        if (newNotifyEvent.isNew) {
            mViewModel.getFirstPageCompanyList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(loginEvent : LoginEvent){
        if (loginEvent.isLogin) {
            mViewModel.getFirstPageCompanyList();
        }
    }

    fun showNotificationDialog() {
        val builder = AlertDialog.Builder(mActivity)
        builder.setTitle("提示")
        builder.setMessage("你需要开启通知权限才能接收通知，请点击确定前往设置开启权限")
        builder.setPositiveButton("确定", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", mActivity.packageName, null))
                startActivity(intent);
            }
        })
        builder.setNegativeButton("取消", null)
        builder.show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            NotifyListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}