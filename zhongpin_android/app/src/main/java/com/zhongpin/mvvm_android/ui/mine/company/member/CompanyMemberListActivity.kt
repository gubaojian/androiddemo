package com.zhongpin.mvvm_android.ui.mine.company.member

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
import com.zhilianshidai.pindan.app.databinding.ActivityCompanyMemberListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.MemberInfoChangeEvent
import com.zhongpin.mvvm_android.bean.MemberItem
import com.zhongpin.mvvm_android.bean.MemberItemSearchQuery
import com.zhongpin.mvvm_android.bean.MemberItemSearchQueryChangeEvent
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.common.goCompanyAddMemberActivity
import com.zhongpin.mvvm_android.ui.common.goCompanyEditMemberActivity
import com.zhongpin.mvvm_android.ui.common.goCompanyMemberSearchQueryActivity
import com.zhongpin.mvvm_android.ui.mine.company.member.search.CompanyMemberSearchQueryActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class CompanyMemberListActivity : BaseVMActivity<CompanyMemberListViewModel>() {


    private lateinit var mBinding: ActivityCompanyMemberListBinding;

    private var searchQuery: MemberItemSearchQuery? = null;

    private var mDatas:MutableList<MemberItem> = mutableListOf()
    private lateinit var listAdapter: CompanyMemberListAdapter
    private var mPageMoreNo:Int = 2

    val filterQuery = hashMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query", MemberItemSearchQuery::class.java)
            if (searchQuery == null) {
                searchQuery = MemberItemSearchQuery();
            }
        }
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityCompanyMemberListBinding.inflate(layoutInflater, container, false)
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
        val startSearchQueryLauncher: ActivityResultLauncher<MemberItemSearchQuery?> = registerForActivityResult(
            object  : ActivityResultContract<MemberItemSearchQuery?,  MemberItemSearchQuery?>() {
                override fun createIntent(context: Context, input: MemberItemSearchQuery?): Intent {
                    val intent = Intent(this@CompanyMemberListActivity,
                        CompanyMemberSearchQueryActivity::class.java)
                    intent.putExtra("query", searchQuery)
                    return  intent
                }

                override fun parseResult(resultCode: Int, intent: Intent?): MemberItemSearchQuery? {
                    var query: MemberItemSearchQuery?  = null;
                    if (intent != null && resultCode == Activity.RESULT_OK) {
                        query = IntentUtils.getSerializableExtra(intent,"query", MemberItemSearchQuery::class.java);
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

        mBinding.addMember.setOnClickListener {
            goCompanyAddMemberActivity()
        }

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getFirstPageCompanyMemberList(filterQuery)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getCompanyMemberListMore(mPageMoreNo, filterQuery)
        }

        listAdapter = CompanyMemberListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
            adapter, view, position ->
            val memberListItem = mDatas.get(position)
            if (memberListItem.isMasterAccount()) {
                return@setOnItemClickListener
            }
            goCompanyEditMemberActivity(memberListItem)
        }
        listAdapter.isStateViewEnable = true;
        listAdapter.setStateViewLayout(this@CompanyMemberListActivity, R.layout.empty_view_search_member_empty)


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
        mViewModel.getFirstPageCompanyMemberList(filterQuery)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMemberItemSearchQueryChangeEvent(event : MemberItemSearchQueryChangeEvent){

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMemberInfoChangeEvent(event : MemberInfoChangeEvent){
         if (event.isChange) {
             searchQuery?.inputText = ""
             searchOrderByQuery()
         }
    }

    fun showSearchQuery() {
        // https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3229871/detail/55977050?spaceKey=space-64stfp
        if(!TextUtils.isEmpty(searchQuery?.inputText)) {
            searchQuery?.inputText?.let {
                filterQuery.put("name", it)
            }
        } else {
            filterQuery.remove("name")
        }
        val query = searchQuery?.toQuery();
        if (query != null && query.isNotEmpty()) {
            mBinding.searchQuery.text = query
        } else {
            mBinding.searchQuery.text = "姓名/手机号"
        }
    }

    fun searchOrderByQuery() {
        showSearchQuery()
        mViewModel.getFirstPageCompanyMemberList(filterQuery)
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
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }


}