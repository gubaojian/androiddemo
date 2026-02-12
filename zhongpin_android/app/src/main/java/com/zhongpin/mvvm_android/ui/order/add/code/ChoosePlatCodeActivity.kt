package com.zhongpin.mvvm_android.ui.order.add.code

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityChoosePlatCodeBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.bean.AddOrderPurchaseItem
import com.zhongpin.mvvm_android.bean.AddOrderPurchaseItemEvent
import com.zhongpin.mvvm_android.bean.AddressInfoChangeEvent
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.ChooseMaterialPriceItemEvent
import com.zhongpin.mvvm_android.bean.MaterialPriceItem
import com.zhongpin.mvvm_android.bean.PlatformMaterialItem
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.ui.order.add.item.add.AddPurchaseItemActivity
import com.zhongpin.mvvm_android.ui.order.preview.PreviewAddOrderActivity
import java.util.UUID



class ChoosePlatCodeActivity : BaseVMActivity<ChoosePlatCodeViewModel>() {

    private lateinit var mBinding: ActivityChoosePlatCodeBinding;

    private var mAddressList: List<AddressListItemResponse>? = null;
    private var mAddressItem:AddressListItemResponse? = null;


    private var mPriceDatas:MutableList<MaterialPriceItem> = mutableListOf()
    private lateinit var mPriceListAdapter: ChoosePlatCodeListAdapter

    private var mPlatformDatas:MutableList<PlatformMaterialItem> = mutableListOf()
    private lateinit var mPlatformListAdapter: PlatformCodeListAdapter

    private var floor:Int? = 2;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            floor = intent.getIntExtra("floor", 2)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityChoosePlatCodeBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        registerDefaultLoad(mBinding.body, Constant.COMMON_KEY)

        mBinding.ivBack.setOnClickListener {
            finish()
        }


        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit();
        }

        mPriceListAdapter = ChoosePlatCodeListAdapter(this, mPriceDatas)
        mPriceListAdapter.selectPosition = 0
        mPriceListAdapter.isStateViewEnable = true;
        mPriceListAdapter.setStateViewLayout(this@ChoosePlatCodeActivity, R.layout.empty_view_choose_plat_code)
        mBinding.priceListRecyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.priceListRecyclerView.adapter = mPriceListAdapter

        mPlatformListAdapter = PlatformCodeListAdapter(this, mPlatformDatas)
        mBinding.platformListRecyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.platformListRecyclerView.adapter = mPlatformListAdapter


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
        mViewModel.getPageData(floor ?: 2)
    }


    override fun onDestroy() {
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
            mViewModel.getReceiveAddressList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSuccessPage() {
        mViewModel.mAllMaterialPriceListData.value?.let {
            val records:List<MaterialPriceItem> = it.data?.records ?: emptyList();
            mPriceDatas.clear();
            if (records.isNotEmpty()) {
                mPriceDatas.addAll(records)
                mBinding.foundNoneTip.visibility = View.VISIBLE
            } else {
                mBinding.foundNoneTip.visibility = View.GONE
            }
            mPriceListAdapter.notifyDataSetChanged()
        }
        mViewModel.mAllPlatformMaterialListData.value?.let {
            val records:List<PlatformMaterialItem> = it.data?.records ?: emptyList();
            mPlatformDatas.clear();
            if (records.isNotEmpty()) {
                mPlatformDatas.addAll(records)
            }
            mPlatformListAdapter.notifyDataSetChanged()
        }
    }

    private fun checkAndSubmit() {
        if (mPriceListAdapter.selectPosition < 0) {
            Toast.makeText(applicationContext, "请选择专属报价单", Toast.LENGTH_LONG).show()
            return
        }
        if (mPriceDatas.isEmpty()) {
            Toast.makeText(applicationContext, "请手动输入材质代码", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        val materialPriceItem = mPriceDatas[mPriceListAdapter.selectPosition]
        EventBusUtils.postEvent(ChooseMaterialPriceItemEvent(materialPriceItem))
        val intent = Intent();
        intent.putExtra("item", materialPriceItem);
        setResult(RESULT_OK, intent);
        finish()
    }



}