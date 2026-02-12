package com.zhongpin.mvvm_android.ui.mine.company.submit

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
import com.zhilianshidai.pindan.app.databinding.ActivitySubmitCompanyInfoBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.CompanySignInfoChangeEvent
import com.zhongpin.mvvm_android.bean.EntInfoResponse
import com.zhongpin.mvvm_android.photo.selector.GlideEngine
import com.zhongpin.mvvm_android.ui.utils.AreaUtil
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.mine.company.sign.CompanySignActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File
import androidx.core.net.toUri
import com.github.gzuliyujiang.wheelpicker.utility.AddressJsonParser
import com.zhongpin.mvvm_android.bean.IdCardInfoResponse
import com.zhongpin.mvvm_android.biz.utils.ConfigAddress
import com.zhongpin.mvvm_android.bugfix.AssetAddressLoaderV2
import com.zhongpin.mvvm_android.ui.common.requestLocation


/**
 * 添加企业认证
 * */
@EventBusRegister
class SubmitCompanyInfoActivity : BaseVMActivity<SubmitCompanyInfoViewModel>(), OnAddressPickedListener {


    private lateinit var mBinding: ActivitySubmitCompanyInfoBinding;
    private var mLoadingDialog: LoadingDialog? = null

    private var yingYeZhiZhaoPath:String? = null;
    private var yingYeZhiZhaoUrl:String? = null;

    private var idCardFrontImagePath:String? = null;
    private var idCardBackImagePath:String? = null;
    private var idCardFrontImageUrl:String? = null;
    private var idCardBackImageUrl:String? = null;


    private var companyInfo: CompanyListItemResponse? = null
    private var companyId:Long ? = null;



