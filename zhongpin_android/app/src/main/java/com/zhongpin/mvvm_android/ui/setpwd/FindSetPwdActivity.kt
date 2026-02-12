package com.zhongpin.mvvm_android.ui.setpwd

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zhilianshidai.pindan.app.databinding.ActivityFindSetPwdBinding
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.biz.utils.RsaUtil
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.login.LoginActivity

class FindSetPwdActivity : BaseVMActivity<FindSetPwdViewModel>() {


    private lateinit var mBinding: ActivityFindSetPwdBinding;
    private lateinit var mLoadingDialog: LoadingDialog

    private var code:String = "";
    private var mobile:String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.immersive(this)
        if(intent != null) {
            code = intent.getStringExtra("code") ?:""
            mobile = intent.getStringExtra("mobile") ?:"";
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityFindSetPwdBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        mLoadingDialog = LoadingDialog(this, false)
        mBinding.ivBack.setOnClickListener { finish() }
        mBinding.btnLogin.setOnClickListener {
            setAndCheck()
        }

    }

    fun setAndCheck(){
        realSetPassword()
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

    fun realSetPassword(){
        val firstPassword = mBinding.editUsername.text.trim().toString()
        val secondPassword = mBinding.confirmPwd.text.trim().toString()
        if (firstPassword.isNotEmpty() and secondPassword.isNotEmpty()) {
            if (!firstPassword.equals(secondPassword)) {
                Toast.makeText(applicationContext,"两次输入密码不一致，请重新输入", Toast.LENGTH_LONG).show()
                return
            }
            if (secondPassword.length < 6) {
                Toast.makeText(applicationContext,"密码长度最小为6", Toast.LENGTH_LONG).show()
                return
            }
            showLoadingDialog()
            val encryptPassword = RsaUtil.encrypt(secondPassword, RsaUtil.PUBLIC_KEY);
            mViewModel.setPassword(
                mobile,
                encryptPassword,
                code
            ).observe(this) {
                dismissLoadingDialog()
                if (it.success) {
                    Toast.makeText(applicationContext,"设置成功，请登录", Toast.LENGTH_LONG).show()
                    goLoginActivity()
                } else {
                    Toast.makeText(applicationContext,"设置失败, " + it.msg, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(applicationContext,"密码或确认密码为空", Toast.LENGTH_LONG).show()
        }
    }

    fun  goLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}