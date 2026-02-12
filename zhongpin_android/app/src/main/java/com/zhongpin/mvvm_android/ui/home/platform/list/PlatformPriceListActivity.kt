package com.zhongpin.mvvm_android.ui.home.platform.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityPlatformPriceListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.MemberItemSearchQuery
import com.zhongpin.mvvm_android.bean.PlatformMaterialItemSearchQuery
import com.zhongpin.mvvm_android.bean.PlatformMaterialItemSearchQueryChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.home.platform.list.search.PlatformPriceListSearchQueryActivity
import com.zhongpin.mvvm_android.ui.mine.company.member.search.CompanyMemberSearchQueryActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class PlatformPriceListActivity : BaseVMActivity<PlatformPriceListViewModel>() {


    private lateinit var mBinding: ActivityPlatformPriceListBinding;

    private var searchQuery: PlatformMaterialItemSearchQuery? = null;

    private var mDatas:MutableList<PlatformListItemEntity> = mutableListOf()
    private lateinit var listAdapter: PlatformPriceListAdapter

    val filterQuery = hashMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query", PlatformMaterialItemSearchQuery::class.java)
            if (searchQuery == null) {
                searchQuery = PlatformMaterialItemSearchQuery();
            }
        }
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPlatformPriceListBinding.inflate(layoutInflater, container, false)
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
        val startSearchQueryLauncher: ActivityResultLauncher<PlatformMaterialItemSearchQuery?> = registerForActivityResult(
            object  : ActivityResultContract<PlatformMaterialItemSearchQuery?,  PlatformMaterialItemSearchQuery?>() {
                override fun createIntent(context: Context, input: PlatformMaterialItemSearchQuery?): Intent {
                    val intent = Intent(this@PlatformPriceListActivity,
                        PlatformPriceListSearchQueryActivity::class.java)
                    intent.putExtra("query", searchQuery)
                    return  intent
                }

                override fun parseResult(resultCode: Int, intent: Intent?): PlatformMaterialItemSearchQuery? {
                    var query: PlatformMaterialItemSearchQuery?  = null;
                    if (intent != null && resultCode == Activity.RESULT_OK) {
                        query = IntentUtils.getSerializableExtra(intent,"query", PlatformMaterialItemSearchQuery::class.java);
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
        mBinding.refreshLayout.setEnableLoadMore(false)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getAllPlatformMaterialList(filterQuery)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getAllPlatformMaterialList(filterQuery)
        }

        listAdapter = PlatformPriceListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
            adapter, view, position ->
            val memberListItem = mDatas.get(position)
            //goCompanyEditMemberActivity(memberListItem)
        }
        listAdapter.isStateViewEnable = true;
        listAdapter.setStateViewLayout(this@PlatformPriceListActivity, R.layout.empty_view_search_platform_price_empty)


        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = listAdapter
    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mAllPlatformMaterialListData.observe(this) {
            if(it.success) {
                val records = it.data?.records
                if (records.isNullOrEmpty()) {
                    showSuccess(Constant.COMMON_KEY)
                    mDatas.clear()
                } else {
                   showSuccess(Constant.COMMON_KEY)
                    mDatas.clear()
                    mDatas.add(PlatformListItemEntity(
                        type = PlatformPriceListAdapter.PLATFORM_PRICE_ITEM_TITLE
                    ))
                    var index = 0;
                    records.forEach { item ->
                        if (index % 2 == 1) {
                            item.cellItemCellColor = "#F5F6FA";
                        } else {
                            item.cellItemCellColor = "#FFFFFF";
                        }
                        index++
                        item.cellListShowIsLastItem = (item == records.last())
                        mDatas.add(PlatformListItemEntity(
                            data = item,
                            type = PlatformPriceListAdapter.PLATFORM_PRICE_ITEM_ITEM
                        ))
                    }
                }
                listAdapter.notifyDataSetChanged()
            }
            mBinding.refreshLayout.finishRefresh()
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getAllPlatformMaterialList(filterQuery)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlatformMaterialItemSearchQueryChangeEvent(event : PlatformMaterialItemSearchQueryChangeEvent){

    }



    fun showSearchQuery() {
        // https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3229871/detail/55977050?spaceKey=space-64stfp
        if(!TextUtils.isEmpty(searchQuery?.inputText)) {
            searchQuery?.inputText?.let {
                filterQuery.put("keyword", it)
            }
        } else {
            filterQuery.remove("keyword")
        }
        val query = searchQuery?.toQuery();
        if (query != null && query.isNotEmpty()) {
            mBinding.searchQuery.text = query
        } else {
            mBinding.searchQuery.text = "原纸品名/材质代码/克重"
        }
    }

    fun searchOrderByQuery() {
        showSearchQuery()
        mViewModel.getAllPlatformMaterialList(filterQuery)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}