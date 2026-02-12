package com.zhongpin.mvvm_android.ui.order.search

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar.*
import com.tencent.mmkv.MMKV
import com.zhilianshidai.pindan.app.databinding.ActivityOrderSearchQueryBinding
import com.zhilianshidai.pindan.app.databinding.ViewHistoryLabelItemBinding
import com.zhongpin.lib_base.ktx.focusAndShowKeyboard
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.OrderSearchQuery
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.order.search.list.OrderSearchListActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class OrderSearchQueryActivity : BaseVMActivity<OrderSearchQueryViewModel>() {


    private val mmkv = MMKV.defaultMMKV()
    private val searchHistoryKey = "searchHistoryKey"

    private lateinit var mBinding: ActivityOrderSearchQueryBinding;

    private var searchQuery: OrderSearchQuery? = null;
    private var mSelectDate:String? = null;

    private var historyList: MutableList<String>  = mutableListOf();


    override fun onCreate(savedInstanceState: Bundle?) {
        with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query", OrderSearchQuery::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityOrderSearchQueryBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        historyList = loadHistory();

        showHistory(historyList)


        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.searchButton.setOnClickListener {
            searchOrder();
        }

        mBinding.clearHistoryContainer.setOnClickListener {
            historyList = mutableListOf()
            saveHistory()
            showHistory(historyList)
        }

        mBinding.clearIcon.setOnClickListener {
            mBinding.searchInput.text.clear()
        }

        mBinding.searchInput.addTextChangedListener(
            afterTextChanged = {
                if (it != null && it.isNotEmpty()) {
                    mBinding.clearIcon.visible()
                } else {
                    mBinding.clearIcon.gone()
                }
            }
        )

        mBinding.submitButton.setOnClickListener {
            searchOrder();
        }

        mBinding.chooseDateAreaText.setOnClickListener {
            val picker: DatePicker = DatePicker(this@OrderSearchQueryActivity)
            picker.setOnDatePickedListener { year, month, day ->
                val date = String.format("%d%02d%02d", year, month, day)
                mSelectDate = date;
                mBinding.chooseDateAreaText.setText(date)
            }
            picker.show();
        }

        searchQuery?.let {
            mBinding.searchInput.text.clear()
            mBinding.searchInput.text.append(it.inputText ?: "")
            mBinding.orderNo.setText(it.orderNo ?: "");
            mBinding.zhiDaiHao.setText(it.zhiDaiHao ?: "");
            mBinding.lengXing.setText(it.lengXing ?: "");
            mBinding.yaXian.setText(it.yaXian ?: "");
            mBinding.xianXing.setText(it.xianXing ?: "");
            mBinding.zhibanFactoryName.setText(it.zhiBanFactorName ?: "");
            mBinding.zhiXiangFactoryName.setText(it.zhiXiangFactorName ?: "");
            mBinding.chooseDateAreaText.setText(it.orderJiaoHuoDate ?: "")
        }

        lifecycleScope.launch {
            delay(150)
            mBinding.searchInput.focusAndShowKeyboard()
        }


    }


    fun searchOrder() {
        val searchInput = mBinding.searchInput.text.trim().toString();
        searchOrderByKeyword(searchInput)
    }

    fun searchOrderByKeyword(searchInput:String) {
        historyList.remove(searchInput);
        if (!TextUtils.isEmpty(searchInput)) {
            historyList.add(0, searchInput)
        }
        if (historyList.size > 10) {
            historyList.removeAt(historyList.size - 1)
        }
        saveHistory()

        var orderNo:String? = mBinding.orderNo.text.trim().toString();
        var zhiDaiHao:String? = mBinding.zhiDaiHao.text.trim().toString();
        var lengXing:String? = mBinding.lengXing.text.trim().toString();
        var yaXian:String? = mBinding.yaXian.text.trim().toString();
        var xianXing:String? = mBinding.xianXing.text.trim().toString();
        var zhiBanFactorName:String? = mBinding.zhibanFactoryName.text.trim().toString();
        var zhiXiangFactorName:String? = mBinding.zhiXiangFactoryName.text.trim().toString();

        val query = OrderSearchQuery(
            orderNo = orderNo,
            zhiDaiHao = zhiDaiHao,
            lengXing = lengXing,
            yaXian = yaXian,
            xianXing = xianXing,
            zhiBanFactorName = zhiBanFactorName,
            zhiXiangFactorName = zhiXiangFactorName,
            orderJiaoHuoDate = mSelectDate,
            inputText = searchInput
        );
        if (searchQuery == null) {
            val intent = Intent(this@OrderSearchQueryActivity, OrderSearchListActivity::class.java)
            intent.putExtra("query", query)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent();
            intent.putExtra("query", query);
            setResult(RESULT_OK, intent);
            finish()
        }
    }

    override fun initDataObserver() {
        super.initDataObserver()
    }

    override fun initData() {
        super.initData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : CompanyInfoChangeEvent){

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showHistory(historyList: MutableList<String>) {
        if (historyList.isEmpty()) {
            mBinding.historyContainer.gone()
        } else {
            mBinding.historyContainer.visible()
            mBinding.flowLayout.removeAllViews()
            historyList.forEach { outIt ->
                val binding = ViewHistoryLabelItemBinding.inflate(LayoutInflater.from(this@OrderSearchQueryActivity))
                binding.label.text = outIt
                binding.root.setOnClickListener {
                    searchOrderByKeyword(outIt)
                }
                mBinding.flowLayout.addView(binding.root)
            }

        }
    }

    private fun saveHistory() {
        try {
            val json = GsonUtils.toJson(historyList)
            mmkv.putString(searchHistoryKey, json)
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun loadHistory(): MutableList<String> {
        try {

            val json =  mmkv.getString(searchHistoryKey, "[]")
            val listType = object : TypeToken<MutableList<String?>?>() {}.getType()
            val array = GsonUtils.fromJson<List<String>>(json, listType);
            return array.map { it.toString() }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace();
            return  mutableListOf();
        }
    }

}