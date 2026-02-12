package com.zhongpin.mvvm_android.ui.home.mineprice

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityMinePriceListBinding
import com.zhongpin.lib_base.ktx.fadeIn
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.HomeNavBarConfig
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.MaterialPriceFilterQuery
import com.zhongpin.mvvm_android.bean.MaterialPriceFilterQueryChangeEvent
import com.zhongpin.mvvm_android.bean.MaterialPriceItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goMinePriceDetailActivity
import com.zhongpin.mvvm_android.view.popup.MinePriceSortPopup
import com.zhongpin.mvvm_android.view.popup.sortByBuyAmountAsc
import com.zhongpin.mvvm_android.view.popup.sortByBuyAmountDesc
import com.zhongpin.mvvm_android.view.popup.sortByBuyDateASC
import com.zhongpin.mvvm_android.view.popup.sortByBuyfDateDesc
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class MinePriceListActivity : BaseVMActivity<MinePriceListViewModel>() {


    private var mDatas:MutableList<MaterialPriceItem> = mutableListOf()
    private lateinit var listAdapter: MinePriceListAdapter
    private var mPageMoreNo:Int = 2

    val filterQuery = hashMapOf<String, Any>()
    private lateinit var mBinding: ActivityMinePriceListBinding;

    private var priceItemFilter = MaterialPriceFilterQuery();

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(true).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityMinePriceListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.sortContainer.setOnClickListener {
            XPopup.Builder(this@MinePriceListActivity)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .hasShadowBg(false) // 去掉半透明背景
                .isViewMode(true)
                .atView(mBinding.sortContainer)
                .setPopupCallback(object: SimpleCallback() {

                    override fun beforeShow(popupView: BasePopupView?) {
                        super.beforeShow(popupView)
                        mBinding.maskView.fadeIn {  }
                    }

                    override fun beforeDismiss(popupView: BasePopupView?) {
                        super.beforeDismiss(popupView)
                        mBinding.maskView.gone()
                    }
                })
                .asCustom(MinePriceSortPopup(this@MinePriceListActivity, priceItemFilter))
                .show()
        }

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(false)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
             doRequestData()
        }

        listAdapter = MinePriceListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
                adapter, view, position ->
            val priceItem = mDatas.get(position)
            goMinePriceDetailActivity(priceItem)
        }
        listAdapter.isStateViewEnable = true;
        listAdapter.setStateViewLayout(this@MinePriceListActivity, R.layout.empty_view_mine_price_list_empty)

        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = listAdapter

        mBinding.bottomBarSpace.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = HomeNavBarConfig.bottomMargin
        }

        registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)

    }

    override fun initDataObserver() {
        super.initDataObserver()

        mViewModel.mAllMaterialPriceListData.observe(this) {
            if(it.success) {
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

        mViewModel.mPriceTipData.observe(this) {
            if (it.success) {
                mBinding.tipContainer.visible()
                mBinding.tipsOne.text = "单个订单面积大于${it.data?.area}㎡的减价${it.data?.stepPrice}元/㎡，订单面积小于${it.data?.costArea}㎡的加价${it.data?.stepPriceCost}元/㎡。"
                mBinding.tipsTwo.text = "订单尺寸小于（长${it.data?.length}mm）或（宽${it.data?.width}mm）在原价材质组合基础上加${it.data?.cutPrice}元/㎡。"
            } else {
                mBinding.tipContainer.gone()
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    override fun initData() {
        super.initData()
        doRequestData()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMaterialPriceFilterQueryChangeEvent(event : MaterialPriceFilterQueryChangeEvent){
        priceItemFilter = event.query
        doRequestData()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderItemInfoChangeEvent(infoEvent : OrderItemInfoChangeEvent){
        if (infoEvent.change) {
            doRequestData()
        }
    }



    private var mLoadingDialog: LoadingDialog? = null
    /**
     * show 加载中
     */
    fun showLoadingDialog() {
        dismissLoadingDialog()
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this, false)
        }
        mLoadingDialog?.showDialogV2(this)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissLoadingDialog() {
        mLoadingDialog?.dismissDialogV2()
        mLoadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    //排序字段（order_count，order_time）
    //升序 asc ;降序 desc
    private fun doRequestData() {
        filterQuery.remove("sortField")
        filterQuery.remove("sortOrder")
        if (TextUtils.equals(priceItemFilter.sortBy, sortByBuyAmountAsc)) {
            filterQuery.put("sortField", "order_count")
            filterQuery.put("sortOrder", "asc")
        } else if (TextUtils.equals(priceItemFilter.sortBy, sortByBuyAmountDesc)) {
            filterQuery.put("sortField", "order_count")
            filterQuery.put("sortOrder", "desc")
        } else if (TextUtils.equals(priceItemFilter.sortBy, sortByBuyDateASC)) {
            filterQuery.put("sortField", "order_time")
            filterQuery.put("sortOrder", "asc")
        } else if (TextUtils.equals(priceItemFilter.sortBy, sortByBuyfDateDesc)) {
            filterQuery.put("sortField", "order_time")
            filterQuery.put("sortOrder", "desc")
        }

        mViewModel.getPriceTipResponse()
        mViewModel.getAllMaterialPriceList(filterQuery)
    }

}