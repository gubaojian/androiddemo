package com.zhongpin.mvvm_android.ui.mine.company.address.choose

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.databinding.ActivityChooseAddressListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.AddressInfoChangeEvent
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.ChooseAddressEvent
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.mine.company.address.add.AddReceiveAddressActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class ChooseAddressListActivity : BaseVMActivity<ChooseAddressListViewModel>() {


    private lateinit var mBinding: ActivityChooseAddressListBinding;


    private var mDatas:MutableList<AddressListItemResponse> = mutableListOf()
    private lateinit var listAdapter: ChooseAddressListAdapter

    private var addressId:Long = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            addressId = intent.getLongExtra("id", -1)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityChooseAddressListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
            mBinding.refreshLayout.finishRefresh()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.addAddress.setOnClickListener {
            val intent = Intent(this@ChooseAddressListActivity, AddReceiveAddressActivity::class.java)
            intent.putExtra("from", "order")
            startActivity(intent)
        }

        mBinding.btnSubmit.setOnClickListener {
            if (listAdapter.selectPosition < 0) {
                Toast.makeText(applicationContext,"请选收货地址", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val address = mDatas[listAdapter.selectPosition]
            EventBusUtils.postEvent(ChooseAddressEvent(item = address))
            val intent = Intent();
            intent.putExtra("address", address);
            setResult(RESULT_OK, intent);
            finish()
        }

        mBinding.refreshLayout.setEnableRefresh(true)
        mBinding.refreshLayout.setEnableLoadMore(false)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getReceiveAddressList()
        }

        listAdapter = ChooseAddressListAdapter(this, {
                pos, item ->
            onSetDefaultAddress(pos, item)
        }, mDatas)

        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = listAdapter

        registerDefaultLoad(mBinding.body, ApiService.ADDRESS_LIST)
    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mData.observe(this) {
            if(it.success) {
                val records = it.data;
                if (records.isNullOrEmpty()) {
                    showEmpty(ApiService.ADDRESS_LIST)
                    mDatas.clear()
                } else {
                   showSuccess(ApiService.ADDRESS_LIST)
                   mDatas.clear()
                   mDatas.addAll(records)
                }
                if (listAdapter.selectPosition < 0) {
                    var selectPos = 0;
                    if (!records.isNullOrEmpty()) {
                        if (addressId >= 0) {
                            for (index in records.indices) {
                                val  item = records[index];
                                if (item.id == addressId) {
                                    selectPos = index;
                                    break;
                                }
                            }
                        } else {
                            for (index in records.indices) {
                                val  item = records[index];
                                if (item.status == 1) {
                                    selectPos = index;
                                    break;
                                }
                            }
                        }
                    }
                    listAdapter.selectPosition = selectPos;
                }

                listAdapter.notifyDataSetChanged()
            } else {
                showError(it.msg, ApiService.ADDRESS_LIST)
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

    fun  onDelete(position: Int, item: AddressListItemResponse) {
        val builder = AlertDialog.Builder(this@ChooseAddressListActivity)
        builder.setMessage("确认删除吗？")
        builder.setNeutralButton("取消", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

            }

        })
        builder.setPositiveButton("确认", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                deleteAddressItem(item)
            }

        })
        builder.show()
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
                Toast.makeText(applicationContext,"删除失败 " + it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }


}