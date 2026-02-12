package com.zhongpin.mvvm_android.ui.order.add

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityAddOrderBinding
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.mvvm_android.bean.AddOrderPurchaseItem
import com.zhongpin.mvvm_android.bean.AddOrderPurchaseItemEvent
import com.zhongpin.mvvm_android.bean.AddressInfoChangeEvent
import com.zhongpin.mvvm_android.bean.EditOrderPurchaseItemEvent
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.ChooseAddressEvent
import com.zhongpin.mvvm_android.bean.DeleteOrderPurchaseItemEvent
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.bean.SubmitBuyOrderDoneEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.common.BoxConfigData
import com.zhongpin.mvvm_android.ui.mine.company.address.add.AddReceiveAddressActivity
import com.zhongpin.mvvm_android.ui.mine.company.address.choose.ChooseAddressListActivity
import com.zhongpin.mvvm_android.ui.order.add.item.add.AddPurchaseItemActivity
import com.zhongpin.mvvm_android.ui.order.preview.PreviewAddOrderActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import kotlin.collections.set


@EventBusRegister
class AddOrderActivity : BaseVMActivity<AddOrderViewModel>() {


    private lateinit var mBinding: ActivityAddOrderBinding;

    private var mAddressList: List<AddressListItemResponse>? = null;
    private var mAddressItem:AddressListItemResponse? = null;


    private var mDatas:MutableList<AddOrderPurchaseItem> = mutableListOf()
    private lateinit var mListAdapter: AddOrderPurchaseItemListAdapter

