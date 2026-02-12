package com.zhongpin.mvvm_android.ui.debug

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.NotificationUtils
import com.google.zxing.client.android.Intents
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityDebugBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.TokenExpiredEvent
import com.zhongpin.mvvm_android.common.login.LoginUtils
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.location.OnceLocationHelper
import com.zhongpin.mvvm_android.service.UserNotificationUtil
import com.zhongpin.mvvm_android.ui.buy.PublishBuyActivity
import com.zhongpin.mvvm_android.ui.common.requestLocation
import com.zhongpin.mvvm_android.ui.feedback.PingZhiFeedbackActivity
import com.zhongpin.mvvm_android.ui.feedback.chooseorder.FeedbackChooseOrderActivity
import com.zhongpin.mvvm_android.ui.feedback.zhongcai.ApplyZhongCaiActivity
import com.zhongpin.mvvm_android.ui.login.LoginActivity
import com.zhongpin.mvvm_android.ui.mine.company.CompanyListActivity
import com.zhongpin.mvvm_android.ui.mine.company.address.add.AddReceiveAddressActivity
import com.zhongpin.mvvm_android.ui.mine.company.address.choose.ChooseAddressListActivity
import com.zhongpin.mvvm_android.ui.mine.company.result.CompanySignResultActivity
import com.zhongpin.mvvm_android.ui.mine.company.sign.CompanySignActivity
import com.zhongpin.mvvm_android.ui.mine.company.submit.SubmitCompanyInfoActivity
import com.zhongpin.mvvm_android.ui.order.add.AddOrderActivity
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity
import com.zhongpin.mvvm_android.ui.private.PrivateTipDialog
import com.zhongpin.mvvm_android.ui.scan.ScanCaptureActivity
import com.zhongpin.mvvm_android.ui.shouhuo.ConfirmShuoHuoActivity
import com.zhongpin.mvvm_android.ui.shouhuo.result.ShuoHuoSuccessActivity
import com.zhongpin.mvvm_android.ui.verify.company.CompanyVerifyActivity
import com.zhongpin.mvvm_android.ui.verify.person.PersonVerifyActivity
import java.util.concurrent.atomic.AtomicInteger


class DebugActivity : BaseVMActivity<DebugViewModel>() {


    private lateinit var mBinding: ActivityDebugBinding;


    private var selectFeedbackType:Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityDebugBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        //registerDefaultLoad(mBinding.loadContainer, ApiService.USER_AUTH_INFO)

        mBinding.ivBack.setOnClickListener { finish() }

        val binding = mBinding;
        val activity = this@DebugActivity;
        val mActivity = this@DebugActivity;
        binding.login.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.unLogin.setOnClickListener {
            EventBusUtils.postEvent(TokenExpiredEvent(true));
        }

        binding.verify.setOnClickListener {
            val intent = Intent(activity, PersonVerifyActivity::class.java)
            startActivity(intent)
        }

        binding.entVerify.setOnClickListener {
            val intent = Intent(activity, CompanyVerifyActivity::class.java)
            startActivity(intent)
        }

        binding.submitCompanyInfoEntry.setOnClickListener {
             submitCompanyInfoEntry()
        }

        binding.submitCompanyInfo.setOnClickListener {
            val intent = Intent(activity, SubmitCompanyInfoActivity::class.java)
            startActivity(intent)
        }

        binding.companySign.setOnClickListener {
            val intent = Intent(activity, CompanySignActivity::class.java)
            startActivity(intent)
        }

        binding.companySignResult.setOnClickListener {
            val intent = Intent(activity, CompanySignResultActivity::class.java)
            startActivity(intent)
        }

        binding.addOrder.setOnClickListener {
            val intent = Intent(activity, AddOrderActivity::class.java)
            startActivity(intent)
        }

        binding.addAddress.setOnClickListener {
            val intent = Intent(activity, AddReceiveAddressActivity::class.java)
            startActivity(intent)
        }

        binding.chooseAddress.setOnClickListener {
            val intent = Intent(activity, ChooseAddressListActivity::class.java)
            startActivity(intent)
        }


