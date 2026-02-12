package com.zhongpin.mvvm_android.ui.mine.company.address.edit

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.gzuliyujiang.wheelpicker.AddressPicker
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode
import com.github.gzuliyujiang.wheelpicker.contract.OnAddressPickedListener
import com.github.gzuliyujiang.wheelpicker.entity.CityEntity
import com.github.gzuliyujiang.wheelpicker.entity.CountyEntity
import com.github.gzuliyujiang.wheelpicker.entity.ProvinceEntity
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityEditReceiveAddressBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.AddressInfoChangeEvent
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.LatLntResponse
import com.zhongpin.mvvm_android.ui.utils.AreaUtil
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil


/**
 * 添加企业认证
 * */
@Deprecated(message = "please use AddReceiveAddressActivity")
class EditReceiveAddressActivity : BaseVMActivity<EditReceiveAddressViewModel>(), OnAddressPickedListener {


    private lateinit var mBinding: ActivityEditReceiveAddressBinding;
    private lateinit var mLoadingDialog: LoadingDialog

    private var yingYeZhiZhaoPath:String? = null;
    private var yingYeZhiZhaoUrl:String? = null;
    private var latLntResponse: LatLntResponse? = null

    private var addressItem: AddressListItemResponse? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            addressItem = IntentUtils.getSerializableExtra(intent, "addressItem", AddressListItemResponse::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityEditReceiveAddressBinding.inflate(layoutInflater, container, false)
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
        mBinding.chooseShouhuoAreaContainer.setOnClickListener {
            val picker = AddressPicker(this)
            picker.setAddressMode(AddressMode.PROVINCE_CITY_COUNTY)
            picker.setDefaultValue(addressItem?.province ?: "", addressItem?.city ?:"", addressItem?.region)
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
            mViewModel.getLntLngInfo(address).observe(this@EditReceiveAddressActivity) {
                if (it.success ) {
                    latLntResponse = it.data
                    mBinding.chooseJingweiduEditText.setText( "" + (it.data?.longitude  ?: "") +  "/" + (it.data?.latitude ?: ""))

                    val lntlats = mBinding.chooseJingweiduEditText.text.trim().toString().split("/")
                    LogUtils.d("CompanyVerifyActivity ", "CompanyVerifyActivity lntlats " + lntlats)
                }
            }
        }

        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit()
        }

        val item = addressItem;
        if (item == null) {
            Toast.makeText(applicationContext, "请传入编辑的地址", Toast.LENGTH_LONG).show()
            return
        }
        autoFillInfo(item)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun autoFillInfo(item:AddressListItemResponse) {
        val areaText = AreaUtil.toArea(
            item.province ?: "",
            item.city ?: "",
            item.region ?: ""
        )
        if (!TextUtils.isEmpty(areaText)) {
            mBinding.chooseShouhuoAreaText.setText(areaText)
        }
        if (!TextUtils.isEmpty(item.address)) {
            mBinding.editShouHuoDetail.setText(item.address ?: "")
        }
        if (!TextUtils.isEmpty(item.abbr)) {
            mBinding.editShouHuoShort.setText(item.abbr ?: "")
        }
        if (!TextUtils.isEmpty(item.longitude) || !TextUtils.isEmpty(item.latitude)) {
            mBinding.chooseJingweiduEditText.setText((item.longitude  ?: "") +  "/" + (item.latitude ?: ""))
        }

        if (!TextUtils.isEmpty(item.name) ) {
            mBinding.editShouHuoName.setText(item.name  ?: "")
        }

        if (!TextUtils.isEmpty(item.mobile) ) {
            mBinding.editShouHuoShouJi.setText(item.mobile  ?: "")
        }
    }


    fun checkAndSubmit() {
        if (mProvince == null
            || mCity == null
            || mCounty == null) {
            Toast.makeText(applicationContext, "请选择省市区", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.editShouHuoDetail.text.trim().toString())) {
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
        parameter["id"] = addressItem?.id ?: -1
        parameter["province"] = mProvince?.name ?: ""
        parameter["city"] = mCity?.name ?: ""
        parameter["region"] = mCounty?.name ?: ""
        parameter["address"] = mBinding.editShouHuoDetail.text.trim().toString()
        parameter["abbr"] = mBinding.editShouHuoShort.text.trim().toString()

        if (mBinding.chooseJingweiduEditText.text.isNotEmpty()) {
            val lntlats = mBinding.chooseJingweiduEditText.text.trim().toString().split("/")
            if(lntlats.size == 2) {
                parameter["longitude"] =  lntlats[0]
                parameter["latitude"] = lntlats[1]
            } else {
                dismissLoadingDialog()
                Toast.makeText(applicationContext, "经度/纬度，格式度不合法", Toast.LENGTH_LONG).show()
                return
            }
        }
        parameter["name"] = mBinding.editShouHuoName.text.trim().toString()
        parameter["mobile"] = mBinding.editShouHuoShouJi.text.trim().toString()
        showLoadingDialog()
        mViewModel.editReceiveAddress(parameter).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                EventBusUtils.postEvent(AddressInfoChangeEvent(true))
                showTipDialog()
            } else {
                Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun showTipDialog() {
        val builder = AlertDialog.Builder(this@EditReceiveAddressActivity)
        builder.setTitle("信息提交成功")
        builder.setMessage("您填写的收货地址信息已提交。")
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