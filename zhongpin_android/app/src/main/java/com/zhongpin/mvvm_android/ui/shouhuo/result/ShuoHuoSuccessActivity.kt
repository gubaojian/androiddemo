package com.zhongpin.mvvm_android.ui.shouhuo.result

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gyf.immersionbar.ImmersionBar.*
import com.zhilianshidai.pindan.app.databinding.ActivityConfirmShouhuoBinding
import com.zhilianshidai.pindan.app.databinding.ActivityConfirmShouhuoSuccessBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.feedback.PingZhiFeedbackActivity
import com.zhongpin.mvvm_android.ui.feedback.chooseorder.FeedbackChooseOrderActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ShuoHuoSuccessActivity : BaseVMActivity<ShuoHuoSuccessViewModel>() {


    private lateinit var mBinding: ActivityConfirmShouhuoSuccessBinding;

    private var companyListItem: CompanyListItemResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        with(this).transparentBar().barColor("#57C248").fullScreen(false).init()
        if (intent != null) {
            companyListItem = IntentUtils.getSerializableExtra(intent, "companyListItem", CompanyListItemResponse::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityConfirmShouhuoSuccessBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.shenshu.setOnClickListener {
            val intent = Intent(this@ShuoHuoSuccessActivity, FeedbackChooseOrderActivity::class.java)
            startActivity(intent)
        }
        //registerPlaceHolderLoad(mBinding.loadContainer, layout.layout_loading_white)

    }

    override fun initDataObserver() {
        super.initDataObserver()

    }

    override fun initData() {
    }




    fun showDeleteConfirmDialog() {
        val builder = AlertDialog.Builder(this@ShuoHuoSuccessActivity)
        builder.setMessage("确认删除吗？")
        builder.setNeutralButton("取消", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

            }

        })
        builder.setPositiveButton("确认", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                deleteEntInfo()
            }

        })
        builder.show()
    }

    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3148316/detail/55818666?spaceKey=space-64stfp
    fun deleteEntInfo(){
        showLoadingDialog()
         val id  = companyListItem?.id ?: 0
        mViewModel.deleteEntInfoAuth(id).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                EventBusUtils.postEvent(CompanyInfoChangeEvent(true))
                finish()
            } else {
                Toast.makeText(applicationContext,"删除失败 " + it.msg, Toast.LENGTH_LONG).show()
            }
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


}