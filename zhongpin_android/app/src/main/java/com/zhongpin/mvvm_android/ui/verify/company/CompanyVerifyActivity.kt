package com.zhongpin.mvvm_android.ui.verify.company

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.gzuliyujiang.wheelpicker.AddressPicker
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode
import com.github.gzuliyujiang.wheelpicker.contract.OnAddressPickedListener
import com.github.gzuliyujiang.wheelpicker.entity.CityEntity
import com.github.gzuliyujiang.wheelpicker.entity.CountyEntity
import com.github.gzuliyujiang.wheelpicker.entity.ProvinceEntity
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.databinding.ActivityCompanyVerifyBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.EntInfoResponse
import com.zhongpin.mvvm_android.bean.LatLntResponse
import com.zhongpin.mvvm_android.photo.selector.GlideEngine
import com.zhongpin.mvvm_android.ui.utils.AreaUtil
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File


/**
 * 添加企业认证
 * */
class CompanyVerifyActivity : BaseVMActivity<CompanyVerifyViewModel>(), OnAddressPickedListener {


    private lateinit var mBinding: ActivityCompanyVerifyBinding;
    private lateinit var mLoadingDialog: LoadingDialog

    private var yingYeZhiZhaoPath:String? = null;
    private var yingYeZhiZhaoUrl:String? = null;
    private var latLntResponse: LatLntResponse? = null

    private var from: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
       ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            from = intent.getStringExtra("from") ?: ""
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityCompanyVerifyBinding.inflate(layoutInflater, container, false)
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



        mBinding.yingYeZhiZhaoContainer.setOnClickListener {
            PictureSelector.create(this@CompanyVerifyActivity)
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
                        Luban.with(this@CompanyVerifyActivity)
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
                        ocrEntImage(filePath)
                    }

