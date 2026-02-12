package com.zhongpin.mvvm_android.ui.order.add.history.search

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.gyf.immersionbar.ImmersionBar.*
import com.zhilianshidai.pindan.app.databinding.ActivityHistoryOrderSearchQueryBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.ui.common.BoxConfigData
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import kotlin.collections.get
import androidx.core.view.isGone
import com.zhongpin.mvvm_android.bean.SelectHistoryOrderSearchQuery
import com.zhongpin.mvvm_android.bean.SelectHistoryOrderSearchQueryEvent
import com.zhongpin.mvvm_android.common.utils.hideKeyboard
import com.zhongpin.mvvm_android.ui.view.ext.setAutoUpperCase


class HistoryOrderSearchQueryActivity : BaseVMActivity<HistoryOrderSearchQueryViewModel>() {

    private lateinit var mBinding: ActivityHistoryOrderSearchQueryBinding;

    private var searchQuery: SelectHistoryOrderSearchQuery? = null;
    private var mSelectDate:String? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query", SelectHistoryOrderSearchQuery::class.java)
        }
        if (searchQuery == null) {
            searchQuery = SelectHistoryOrderSearchQuery();
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityHistoryOrderSearchQueryBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.submitButton.setOnClickListener {
            searchOrder();
        }

        mBinding.scrollBody.setOnClickListener {
            hideKeyboard()
        }

        mBinding.layerCodeContainer.setOnClickListener {
            hideKeyboard()
            val allText = "全部"
            val layerCodes = BoxConfigData.getAllLayerCode().toMutableList()
            layerCodes.add(0, allText)
            val picker = OptionPicker(this)
            picker.setTitle("请选择瓦型")
            picker.setData(layerCodes)
            picker.setDefaultValue(searchQuery?.layerCode ?: allText)
            picker.setOnOptionPickedListener { position, item ->
                mBinding.layerCodeText.text = layerCodes[position]
                searchQuery?.layerCode =  layerCodes[position]
                searchQuery?.trimAllText()
            }
            picker.show()
        }

        mBinding.lineTypeContainer.setOnClickListener {
            hideKeyboard()
            val picker = OptionPicker(this)
            val allText = "全部"
            val labels = BoxConfigData.lines.values.toMutableList();
            labels.add(0, allText)
            picker.setTitle("请选择压线")
            picker.setData(labels)
            picker.setDefaultValue(searchQuery?.lineType ?: allText)
            picker.setOnOptionPickedListener { position, item ->
                mBinding.lineTypeText.text = labels[position]
                searchQuery?.lineType = labels[position]
                searchQuery?.trimAllText()
                if (BoxConfigData.noneLineDesc.equals(mBinding.lineTypeText.text.trim().toString())
                    || TextUtils.isEmpty(searchQuery?.lineType)) {
                    mBinding.lineDescContainer.visibility = View.GONE
                } else {
                    mBinding.lineDescContainer.visibility = View.VISIBLE
                }
            }
            picker.show()
        }

        mBinding.platCode.setAutoUpperCase();

        searchQuery?.let {
            mBinding.platCode.setText(it.platCode ?: "");
            mBinding.layerCodeText.setText(it.layerCode ?: it.lastTrimLayerCode);
            mBinding.lineTypeText.setText(it.lineType ?: it.lastTrimLineType);
            mBinding.paperSize.setText(it.paperSize ?: "");
            mBinding.lineDesc.setText(it.lineDesc ?: "");
            if (BoxConfigData.noneLineDesc.equals(mBinding.lineTypeText.text.trim().toString())) {
                mBinding.lineDescContainer.visibility = View.GONE
            } else {
                mBinding.lineDescContainer.visibility = View.VISIBLE
            }
        }

    }

    fun searchOrder() {
        val platCode:String? = mBinding.platCode.text.trim().toString();
        val layerCode:String? = mBinding.layerCodeText.text.trim().toString();
        val lineType:String? = mBinding.lineTypeText.text.trim().toString();
        val paperSize:String? = mBinding.paperSize.text.trim().toString();
        val lineDesc:String? = mBinding.lineDesc.text.trim().toString();


        val query = SelectHistoryOrderSearchQuery(
            platCode = platCode,
            layerCode = layerCode,
            lineType = lineType,
            paperSize = paperSize,
            lineDesc = lineDesc
        )

        query.trimAllText()

        if (mBinding.lineDescContainer.isGone) {
            query.lineDesc = null
        }
        EventBusUtils.postEvent(SelectHistoryOrderSearchQueryEvent(query))
        val intent = Intent();
        intent.putExtra("query", query);
        setResult(RESULT_OK, intent);
        finish()
    }

    override fun initDataObserver() {
        super.initDataObserver()
    }

    override fun initData() {
        super.initData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}