    override fun onCreate(savedInstanceState: Bundle?) {
       ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            companyInfo = IntentUtils.getSerializableExtra(intent, "companyInfo", CompanyListItemResponse::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivitySubmitCompanyInfoBinding.inflate(layoutInflater, container, false)
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

        companyInfo?.let {
            fillCompanyInfo(it)
        }

        mBinding.yingYeZhiZhaoContainer.setOnClickListener {
            PictureSelector.create(this@SubmitCompanyInfoActivity)
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
                        Luban.with(this@SubmitCompanyInfoActivity)
                            .load(source)
                            .ignoreBy(100).setCompressListener(
                                object : OnNewCompressListener {
                                    override fun onStart() {

                                    }

                                    override fun onSuccess(source: String?, compressFile: File?) {
                                        if (call != null) {
                                            if (compressFile != null) {
                                                LogUtils.d(
                                                    "SubmitCompanyInfoActivity",
                                                    "SubmitCompanyInfoActivity compressFile " + source + " compress " + compressFile?.absolutePath
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

        mBinding.companyRegisterAreaTextContainer.setOnClickListener {
            if (isRequestLocationPermission) {
                return@setOnClickListener
            }
            if (!hasRequestLocation) {
                hasRequestLocation = true
                isRequestLocationPermission = true
                requestLocation {
                    isRequestLocationPermission = false
                    if (ConfigAddress.isAddressValid(
                            it?.province,
                            it?.city,
                            it?.district
                        )) {
                        mProvince = ProvinceEntity()
                        mProvince?.name = it?.province
                        mCity = CityEntity();
                        mCity?.name = it?.city ?: ""
                        mCounty = CountyEntity();
                        mCounty?.name = it?.district ?: ""
                        val areaAddress = AreaUtil.toArea(
                            mProvince?.name,
                            mCity?.name,
                            mCounty?.name
                        )
                        mBinding.companyRegisterAreaText.setText(areaAddress)
                    }
                    showAddressPicker()
                }
            } else {
                showAddressPicker()
            }
        }

        mBinding.idCardFrontContainer.setOnClickListener {
            PictureSelector.create(this@SubmitCompanyInfoActivity)
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
                        Luban.with(this@SubmitCompanyInfoActivity)
                            .load(source)
                            .ignoreBy(100).setCompressListener(
                                object : OnNewCompressListener {
                                    override fun onStart() {

                                    }

                                    override fun onSuccess(source: String?, compressFile: File?) {
                                        if (call != null) {
                                            if (compressFile != null) {
                                                LogUtils.d(
                                                    "SubmitCompanyInfoActivity",
                                                    "SubmitCompanyInfoActivity compressFile " + source + " compress " + compressFile?.absolutePath
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
            PictureSelector.create(this@SubmitCompanyInfoActivity)
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
                        Luban.with(this@SubmitCompanyInfoActivity)
                            .load(source)
                            .ignoreBy(100).setCompressListener(
                                object : OnNewCompressListener {
                                    override fun onStart() {

                                    }

                                    override fun onSuccess(source: String?, compressFile: File?) {
                                        if (call != null) {
                                            if (compressFile != null) {
                                                LogUtils.d(
                                                    "SubmitCompanyInfoActivity",
                                                    "SubmitCompanyInfoActivity compressFile " + source + " compress " + compressFile?.absolutePath
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


        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit()
        }
        ConfigAddress.loadAddress(this)
    }

    override fun onDestroy() {
        ConfigAddress.releaseAddress()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : CompanySignInfoChangeEvent){
        if (infoEvent.isChange) {
            finish()
        }
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
        mBinding.companyRegisterAreaText.text = address
    }

    fun showAddressPicker() {
        val picker = AddressPicker(this)
        picker.setAddressMode(ConfigAddress.chinaAddressJsonFile, AddressMode.PROVINCE_CITY_COUNTY)
        val jsonParser = AddressJsonParser();
        val addressLoader = AssetAddressLoaderV2(this, ConfigAddress.chinaAddressJsonFile)
        picker.setAddressLoader(addressLoader, jsonParser)
        picker.setDefaultValue(mProvince?.name, mCity?.name, mCounty?.name)
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

    private fun getAreaText():String {
        if(!mBinding.companyRegisterAreaText.text.toString().contains("请选择")) {
            return mBinding.companyRegisterAreaText.text.trim()
                .toString()
        }
        return ""
    }

    private var hasRequestLocation = false
    private var isRequestLocationPermission = false
    private var mProvince: ProvinceEntity? = null;
    private var mCity: CityEntity? = null;
    private var mCounty: CountyEntity? = null;


    fun fillCompanyInfo(item: CompanyListItemResponse) {
        // 企业信息
        yingYeZhiZhaoUrl = item.license
        mBinding.yingYeZhiZhaoText.text  = "重新上传"
        mBinding.companyInfoContainer.visibility = View.VISIBLE
        Glide.with(this@SubmitCompanyInfoActivity)
            .load((yingYeZhiZhaoUrl ?: "").toUri())
            .placeholder(mBinding.yingYeZhiZhao.drawable)
            .into(mBinding.yingYeZhiZhao)

        if (TextUtils.isEmpty(mBinding.editCompanyName.text.toString())) {
            mBinding.editCompanyName.setText(item.companyName ?: "")
        }

        if (TextUtils.isEmpty(mBinding.editXinYongCode.text.toString())) {
            mBinding.editXinYongCode.setText(item.creditCode ?: "")
        }

        if (TextUtils.isEmpty(mBinding.editCompanyAddress.text.toString())) {
            mBinding.editCompanyAddress.setText(item.registerAddress ?: "")
        }
        val province = ProvinceEntity();
        val city = CityEntity();
        val county = CountyEntity();
        province.name = item.province;
        city.name = item.city
        county.name = item.region
        mProvince = province;
        mCity = city
        mCounty = county
        val address = AreaUtil.toArea(
            province.name,
            city.name,
            county.name
        )
        mBinding.companyRegisterAreaText.text = address

        //身份证信息
        idCardFrontImageUrl = item.leftCard
        mBinding.editFaren.setText(item.legal ?:"")
        mBinding.editFarenIdCardNo.setText(item.idCard ?: "")
        mBinding.idCardFrontText.text = "重新上传"
        mBinding.faRenInfoContainer.visibility = View.VISIBLE
        Glide.with(this@SubmitCompanyInfoActivity)
            .load((idCardFrontImageUrl ?: "").toUri())
            .placeholder(mBinding.idCardFront.drawable)
            .into(mBinding.idCardFront)
        idCardBackImageUrl = item.rightCard
        Glide.with(this@SubmitCompanyInfoActivity)
            .load((idCardBackImageUrl ?: "").toUri())
            .placeholder(mBinding.idCardBack.drawable)
            .into(mBinding.idCardBack)

        //联系人
        mBinding.contractName.setText(item.contactPeople ?:"")
        mBinding.contractPhone.setText(item.contactTel ?: "")

    }

    fun ocrEntImage(filePath: String) {
        showLoadingDialog()
        mViewModel.identifyEntInfo(filePath).observe(this) { outerIt ->
            dismissLoadingDialog();
            if (outerIt.success) {
                outerIt.data?.let {
                   updateEntInfo(it)
                }
                //update info
                updateEntImage(filePath)
            } else {
                //Toast.makeText(applicationContext,outerIt.msg, Toast.LENGTH_LONG).show()
                updateEntImage(filePath)
            }
        }
    }

    fun  ocrFrontImage(filePath: String) {
        showLoadingDialog()
        mViewModel.identifyIdCardInfo(filePath).observe(this) { outerIt ->
            dismissLoadingDialog()
            if (outerIt.success) {
                ocrFrontImageDone(filePath,outerIt.data)
            } else {
                //Toast.makeText(applicationContext,outerIt.msg, Toast.LENGTH_SHORT).show()
                ocrFrontImageDone(filePath,outerIt.data)
            }
        }
    }

    fun ocrFrontImageDone(filePath: String, response: IdCardInfoResponse?) {
        idCardFrontImagePath = filePath
        response?.let {
            mBinding.editFaren.setText(it.name ?:"")
            mBinding.editFarenIdCardNo.setText(it.idCard ?: "")
        }
        //update info
        mBinding.idCardFrontText.text = "重新上传"
        mBinding.faRenInfoContainer.visibility = View.VISIBLE
        Glide.with(this@SubmitCompanyInfoActivity)
            .load(Uri.fromFile(File(filePath)))
            .placeholder(mBinding.idCardFront.drawable)
            .into(mBinding.idCardFront)
    }

    fun  uploadBackImage(filePath: String) {
        idCardBackImagePath = filePath
        mBinding.idCardBackText.text = "重新上传"
        Glide.with(this@SubmitCompanyInfoActivity)
            .load(Uri.fromFile(File(filePath)))
            .placeholder(mBinding.idCardBack.drawable)
            .into(mBinding.idCardBack)
    }

    fun  updateEntInfo(entInfo: EntInfoResponse) {
        if (!TextUtils.isEmpty(entInfo.companyName)) {
            mBinding.editCompanyName.setText(entInfo.companyName ?: "")
        }

        if (!TextUtils.isEmpty(entInfo.creditCode)) {
            mBinding.editXinYongCode.setText(entInfo.creditCode ?: "")
        }

        if (!TextUtils.isEmpty(entInfo.address)) {
            mBinding.editCompanyAddress.setText(entInfo.address ?: "")
        }

    }

    fun  updateEntImage(filePath: String) {
        yingYeZhiZhaoPath = filePath
        mBinding.yingYeZhiZhaoText.text  = "重新上传"
        mBinding.companyInfoContainer.visibility = View.VISIBLE
        Glide.with(this@SubmitCompanyInfoActivity)
            .load(Uri.fromFile(File(filePath)))
            .placeholder(mBinding.yingYeZhiZhao.drawable)
            .into(mBinding.yingYeZhiZhao)
    }


    fun checkAndSubmit() {
        if (TextUtils.isEmpty(yingYeZhiZhaoPath) && TextUtils.isEmpty(yingYeZhiZhaoUrl)) {
            Toast.makeText(applicationContext,"请上传营业执照", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.editCompanyName.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入企业名称", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.editXinYongCode.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入统一社会信用代码", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.editCompanyAddress.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入注册地址", Toast.LENGTH_LONG).show()
            return
        }

        if (!(mProvince != null
                    && mCity != null
                    && mCounty != null)) {
            Toast.makeText(applicationContext,"请选择省市区", Toast.LENGTH_LONG).show()
            return
        }


        if (TextUtils.isEmpty(idCardFrontImagePath) && TextUtils.isEmpty(idCardFrontImageUrl)) {
            Toast.makeText(applicationContext,"请上传身份证人像面", Toast.LENGTH_LONG).show()
            return;
        }

        if (TextUtils.isEmpty(idCardBackImagePath)&& TextUtils.isEmpty(idCardBackImageUrl)) {
            Toast.makeText(applicationContext,"请上传身份证国辉面", Toast.LENGTH_LONG).show()
            return;
        }

        if (TextUtils.isEmpty(mBinding.editFaren.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入法人代表姓名", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.editFarenIdCardNo.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入法人代表身份证号", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.contractName.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入联系人", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.contractPhone.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入联系电话", Toast.LENGTH_LONG).show()
            return
        }

        uploadYingYeZhiZhao()
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

    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3148316/detail/55650603?spaceKey=space-64stfp
    fun uploadYingYeZhiZhao() {
        showLoadingDialog()
        if (!TextUtils.isEmpty(yingYeZhiZhaoPath)) {
            mViewModel.uploadImage(yingYeZhiZhaoPath!!).observe(this) {
                if (it.success) {
                    yingYeZhiZhaoUrl = it.data ?: ""
                    uploadFrontImage()
                } else {
                    dismissLoadingDialog()
                    Toast.makeText(applicationContext,"营业执照图片上传失败 " + it.msg, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            uploadFrontImage()
        }
    }

    fun uploadFrontImage() {
        if (!TextUtils.isEmpty(idCardFrontImagePath)) {
            mViewModel.uploadImage(idCardFrontImagePath!!).observe(this) {
                if (it.success) {
                    idCardFrontImageUrl = it.data ?: ""
                    uploadBackImage()
                } else {
                    dismissLoadingDialog()
                    Toast.makeText(applicationContext,"身份证人像面图片上传失败 " + it.msg, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            uploadBackImage()
        }

    }

    fun uploadBackImage() {
        if (!TextUtils.isEmpty(idCardBackImagePath)) {
            mViewModel.uploadImage(idCardBackImagePath!!).observe(this) {
                if (it.success) {
                    idCardBackImageUrl = it.data ?: ""
                    submitFormInfo()
                } else {
                    dismissLoadingDialog()
                    Toast.makeText(applicationContext,"身份证国辉面上传失败 " + it.msg, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            submitFormInfo()
        }
    }

    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3217641/detail/55937859?spaceKey=space-64stfp
    fun submitFormInfo() {
        val parameter:HashMap<String,Any> = hashMapOf()
        parameter["companyName"] = mBinding.editCompanyName.text.trim().toString()
        parameter["creditCode"] = mBinding.editXinYongCode.text.trim().toString()

        parameter["province"] = mProvince?.name ?: ""
        parameter["city"] = mCity?.name ?: ""
        parameter["region"] = mCounty?.name ?: ""

        parameter["registerAddress"] = mBinding.editCompanyAddress.text.trim().toString()
        parameter["license"] = yingYeZhiZhaoUrl ?: ""


        parameter["leftCard"] = idCardFrontImageUrl ?: ""
        parameter["rightCard"] = idCardBackImageUrl ?:""
        parameter["legal"] = mBinding.editFaren.text.trim().toString()
        parameter["idCard"] = mBinding.editFarenIdCardNo.text.trim().toString()


        parameter["contactTel"] = mBinding.contractPhone.text.trim().toString()
        parameter["contactPeople"] = mBinding.contractName.text.trim().toString()

        if (companyInfo == null && companyId == null) {
            mViewModel.submitEntInfoAuth(parameter).observe(this) {
                dismissLoadingDialog()
                if (it.success) {
                    companyId = it.data;
                    EventBusUtils.postEvent(CompanyInfoChangeEvent(true))
                    goCompanySignPage();
                } else {
                    Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            parameter["id"] = companyInfo?.id ?: (companyId ?: 0)
            mViewModel.editEntInfoAuth(parameter).observe(this) {
                dismissLoadingDialog()
                if (it.success) {
                    EventBusUtils.postEvent(CompanyInfoChangeEvent(true))
                    goCompanySignPage();
                } else {
                    Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun goCompanySignPage() {
        val intent = Intent(this@SubmitCompanyInfoActivity, CompanySignActivity::class.java)
        intent.putExtra("from", "submit")
        intent.putExtra("companyInfo", companyInfo)
        intent.putExtra("companyId", companyId)
        startActivity(intent)
    }

}