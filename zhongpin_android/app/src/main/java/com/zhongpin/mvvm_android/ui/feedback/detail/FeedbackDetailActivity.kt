package com.zhongpin.mvvm_android.ui.feedback.detail

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.zhilianshidai.pindan.app.databinding.ActivityFeedbackDetailBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class FeedbackDetailActivity : BaseVMActivity<FeedbackDetailViewModel>() {


    private lateinit var mBinding: ActivityFeedbackDetailBinding;

    private var feedbackListItem: CompanyListItemResponse? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        if (intent != null) {
            feedbackListItem = IntentUtils.getSerializableExtra(intent, "feedbackListItem", CompanyListItemResponse::class.java)
        }
        super.onCreate(savedInstanceState)
    }

    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityFeedbackDetailBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener { finish() }


        val item = feedbackListItem;
        if (item == null) {
            Toast.makeText(applicationContext,"请传入公司详情信息", Toast.LENGTH_LONG).show()
            return;
        }

        val imageUrls = mutableListOf<String>();
        imageUrls.add("https://img.alicdn.com/bao/uploaded/i3/6000000000697/O1CN01BFfbeq1H1H6kz1SbU_!!6000000000697-0-mia.jpg_460x460q90.jpg_.webp")

        imageUrls.add("https://img.alicdn.com/bao/uploaded/i4/2212960743717/O1CN01h5wJz61dKR6S4hPTu_!!0-item_pic.jpg_460x460q90.jpg_.webp")

        imageUrls.add("https://img.alicdn.com/bao/uploaded/i3/6000000000697/O1CN01BFfbeq1H1H6kz1SbU_!!6000000000697-0-mia.jpg_460x460q90.jpg_.webp")

        imageUrls.add("https://img.alicdn.com/bao/uploaded/i3/6000000000697/O1CN01BFfbeq1H1H6kz1SbU_!!6000000000697-0-mia.jpg_460x460q90.jpg_.webp")

        imageUrls.add("https://img.alicdn.com/bao/uploaded/i3/6000000000697/O1CN01BFfbeq1H1H6kz1SbU_!!6000000000697-0-mia.jpg_460x460q90.jpg_.webp")


        mBinding.feedbackPhotos.setImageUrls(imageUrls);

        /**
        //registerDefaultLoad(mBinding.loadContainer, ApiService.USER_AUTH_INFO)
        */
    }

    override fun initDataObserver() {
        super.initDataObserver()
        /**
        mViewModel.mUserAuthInfoData.observe(this) {
            showSuccess(ApiService.USER_AUTH_INFO)
            mBinding.companyType.text = CompanyConfigData.getCompanyType(it.data?.entType ?: -1)
        }*/
    }

    override fun initData() {
        super.initData()
        //mViewModel.getUserAuthInfoData()
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : CompanyInfoChangeEvent){
        if (infoEvent.isChange) {
            finish()
        }
    }

    fun showDeleteConfirmDialog() {
        val builder = AlertDialog.Builder(this@FeedbackDetailActivity)
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
         val id  = feedbackListItem?.id ?: 0
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