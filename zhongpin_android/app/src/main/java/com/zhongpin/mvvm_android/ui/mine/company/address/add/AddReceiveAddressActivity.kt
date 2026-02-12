package com.zhongpin.mvvm_android.ui.mine.company.address.add

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.github.gzuliyujiang.wheelpicker.AddressPicker
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode
import com.github.gzuliyujiang.wheelpicker.contract.OnAddressPickedListener
import com.github.gzuliyujiang.wheelpicker.entity.CityEntity
import com.github.gzuliyujiang.wheelpicker.entity.CountyEntity
import com.github.gzuliyujiang.wheelpicker.entity.ProvinceEntity
import com.github.gzuliyujiang.wheelpicker.utility.AddressJsonParser
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityAddReceiveAddressBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.AddressInfoChangeEvent
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.biz.utils.ConfigAddress
import com.zhongpin.mvvm_android.bugfix.AssetAddressLoaderV2
import com.zhongpin.mvvm_android.ui.utils.AreaUtil
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.requestLocation
import com.zhongpin.mvvm_android.ui.utils.IntentUtils

/**
 * 添加企业认证
 * */
class AddReceiveAddressActivity : BaseVMActivity<AddReceiveAddressViewModel>(), OnAddressPickedListener {


    private lateinit var mBinding: ActivityAddReceiveAddressBinding;
    private lateinit var mLoadingDialog: LoadingDialog

    private var entId:Long = -1;
    private var from:String = "";
    private var mode:String = "";

    private var addressItem: AddressListItemResponse? = null;



    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            entId = intent.getLongExtra("entId", -1)
            from = intent.getStringExtra("from") ?: ""
            addressItem = IntentUtils.getSerializableExtra(intent, "addressItem", AddressListItemResponse::class.java)
            if (addressItem != null) {
                mode = "edit";
            }
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityAddReceiveAddressBinding.inflate(layoutInflater, container, false)
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

        mBinding.receiveAreaTextContainer.setOnClickListener {
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
                        val prefix = AreaUtil.toArea(it?.province, it?.city, it?.district)
                        val detailAddress = ConfigAddress.getSubAddress(it?.address, it?.province, it?.city, it?.district)
                        if (!TextUtils.isEmpty(detailAddress)) {
                            mBinding.chooseShouHuoAreaText.setText(prefix)
                            mBinding.detailAddress.setText(detailAddress)
                        }
                    }
                    showAddressPicker()
                }
            } else {
                showAddressPicker()
            }
        }


        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit()
        }

        if (mode == "edit") {
            addressItem?.let {
                autoFillInfo(it)
            }
        }
        ConfigAddress.loadAddress(this)
    }

    override fun onDestroy() {
        ConfigAddress.releaseAddress()
        super.onDestroy()
    }

    fun autoFillInfo(item: AddressListItemResponse) {
        if (!TextUtils.isEmpty(item.name) ) {
            mBinding.contractName.setText(item.name  ?: "")
        }

        if (!TextUtils.isEmpty(item.mobile)) {
            mBinding.contractPhone.setText(item.mobile ?: "")
        }

        val areaText = AreaUtil.toArea(
            item.province ?: "",
            item.city ?: "",
            item.region ?: ""
        )
        if (!TextUtils.isEmpty(areaText)) {
            mBinding.chooseShouHuoAreaText.setText(areaText)
            mProvince = ProvinceEntity()
            mProvince?.name = item.province ?: ""
            mCity = CityEntity();
            mCity?.name = item.city ?: ""
            mCounty = CountyEntity();
            mCounty?.name = item.region ?: ""
        }

        if (!TextUtils.isEmpty(item.address)) {
            mBinding.detailAddress.setText(item.address ?: "")
        }

        if (item.status == 1) {
            mBinding.defaultSwitch.isChecked = true
        }

        mBinding.ivTitle.text = "编辑收货地址"
        mBinding.btnSubmit.text = "保存"
    }


    fun checkAndSubmit(){
        if (mBinding.contractName.text.trim().isEmpty()) {
            Toast.makeText(applicationContext, "请输入收货企业名称", Toast.LENGTH_LONG).show()
            return
        }
        if (mBinding.contractPhone.text.trim().isEmpty()) {
            Toast.makeText(applicationContext, "请输入联系电话", Toast.LENGTH_LONG).show()
            return
        }

        if (mProvince == null
                || mCity == null
                || mCounty == null) {
            Toast.makeText(applicationContext, "请选择省市区", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.detailAddress.text.trim().toString())) {
            Toast.makeText(applicationContext, "请填写详细地址", Toast.LENGTH_LONG).show()
            return
        }
        submitFormInfo()
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


    //https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3181096/detail/55805747?spaceKey=space-64stfp
    fun submitFormInfo(){
        val parameter:HashMap<String,Any> = hashMapOf()
        parameter["name"] = mBinding.contractName.text.trim().toString()
        parameter["mobile"] = mBinding.contractPhone.text.trim().toString()

        parameter["province"] = mProvince?.name ?: ""
        parameter["city"] = mCity?.name ?: ""
        parameter["region"] = mCounty?.name ?: ""
        parameter["address"] = mBinding.detailAddress.text.trim().toString()

        if (mBinding.defaultSwitch.isChecked) {
            parameter["status"] = 1;
        }


        if (mode == "edit") {//编辑模式
            parameter["id"] = addressItem?.id ?: -1

            showLoadingDialog()
            mViewModel.editReceiveAddress(parameter).observe(this) {
                dismissLoadingDialog()
                if (it.success) {
                    EventBusUtils.postEvent(AddressInfoChangeEvent(true))
                    finish()
                } else {
                    Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
        showLoadingDialog()
        mViewModel.addReceiveAddress(parameter).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                EventBusUtils.postEvent(AddressInfoChangeEvent(true))
                doneSubmit()
            } else {
                Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun doneSubmit() {
        if (from == "order") {
            finish()
        } else {
            finish()
            //showTipDialog()
        }
    }

    fun showTipDialog() {
        val dialog = ConfirmDialog(
            mContext = this,
            title = "信息提交成功",
            message = HtmlCompat.fromHtml("您填写的收货地址信息已提交。<br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
            confirmText = "我知道了",
            onConfirm = {
                finish()
            }
        );
        dialog.showDialog(this)
    }

    override fun onAddressPicked(
        province: ProvinceEntity?,
        city: CityEntity?,
        county: CountyEntity?
    ) {
        if (province == null || city == null || county == null) {
            return;
        }
        val address = AreaUtil.toArea(
            province.name,
            city.name,
            county.name
        )
        mProvince = province;
        mCity = city
        mCounty = county
        mBinding.chooseShouHuoAreaText.text = address
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
        if(!mBinding.chooseShouHuoAreaText.text.toString().contains("请选择")) {
            return mBinding.chooseShouHuoAreaText.text.trim()
                .toString()
        }
        return ""
    }

    private var hasRequestLocation = false
    private var isRequestLocationPermission = false
    private var mProvince: ProvinceEntity? = null;
    private var mCity: CityEntity? = null;
    private var mCounty: CountyEntity? = null;


}