    private var buyAgainItem: AddOrderPurchaseItem? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            buyAgainItem = IntentUtils.getSerializableExtra(intent, "buyAgainItem", AddOrderPurchaseItem::class.java)
            val buyAgainFromPurchaseOrder = intent.getBooleanExtra("buyAgainFromPurchaseOrder", false)
            if (buyAgainFromPurchaseOrder) {
                handleDataFromBuyAgainFromPurchaseOrder();
            }
        }
        super.onCreate(savedInstanceState)
    }




    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityAddOrderBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        registerDefaultLoad(mBinding.body, ApiService.COMMON_KEY)

        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.addAddressContainer.setOnClickListener {
            val intent = Intent(this@AddOrderActivity, AddReceiveAddressActivity::class.java)
            intent.putExtra("from", "order")
            startActivity(intent)
        }

        mBinding.changeAddressContainer.setOnClickListener {
            val intent = Intent(this@AddOrderActivity, ChooseAddressListActivity::class.java)
            intent.putExtra("id", mAddressItem?.id ?: -1)
            startActivity(intent)
        }

        mBinding.addressItemContainer.setOnClickListener {
            val intent = Intent(this@AddOrderActivity, ChooseAddressListActivity::class.java)
            intent.putExtra("id", mAddressItem?.id ?: -1)
            startActivity(intent)
        }

        mBinding.addOrderContainer.setOnClickListener {
            val intent = Intent(this@AddOrderActivity, AddPurchaseItemActivity::class.java)
            startActivity(intent)
        }

        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit();
        }

        buyAgainItem?.let {
            mDatas.add(it)
        }


        mListAdapter = AddOrderPurchaseItemListAdapter(this, mDatas)
        mListAdapter.isStateViewEnable = true;
        mListAdapter.setStateViewLayout(this@AddOrderActivity, R.layout.empty_view_add_order_purchase_item_list)
        mListAdapter.stateView?.let {
            it.findViewById<View>(R.id.empty_add_order_purchase_item).setOnClickListener {
                val intent = Intent(this@AddOrderActivity, AddPurchaseItemActivity::class.java)
                startActivity(intent)
            }
        }

        mListAdapter.setOnItemClickListener { _,view, position ->
            val item = mDatas[position];
            val intent = Intent(this@AddOrderActivity, AddPurchaseItemActivity::class.java)
            intent.putExtra("mode", "edit")
            intent.putExtra("item", item)
            startActivity(intent)
        }

        mBinding.orderListRecyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.orderListRecyclerView.adapter = mListAdapter
        refreshPurchaseItems()

    }


    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mPageData.observe(this) { outerIt ->
            if(outerIt.success) {
                showSuccess(Constant.COMMON_KEY)
                showSuccessPage()
            } else {
                showError(outerIt.msg, Constant.COMMON_KEY)
            }
        }
    }


    override fun initData() {
        super.initData()
        mViewModel.getPageData()
    }


    override fun onDestroy() {
        shareAddressItem = null;
        sharePurchaseItemDatas = null;
        super.onDestroy()
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshAddress(infoEvent : AddressInfoChangeEvent){
        if (infoEvent.isChange) {
            mViewModel.getPageData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChooseAddress(infoEvent : ChooseAddressEvent) {
        mAddressItem = infoEvent.item
        showAddressItem(mAddressItem)
        mViewModel.getPageData()
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddOrderPurchaseItem(event : AddOrderPurchaseItemEvent) {
        mDatas.add(event.item)
        refreshPurchaseItems()
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEditOrderPurchaseItem(event : EditOrderPurchaseItemEvent) {
        val target = mDatas.find { it.uuid == event.item.uuid }
        if (target != null) {
            val index = mDatas.indexOf(target)
            if (index >= 0) {
                mDatas[index] = event.item
            }
        }
        refreshPurchaseItems()
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeleteOrderPurchaseItem(event : DeleteOrderPurchaseItemEvent) {
        val target = mDatas.find { it.uuid == event.item.uuid }
        if (target != null) {
            mDatas.remove(target)
        }
        refreshPurchaseItems()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubmitOrderDoneEvent(event : SubmitBuyOrderDoneEvent) {
        finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshPurchaseItems() {
        if (mDatas.isEmpty()) {
            mBinding.addOrderContainer.visibility = View.GONE
        } else {
            mBinding.addOrderContainer.visibility = View.VISIBLE
        }
        mListAdapter.notifyDataSetChanged()
    }

    private fun showSuccessPage() {
        mViewModel.mAddressData.value?.let { outerIt ->
            if(outerIt.success) {
                mAddressList = outerIt.data;
                val records:List<AddressListItemResponse>? = outerIt.data
                if (!records.isNullOrEmpty()) {
                    records.forEach {
                        if (it.status == 1 && mAddressItem == null) {
                            mAddressItem = it
                        }
                    }
                    if (mAddressItem == null) {
                        mAddressItem = records[0]
                    }
                } else {
                    mAddressItem = null
                }
                showAddressItem(mAddressItem)
            }
        }
        mViewModel.mLenTypeConfig.value?.let { outerIt ->
            if (outerIt.success) {
                BoxConfigData.updateLenConfig(outerIt.data)
            }
        }
    }

    private fun showAddressItem(item: AddressListItemResponse?) {
        if (item == null) {
            mBinding.emptyAddressTip.visibility = View.VISIBLE
            mBinding.addAddressContainer.visibility = View.VISIBLE
            mBinding.changeAddressContainer.visibility = View.GONE
            mBinding.addressItemContainer.visibility = View.GONE
        } else {
            mBinding.emptyAddressTip.visibility = View.GONE
            mBinding.addAddressContainer.visibility = View.GONE
            mBinding.changeAddressContainer.visibility = View.VISIBLE
            mBinding.addressItemContainer.visibility = View.VISIBLE
            mBinding.detailAddress.text =  item.toShouHuoAddress()
            mBinding.name.text = item.name ?: ""
            mBinding.contractPhone.text = item.mobile ?: ""
        }
    }


    private fun showErrorDialog(msg:String?) {
        val confirmDialog = ConfirmDialog(
            mContext = this@AddOrderActivity,
            title = "提示",
            message =  "${msg ?: "系统异常"}\n",
            confirmText = "确定",
            showCancelButton = false,
            onConfirm = {
            }
        );
        confirmDialog.show()
    }

    private fun checkAndSubmit() {
        if (mAddressItem == null) {
            Toast.makeText(applicationContext, "请添加收货地址", Toast.LENGTH_LONG).show()
            return
        }
        if (mDatas.isEmpty()) {
            Toast.makeText(applicationContext, "请添加采购单", Toast.LENGTH_LONG).show()
            return
        }
        var hasItemError = false;
        mDatas.forEach { item ->
            if (!hasItemError) {
                if (TextUtils.isEmpty(item.demandTime)) {
                    Toast.makeText(applicationContext, "请选择${item.platCode}的订购日期", Toast.LENGTH_LONG).show()
                    hasItemError = true;
                } else if (item.num <= 0) {
                    Toast.makeText(applicationContext, "请选择${item.platCode}的订购数量", Toast.LENGTH_LONG).show()
                    hasItemError = true;
                }
            }
        }
        if (hasItemError) {
            return
        }

        //参数比较大，不采用intent传递，采用内存约定传递
        shareAddressItem = mAddressItem;
        sharePurchaseItemDatas = mDatas;

        mViewModel.orderPreview(getOrderParameter()).observe(this) {
            if (it.success) {
                val intent = Intent(this@AddOrderActivity, PreviewAddOrderActivity::class.java)
                startActivity(intent)
            } else {
                showErrorDialog(it.msg)
            }
        }

    }

    private fun handleDataFromBuyAgainFromPurchaseOrder() {
        val purchaseOrderDetail = ShareParamDataUtils.getParams<PurchaseOrderDetail>("purchaseOrderItem")
        purchaseOrderDetail?.let { item ->
            val records = item.orders ?: emptyList()
            records.forEach { orderItem ->
                val purchaseItem = AddOrderPurchaseItem(
                    floor = orderItem.floor,
                    platCode = orderItem.platCode,
                    flute = orderItem.lenType,
                    pageSize = "${orderItem.length}*${orderItem.width}",
                    paperLength = orderItem.length,
                    paperWidth = orderItem.width,
                    line = orderItem.line?.toString() ?: "1",
                    num = orderItem.num ?: 1
                );
                val types = BoxConfigData.splitTouch(orderItem.touchSize, orderItem.touch)
                if (types.size > 0) {
                    purchaseItem.lineLength = types[0].toIntOrNull()
                }
                if (types.size > 1) {
                    purchaseItem.lineWidth = types[1].toIntOrNull()
                }
                if (types.size > 2) {
                    purchaseItem.lineHeight = types[2].toIntOrNull()
                }

                if (purchaseItem.line == "1") {
                    purchaseItem.touch =  (purchaseItem.lineLength?.toString() ?: "") +
                            "+" + (purchaseItem.lineWidth?.toString() ?: "") +
                            "+" + (purchaseItem.lineHeight?.toString() ?: "")
                } else {
                    purchaseItem.line = "0"
                    purchaseItem.touch = ""
                }

                mDatas.add(purchaseItem)
            }
        }
        ShareParamDataUtils.clearParams()
    }


    companion object {
        var shareAddressItem:AddressListItemResponse? = null;
        var sharePurchaseItemDatas:MutableList<AddOrderPurchaseItem>? = mutableListOf()

        fun getItemCheckForm(item: AddOrderPurchaseItem):HashMap<String, Any> {
            val checkForm: HashMap<String, Any> = hashMapOf()
            checkForm["platCode"] = item.platCode ?: ""
            checkForm["lenType"] = item.flute ?: 0
            checkForm["line"] = item.line ?: 0
            checkForm["touch"] = PingDanAppUtils.getServerFormatTouch(item.touch)
            checkForm["touchSize"] = item.touch ?: ""
            checkForm["size"] = item.pageSize ?: ""
            checkForm["num"] = item.num
            checkForm["demandTime"] = item.demandTime ?: ""
            checkForm["floor"] = item.floor ?: ""
            if (item.boxLength != null
                && item.boxWidth != null
                && item.boxHeight != null
                && item.boxTypeNum != null) {
                checkForm["boxType"] = item.boxTypeNum ?: ""
                checkForm["boxTypeName"] = item.boxTypeName ?: ""
                checkForm["boxSize"] = "${item.boxLength}*${item.boxWidth}*${item.boxHeight}"
            }
            checkForm["remark"] = item.inputRemark ?: ""
            return checkForm;
        }
        fun getOrderParameter():HashMap<String, Any> {
            val parameter: HashMap<String, Any> = hashMapOf()
            val orderList = mutableListOf<Any>();
            sharePurchaseItemDatas?.forEach {
                val item: HashMap<String, Any> = hashMapOf()
                item["platCode"] = it.platCode ?: ""
                item["lenType"] = it.flute ?: 0
                item["line"] = it.line ?: 0
                item["touch"] = PingDanAppUtils.getServerFormatTouch(it.touch)
                item["touchSize"] = it.touch ?: ""
                item["size"] = it.pageSize ?: ""
                item["num"] = it.num
                item["demandTime"] = it.demandTime ?: ""
                item["floor"] = it.floor ?: ""
                if (it.boxLength != null
                    && it.boxWidth != null
                    && it.boxHeight != null
                    && it.boxTypeNum != null) {
                    item["boxType"] = it.boxTypeNum ?: ""
                    item["boxTypeName"] = it.boxTypeName ?: ""
                    item["boxSize"] = "${it.boxLength}*${it.boxWidth}*${it.boxHeight}"
                }

                item["remark"] = it.inputRemark ?: ""

                orderList.add(item)
            }
            if (shareAddressItem?.id != null) {
                parameter["addressId"] = AddOrderActivity.shareAddressItem?.id ?: 0
            }
            parameter["orders"] = orderList;
            return parameter
        }


    }


}