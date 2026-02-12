package com.zhongpin.mvvm_android.ui.verify.person

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.databinding.ActivityPersonVerifyBinding
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.photo.selector.GlideEngine
import com.zhongpin.mvvm_android.ui.common.BoxConfigData
import com.zhongpin.mvvm_android.ui.verify.company.CompanyVerifyActivity
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File


class PersonVerifyActivity : BaseVMActivity<PersonVerifyViewModel>() {


    private lateinit var mBinding: ActivityPersonVerifyBinding;
    private lateinit var mLoadingDialog: LoadingDialog

    private var idCardFrontImagePath:String? = null;
    private var idCardBackImagePath:String? = null;
    private var idCardFrontImageUrl:String? = null;
    private var idCardBackImageUrl:String? = null;
    private var selectCompanyType:Int = -1;



    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPersonVerifyBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mLoadingDialog = LoadingDialog(this, false)
        mBinding.ivBack.setOnClickListener { finish() }
        mBinding.btnNext.setOnClickListener {
            checkAndSubmit()
        }

        mBinding.ivRightTitle.setOnClickListener {
            finish()
        }

        mBinding.idCardFrontContainer.setOnClickListener {
            PictureSelector.create(this@PersonVerifyActivity)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setCompressEngine(object: CompressFileEngine {
                    override fun onStartCompress(
                        context: Context?,
                        source: java.util.ArrayList<Uri>?,
                        call: OnKeyValueResultCallbackListener?
                    ) {
                        if (source == null || source.isEmpty()) {
                            return;
                        }
                        Luban.with(this@PersonVerifyActivity)
                            .load(source)
                            .ignoreBy(100).setCompressListener(
                                object : OnNewCompressListener {
                                    override fun onStart() {

                                    }

                                    override fun onSuccess(source: String?, compressFile: File?) {
                                        if (call != null) {
                                            if (compressFile != null) {
                                                LogUtils.d(
                                                    "PersonVerifyActivity",
                                                    "PersonVerifyActivity compressFile " + source + " compress " + compressFile?.absolutePath
                                                            + " length " + compressFile!!.length() / 1024
                                                )
                                            }
                                            call.onCallback(source, compressFile?.absolutePath);
                                        }
                                    }

                                    override fun onError(source: String?, e: Throwable?) {
                                        if (call != null) {
                                            call.onCallback(source, null);
                                        }
                                    }
                                }
                            ).launch();
                    }

                })
                .setMaxSelectNum(1)
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>) {
                        if (result.isNullOrEmpty()) {
                            return;
                        }
                        val localMedia = result[0];
                        if (localMedia == null) {
                            return;
                        }
                        if(BuildConfig.DEBUG) {
                            //content://
                            Log.e("photo", "photo selector " + localMedia.path + " cut path " + localMedia.cutPath)
                        }
                        val filePath = localMedia.compressPath ?: localMedia.realPath;
                        if (filePath.isNullOrEmpty()) {
                            return
                        }
                        ocrFrontImage(filePath)
                    }

