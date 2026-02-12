package com.zhongpin.mvvm_android.ui.mine.company.result

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
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityCompanySignResultBinding
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.showContractKeFuDialog
import com.zhongpin.mvvm_android.ui.mine.company.submit.SubmitCompanyInfoActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils


/**
 * 添加企业认证
 * */
class CompanySignResultActivity : BaseVMActivity<CompanySignResultViewModel>() {


    private lateinit var mBinding: ActivityCompanySignResultBinding;
    private var mLoadingDialog: LoadingDialog? = null



    private var companyInfo:CompanyListItemResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
       ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            companyInfo = IntentUtils.getSerializableExtra(intent, "companyInfo", CompanyListItemResponse::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityCompanySignResultBinding.inflate(layoutInflater, container, false)
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
            .append("如有疑问，可")
            .append("联系客服")
            .setForegroundColor(Color.parseColor("#557EF7"))
            .setClickSpan(object: ClickableSpan(){
                override fun onClick(p0: View) {
                    showContractKeFuDialog()
                }
            }).append("。").create()

        SpanUtils.with(mBinding.failedContractTip)
            .append("如有疑问，可")
            .append("联系客服")
            .setForegroundColor(Color.parseColor("#557EF7"))
            .setClickSpan(object: ClickableSpan(){
                override fun onClick(p0: View) {
                    showContractKeFuDialog()
                }
            }).append("。").create()

        //审核状态(0:新申请,1:待审核,2:审核成功,3:审核拒绝)
        if (companyInfo != null && companyInfo?.status == 3) {
            val rejectReason = companyInfo?.rejectReason ?: "失败"
            mBinding.message.setText(rejectReason)
            if (TextUtils.isEmpty(rejectReason)) {
                mBinding.message.visibility = View.GONE
            }
            mBinding.signFailedContainer.visibility = View.VISIBLE
            mBinding.signPendingContainer.visibility = View.GONE
        } else if (companyInfo != null && companyInfo?.status == 2) {
            Toast.makeText(applicationContext,"企业认证审核已通过", Toast.LENGTH_LONG).show()
            finish()
        } else {
            mBinding.signFailedContainer.visibility = View.GONE
            mBinding.signPendingContainer.visibility = View.VISIBLE
        }

        mBinding.applyAgain.setOnClickListener {
            applyAgain()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
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



    fun applyAgain() {
        val intent = Intent(this@CompanySignResultActivity, SubmitCompanyInfoActivity::class.java)
        intent.putExtra("companyInfo", companyInfo)
        startActivity(intent)
        finish()
    }



}