package com.zhongpin.mvvm_android.ui.mine.company.sign

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityCompanySignBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.CompanySignInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.showContractKeFuDialog
import com.zhongpin.mvvm_android.ui.mine.company.result.CompanySignResultActivity
import com.zhongpin.mvvm_android.ui.mine.company.submit.SubmitCompanyInfoActivity
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils


/**
 * 添加企业认证
 * */
class CompanySignActivity : BaseVMActivity<CompanySignViewModel>() {


    private lateinit var mBinding: ActivityCompanySignBinding;
    private var mLoadingDialog: LoadingDialog? = null



    private lateinit var listAdapter: CompanySignImageUploadListAdapter
    private var mDatas:MutableList<CompanySignImageItem> = mutableListOf()


    private var from: String? = null
    private var companyInfo: CompanyListItemResponse? = null
    private var companyId:Long ? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
       ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            from = intent.getStringExtra("from") ?: ""
            companyInfo = IntentUtils.getSerializableExtra(intent, "companyInfo", CompanyListItemResponse::class.java)
            companyId = intent.getLongExtra("companyId", 0);
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityCompanySignBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    @SuppressLint("UseKtx")
    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener { finish() }

        SpanUtils.with(mBinding.contractTip)
            .append("请等待区域经理联系签约，如有疑问，可")
            .append("联系客服")
            .setForegroundColor(Color.parseColor("#557EF7"))
            .setClickSpan(object: ClickableSpan() {
                override fun onClick(p0: View) {
                    showContractKeFuDialog()
                }
            }).append("。").create()

        if (companyInfo != null && companyInfo?.contract != null) {
            val contract =  companyInfo?.contract ?: emptyList();
            contract.forEach {
                mDatas.add(
                    CompanySignImageItem(
                        isAdd = false,
                        filePath = null,
                        imageUrl = it
                    ))
            }
        }

        if (companyInfo != null) {
            mBinding.areaManagerPhone.setText(companyInfo?.regionMobile ?: "")
        }

        listAdapter = CompanySignImageUploadListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
                adapter, view, position ->
            val  item = mDatas.get(position)
            val intent = Intent(this@CompanySignActivity, PhonePreviewerActivity::class.java)
            intent.putExtra("imageUrls", arrayOf<String>(item.getSignImageUrl()))
            startActivity(intent)
        }
        listAdapter.setBuyEditMode(true)
        val layoutManager = GridLayoutManager(this@CompanySignActivity, 3)
         mBinding.uploadImageRecyclerView.layoutManager = layoutManager
        mBinding.uploadImageRecyclerView.adapter = listAdapter


        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit()
        }

        mBinding.backEdit.setOnClickListener {
            if (from != "submit") {
                val intent = Intent(this@CompanySignActivity, SubmitCompanyInfoActivity::class.java)
                intent.putExtra("companyInfo", companyInfo)
                intent.putExtra("companyId", companyId)
                startActivity(intent)
            }
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }


    fun checkAndSubmit() {
        val selectFiles: MutableList<String> = mutableListOf();
        for (item in mDatas) {
            if (!item.isAdd && !item.filePath.isNullOrEmpty()) {
                selectFiles.add(item.filePath ?: "");
            }
        }

        val contract = mutableListOf<String>();
        for (item in mDatas) {
            if (!item.isAdd && !TextUtils.isEmpty(item.getHttpSignImageUrl())) {
                contract.add(item.getHttpSignImageUrl());
            }
        }

        if (selectFiles.isEmpty() && contract.isEmpty()) {
            Toast.makeText(applicationContext,"请上传服务合同", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.areaManagerPhone.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入区域经理手机号", Toast.LENGTH_LONG).show()
            return
        }
        uploadSignImages()
    }

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

    fun uploadSignImages() {
        val selectFiles: MutableList<String> = mutableListOf();
        for (item in mDatas) {
            if (!item.isAdd && !TextUtils.isEmpty(item.filePath)) {
                selectFiles.add(item.filePath ?: "");
            }
        }
        showLoadingDialog();
        if (selectFiles.isEmpty()) {
            submitFormInfo();
            return
        }
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
                    if (!item.isAdd && !TextUtils.isEmpty(item.filePath)) {
                        item.imageUrl = imageUrls[i];
                        i++;
                    }
                }
                submitFormInfo();
            } else {
                dismissLoadingDialog()
                Toast.makeText(applicationContext,"合同上传失败 " + message, Toast.LENGTH_LONG).show()
            }
        }
    }

    
    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3217641/detail/55937859?spaceKey=space-64stfp
    fun submitFormInfo() {
        val contract = mutableListOf<String>();
        for (item in mDatas) {
            if (!item.isAdd && !TextUtils.isEmpty(item.getHttpSignImageUrl())) {
                contract.add(item.getHttpSignImageUrl());
            }
        }

        val parameter:HashMap<String,Any> = hashMapOf()
        parameter["id"] = companyInfo?.id ?: (companyId ?: 0)
        parameter["regionMobile"] = mBinding.areaManagerPhone.text.trim().toString()
        parameter["contract"] = contract


        mViewModel.signEntInfoAuth(parameter).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                EventBusUtils.postEvent(CompanyInfoChangeEvent(true))
                EventBusUtils.postEvent(CompanySignInfoChangeEvent(true))
                goCompanySignResultPage()
            } else {
                Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goCompanySignResultPage() {
        val intent = Intent(this@CompanySignActivity, CompanySignResultActivity::class.java)
        startActivity(intent)
        finish()
    }
}