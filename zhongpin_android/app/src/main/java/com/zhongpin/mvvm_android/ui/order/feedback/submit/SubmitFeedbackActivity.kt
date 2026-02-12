package com.zhongpin.mvvm_android.ui.order.feedback.submit

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Box
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivitySubmitFeedbackBinding
import com.zhongpin.lib_base.ktx.limit2Decimal
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.FeedbackChangeEvent
import com.zhongpin.mvvm_android.bean.OrderFeedbackItem
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.common.BoxConfigData
import com.zhongpin.mvvm_android.ui.common.FeedbackLabelItem
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import com.zhongpin.mvvm_android.view.upload.GirdImageUploadListAdapter
import com.zhongpin.mvvm_android.view.upload.GridUploadImageItem
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class SubmitFeedbackActivity : BaseVMActivity<SubmitFeedbackViewModel>() {


    private lateinit var mBinding: ActivitySubmitFeedbackBinding;

    private var feedbackItem: OrderFeedbackItem? = null

    private var selectFeedbackType: FeedbackLabelItem? = null;
    private var solutionType: FeedbackLabelItem? = null;

    private lateinit var listAdapter: GirdImageUploadListAdapter
    private var mDatas:MutableList<GridUploadImageItem> = mutableListOf()

    private var orderId:Long = 0;
    private var orderItem: OrderItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            orderId = intent.getLongExtra("orderId", 0)
            feedbackItem = IntentUtils.getSerializableExtra(intent, "feedbackItem", OrderFeedbackItem::class.java)
            if (orderId >= 0) {
                orderItem = ShareParamDataUtils.orderItem
                ShareParamDataUtils.orderItem = null
            }
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivitySubmitFeedbackBinding.inflate(layoutInflater, container, false)
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

        mBinding.feedbackMoneyAmount.limit2Decimal()

        feedbackItem?.let {
            orderId = it.orderId ?: 0
            showEditFeedbackItem(it)
        }
        if (orderId <= 0) {
            Toast.makeText(applicationContext,"请传入订单详情信息", Toast.LENGTH_LONG).show()
            finish()
            return;
        }

        mBinding.feedbackTypes.setOnClickListener {
            val feedbackTypes = BoxConfigData.feedbackMap.keys.toList()
            val labels = feedbackTypes.map { it.label }.toList()
            val picker = OptionPicker(this@SubmitFeedbackActivity)
            picker.setTitle("请选择申诉类型")
            picker.setData(labels)
            picker.setDefaultValue(selectFeedbackType?.label ?: "")
            picker.setOnOptionPickedListener { position, item ->
                mBinding.feedbackTypeText.text = labels[position]
                selectFeedbackType = feedbackTypes[position]
                solutionType = null
                mBinding.solutionTypeText.text = ""
            }
            picker.show()
        }

        mBinding.solutionTypes.setOnClickListener {
            if (selectFeedbackType  == null) {
                Toast.makeText(applicationContext,"请先选择申诉类型", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val feedbackTypes = BoxConfigData.feedbackMap.keys.toList()
            val solutionTypes = BoxConfigData.feedbackMap[selectFeedbackType] ?: emptyList();
            val labels = solutionTypes.map { it.label }.toList()
            val picker = OptionPicker(this@SubmitFeedbackActivity)
            picker.setTitle("请选择处理方案")
            picker.setData(labels)
            picker.setDefaultValue(solutionType?.label ?: "")
            picker.setOnOptionPickedListener { position, item ->
                mBinding.solutionTypeText.text = labels[position]
                solutionType = solutionTypes[position]
            }
            picker.show()
        }


        listAdapter = GirdImageUploadListAdapter(this, mDatas)
        listAdapter.maxSelectNum = 5
        listAdapter.setOnItemClickListener {
                adapter, view, position ->
            val  item = mDatas.get(position)
            val intent = Intent(this@SubmitFeedbackActivity, PhonePreviewerActivity::class.java)
            intent.putExtra("imageUrls", arrayOf<String>(item.getHttpOrFileImageUrl()))
            startActivity(intent)
        }
        listAdapter.setBuyEditMode(true)
        val layoutManager = GridLayoutManager(this@SubmitFeedbackActivity, 3)
        mBinding.uploadImageRecyclerView.layoutManager = layoutManager
        mBinding.uploadImageRecyclerView.adapter = listAdapter

        //添加模式
        if (feedbackItem == null) {
            orderItem?.let {
                mBinding.feedbackAmount.addTextChangedListener(
                    afterTextChanged = { input ->
                        val amount = input?.trim().toString().toIntOrNull() ?: 0
                        val price = orderItem?.price?.toDoubleOrNull() ?: 0.0
                        val totalPrice = amount*price;
                        if (totalPrice > 0) {
                            mBinding.feedbackMoneyAmount.text.clear()
                            mBinding.feedbackMoneyAmount.text.append(totalPrice.toString())
                        }
                    }
                )
            }
        }


        mBinding.btnSubmit.setOnClickListener {
             checkAndSubmit()
        }

        //registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)
    }

    private fun showEditFeedbackItem(item: OrderFeedbackItem) {
        mBinding.ivTitle.text = "编辑申诉";
        if (BoxConfigData.isFeedbackCodeValid(item.appealType)
            && BoxConfigData.isFeedbackCodeValid(item.handleType)) {
            selectFeedbackType = FeedbackLabelItem(
                code = (item.appealType ?: 0).toLong(),
                label = item.appealTypeName ?: ""
            )
            solutionType = FeedbackLabelItem(
                code = (item.handleType ?: 0).toLong(),
                label = item.handleTypeName ?: ""
            )
            mBinding.feedbackTypeText.text = item.appealTypeName
            mBinding.solutionTypeText.text = item.handleTypeName
        }

        item.num?.let {
            mBinding.feedbackAmount.text.append(it.toString())
        }
        item.price?.let {
            mBinding.feedbackMoneyAmount.text.append(it)
        }
        item.description?.let {
            mBinding.feedbackDesc.text.append(it)
        }
        item.imageList?.forEach {
            mDatas.add(GridUploadImageItem(
                isAdd = false,
                imageUrl = it
            ))
        }
    }

    override fun initDataObserver() {
        super.initDataObserver()
    }

    override fun initData() {
        super.initData()
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFeedbackChangeEvent(event : FeedbackChangeEvent){
        if (event.isFeedbackSuccess) {

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

    fun  checkAndSubmit() {
        if (TextUtils.isEmpty(mBinding.feedbackTypeText.text)
            || selectFeedbackType == null) {
            Toast.makeText(applicationContext,"请选择申诉类型", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.solutionTypeText.text)
            || solutionType == null) {
            Toast.makeText(applicationContext,"请选择处理方案", Toast.LENGTH_LONG).show()
            return
        }

        val feedbackMoneyAmount = mBinding.feedbackMoneyAmount.text.trim().toString().toDoubleOrNull() ?: 0.0
        val maxAmount = 99999999.99
        if (feedbackMoneyAmount > maxAmount) {
            Toast.makeText(applicationContext,"申诉金额不能超过99999999.99", Toast.LENGTH_LONG).show()
            return
        }

        val selectFiles: MutableList<String> = mutableListOf();
        for (item in mDatas) {
            if (!item.isAdd && !item.filePath.isNullOrEmpty()) {
                selectFiles.add(item.filePath ?: "");
            }
        }

        val allFiles = mutableListOf<String>();
        for (item in mDatas) {
            if (!item.isAdd && !TextUtils.isEmpty(item.getHttpOrFileImageUrl())) {
                allFiles.add(item.getHttpOrFileImageUrl());
            }
        }

        if (selectFiles.isEmpty() && allFiles.isEmpty()) {
            Toast.makeText(applicationContext,"请上传申诉凭证", Toast.LENGTH_LONG).show()
            return
        }

        uploadSignImages()
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
                Toast.makeText(applicationContext,"上传失败 " + message, Toast.LENGTH_LONG).show()
            }
        }
    }

    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3217641/detail/55937859?spaceKey=space-64stfp
    fun submitFormInfo() {
        val imageUrls = mutableListOf<String>();
        for (item in mDatas) {
            if (!item.isAdd && !TextUtils.isEmpty(item.getHttpImageUrl())) {
                imageUrls.add(item.getHttpImageUrl());
            }
        }

        val parameter:HashMap<String,Any> = hashMapOf()
        parameter["orderId"] = orderId
        parameter["appealType"] =  selectFeedbackType?.code ?: 0
        parameter["handleType"] = solutionType?.code ?: 0
        parameter["imageList"] = imageUrls
        if (!TextUtils.isEmpty(mBinding.feedbackAmount.text.trim().toString())) {
            parameter["num"] = mBinding.feedbackAmount.text.trim().toString()
        }

        if (!TextUtils.isEmpty(mBinding.feedbackMoneyAmount.text.trim().toString())) {
            parameter["price"] = mBinding.feedbackMoneyAmount.text.trim().toString()
        }

        if (!TextUtils.isEmpty(mBinding.feedbackDesc.text.trim().toString())) {
            parameter["description"] = mBinding.feedbackDesc.text.trim().toString()
        }
        if (feedbackItem == null) {
            mViewModel.addOrderFeedback(parameter).observe(this) {
                dismissLoadingDialog()
                if (it.success) {
                    EventBusUtils.postEvent(FeedbackChangeEvent(true))
                    EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                    finish()
                } else {
                    ToastExt.throttleToast(it.msg, {
                        Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                    })
                }
            }
        } else {
            parameter.remove("orderId")
            parameter["id"] = feedbackItem?.id ?: 0
            mViewModel.editOrderFeedback(parameter).observe(this) {
                dismissLoadingDialog()
                if (it.success) {
                    EventBusUtils.postEvent(FeedbackChangeEvent(true))
                    EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                    finish()
                } else {
                    ToastExt.throttleToast(it.msg, {
                        Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                    })
                }
            }
        }

    }



}