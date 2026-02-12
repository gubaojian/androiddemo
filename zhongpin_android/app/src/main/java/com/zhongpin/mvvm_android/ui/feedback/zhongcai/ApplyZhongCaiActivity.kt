package com.zhongpin.mvvm_android.ui.feedback.zhongcai

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.zhilianshidai.pindan.app.databinding.ActivityApplyZhongcaiBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.FeedbackChangeEvent
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.common.BoxConfigData
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class ApplyZhongCaiActivity : BaseVMActivity<ApplyZhongCaiViewModel>() {


    private lateinit var mBinding: ActivityApplyZhongcaiBinding;

    private var orderListItem: CompanyListItemResponse? = null

    private var selectFeedbackType:Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        if (intent != null) {
            orderListItem = IntentUtils.getSerializableExtra(intent, "orderListItem", CompanyListItemResponse::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityApplyZhongcaiBinding.inflate(layoutInflater, container, false)
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
       // BannerUtils.setBannerRound(mBinding.yingYeZhiZhao, SizeUtils.dp2px(8.0f).toFloat())
        val item = orderListItem;
        if (item == null) {
            Toast.makeText(applicationContext,"请传入订单详情信息", Toast.LENGTH_LONG).show()
            //return;
        }

        mBinding.discountRequireContainer.visibility = View.GONE;
        mBinding.chooseFeedbackTypeContainer.setOnClickListener {
            val feedbackTypes = BoxConfigData.feedbackTypes
            val picker = OptionPicker(this@ApplyZhongCaiActivity)
            picker.setTitle("请选择申诉类别")
            picker.setData(feedbackTypes.toList())
            picker.setDefaultPosition(selectFeedbackType)
            picker.setOnOptionPickedListener { position, item ->
                mBinding.chooseFeedbackTypeText.text = feedbackTypes[position]
                selectFeedbackType = position
                if (selectFeedbackType == 1 || selectFeedbackType == 3) {
                    mBinding.discountRequireContainer.visibility = View.VISIBLE
                } else {
                    mBinding.discountRequireContainer.visibility = View.GONE
               }
            }
            picker.show()
        }

        mBinding.addFeedback.setOnClickListener {
             checkAndSubmit()
        }

        //registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)
    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mUserAuthInfoData.observe(this) {
            showSuccess(ApiService.USER_AUTH_INFO)
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getUserAuthInfoData()
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : CompanyInfoChangeEvent){
        if (infoEvent.isChange) {
            finish()
        }
    }

    fun showDeleteConfirmDialog() {
        val builder = AlertDialog.Builder(this@ApplyZhongCaiActivity)
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
         val id  = orderListItem?.id ?: 0
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

    fun showTipDialogFrom() {
        val builder = AlertDialog.Builder(this@ApplyZhongCaiActivity)
        builder.setTitle("信息提交成功")
        builder.setMessage("您填写的仲裁信息已提交，\n" +
                "请等待人工处理。\n" +
                "如需查看仲裁详情，可在“我的-我的申诉”查看。")
        builder.setPositiveButton("我知道了", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }
        })
        builder.show()
    }

    fun  checkAndSubmit() {
        val selectImages = mBinding.uploadImageGridView.getSelectImages();
        val imageUrls:MutableList<String> = mutableListOf();
        if (selectImages.isNotEmpty()) {
             showLoadingDialog();
             val filePaths:MutableList<String> = mutableListOf();
             for(selectImage in selectImages) {
                 filePaths.add(selectImage.filePath)
             }
             mViewModel.uploadImageList(filePaths).observe(this) {
                 var allSuccess = true;
                 var message : String = "";
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
                    submitFormInfo();
                } else {
                    dismissLoadingDialog()
                    Toast.makeText(applicationContext,"图片上传失败 " + message, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            showLoadingDialog()
            submitFormInfo()
        }
    }

    fun submitFormInfo() {
        dismissLoadingDialog();
        showTipDialogFrom();
        EventBusUtils.postEvent(FeedbackChangeEvent(true));
    }


}