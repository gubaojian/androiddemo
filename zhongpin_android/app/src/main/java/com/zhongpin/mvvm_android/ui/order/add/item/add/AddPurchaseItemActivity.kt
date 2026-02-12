package com.zhongpin.mvvm_android.ui.order.add.item.add

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.common.oss.jpgMaxWith
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityAddPurchaseItemBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.height
import com.zhongpin.lib_base.ktx.isVisible
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.AddOrderPurchaseItem
import com.zhongpin.mvvm_android.bean.AddOrderPurchaseItemEvent
import com.zhongpin.mvvm_android.bean.BoxTypeConfigItem
import com.zhongpin.mvvm_android.bean.ChooseMaterialPriceItemEvent
import com.zhongpin.mvvm_android.bean.ChooseSelectHistoryOrderItemEvent
import com.zhongpin.mvvm_android.bean.DeleteOrderPurchaseItemEvent
import com.zhongpin.mvvm_android.bean.EditOrderPurchaseItemEvent
import com.zhongpin.mvvm_android.bean.MaterialPriceItem
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.common.utils.hideKeyboard
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.common.BoxConfigData
import com.zhongpin.mvvm_android.ui.common.setImageUrlAndEnablePreviewByXPopup
import com.zhongpin.mvvm_android.ui.order.add.AddOrderActivity
import com.zhongpin.mvvm_android.ui.order.add.code.ChoosePlatCodeActivity
import com.zhongpin.mvvm_android.ui.order.add.history.HistoryOrderSearchListActivity
import com.zhongpin.mvvm_android.ui.order.add.item.add.view.showChooseBoxTypeConfigDialog
import com.zhongpin.mvvm_android.ui.order.add.item.add.view.showChooseWaLenTypeConfigDialog
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import com.zhongpin.mvvm_android.ui.view.ext.setAutoUpperCase
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.UUID
import kotlin.collections.get
import kotlin.math.max

/**
 * 添加订单
 * */
@EventBusRegister
class AddPurchaseItemActivity : BaseVMActivity<AddPurchaseItemViewModel>() {

    private val layerMap = BoxConfigData.layerMap

    private val lines = BoxConfigData.lines

    private lateinit var mBinding: ActivityAddPurchaseItemBinding;
    private lateinit var mLoadingDialog: LoadingDialog

    private var mPurchaseItem = AddOrderPurchaseItem();
    private var mode:String = "";

    private var buyAgainOrderItem: OrderItem? = null

    private var buyAgainMaterialPriceItem: MaterialPriceItem? = null;