        binding.companyList.setOnClickListener {
            LoginUtils.ensureLogin(activity) {
                val intent = Intent(activity, CompanyListActivity::class.java)
                startActivity(intent)
            }
        }

        binding.photo.setOnClickListener {
            val intent = Intent(activity, PhonePreviewerActivity::class.java)
            intent.putExtra("imageUrls", arrayOf<String>(
                "https://img.alicdn.com/bao/uploaded/i2/2268175280/O1CN01wvhOlY1osIBwjOCOP_!!2268175280.jpg_460x460q90.jpg_.webp",
                "https://img.alicdn.com/bao/uploaded/i3/3928142771/O1CN01tzdOzK1WLANtDlnTJ_!!3928142771.jpg_460x460q90.jpg_.webp"))
            startActivity(intent)
        }

        val id = AtomicInteger();
        binding.notify.setOnClickListener {
            NotificationUtils.notify(id.incrementAndGet(), {
                    param ->
                val intent = Intent(mActivity, ShuoHuoSuccessActivity::class.java)
                param.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("众品")
                    .setContentText("众品通知服务")
                    .setContentIntent(PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE))
                    .setAutoCancel(true)
            })
        }

        binding.start.setOnClickListener {
            UserNotificationUtil.startService()
        }

        binding.stop.setOnClickListener {
            UserNotificationUtil.stopService()
        }

        binding.scan2.setOnClickListener {
            val intent = Intent(activity, ScanCaptureActivity::class.java)
            startActivityForResult(intent, 100)
        }

        binding.tokenExpired.setOnClickListener {
            EventBusUtils.postEvent(TokenExpiredEvent(true));
        }

        mBinding.confirmShouhuo.setOnClickListener {
            val intent = Intent(activity, ConfirmShuoHuoActivity::class.java)
            startActivity(intent)
        }

        mBinding.confirmFeedbackChooseOrder.setOnClickListener {
            val intent = Intent(activity, FeedbackChooseOrderActivity::class.java)
            startActivity(intent)
        }

        mBinding.confirmFeedback.setOnClickListener {
            val intent = Intent(activity, PingZhiFeedbackActivity::class.java)
            startActivity(intent)
        }

        mBinding.addBuyXuqiu.setOnClickListener {
            val intent = Intent(activity, PublishBuyActivity::class.java)
            startActivity(intent)
        }

        mBinding.applyZhongCai.setOnClickListener {
            val intent = Intent(activity, ApplyZhongCaiActivity::class.java)
            startActivity(intent)
        }

        mBinding.privacyContent.setOnClickListener {
            val privateTipDialog = PrivateTipDialog(activity);
            privateTipDialog.show()
        }

        mBinding.dialog.setOnClickListener {
            val dialog = ConfirmDialog(
                mContext = activity,
                title = "确认下单",
                message = "即将提交订单\n确认提交后将从企业账户中支付xxxx元\n此操作不撤销，请再次核对订单详细信息",
                confirmText = "支付订单"
                );
            dialog.showDialog(activity)
        }

        mBinding.locationTest.setOnClickListener {
            requestLocation {
                LogUtils.d("Location", "Location ${GsonUtils.toJson(it)}")
            }
        }

    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mUserAuthInfoData.observe(this) {
            showSuccess(Constant.COMMON_KEY)
        }
    }

    override fun initData() {
        super.initData()
        /**
        mViewModel.getUserAuthInfoData()
        */
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(applicationContext,"扫描结果," + data?.getStringExtra(Intents.Scan.RESULT), Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun submitCompanyInfoEntry() {
        mViewModel.getCompanyInfo().observe(this) {
            if (it.success) {
               val item = it.data;
               if (item == null) {
                   val intent = Intent(this@DebugActivity, SubmitCompanyInfoActivity::class.java)
                   startActivity(intent)
               } else if (item.status == 0) {
                    val intent = Intent(this@DebugActivity, CompanySignActivity::class.java)
                    intent.putExtra("companyInfo", item)
                    startActivity(intent)
                } else {
                   val intent = Intent(this@DebugActivity, CompanySignResultActivity::class.java)
                   intent.putExtra("companyInfo", item)
                   startActivity(intent)
               }
            } else {
                Toast.makeText(applicationContext, "错误" + it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }


}