                    override fun onCancel() {
                    }
                })
        }

        mBinding.chooseShouhuoAreaContainer.setOnClickListener {
            val picker = AddressPicker(this)
            picker.setAddressMode(AddressMode.PROVINCE_CITY_COUNTY)
            picker.setDefaultValue("", "", "")
            picker.setOnAddressPickedListener(this)
            picker.wheelLayout.setOnLinkageSelectedListener { first, second, third ->
                picker.titleView.text = AreaUtil.toArea(
                    picker.firstWheelView.formatItem(first),
                    picker.secondWheelView.formatItem(second),
                    picker.thirdWheelView.formatItem(third)
                )
            }
            picker.show()
        }

        mBinding.autoChooseJingweidu.setOnClickListener {
            if (TextUtils.isEmpty(getAreaText())) {
                Toast.makeText(applicationContext,"请选择省市区", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val address = mBinding.chooseShouhuoAreaText.text.trim().toString() +  mBinding.editShouHuoDetail.text.trim().toString()
            mViewModel.getLntLngInfo(address).observe(this@CompanyVerifyActivity) {
                if (it.success ) {
                    latLntResponse = it.data
                    mBinding.chooseJingweiduEditText.setText( "" + (it.data?.longitude  ?: "") +  "/" + (it.data?.latitude ?: ""))

                    val lntlats = mBinding.chooseJingweiduEditText.text.trim().toString().split("/")
                    LogUtils.d("CompanyVerifyActivity ", "CompanyVerifyActivity lntlats " + lntlats)
                }
            }
        }

        mBinding.btnLater.setOnClickListener {
            finish()
        }

        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun  ocrEntImage(filePath: String) {
        mViewModel.identifyEntInfo(filePath).observe(this) { outerIt ->
            if (outerIt.success) {
                outerIt.data?.let {
                   updateEntInfo(it)
                }
                //update info
                updateEntImage(filePath)
            } else {
                Toast.makeText(applicationContext,"信息识别失败," + outerIt.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun  updateEntInfo(entInfo: EntInfoResponse) {
        if (TextUtils.isEmpty(mBinding.editCompanyName.text.toString())) {
            mBinding.editCompanyName.setText(entInfo.companyName ?: "")
        }
        if (TextUtils.isEmpty(mBinding.editCompanyAddress.text.toString())) {
            mBinding.editCompanyAddress.setText(entInfo.address ?: "")
        }
        if (TextUtils.isEmpty(mBinding.editFaren.text.toString())) {
            mBinding.editFaren.setText(entInfo.legalPerson ?: "")
        }
        if (TextUtils.isEmpty(mBinding.editXinYongCode.text.toString())) {
            mBinding.editXinYongCode.setText(entInfo.creditCode ?: "")
        }
        if (TextUtils.isEmpty(mBinding.editNaShuiCode.text.toString())) {
            mBinding.editNaShuiCode.setText(entInfo.creditCode ?: "")
        }
    }

    fun  updateEntImage(filePath: String) {
        yingYeZhiZhaoPath = filePath
        mBinding.yingYeZhiZhaoText.visibility = View.GONE
        Glide.with(this@CompanyVerifyActivity)
            .load(Uri.fromFile(File(filePath)))
            .placeholder(mBinding.yingYeZhiZhao.drawable)
            .into(mBinding.yingYeZhiZhao)
    }


    fun checkAndSubmit(){
        if (TextUtils.isEmpty(mBinding.editCompanyName.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入公司全称", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.editCompanyAddress.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入公司注册地址", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.editXinYongCode.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入统一社会信用代码", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.editFaren.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入法定代表人", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.contractPhone.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入联系电话", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.editNaShuiCode.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入纳税人识别号", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(yingYeZhiZhaoPath)) {
            Toast.makeText(applicationContext,"请上传营业执照", Toast.LENGTH_LONG).show()
            return
        }
        uploadYingYeZhiZhao()
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

    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3148316/detail/55650603?spaceKey=space-64stfp
    fun uploadYingYeZhiZhao(){
        showLoadingDialog()
        mViewModel.uploadImage(yingYeZhiZhaoPath!!).observe(this) {
            if (it.success) {
                yingYeZhiZhaoUrl = it.data ?: ""
                submitFormInfo()
            } else {
                dismissLoadingDialog()
                Toast.makeText(applicationContext,"图片上传失败 " + it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3148316/detail/55650603?spaceKey=space-64stfp
    fun submitFormInfo(){
        val parameter:HashMap<String,Any> = hashMapOf()
        parameter["name"] = mBinding.editCompanyName.text.trim().toString()
        parameter["address"] = mBinding.editCompanyAddress.text.trim().toString()
        parameter["unite"] = mBinding.editXinYongCode.text.trim().toString()
        parameter["legal"] = mBinding.editFaren.text.trim().toString()
        parameter["mobile"] = mBinding.contractPhone.text.trim().toString()
        parameter["identify"] = mBinding.editNaShuiCode.text.trim().toString()
        parameter["license"] = yingYeZhiZhaoUrl ?: ""
        parameter["bankAccount"] = mBinding.editBankCode.text.trim().toString()
        parameter["bankName"] = mBinding.editKaiHuHang.text.trim().toString()

        val receiveAddress:HashMap<String,Any?> = hashMapOf()
        receiveAddress["name"] = mBinding.editShouHuoName.text.trim().toString()
        receiveAddress["mobile"] = mBinding.editShouHuoShouJi.text.trim().toString()
        receiveAddress["address"] = mBinding.editShouHuoDetail.text.trim().toString()
        receiveAddress["abbr"] = mBinding.editShouHuoShort.text.trim().toString()

        if (mBinding.chooseJingweiduEditText.text.isNotEmpty()) {
            val lntlats = mBinding.chooseJingweiduEditText.text.trim().toString().split("/")
            if(lntlats.size == 2) {
                receiveAddress["longitude"] =  lntlats[0]
                receiveAddress["latitude"] = lntlats[1]
            } else {
                dismissLoadingDialog()
                Toast.makeText(applicationContext, "经度/纬度，格式度不合法", Toast.LENGTH_LONG).show()
                return
            }
        }
        receiveAddress["province"] = mProvince?.name
        receiveAddress["city"] = mCity?.name
        receiveAddress["region"] = mCounty?.name
        if (mProvince != null
            && mCity != null
            && mCounty != null
            && !TextUtils.isEmpty(mBinding.editShouHuoDetail.text.trim().toString())) {
            parameter["receiveAddressList"] = arrayOf(receiveAddress);
        } else {
            parameter["receiveAddressList"] = emptyArray<Any?>();
        }

        mViewModel.submitEntInfoAuth(parameter).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                EventBusUtils.postEvent(CompanyInfoChangeEvent(true))
                if (TextUtils.isEmpty(from)) {
                    showTipDialogFromRegister()
                } else {
                    showTipDialogFromMine()
                }
            } else {
                Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun showTipDialogFromRegister() {
        val builder = AlertDialog.Builder(this@CompanyVerifyActivity)
        builder.setTitle("信息提交成功")
        builder.setMessage("您填写的企业实名认证信息已提交，\n" +
                "请等待人工审核。\n" +
                "如需认证企业，可在“我的-我的企业-添加企业”中重新申请认证。")
        builder.setPositiveButton("我知道了", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }

        })
        builder.show()
    }

    fun showTipDialogFromMine() {
        val builder = AlertDialog.Builder(this@CompanyVerifyActivity)
        builder.setTitle("信息提交成功")
        builder.setMessage("您填写的企业实名认证信息已提交，\n" +
                "请等待人工审核。\n" +
                "如需认证企业，可在“我的-我的企业”中重新申请认证。")
        builder.setPositiveButton("我知道了", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }

        })
        builder.show()
    }

    override fun onAddressPicked(
        province: ProvinceEntity?,
        city: CityEntity?,
        county: CountyEntity?
    ) {
        if (province == null || city == null || county == null) {
            return;
        }

        //Toast.makeText(this, province.toString() + " " + city + " " + county, Toast.LENGTH_SHORT).show()

        val address = AreaUtil.toArea(
            province.name,
            city.name,
            county.name
        )
        mProvince = province;
        mCity = city
        mCounty = county
        mBinding.chooseShouhuoAreaText.text = address
    }

    private fun getAreaText():String {
        if(!mBinding.chooseShouhuoAreaText.text.toString().contains("请选择")) {
            return mBinding.chooseShouhuoAreaText.text.trim()
                .toString()
        }
        return ""
    }

    private var mProvince: ProvinceEntity? = null;
    private var mCity: CityEntity? = null;
    private var mCounty: CountyEntity? = null;


}