package com.zhongpin.mvvm_android.ui.buy.add

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.zhilianshidai.pindan.app.databinding.ActivityAddPublishBuyDetailListBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.PublishBuyEvent
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.mine.company.CompanyListActivity
import com.zhongpin.mvvm_android.ui.mine.company.address.AddressListActivity


@EventBusRegister
class AddPublishBuyDetailActivity : BaseVMActivity<AddPublishBuyDetailViewModel>() {


    private lateinit var mBinding: ActivityAddPublishBuyDetailListBinding;


    private var mDatas:MutableList<AddPublishBuyImageItem> = mutableListOf()
    private lateinit var listAdapter: AddPublishBuyDetailListAdapter

    private var mCompanies:MutableList<CompanyListItemResponse> = mutableListOf()
    private var mPageMoreNo:Int = 2
    private var mSelectDate:String? = null;
    private var mSelectCompany = -1;

    private var mAddresss:MutableList<AddressListItemResponse> = mutableListOf()
    private var mSelectAddress = -1;


    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityAddPublishBuyDetailListBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        registerDefaultLoad(mBinding.refreshLayout, ApiService.COMPANY_LIST)
        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.chooseCompanyContainer.setOnClickListener {
            if (mCompanies.isEmpty()) {
                showAddCompanyDialog()
                return@setOnClickListener
            }
            val companies = mutableListOf<String>();
            mCompanies.forEach {
                companies.add(it.companyName ?: "")
            }
            val picker = OptionPicker(this@AddPublishBuyDetailActivity)
            picker.setTitle("请选择公司")
            picker.setData(companies.toList())
            picker.setDefaultPosition(mSelectCompany)
            picker.setOnOptionPickedListener { position, item ->
                if (position != mSelectCompany) {
                    mBinding.chooseCompanyAreaText.setText(companies[position])
                    mSelectCompany = position
                    mAddresss.clear()
                    mSelectAddress = -1;
                    mBinding.chooseShouhuoAddressText.setText("")
                    requestShouHuoAddress()
                }
            }
            picker.show()
        }