    private val boxTypeConfigItems: MutableList<BoxTypeConfigItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            mode = intent.getStringExtra("mode") ?: ""
            if (mode == "edit") {
                mPurchaseItem = IntentUtils.getSerializableExtra(intent, "item", AddOrderPurchaseItem::class.java) ?: mPurchaseItem
            }
            val orderId = intent.getLongExtra("buyAgainOrderId", 0L)
            if (orderId > 0) {
                buyAgainOrderItem = ShareParamDataUtils.orderItem
                if (buyAgainOrderItem != null) {
                    mode  = "buyAgain"
                    ShareParamDataUtils.orderItem = null
                }
            }
            val buyAgainMaterialPriceItemId = intent.getLongExtra("buyAgainMaterialPriceItemId", 0L)
            if (buyAgainMaterialPriceItemId > 0) {
                buyAgainMaterialPriceItem = ShareParamDataUtils.getParams("buyAgainMaterialPriceItem")
                if (buyAgainMaterialPriceItem != null) {
                    mode  = "buyAgainMaterial"
                    ShareParamDataUtils.clearParams()
                }
            }
        }
        if (TextUtils.isEmpty(mode)) {
            mPurchaseItem.uuid = UUID.randomUUID().toString()
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityAddPurchaseItemBinding.inflate(layoutInflater, container, false)
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

        mBinding.chooseFromHistoryOrder.setOnClickListener {
            val intent = Intent(this@AddPurchaseItemActivity, HistoryOrderSearchListActivity::class.java)
            startActivity(intent)
        }

        mBinding.useBoxTypeSwitch.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 mPurchaseItem.useBoxTypeSwitch = true
                 mBinding.useBoxTypeSwitchDesc.text = "是"
                 mBinding.boxTypeContainer.visible()
             } else {
                 mPurchaseItem.useBoxTypeSwitch = false
                 mBinding.useBoxTypeSwitchDesc.text = "否"
                 mBinding.boxTypeContainer.gone()
             }
        }

        mBinding.chooseBoxTypeContainer.setOnClickListener {
            hideKeyboard()
            showChooseBoxTypeConfigDialog(
                selectItemCode = mPurchaseItem.boxTypeName,
                items = boxTypeConfigItems,
                chooseAction = {
                    mPurchaseItem.boxTypeName = it?.typeName;
                    mPurchaseItem.boxTypeNum = it?.type;
                    mBinding.boxTypeText.text = it?.typeName;
                    mBinding.boxTypeImage.setImageUrlAndEnablePreviewByXPopup(it?.image)
                    configForStrangeBox()
                }
          )
        }

        mBinding.layerContainer.setOnClickListener {
            hideKeyboard()
            val layers = layerMap.keys.toList()
            val layerTexts = layerMap.keys.map {"${it}层"}.toList()
            val picker = OptionPicker(this)
            picker.setTitle("请选择层数")
            picker.setData(layerTexts)
            mPurchaseItem.floor?.let {
                picker.setDefaultValue("${it}层")
            }
            picker.setOnOptionPickedListener { position, item ->
                if (TextUtils.equals(mPurchaseItem.floor, layers[position])) {
                    return@setOnOptionPickedListener
                }
                mBinding.materialText.text.clear()
                mBinding.layerText.text = layerTexts[position]
                mPurchaseItem.floor = layers[position];
                val flutes = layerMap[mPurchaseItem.floor] ?: emptyList()
                var defaultFlute = flutes.firstOrNull()
                //层数选择“2/3”层时，应默认选择B瓦
                if (layerTexts[position].contains("2") || layerTexts[position].contains("3")) {
                    defaultFlute = flutes.find { fluteId ->
                        fluteId.type == "B"
                    }
                    if (defaultFlute == null) {
                        defaultFlute = flutes.firstOrNull()
                    }
                }
                defaultFlute?.let {
                    mBinding.fluteText.text = defaultFlute.type
                    mPurchaseItem.flute = defaultFlute.type;
                }
            }
            picker.show()
        }
        mBinding.materialText.setAutoUpperCase();
        val chooseMaterialAction = {
            val layer = mPurchaseItem.floor;
            if (!layerMap.containsKey(layer)) {
                Toast.makeText(applicationContext, "请先选择层数", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this@AddPurchaseItemActivity, ChoosePlatCodeActivity::class.java)
                intent.putExtra("floor",  layer?.toIntOrNull() ?: 2)
                startActivity(intent)
            }
        };
        mBinding.chooseMaterialContainer.setOnClickListener {
            chooseMaterialAction.invoke()
        }
        mBinding.materialContainer.setOnClickListener {
            chooseMaterialAction.invoke()
        }

        mBinding.fluteContainer.setOnClickListener {
            hideKeyboard()
            chooseWaLenTypeByBottomSheet()
        }


        mBinding.lineContainer.setOnClickListener {
            hideKeyboard()
            if (mPurchaseItem.isStrangeBox()
                && BoxConfigData.noneLineDesc.equals(mBinding.lineText.text.trim().toString())) {
                Toast.makeText(applicationContext, "异形箱暂时不支持设置压线", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val picker = OptionPicker(this)
            val labels = lines.values.toList();
            picker.setTitle("请选择压线")
            picker.setData(labels)
            picker.setDefaultValue(mBinding.lineText.text.trim().toString())
            picker.setOnOptionPickedListener { position, item ->
                mBinding.lineText.text = labels[position]
                if (BoxConfigData.noneLineDesc.equals(mBinding.lineText.text.trim().toString())) {
                    mBinding.lineConfigContainer.visibility = View.GONE
                } else {
                    mBinding.lineConfigContainer.visibility = View.VISIBLE
                }
            }
            picker.show()
        }

        mBinding.deliveryDateContainer.setOnClickListener {
            hideKeyboard()
            val picker = DatePicker(this@AddPurchaseItemActivity)
            //交换日期不能为当天
            picker.wheelLayout.setRange(DateEntity.dayOnFuture(1), null)
            picker.wheelLayout.setDefaultValue(PingDanAppUtils.getDateEntity(mBinding.deliveryDateText.text.toString()))
            picker.setOnDatePickedListener { year, month, day ->
                //2025-06-28
                val date = PingDanAppUtils.getDate(year, month, day)
                mBinding.deliveryDateText.setText(date)
            }
            picker.show();
        }

        mBinding.autoGenerateBtn.setOnClickListener {
            if (TextUtils.isEmpty(mPurchaseItem.floor)) {
                Toast.makeText(applicationContext, "请选择层数", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val selectBoxTypeItem = boxTypeConfigItems.find { it.typeName == mPurchaseItem.boxTypeName }
            if (selectBoxTypeItem == null) {
                Toast.makeText(applicationContext, "请选择箱箱型", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(mBinding.boxLength.text.trim().toString())) {
                Toast.makeText(applicationContext, "请输入纸箱长", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(mBinding.boxWidth.text.trim().toString())) {
                Toast.makeText(applicationContext, "请输入纸箱宽", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(mBinding.boxHeight.text.trim().toString())) {
                Toast.makeText(applicationContext, "请输入纸箱高", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val parameters = hashMapOf<String, Any>(
                "length" to (mBinding.boxLength.text.trim().toString().toIntOrNull() ?: 0),
                "width" to (mBinding.boxWidth.text.trim().toString().toIntOrNull() ?: 0),
                "height" to (mBinding.boxHeight.text.trim().toString().toIntOrNull() ?: 0),
                "type" to (selectBoxTypeItem.type ?: 0),
                "floor" to (mPurchaseItem.floor ?: 0)
            )
            mViewModel.calculateBoxConvert(parameters).observe(this@AddPurchaseItemActivity) {
                if (it.success) {
                    mBinding.paperLength.text.clear()
                    mBinding.paperLength.text.append("${it.data?.length ?: ""}")
                    mBinding.paperWidth.text.clear()
                    mBinding.paperWidth.text.append("${it.data?.width ?: ""}")

                    mBinding.lineConfigContainer.visibility = View.VISIBLE
                    mBinding.lineText.text = BoxConfigData.hasLineDesc

                    mBinding.lineLength.text.clear()
                    mBinding.lineLength.text.append("${it.data?.boxHeightOne ?: ""}")

                    mBinding.lineWidth.text.clear()
                    mBinding.lineWidth.text.append("${it.data?.boxWidth ?: ""}")

                    mBinding.lineHeight.text.clear()
                    mBinding.lineHeight.text.append("${it.data?.boxHeightTwo ?: ""}")

                    //自动滑动到底部
                    val y = max(mBinding.scrollViewContent.height - mBinding.scrollView.height, 0)
                    mBinding.scrollView.scrollTo(0, y);

                } else {
                    Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
                }
            }
        }


        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit()
        }



        registerDefaultLoad(mBinding.body, ApiService.COMMON_KEY)
    }


    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mPageData.observe(this) { outerIt ->
            if(outerIt.success) {
                showSuccess(Constant.COMMON_KEY)
                showSuccessPage()
            } else {
                showError(outerIt.msg, Constant.COMMON_KEY)
            }
        }
    }


    override fun initData() {
        super.initData()
        mViewModel.getPageData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun showSuccessPage() {
        mViewModel.mLenTypeConfig.value?.let { outerIt ->
            if (outerIt.success) {
                BoxConfigData.updateLenConfig(outerIt.data)
            }
        }

        mViewModel.mBoxTypeConfig.value?.let { outerIt ->
            if (outerIt.success) {
                outerIt.data?.let {
                    boxTypeConfigItems.clear()
                    boxTypeConfigItems.addAll(it)
                }
            }
        }

        if ("edit".equals(mode)) {
            fillEditInfo()
        }

        if ("buyAgain".equals(mode)) {
            fillOrderItemEditInfo()
        }

        if ("buyAgainMaterial".equals(mode)) {
            fillMaterialItemEditInfo()
        }

        if (TextUtils.isEmpty(mBinding.boxTypeText.text)) {
            val item = boxTypeConfigItems.firstOrNull()
            item?.let {
                mBinding.boxTypeText.text = item.typeName
                mBinding.boxTypeImage.setImageUrlAndEnablePreviewByXPopup(item.image.jpgMaxWith(640))
                mPurchaseItem.boxTypeName = item.typeName
                mPurchaseItem.boxTypeNum = item.type;
            }
        }

        //默认选择3层，默认选择B
        if (TextUtils.isEmpty(mBinding.layerText.text.trim().toString())) {
            mBinding.layerText.text = "3层"
            mPurchaseItem.floor = "3";
            val flutes = layerMap[mPurchaseItem.floor] ?: emptyList()

            if (flutes.isNotEmpty()) {
                val fluteItem = flutes.find { it.type == "B" } ?: flutes.first()
                mBinding.fluteText.text = fluteItem.type
                mPurchaseItem.flute = fluteItem.type;
            }
        }


        configForStrangeBox()

        //默认明线
        if (TextUtils.isEmpty(mBinding.lineText.text.trim().toString())
            && mBinding.lineConfigContainer.isVisible) {
            mBinding.lineText.text = BoxConfigData.hasLineDesc
        }
    }

    private fun configForStrangeBox() {
        if (mPurchaseItem.isStrangeBox()) {
            mBinding.lineText.text = BoxConfigData.noneLineDesc
            mBinding.autoGenerateBoxContainer.gone()
            mBinding.lineConfigContainer.gone()
        } else {
            mBinding.autoGenerateBoxContainer.visible()
            if (BoxConfigData.noneLineDesc.equals(mBinding.lineText.text.toString())) {
                mBinding.lineConfigContainer.gone()
            } else {
                mBinding.lineConfigContainer.visible()
            }
        }
        if (mPurchaseItem.isYouDiWuGaiBox()) {
            //无盖
            mBinding.lineLength.text.clear()
            mBinding.lineLength.text.append("0");

            mBinding.lineHeight.visible()
            mBinding.lineHeightContainer.visible()
        } else if(mPurchaseItem.isYouGaiWuDiBox()) {
            mBinding.lineLengthContainer.visible()
            mBinding.lineLength.visible()

            //无底
            mBinding.lineHeight.text.clear()
            mBinding.lineHeight.text.append("0");
        } else {
            mBinding.lineLengthContainer.visible()
            mBinding.lineLength.visible()
            mBinding.lineHeight.visible()
            mBinding.lineHeightContainer.visible()
        }
    }


    private fun chooseWaLenTypeByBottomSheet() {
        val layer = mPurchaseItem.floor;
        if (!layerMap.containsKey(layer)) {
            Toast.makeText(applicationContext, "请先选择层数", Toast.LENGTH_LONG).show()
            return
        }
        val flutes = layerMap[layer] ?: emptyList()
        showChooseWaLenTypeConfigDialog(
            selectCode = mPurchaseItem.flute,
            items = flutes,
            chooseAction = {
                mBinding.fluteText.text = it?.type
                mPurchaseItem.flute = it?.type;
            }
        )
    }

    private fun chooseWaLenTypeByOptionPicker() {
        val layer = mPurchaseItem.floor;
        if (!layerMap.containsKey(layer)) {
            Toast.makeText(applicationContext, "请先选择层数", Toast.LENGTH_LONG).show()
            return
        }
        val flutes = layerMap[layer] ?: emptyList()
        val picker = OptionPicker(this)
        picker.setTitle("请选择瓦型")
        picker.setData(flutes)
        picker.setDefaultValue(mPurchaseItem.flute)
        picker.setOnOptionPickedListener { position, item ->
            mBinding.fluteText.text = flutes[position].type
            mPurchaseItem.flute = flutes[position].type;
        }
        picker.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChooseMaterialPriceItemEvent(event : ChooseMaterialPriceItemEvent){
        mBinding.materialText.text.clear();
        mBinding.materialText.text.append(event.item.platCode ?: "")

        var floor = event.item.floor?.toIntOrNull()
        if (floor == null) {
            if(event.item.platCode != null) {
                floor = event.item.platCode?.length ?: 0
            }
        }
        if (floor != null) {
            val layerCodes = BoxConfigData.getFloorLayerCodes(floor.toString())
            if (layerCodes.contains(event.item.lenType ?: "")) {
                mBinding.layerText.text = "${floor}层"
                mPurchaseItem.floor = floor.toString();
                mBinding.fluteText.text = event.item.lenType ?: ""
                mPurchaseItem.flute = event.item.lenType ?: ""
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChooseHistoryOrderItemEvent(event : ChooseSelectHistoryOrderItemEvent) {
        var floor = event.item.floor
        if (floor == null) {
             if(event.item.platCode != null) {
                 floor = event.item.platCode?.length ?: 0
             }
        }
        if (floor != null) {
            val layerCodes = BoxConfigData.getFloorLayerCodes(floor.toString())
            if (layerCodes.contains(event.item.lenType ?: "")) {
                mBinding.layerText.text = "${floor}层"
                mPurchaseItem.floor = floor.toString();
                mBinding.fluteText.text = event.item.lenType ?: ""
                mPurchaseItem.flute = event.item.lenType ?: ""
            }
        }
        mBinding.materialText.text.clear();
        mBinding.materialText.text.append(event.item.platCode ?: "")

        mBinding.paperLength.text.clear()
        mBinding.paperLength.text.append("${event.item.length  ?: ""}")
        mBinding.paperWidth.text.clear()
        mBinding.paperWidth.text.append("${event.item.width  ?: ""}")

        if (event.item.line == 0) {
            mBinding.lineConfigContainer.visibility = View.GONE
            mBinding.lineText.text = BoxConfigData.noneLineDesc


            val types = BoxConfigData.splitTouch(event.item.touchSize, event.item.touch)
            if (types.size == 3) {
                mBinding.lineLength.text.clear()
                mBinding.lineLength.text.append(types[0] ?: "")

                mBinding.lineWidth.text.clear()
                mBinding.lineWidth.text.append(types[1] ?: "")

                mBinding.lineHeight.text.clear()
                mBinding.lineHeight.text.append(types[2] ?: "")
            }

        } else {
            mBinding.lineConfigContainer.visibility = View.VISIBLE
            mBinding.lineText.text = BoxConfigData.hasLineDesc

            val types = BoxConfigData.splitTouch(event.item.touchSize, event.item.touch)
            if (types.size == 3) {
                mBinding.lineLength.text.clear()
                mBinding.lineLength.text.append(types[0] ?: "")

                mBinding.lineWidth.text.clear()
                mBinding.lineWidth.text.append(types[1] ?: "")

                mBinding.lineHeight.text.clear()
                mBinding.lineHeight.text.append(types[2] ?: "")
            }
        }
    }

    fun fillEditInfo() {
        val floor = mPurchaseItem.floor
        if (floor != null) {
            mBinding.layerText.text = "${floor}层"
        }

        mBinding.materialText.text.clear()
        mBinding.materialText.text.append(mPurchaseItem.platCode ?: "")

        mBinding.fluteText.text = mPurchaseItem.flute ?: ""


        mBinding.boxTypeText.text = mPurchaseItem.boxTypeName ?: ""

        val item = boxTypeConfigItems.find { it.typeName == mPurchaseItem.boxTypeName }
        item?.let {
            mBinding.boxTypeImage.setImageUrlAndEnablePreviewByXPopup(item.image)
        }

        mBinding.useBoxTypeSwitch.isChecked = mPurchaseItem.useBoxTypeSwitch

        if (mPurchaseItem.boxLength != null) {
            mBinding.boxLength.text.clear()
            mBinding.boxLength.text.append((mPurchaseItem.boxLength ?: "").toString())
        }

        if (mPurchaseItem.boxWidth != null) {
            mBinding.boxWidth.text.clear()
            mBinding.boxWidth.text.append((mPurchaseItem.boxWidth ?: "").toString())
        }

        if (mPurchaseItem.boxHeight != null) {
            mBinding.boxHeight.text.clear()
            mBinding.boxHeight.text.append((mPurchaseItem.boxHeight ?: "").toString())
        }


        if (mPurchaseItem.paperLength != null) {
            mBinding.paperLength.text.clear()
            mBinding.paperLength.text.append("${mPurchaseItem.paperLength ?: ""}")
        }

        if (mPurchaseItem.paperWidth != null) {
            mBinding.paperWidth.text.clear()
            mBinding.paperWidth.text.append("${mPurchaseItem.paperWidth ?: ""}")
        }


        if (mPurchaseItem.line == "0") {
            mBinding.lineConfigContainer.visibility = View.GONE
            mBinding.lineText.text = BoxConfigData.noneLineDesc

            if (mPurchaseItem.lineLength != null) {
                mBinding.lineLength.text.clear()
                mBinding.lineLength.text.append((mPurchaseItem.lineLength ?: "").toString())
            }

            if (mPurchaseItem.lineWidth != null) {
                mBinding.lineWidth.text.clear()
                mBinding.lineWidth.text.append((mPurchaseItem.lineWidth ?: "").toString())
            }

            if (mPurchaseItem.lineHeight != null) {
                mBinding.lineHeight.text.clear()
                mBinding.lineHeight.text.append((mPurchaseItem.lineHeight ?: "").toString())
            }
        } else {
            mBinding.lineConfigContainer.visibility = View.VISIBLE
            mBinding.lineText.text = BoxConfigData.hasLineDesc

            if (mPurchaseItem.lineLength != null) {
                mBinding.lineLength.text.clear()
                mBinding.lineLength.text.append((mPurchaseItem.lineLength ?: "").toString())
            }

            if (mPurchaseItem.lineWidth != null) {
                mBinding.lineWidth.text.clear()
                mBinding.lineWidth.text.append((mPurchaseItem.lineWidth ?: "").toString())
            }

            if (mPurchaseItem.lineHeight != null) {
                mBinding.lineHeight.text.clear()
                mBinding.lineHeight.text.append((mPurchaseItem.lineHeight ?: "").toString())
            }
        }

        if (mPurchaseItem.num > 0) {
            mBinding.purchaseAmount.text.clear()
            mBinding.purchaseAmount.text.append((mPurchaseItem.num).toString())
        }
        if (mPurchaseItem.demandTime != null) {
            mBinding.deliveryDateText.text = mPurchaseItem.demandTime ?: ""
        }

        if (!TextUtils.isEmpty(mPurchaseItem.inputRemark)) {
            mBinding.inputDesc.text.clear()
            mBinding.inputDesc.text.append(mPurchaseItem.inputRemark)
        }

        mBinding.deleteOrder.setOnClickListener {
            deleteOrderItem();
        }

        mBinding.deleteOrder.visibility = View.VISIBLE
        mBinding.chooseFromHistoryOrder.visibility = View.GONE

        mBinding.ivTitle.text = "编辑采购信息"
        mBinding.btnSubmit.text = "保存"
    }

    fun fillMaterialItemEditInfo() {
        buyAgainMaterialPriceItem?.let {
            val floor = it.floor
            if (floor != null) {
                mBinding.layerText.text = "${floor}层"
                mPurchaseItem.floor = floor
            }

            mBinding.materialText.text.clear()
            mBinding.materialText.text.append(it.platCode ?: "")

            mBinding.fluteText.text = it.lenType ?: ""
            mPurchaseItem.flute = it.lenType

        }

    }

    fun fillOrderItemEditInfo() {
        buyAgainOrderItem?.let {
            val floor = it.floor
            if (floor != null) {
                mBinding.layerText.text = "${floor}层"
                mPurchaseItem.floor = floor
            }

            mBinding.materialText.text.clear()
            mBinding.materialText.text.append(it.platCode ?: "")

            mBinding.fluteText.text = it.lenType ?: ""
            mPurchaseItem.flute = it.lenType

            if (it.length != null) {
                mBinding.paperLength.text.clear()
                mBinding.paperLength.text.append("${it.length ?: ""}")
            }

            if (it.width != null) {
                mBinding.paperWidth.text.clear()
                mBinding.paperWidth.text.append("${it.width ?: ""}")
            }


            val lineSizes = BoxConfigData.splitTouch( it.touchSize ,  it.touch)
            if (lineSizes.size == 3) {
                mBinding.lineLength.text.clear()
                mBinding.lineLength.text.append(lineSizes[0].toString())

                mBinding.lineWidth.text.clear()
                mBinding.lineWidth.text.append((lineSizes[1]).toString())

                mBinding.lineHeight.text.clear()
                mBinding.lineHeight.text.append((lineSizes[2]).toString())
            }

            if (it.line == 0) {
                mBinding.lineConfigContainer.visibility = View.GONE
                mBinding.lineText.text = BoxConfigData.noneLineDesc
            } else {
                mBinding.lineConfigContainer.visibility = View.VISIBLE
                mBinding.lineText.text = BoxConfigData.hasLineDesc
            }

        }

    }

    private fun deleteOrderItem() {
        EventBusUtils.postEvent(DeleteOrderPurchaseItemEvent(mPurchaseItem))
        val intent = Intent();
        intent.putExtra("item", mPurchaseItem);
        setResult(RESULT_OK, intent);
        finish()
    }

    fun checkAndSubmit() {
        if (TextUtils.isEmpty(mPurchaseItem.floor)) {
            Toast.makeText(applicationContext, "请选择层数", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.materialText.text.trim().toString())) {
            Toast.makeText(applicationContext, "请输入或选择材质代码", Toast.LENGTH_LONG).show()
            return
        }
        val material = mBinding.materialText.text.trim().toString()
        val layer = mPurchaseItem.floor?.toIntOrNull() ?: 0
        if (material.length != layer) {
            Toast.makeText(applicationContext, "材质代码需为${layer}层的长度", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mPurchaseItem.flute)) {
            Toast.makeText(applicationContext, "请选择瓦型", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.paperLength.text.trim().toString())) {
            Toast.makeText(applicationContext, "请输入纸板长", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.paperWidth.text.trim().toString())) {
            Toast.makeText(applicationContext, "请输入纸板宽", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.lineText.text.trim().toString())) {
            Toast.makeText(applicationContext, "请选择压线", Toast.LENGTH_LONG).show()
            return
        }


        if (!BoxConfigData.noneLineDesc.equals(mBinding.lineText.text.trim().toString())) {
            val lineLength = mBinding.lineLength.text.trim().toString().toIntOrNull() ?: 0
            val lineWidth = mBinding.lineWidth.text.trim().toString().toIntOrNull() ?: 0
            val lineHeight = mBinding.lineHeight.text.trim().toString().toIntOrNull() ?: 0
            val expectWidth = mBinding.paperWidth.text.trim().toString().toIntOrNull() ?: 0;
            if (expectWidth <= 0) {
                Toast.makeText(applicationContext, "纸板宽必须为正数", Toast.LENGTH_LONG).show()
                return
            }
            val sumWith = lineLength + lineWidth + lineHeight
            if (sumWith != expectWidth) {
                Toast.makeText(applicationContext, "压线的数据总和应等于纸板宽度", Toast.LENGTH_LONG).show()
                return
            }

            /**
            if (TextUtils.isEmpty(mBinding.lineLength.text.trim().toString())) {
                Toast.makeText(applicationContext, "请输入压线箱盖", Toast.LENGTH_LONG).show()
                return
            }

            if (TextUtils.isEmpty(mBinding.lineWidth.text.trim().toString())) {
                Toast.makeText(applicationContext, "请输入压线箱高", Toast.LENGTH_LONG).show()
                return
            }

            val lineWidth = mBinding.lineWidth.text.trim().toString().toIntOrNull() ?: 0
            if (lineWidth <= 0) {
                Toast.makeText(applicationContext, "压线箱高必须为正数", Toast.LENGTH_LONG).show()
                return
            }

            if (TextUtils.isEmpty(mBinding.lineHeight.text.trim().toString())) {
                Toast.makeText(applicationContext, "请输入压线箱盖", Toast.LENGTH_LONG).show()
                return
            }*/
        }

        if (TextUtils.isEmpty(mBinding.purchaseAmount.text.trim().toString())) {
            Toast.makeText(applicationContext, "请输入订购数量", Toast.LENGTH_LONG).show()
            return
        }

        val purchaseAmount = mBinding.purchaseAmount.text.trim().toString().toIntOrNull() ?: 0
        if (purchaseAmount <= 0) {
            Toast.makeText(applicationContext, "订购数量最小为1", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(mBinding.deliveryDateText.text.trim().toString())) {
            Toast.makeText(applicationContext, "请选择交货日期", Toast.LENGTH_LONG).show()
            return
        }

        showLoadingDialog()
        mViewModel.checkMaterial(material).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                submitFormInfo()
            } else {
                Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
            }
        }


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


     fun submitFormInfo() {

         mPurchaseItem.platCode = mBinding.materialText.text.trim().toString()

         mPurchaseItem.boxLength  = mBinding.boxLength.text.trim().toString().toIntOrNull()
         mPurchaseItem.boxWidth  = mBinding.boxWidth.text.trim().toString().toIntOrNull()
         mPurchaseItem.boxHeight = mBinding.boxHeight.text.trim().toString().toIntOrNull()

         mPurchaseItem.paperLength  = mBinding.paperLength.text.trim().toString().toIntOrNull()
         mPurchaseItem.paperWidth  = mBinding.paperWidth.text.trim().toString().toIntOrNull()
         mPurchaseItem.pageSize = mBinding.paperLength.text.trim().toString() + "*" + mBinding.paperWidth.text.trim().toString()

         if (!BoxConfigData.noneLineDesc.equals(mBinding.lineText.text.trim().toString())) {
             mPurchaseItem.line = "1"
             mPurchaseItem.touch = mBinding.lineLength.text.trim().toString() +
                     "+" + mBinding.lineWidth.text.trim().toString() +
                     "+" + mBinding.lineHeight.text.trim().toString()

             mPurchaseItem.lineLength  = mBinding.lineLength.text.trim().toString().toIntOrNull()
             mPurchaseItem.lineWidth  = mBinding.lineWidth.text.trim().toString().toIntOrNull()
             mPurchaseItem.lineHeight  = mBinding.lineHeight.text.trim().toString().toIntOrNull()
         } else {
             mPurchaseItem.line = "0"
             mPurchaseItem.touch = ""
             mPurchaseItem.lineLength  = mBinding.lineLength.text.trim().toString().toIntOrNull()
             mPurchaseItem.lineWidth  = mBinding.lineWidth.text.trim().toString().toIntOrNull()
             mPurchaseItem.lineHeight  = mBinding.lineHeight.text.trim().toString().toIntOrNull()
         }

         mPurchaseItem.num = mBinding.purchaseAmount.text.trim().toString().toIntOrNull() ?: 0
         mPurchaseItem.demandTime = mBinding.deliveryDateText.text.trim().toString();

         mPurchaseItem.inputRemark = mBinding.inputDesc.text.trim().toString()

         if ("edit".equals(mode)) {
             EventBusUtils.postEvent(EditOrderPurchaseItemEvent(mPurchaseItem))
             val intent = Intent();
             intent.putExtra("item", mPurchaseItem);
             setResult(RESULT_OK, intent);
             finish()
         } else if ("buyAgain".equals(mode)) {
             val intent = Intent(
                 this,
                 AddOrderActivity::class.java
             )
             intent.putExtra("buyAgainItem", mPurchaseItem);
             startActivity(intent)
             finish()
         } else if ("buyAgainMaterial".equals(mode)) {
             val intent = Intent(
                 this,
                 AddOrderActivity::class.java
             )
             intent.putExtra("buyAgainItem", mPurchaseItem);
             startActivity(intent)
             finish()
         }   else {
             EventBusUtils.postEvent(AddOrderPurchaseItemEvent(mPurchaseItem))
             val intent = Intent();
             intent.putExtra("item", mPurchaseItem);
             setResult(RESULT_OK, intent);
             finish()
         }

    }




    fun showTipDialog() {
        val builder = AlertDialog.Builder(this@AddPurchaseItemActivity)
        builder.setTitle("信息提交成功")
        builder.setMessage("您填写的收货地址信息已提交。")
        builder.setPositiveButton("我知道了", object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }

        })
        builder.show()
    }


}