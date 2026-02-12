package com.zhongpin.mvvm_android.ui.home.platform.list.search

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar.*
import com.tencent.mmkv.MMKV
import com.zhilianshidai.pindan.app.databinding.ActivityCompanyMemberSearchQueryBinding
import com.zhilianshidai.pindan.app.databinding.ActivityPlatformPriceListSearchQueryBinding
import com.zhilianshidai.pindan.app.databinding.ViewHistoryLabelItemBinding
import com.zhongpin.lib_base.ktx.focusAndShowKeyboard
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.MemberItemSearchQueryChangeEvent
import com.zhongpin.mvvm_android.bean.PlatformMaterialItemSearchQuery
import com.zhongpin.mvvm_android.bean.PlatformMaterialItemSearchQueryChangeEvent
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.home.platform.list.PlatformPriceListActivity
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class PlatformPriceListSearchQueryActivity : BaseVMActivity<PlatformPriceListSearchQueryViewModel>() {


    private val mmkv = MMKV.defaultMMKV()
    private val searchHistoryKey = "platformPriceListSearchHistoryKey"

    private lateinit var mBinding: ActivityPlatformPriceListSearchQueryBinding;

    private var searchQuery: PlatformMaterialItemSearchQuery? = null;
    private var mSelectDate:String? = null;

    private var historyList: MutableList<String>  = mutableListOf();


    override fun onCreate(savedInstanceState: Bundle?) {
        with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            searchQuery = IntentUtils.getSerializableExtra(intent, "query",  PlatformMaterialItemSearchQuery::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPlatformPriceListSearchQueryBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.toolbar)

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





        searchQuery?.let {
            mBinding.searchInput.text.clear()
            mBinding.searchInput.text.append(it.inputText ?: "")
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

        val query = PlatformMaterialItemSearchQuery(
            inputText = searchInput
        );
        EventBusUtils.postEvent(PlatformMaterialItemSearchQueryChangeEvent(query = query))
        if (searchQuery == null) {
            val intent = Intent(this@PlatformPriceListSearchQueryActivity,
                PlatformPriceListActivity::class.java)
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
    fun onMemberItemSearchQueryChangeEvent(event : MemberItemSearchQueryChangeEvent){

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
                val binding = ViewHistoryLabelItemBinding.inflate(LayoutInflater.from(this@PlatformPriceListSearchQueryActivity))
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