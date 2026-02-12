package com.zhongpin.mvvm_android.ui.mine.company.address

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.databinding.ActivityAddressListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.AddressInfoChangeEvent
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.mine.company.address.add.AddReceiveAddressActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class AddressListActivity : BaseVMActivity<AddressListViewModel>() {


    private lateinit var mBinding: ActivityAddressListBinding;


    private var mDatas:MutableList<AddressListItemResponse> = mutableListOf()
    private lateinit var listAdapter: AddressListAdapter

    //private var entId:Long = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            //entId = intent.getLongExtra("entId", -1)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityAddressListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.addAddress.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddReceiveAddressActivity::class.java)
            startActivity(intent)
        }

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(false)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getReceiveAddressList()
        }

        listAdapter = AddressListAdapter(this, {
                                               pos, item ->
            onDelete(pos, item)
        },{
          pos, item ->
            onEdit(pos, item)
        },{
                pos, item ->
            onSetDefaultAddress(pos, item)
        }, mDatas)
        listAdapter.setOnItemClickListener {
            adapter, view, position ->
            //val addressListItem = mDatas.get(position)
            //val intent = Intent(this@AddressListActivity, CompanyDetailActivity::class.java)
            //intent.putExtra("companyListItem", companyListItem)
            //startActivity(intent)
        }
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = listAdapter

        registerDefaultLoad(mBinding.refreshLayout, Constant.COMMON_KEY)
    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mData.observe(this) {
            if(it.success) {
                val records = it.data;
                if (records.isNullOrEmpty()) {
                    showEmpty(Constant.COMMON_KEY)
                    mDatas.clear()
                } else {
                   showSuccess(Constant.COMMON_KEY)
                   mDatas.clear()
                   mDatas.addAll(records)
                }
                listAdapter.notifyDataSetChanged()
            } else {
                showError(it.msg, Constant.COMMON_KEY)
            }
            mBinding.refreshLayout.finishRefresh()
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getReceiveAddressList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : AddressInfoChangeEvent){
        if (infoEvent.isChange) {
            mViewModel.getReceiveAddressList()
        }
    }

    fun  onDelete(position: Int, item: AddressListItemResponse) {
        val dialog = ConfirmDialog(
            mContext = this,
            title = "确认删除吗？",
            message = HtmlCompat.fromHtml("<br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
            confirmText = "确认",
            onConfirm = {
                deleteAddressItem(item)
            }
        );
        dialog.showDialog(this)
    }


    fun onEdit(position: Int, item: AddressListItemResponse) {
        val intent = Intent(this@AddressListActivity, AddReceiveAddressActivity::class.java)
        intent.putExtra("addressItem", item)
        startActivity(intent)
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

    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3148316/detail/55818666?spaceKey=space-64stfp
    fun deleteAddressItem(item: AddressListItemResponse){
        showLoadingDialog()
        val id  = item.id ?: 0
        mViewModel.deleteReceiveAddress(id).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                EventBusUtils.postEvent(AddressInfoChangeEvent(true))
            } else {
                Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3221912/detail/55951609?spaceKey=space-64stfp
    fun onSetDefaultAddress(pos:Int, addressItem: AddressListItemResponse) {
        val parameter:HashMap<String,Any> = hashMapOf()
        parameter["id"] = addressItem.id ?: -1
        parameter["status"] = 1
        showLoadingDialog()
        mViewModel.editReceiveAddress(parameter).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                EventBusUtils.postEvent(AddressInfoChangeEvent(true))
            } else {
                Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }


}