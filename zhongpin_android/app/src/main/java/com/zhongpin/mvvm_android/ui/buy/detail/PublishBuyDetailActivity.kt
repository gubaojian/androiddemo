package com.zhongpin.mvvm_android.ui.buy.detail

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.databinding.ActivityPublishBuyDetailListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.PublishBuyEvent
import com.zhongpin.mvvm_android.ui.buy.edit.EditPublishBuyDetailActivity
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class PublishBuyDetailActivity : BaseVMActivity<PublishBuyDetailViewModel>() {


    private lateinit var mBinding: ActivityPublishBuyDetailListBinding;


    private var mDatas:MutableList<CompanyListItemResponse> = mutableListOf()
    private lateinit var listAdapter: PublishBuyDetailListAdapter
    private var mPageMoreNo:Int = 2

    private var buyStatus = 1;
    private var buyOrderItem:CompanyListItemResponse? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        if (intent != null) {
            buyStatus = intent.getIntExtra("buyStatus", buyStatus)
            buyOrderItem = IntentUtils.getSerializableExtra(intent, "buyOrderItem", CompanyListItemResponse::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPublishBuyDetailListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        //registerDefaultLoad(mBinding.refreshLayout, ApiService.COMPANY_LIST)
        mBinding.ivBack.setOnClickListener {
            finish()
        }

        if (buyStatus == 0) {
            mBinding.operationButtonsContainer.visibility = View.VISIBLE
            mBinding.deleteButtonContainer.setOnClickListener {
                  confirmDeleteBuy()
            }

            mBinding.editButtonContainer.setOnClickListener {
                val intent = Intent(this@PublishBuyDetailActivity, EditPublishBuyDetailActivity::class.java)
                intent.putExtra("buyOrderItem", "")
                startActivity(intent)
            }
        }

        mBinding.refreshLayout.setEnableRefresh(false)
        mBinding.refreshLayout.setEnableLoadMore(false)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {

        }
        mBinding.refreshLayout.setOnLoadMoreListener {

        }

        mDatas.add(CompanyListItemResponse(
            id = 10,
            userId = 10,
            companyName = "",
            legal = "",
            identify = "",
            license = "",
            registerAddress = "",
            creditCode = "",
            mobile = "",
            bankAccount = "",
            bankName = "",
            describe = "",
            entType = 10,
            handStatus = 1,
            status = 2
        ))

        mDatas.add(CompanyListItemResponse(
            id = 10,
            userId = 10,
            companyName = "",
            legal = "",
            identify = "",
            license = "",
            registerAddress = "",
            creditCode = "",
            mobile = "",
            bankAccount = "",
            bankName = "",
            describe = "",
            entType = 10,
            handStatus = 1,
            status = 2
        ))

        listAdapter = PublishBuyDetailListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
            adapter, view, position ->
            val companyListItem = mDatas.get(position)
            val intent = Intent(this@PublishBuyDetailActivity, PhonePreviewerActivity::class.java)
            intent.putExtra("imageUrls", arrayOf<String>("https://img.alicdn.com/imgextra/i1/36976852/O1CN01xjcBhG20UGsIv7TR9_!!36976852.jpg"))
            startActivity(intent)
        }
        val layoutManager = GridLayoutManager(this@PublishBuyDetailActivity, 2)
        mBinding.recyclerView.layoutManager = layoutManager
        mBinding.recyclerView.adapter = listAdapter
    }

    override fun initDataObserver() {
        super.initDataObserver()
    }

    override fun initData() {
        super.initData()
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
    fun onRefreshUser(publishBuyEvent : PublishBuyEvent){
        if (publishBuyEvent.isPublishSuccess) {
            finish();
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    fun  confirmDeleteBuy() {
        val builder = AlertDialog.Builder(this@PublishBuyDetailActivity)
        builder.setMessage("确认删除吗？")
        builder.setNeutralButton("取消", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

            }

        })
        builder.setPositiveButton("确认", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                deleteBuyItem()
            }

        })
        builder.show()
    }

    fun deleteBuyItem(){
        showLoadingDialog()
        val id  =  0L;
        mViewModel.deleteReceiveAddress(id).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                EventBusUtils.postEvent(PublishBuyEvent(true))
            } else {
                Toast.makeText(applicationContext,"删除失败 " + it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

}