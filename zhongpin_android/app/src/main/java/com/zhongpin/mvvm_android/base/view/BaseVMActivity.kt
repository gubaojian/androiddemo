package com.zhongpin.mvvm_android.base.view

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.base.viewstate.StateType
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.common.callback.EmptyCallBack
import com.zhongpin.mvvm_android.common.callback.ErrorCallBack
import com.zhongpin.mvvm_android.common.callback.LoadingCallBack
import com.zhongpin.mvvm_android.common.callback.PlaceHolderCallBack
import com.zhongpin.mvvm_android.common.utils.CommonUtils
import com.zhongpin.mvvm_android.common.utils.Constant

abstract class BaseVMActivity<VM : BaseViewModel<*>> : BaseActivity(),BaseView {

    protected lateinit var mViewModel: VM
    private var loadService: LoadService<*>? = null
    private var loadKeys:MutableList<String> = ArrayList()

    abstract fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?):View;


    override fun setContentView(){
        val inflater = LayoutInflater.from(this)
        val rootView = createContentViewByBinding(inflater, null)
        setContentView(rootView)
        mViewModel = getActivityViewModelProvider(this).get(CommonUtils.getClass(this))
    }



    override fun initView() {
        mViewModel.loadState.observe(this, observer)
        mViewModel.loadState.observe(this, {
            dismissLoadingDialogV2()
        })
        mViewModel.dialogLoadState.observe(this, {
            if (it.code == StateType.ERROR || it.code == StateType.NETWORK_ERROR) {
                ToastExt.throttleToast(it.message, {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
            }
            dismissLoadingDialogV2()
        })
        initDataObserver()

    }

    fun registerPlaceHolderLoad(view: View,placeHolderLayoutID:Int){
        val loadSir = LoadSir.Builder()
            .addCallback(PlaceHolderCallBack(placeHolderLayoutID))
            .addCallback(EmptyCallBack())
            .addCallback(ErrorCallBack())
            .setDefaultCallback(PlaceHolderCallBack::class.java)
            .build()
        loadService =  loadSir.register(view) {  reLoad() }
    }

    override fun registerDefaultLoad(view: View, key:String){
        if(!TextUtils.isEmpty(key))loadKeys.add(key)
        loadService =  LoadSir.getDefault().register(view) {  reLoad() }
    }

    override fun initData() {
        if(loadKeys.size>0)
            showLoading(loadKeys[0])
    }

    open fun initDataObserver() {}

    override fun onDestroy() {
        super.onDestroy()
    }


    fun showPlaceHolder() {
        loadService?.showCallback(PlaceHolderCallBack::class.java)
    }

    override fun showLoading(key:String) {
        if(loadKeys.contains(key)
            || Constant.COMMON_KEY == key
            || TextUtils.isEmpty(key)
            || loadKeys.size == 1){
            loadService?.showCallback(LoadingCallBack::class.java)
        }
    }

    override fun showSuccess(key:String) {
        if(loadKeys.contains(key)
            || Constant.COMMON_KEY == key
            || TextUtils.isEmpty(key)
            || loadKeys.size == 1){
            loadService?.showCallback(SuccessCallback::class.java)
        }
    }

    override fun showEmpty(key:String) {
        if(loadKeys.contains(key)
            || Constant.COMMON_KEY == key
            || TextUtils.isEmpty(key)
            || loadKeys.size == 1){
            loadService?.showCallback(EmptyCallBack::class.java)
        }
    }


    override fun showError(msg: String, key:String) {
        if (!TextUtils.isEmpty(msg)) {
            ToastExt.throttleToast(msg, {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            })
        }
        if(loadKeys.contains(key)
            || Constant.COMMON_KEY == key
            || TextUtils.isEmpty(key)
            || loadKeys.size == 1){
            loadService?.showCallback(ErrorCallBack::class.java)
        }
    }

    private val observer by lazy {
        Observer<State> {
            it?.let {
                var errorMsg = it.message;
                if (TextUtils.isEmpty(errorMsg)) {
                    errorMsg = "网络异常";
                }
                when {
                    it.code == StateType.SUCCESS -> showSuccess(it.urlKey)
                    it.code == StateType.ERROR -> showError(errorMsg, it.urlKey)
                    it.code == StateType.NETWORK_ERROR -> showError(errorMsg, it.urlKey)
                    it.code == StateType.EMPTY -> showEmpty(it.urlKey)
                }
            }
        }
    }

    private var mLoadingDialogV2: LoadingDialog? = null
    /**
     * show 对话框loading，区别于页面的加载loading
     */
    fun showLoadingDialogV2() {
        dismissLoadingDialogV2()
        if (mLoadingDialogV2 == null) {
            mLoadingDialogV2 = LoadingDialog(this, false)
        }
        mLoadingDialogV2?.showDialogV2(this)
    }

    /**
     * dismiss 对话框loading，区别于页面的加载loading
     */
    fun dismissLoadingDialogV2() {
        mLoadingDialogV2?.dismissDialogV2()
        mLoadingDialogV2 = null
    }
}