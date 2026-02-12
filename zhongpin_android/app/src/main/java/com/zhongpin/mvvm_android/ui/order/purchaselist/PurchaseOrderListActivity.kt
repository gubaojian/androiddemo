package com.zhongpin.mvvm_android.ui.order.purchaselist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityPurchaseOrderListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.mvvm_android.base.ext.HomeNavBarConfig
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.bean.SubmitBuyOrderDoneEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.common.goChargeInputActivity
import com.zhongpin.mvvm_android.ui.common.goPayPurchaseOrderActivity
import com.zhongpin.mvvm_android.ui.common.goSubmitOrderResultActivity
import com.zhongpin.mvvm_android.ui.common.showConfirmPayPurchaseBottomSheet
import com.zhongpin.mvvm_android.view.dialog.ConfirmImageDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class PurchaseOrderListActivity : BaseVMActivity<PurchaseOrderListViewModel>() {


    private lateinit var mBinding: ActivityPurchaseOrderListBinding;


    private var mDatas:MutableList<PurchaseOrderDetail> = mutableListOf()
    private lateinit var listAdapter: PurchaseOrderListAdapter
    private var mPageMoreNo:Int = 2

    val filterQuery = hashMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent != null) {

        }
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(true).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPurchaseOrderListBinding.inflate(layoutInflater, container, false)
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

        mBinding.safeBody.updateLayoutParams<MarginLayoutParams> {
            bottomMargin = HomeNavBarConfig.bottomMargin
        }

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(true)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getFirstPagePurchaseOrderList(filterQuery)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.getPurchaseOrderListMore(mPageMoreNo, filterQuery)
        }

        listAdapter = PurchaseOrderListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
            adapter, view, position ->
            val purchaseListItem = mDatas.get(position)
            goPayPurchaseOrderActivity(purchaseListItem.id)
        }
        listAdapter.isStateViewEnable = true;
        listAdapter.setStateViewLayout(this@PurchaseOrderListActivity, R.layout.empty_view_search_member_empty)


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
        mViewModel.getFirstPagePurchaseOrderList(filterQuery)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderItemInfoChangeEvent(event : OrderItemInfoChangeEvent){
         if (event.change) {
             searchOrderByQuery()
         }
    }



    fun searchOrderByQuery() {
        mViewModel.getFirstPagePurchaseOrderList(filterQuery)
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


    fun onCancelPay(item: PurchaseOrderDetail) {
        val dialog = ConfirmDialog(
            mContext = this,
            title = "取消订购",
            message = HtmlCompat.fromHtml("<br/>确定取消订购吗？<br/><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
            cancelText = "确定取消",
            confirmText = "继续支付",
            onCancel = {
                doCancelPay(item);
            },
            onConfirm = {
                doPay(item)
            }
        );
        dialog.showDialog(this)
    }

    fun doCancelPay(item: PurchaseOrderDetail) {
        showLoadingDialogV2()
        mViewModel.cancelPurchaseOrder(item.id ?: 0).observe(this) {
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

    fun onConfirmPay(item: PurchaseOrderDetail) {
        manualShowConfirmPayPurchaseBottomSheet(item)
    }

    fun manualShowConfirmPayPurchaseBottomSheet(item: PurchaseOrderDetail) {
        showConfirmPayPurchaseBottomSheet(
            confirmAction = {
                doPay(item)
            },
            inFullScreenActivity = true,
            purchaseOrderDetail = item
        );
    }

    fun doPay(item: PurchaseOrderDetail) {
        showLoadingDialogV2()
        mViewModel.payPurchaseOrder(item.id ?: 0L).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                goSubmitOrderResultActivity();
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
            } else {
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                if (5002 == it.code) { //账户余额不足
                    showAmountNotEnoughWithErrorImage();
                    return@observe
                }
                Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun showAmountNotEnoughWithErrorImage() {
        val confirmDialog = ConfirmImageDialog(
            mContext = this@PurchaseOrderListActivity,
            title = "支付失败",
            message = "账户预付款余额不足，请充值后继续支付\n",
            confirmText = "去充值",
            onConfirm = {
                goChargeInputActivity();
            }
        );
        confirmDialog.show()
    }

}