                    override fun onCancel() {
                    }
                })
        }

        mBinding.idCardBackContainer.setOnClickListener {
            PictureSelector.create(this@PersonVerifyActivity)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setCompressEngine(object: CompressFileEngine {
                    override fun onStartCompress(
                        context: Context?,
                        source: java.util.ArrayList<Uri>?,
                        call: OnKeyValueResultCallbackListener?
                    ) {
                        if (source == null || source.isEmpty()) {
                            return;
                        }
                        Luban.with(this@PersonVerifyActivity)
                            .load(source)
                            .ignoreBy(100).setCompressListener(
                            object : OnNewCompressListener {
                                override fun onStart() {

                                }

                                override fun onSuccess(source: String?, compressFile: File?) {
                                    if (call != null) {
                                        if (compressFile != null) {
                                            LogUtils.d(
                                                "PersonVerifyActivity",
                                                "PersonVerifyActivity compressFile " + source + " compress " + compressFile?.absolutePath
                                                        + " length " + compressFile!!.length() / 1024
                                            )
                                        }
                                        call.onCallback(source, compressFile?.absolutePath);
                                    }
                                }

                                override fun onError(source: String?, e: Throwable?) {
                                    if (call != null) {
                                        call.onCallback(source, null);
                                    }
                                }
                            }
                        ).launch();
                    }

                })
                .setMaxSelectNum(1)
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>) {
                        if (result.isNullOrEmpty()) {
                            return;
                        }
                        val localMedia = result[0];
                        if (localMedia == null) {
                            return;
                        }
                        if(BuildConfig.DEBUG) {
                            //content://
                            Log.e("photo", "photo selector " + localMedia.path + " cut path " + localMedia.cutPath)
                        }
                        val filePath = localMedia.compressPath ?: localMedia.realPath;
                        if (filePath.isNullOrEmpty()) {
                            return
                        }
                        uploadBackImage(filePath)
                    }

                    override fun onCancel() {
                    }
                })
        }


        val companyTypes = BoxConfigData.companyTypes
        /**
        mBinding.chooseCompanyTypeContainer.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("选择单位类别")

            // 设置对话框内容为单选列表项，默认选中第一项（索引为0）
            builder.setSingleChoiceItems(companyTypes, selectCompanyType, DialogInterface.OnClickListener { dialog, which -> // 当用户选择一个单位类别时，更新文本视图显示所选单位
                mBinding.chooseCompanyTypeText.text = companyTypes[which]
                selectCompanyType = which
                dialog.dismiss()
            })
            // 添加取消按钮
            builder.setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })


            // 显示对话框
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }*/

        mBinding.chooseCompanyTypeContainer.setOnClickListener {
            val picker = OptionPicker(this)
            picker.setTitle("请选择单位类别")
            picker.setData(companyTypes.toList())
            picker.setDefaultPosition(selectCompanyType)
            picker.setOnOptionPickedListener { position, item ->
                mBinding.chooseCompanyTypeText.text = companyTypes[position]
                selectCompanyType = position
            }
            picker.show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun  ocrFrontImage(filePath: String) {
        mViewModel.identifyIdCardInfo(filePath).observe(this) { outerIt ->
            if (outerIt.success) {
                idCardFrontImagePath = filePath
                outerIt.data?.let {
                    mBinding.editUserName.setText(it.name ?:"")
                    mBinding.editIdCardNo.setText(it.idCard ?: "")
                    mBinding.editAddress.setText(it.address ?: "")
                }
                //update info
                mBinding.idCardFrontText.visibility = View.GONE
                Glide.with(this@PersonVerifyActivity)
                    .load(Uri.fromFile(File(filePath)))
                    .placeholder(mBinding.idCardFront.drawable)
                    .into(mBinding.idCardFront)
            } else {
                Toast.makeText(applicationContext,"身份证信息识别失败," + outerIt.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun  uploadBackImage(filePath: String) {
        idCardBackImagePath = filePath
        mBinding.idCardBackText.visibility = View.GONE
        Glide.with(this@PersonVerifyActivity)
            .load(Uri.fromFile(File(filePath)))
            .placeholder(mBinding.idCardBack.drawable)
            .into(mBinding.idCardBack)
    }

    /**
     * show 加载中
     */
    fun showLoadingDialog() {
        mLoadingDialog.showDialog(this, false)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissLoadingDialog() {
        mLoadingDialog.dismissDialog()
    }

    fun checkAndSubmit(){
        if (TextUtils.isEmpty(idCardFrontImagePath) || TextUtils.isEmpty(idCardBackImagePath)) {
            Toast.makeText(applicationContext,"请上传身份证正反面", Toast.LENGTH_LONG).show()
            return;
        }
        if (TextUtils.isEmpty(mBinding.editUserName.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入姓名", Toast.LENGTH_LONG).show()
            return;
        }
        if (TextUtils.isEmpty(mBinding.editIdCardNo.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入身份证号", Toast.LENGTH_LONG).show()
            return;
        }
        if (TextUtils.isEmpty(mBinding.editAddress.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入联系地址", Toast.LENGTH_LONG).show()
            return;
        }
        if (selectCompanyType < 0) {
            Toast.makeText(applicationContext,"请选择单位类型", Toast.LENGTH_LONG).show()
            return;
        }
        showLoadingDialog()
        mViewModel.uploadImage(idCardFrontImagePath!!).observe(this) {
            if (it.success) {
                idCardFrontImageUrl = it.data ?: ""
                uploadBackImage()
            } else {
                dismissLoadingDialog()
                Toast.makeText(applicationContext,"图片上传失败 " + it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun uploadBackImage() {
        mViewModel.uploadImage(idCardBackImagePath!!).observe(this) {
            if (it.success) {
                idCardBackImageUrl = it.data ?: ""
                submitFormInfo()
            } else {
                dismissLoadingDialog()
                Toast.makeText(applicationContext,"图片上传失败 " + it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun submitFormInfo() {
        val parameter:HashMap<String,Any> = hashMapOf()
        parameter["address"] = mBinding.editAddress.text.trim().toString()
        parameter["idCard"] = mBinding.editIdCardNo.text.trim().toString()
        parameter["name"] = mBinding.editUserName.text.trim().toString()
        parameter["entType"] = selectCompanyType
        parameter["idCardLeft"] = idCardFrontImageUrl ?: ""
        parameter["idCardRight"] = idCardBackImageUrl ?:""
        mViewModel.submitUserInfoAuth(parameter).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                goCompanyVerify()
                finish()
            } else {
                Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun goCompanyVerify() {
        val intent = Intent(this@PersonVerifyActivity, CompanyVerifyActivity::class.java)
        startActivity(intent)
    }

}