        mBinding.chooseAddressContainer.setOnClickListener {
            if (mCompanies.isEmpty()) {
                showAddCompanyDialog()
                return@setOnClickListener
            }
            if (mSelectCompany < 0) {
                Toast.makeText(applicationContext,"请先选择企业", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val addresss = mutableListOf<String>();
            mAddresss.forEach {
                addresss.add(it.toShouHuoAddress())
            }
            val picker = OptionPicker(this@AddPublishBuyDetailActivity)
            picker.setTitle("请选择收货地址")
            picker.setData(addresss.toList())
            picker.setDefaultPosition(mSelectAddress)
            picker.setOnOptionPickedListener { position, item ->
                if (position != mSelectAddress) {
                    mBinding.chooseShouhuoAddressText.setText(addresss[position])
                    mSelectAddress = position
                }
            }
            picker.show()
        }

        mBinding.chooseDateContainer.setOnClickListener {
            val picker: DatePicker = DatePicker(this@AddPublishBuyDetailActivity)
            picker.setOnDatePickedListener { year, month, day ->
                val date = String.format("%d%02d%02d", year, month, day)
                mSelectDate = date;
                mBinding.chooseDateAreaText.setText(date)
            }
            picker.show();
        }



        mBinding.refreshLayout.setEnableRefresh(false)
        mBinding.refreshLayout.setEnableLoadMore(false)
        mBinding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        mBinding.refreshLayout.setRefreshFooter(ClassicsFooter(this).setFinishDuration(0))
        mBinding.refreshLayout.setOnRefreshListener {

        }
        mBinding.refreshLayout.setOnLoadMoreListener {

        }

        mDatas.add(
            AddPublishBuyImageItem(
            isAdd = false,
            filePath = null,
            imageUrl = "https://img.alicdn.com/imgextra/i3/2212396681964/O1CN017Q1xkB1QNYqvrbZah_!!2212396681964.jpg"
        ))

        listAdapter = AddPublishBuyDetailListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
            adapter, view, position ->
            val  item = mDatas.get(position)
            val intent = Intent(this@AddPublishBuyDetailActivity, PhonePreviewerActivity::class.java)
            intent.putExtra("imageUrls", arrayOf<String>(item.getBuyImageUrl()))
            startActivity(intent)
        }
        listAdapter.setBuyEditMode(true)
        val layoutManager = GridLayoutManager(this@AddPublishBuyDetailActivity, 2)
        mBinding.recyclerView.layoutManager = layoutManager
        mBinding.recyclerView.adapter = listAdapter

    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mFirstPageData.observe(this) {
            if(it.success) {
                mPageMoreNo = 2
                val records = it.data?.records
                if (records.isNullOrEmpty()) {
                    showEmpty(ApiService.COMPANY_LIST)
                    mCompanies.clear()
                } else {
                    showSuccess(ApiService.COMPANY_LIST)
                    mCompanies.clear()
                    mCompanies.addAll(records.filter { it.status == 1 }.toList())
                }
            }
        }

        mViewModel.mAddressData.observe(this) {
             dismissLoadingDialog()
            if(it.success) {
                val records = it.data;
                if (records.isNullOrEmpty()) {
                    mAddresss.clear()
                } else {
                    mAddresss.clear()
                    mAddresss.addAll(records)
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getFirstPageCompanyList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : CompanyInfoChangeEvent){
        if (infoEvent.isChange) {
        }
    }



    fun showAddCompanyDialog() {
        val builder = AlertDialog.Builder(this@AddPublishBuyDetailActivity)
        builder.setTitle("请先添加公司")
        builder.setMessage("请在我的公司中添加公司后，再进行采购。")
        builder.setPositiveButton("去添加", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val intent = Intent(this@AddPublishBuyDetailActivity, CompanyListActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        builder.setNegativeButton("取消", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }
        })
        builder.show()
    }

    fun requestShouHuoAddress() {
        if (mSelectCompany < 0){
            return
        }
        showLoadingDialog()
        val item = mCompanies[mSelectCompany];
        mViewModel.getEntReceiveAddressList(item.id)
    }

    fun showShouHuoAddressDialog() {
        val builder = AlertDialog.Builder(this@AddPublishBuyDetailActivity)
        builder.setTitle("请先添加公司")
        builder.setMessage("请在我的公司中添加公司后，再进行采购。")
        builder.setPositiveButton("去添加", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val item = mCompanies[mSelectCompany];
                val intent = Intent(this@AddPublishBuyDetailActivity, AddressListActivity::class.java)
                intent.putExtra("entId", item.id)
                startActivity(intent)
                finish()
            }
        })
        builder.setNegativeButton("取消", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }
        })
        builder.show()
    }

    fun showTipDialog() {
        val builder = AlertDialog.Builder(this@AddPublishBuyDetailActivity)
        builder.setTitle("信息提交成功")
        builder.setMessage("您填写的采购信息已提交，\n" +
                "请等待人工处理。\n")
        builder.setPositiveButton("我知道了", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }
        })
        builder.show()
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

    fun submitPublishBuy() {
        val selectFiles: MutableList<String> = mutableListOf();
        for (item in mDatas) {
            if (!item.filePath.isNullOrEmpty()) {
                selectFiles.add(item.filePath ?: "");
            }
        }
        if (selectFiles.isNotEmpty()) {
            showLoadingDialog();
            mViewModel.uploadImageList(selectFiles).observe(this) {
                var allSuccess = true;
                var message : String = "";
                val imageUrls:MutableList<String> = mutableListOf();
                for(response in it) {
                    if (!response.success) {
                        allSuccess = false;
                        if (!TextUtils.isEmpty(response.msg)) {
                            message = response.msg;
                        }
                    }
                    response.data?.let {
                            imageUrl ->
                        imageUrls.add(imageUrl)
                    }
                }
                if (allSuccess) {
                    var i=0;
                    for (item in mDatas) {
                        if (!item.filePath.isNullOrEmpty()) {
                            item.imageUrl = imageUrls[i];
                            i++;
                        }
                    }
                    submitFormInfo();
                } else {
                    dismissLoadingDialog()
                    Toast.makeText(applicationContext,"图片上传失败 " + message, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            showLoadingDialog()
            submitFormInfo();
        }
    }

    private fun submitFormInfo() {
        dismissLoadingDialog();
        listAdapter.setBuyEditMode(false)
        EventBusUtils.postEvent(PublishBuyEvent(true))
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}