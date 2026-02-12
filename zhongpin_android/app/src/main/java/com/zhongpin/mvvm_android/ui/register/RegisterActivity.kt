package com.zhongpin.mvvm_android.ui.register

import android.content.DialogInterface
import android.content.Intent
import android.os.Build.*
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Html.*
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.SPUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityRegisterBinding
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.LoginEvent
import com.zhongpin.mvvm_android.biz.utils.RsaUtil
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.common.utils.hideKeyboard
import com.zhongpin.mvvm_android.ui.login.LoginActivity
import com.zhongpin.mvvm_android.ui.verify.person.PersonVerifyActivity
import com.zhongpin.mvvm_android.ui.web.WebActivity
import org.greenrobot.eventbus.EventBus

class RegisterActivity : BaseVMActivity<RegisterViewModel>() {


    private lateinit var mBinding: ActivityRegisterBinding;
    private lateinit var countDownTimer: CountDownTimer


    private var mLoadingDialog: LoadingDialog? = null

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityRegisterBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        ImmersionBar.with(this).transparentBar().titleBarMarginTop(mBinding.content).fullScreen(false).keyboardEnable(true).init()
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.btnLogin.setOnClickListener {
            checkAndRegister()
        }


        mBinding.scrollBody.setOnClickListener {
            hideKeyboard()
        }

        mBinding.pwdSeeMaskContainer.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            // 更新输入类型
            if (isPasswordVisible) {
                // 显示密码
                mBinding.loginPwd.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                mBinding.pwdSeeMask.setImageResource(R.mipmap.login_pwd_see)
            } else {
                // 隐藏密码
                mBinding.loginPwd.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                mBinding.pwdSeeMask.setImageResource(R.mipmap.login_pwd_mask)
            }
            // 光标移至末尾
            mBinding.loginPwd.setSelection(mBinding.loginPwd.text.length)
        }

        mBinding.pwdConfirmSeeMaskContainer.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            // 更新输入类型
            if (isConfirmPasswordVisible) {
                // 显示密码
                mBinding.loginPwdConfirm.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                mBinding.pwdConfirmSeeMask.setImageResource(R.mipmap.login_pwd_see)
            } else {
                // 隐藏密码
                mBinding.loginPwdConfirm.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                mBinding.pwdConfirmSeeMask.setImageResource(R.mipmap.login_pwd_mask)
            }
            // 光标移至末尾
            mBinding.loginPwdConfirm.setSelection(mBinding.loginPwdConfirm.text.length)
        }


        countDownTimer =  object: CountDownTimer(60*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val leftSecond = millisUntilFinished/1000;
                if (leftSecond > 0) {
                    mBinding.smsGetVerifyCode.text = "" + (leftSecond) + "秒后重发"
                }
            }

            override fun onFinish() {
                mBinding.smsGetVerifyCode.setEnabled(true)
                mBinding.smsGetVerifyCode.text = "获取验证码"
            }
        }

        mBinding.smsGetVerifyCode.setOnClickListener {
            if (mBinding.loginPhoneNum.text.isNotEmpty()) {
                mViewModel.sendVerifyCode(mBinding.loginPhoneNum.text.trim().toString()).observe(this){
                    if (it.success) {
                        mBinding.smsGetVerifyCode.setEnabled(false)
                        countDownTimer.start()
                    } else {
                        Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext,"请输入手机号", Toast.LENGTH_LONG).show()
            }
        }


        /**
        val hasAccountHtmlText =
        "已有账号？<u>立即登录</u>"
        val hasAccountSpannedText = if (VERSION.SDK_INT >= VERSION_CODES.N) {
        fromHtml(hasAccountHtmlText, FROM_HTML_MODE_COMPACT)
        } else {
        fromHtml(hasAccountHtmlText)
        }
        mBinding.hasAccountTip.text = hasAccountSpannedText

        mBinding.hasAccountTip.setOnClickListener {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
        } */

        /**
        val htmlText =
        "阅读并同意 <font color='#57C248'>《用户服务协议》</font>"
        val spannedText = if (VERSION.SDK_INT >= VERSION_CODES.N) {
        fromHtml(htmlText, FROM_HTML_MODE_COMPACT)
        } else {
        fromHtml(htmlText)
        }
        mBinding.tvProtocol.text = spannedText
        mBinding.tvProtocol.setOnClickListener {
        val intent = Intent(this@RegisterActivity, WebActivity::class.java)
        intent.putExtra("title","用户服务协议")
        intent.putExtra("url","https://www.baidu.com")
        startActivity(intent)
        }*/

        mBinding.platformProtocol.setOnClickListener {
            val intent = Intent(this@RegisterActivity, WebActivity::class.java)
            intent.putExtra("title","用户协议")
            intent.putExtra("url", Constant.USER_TERM_URL)
            startActivity(intent)
        }

        mBinding.privateProtocol.setOnClickListener {
            val intent = Intent(this@RegisterActivity, WebActivity::class.java)
            intent.putExtra("title","隐私政策")
            intent.putExtra("url", Constant.PRIVATE_TERM_URL)
            startActivity(intent)
        }

        val checkLoginButtonStatus = {
            var enabled = true;
            if (mBinding.editUserNick.text.trim().isEmpty()) {
                enabled = false;
            }
            if (mBinding.loginPwd.text.trim().isEmpty()) {
                enabled = false;
            }
            if (mBinding.loginPwdConfirm.text.trim().isEmpty()) {
                enabled = false;
            }
            if (mBinding.editCompanyName.text.trim().isEmpty()) {
                enabled = false;
            }
            if (mBinding.loginPhoneNum.text.trim().isEmpty()) {
                enabled = false;
            }
            if (mBinding.registerSmsVerifyCode.text.trim().isEmpty()) {
                enabled = false;
            }
            mBinding.btnLogin.isEnabled = enabled;
        };

        mBinding.editUserNick.addTextChangedListener {
            checkLoginButtonStatus();
        }

        mBinding.editCompanyName.addTextChangedListener {
            checkLoginButtonStatus();
        }

        mBinding.loginPwd.addTextChangedListener {
            checkLoginButtonStatus();
            if (mBinding.loginPwd.text.isNotEmpty()) {
                mBinding.pwdSeeMaskContainer.visibility = View.VISIBLE
            } else {
                mBinding.pwdSeeMaskContainer.visibility = View.GONE
            }
        }

        mBinding.loginPwdConfirm.addTextChangedListener {
            checkLoginButtonStatus();
            if (mBinding.loginPwdConfirm.text.isNotEmpty()) {
                mBinding.pwdConfirmSeeMaskContainer.visibility = View.VISIBLE
            } else {
                mBinding.pwdConfirmSeeMaskContainer.visibility = View.GONE
            }
        }

        mBinding.loginPhoneNum.addTextChangedListener {
            checkLoginButtonStatus();
        }

        mBinding.registerSmsVerifyCode.addTextChangedListener {
            checkLoginButtonStatus();
        }


    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    fun checkAndRegister(){
        if(mBinding.cbProtocol.isChecked){
            realRegister()
        } else {
            val dialog = ConfirmDialog(
                mContext = this,
                title = "同意协议",
                message = HtmlCompat.fromHtml("我已阅读并同意《用户协议》和《隐私政策》<br/><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
                confirmText = "确认",
                onConfirm = {
                    mBinding.cbProtocol.isChecked = true
                    realRegister()
                }
            );
            dialog.showDialog(this)
        }
    }

    /**
     * show 加载中
     */
    fun showLoadingDialog() {
        dismissLoadingDialog()
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this, false)
        }
        mLoadingDialog?.showDialog(this, false)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissLoadingDialog() {
        mLoadingDialog?.dismissDialog()
        mLoadingDialog = null
    }

    fun realRegister() {
        UserInfoUtil.setUserAgreeProtocol()
        if (mBinding.editUserNick.text.trim().toString().isEmpty()) {
            Toast.makeText(applicationContext,"昵称为空", Toast.LENGTH_LONG).show()
            return
        }
        if (mBinding.loginPwd.text.trim().toString().isEmpty()) {
            Toast.makeText(applicationContext,"密码为空", Toast.LENGTH_LONG).show()
            return
        }
        if (mBinding.loginPwd.text.trim().toString().length < 6) {
            Toast.makeText(applicationContext,"密码长度最小6位", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.loginPwd.text.toString().contains(' ')) {
            Toast.makeText(applicationContext,"密码不能包含空格", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.loginPwdConfirm.text.toString().contains(' ')) {
            Toast.makeText(applicationContext,"确认密码不能包含空格", Toast.LENGTH_LONG).show()
            return
        }

        if (!mBinding.loginPwd.text.trim().toString().equals(mBinding.loginPwdConfirm.text.trim().toString())) {
            Toast.makeText(applicationContext,"两次输入密码不一致，请重新输入", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.editCompanyName.text.trim().toString().isEmpty()) {
            Toast.makeText(applicationContext,"企业名称为空", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.loginPhoneNum.text.trim().toString().isEmpty()) {
            Toast.makeText(applicationContext,"手机号为空", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.registerSmsVerifyCode.text.trim().toString().isEmpty()) {
            Toast.makeText(applicationContext,"验证码为空", Toast.LENGTH_LONG).show()
            return
        }

        showLoadingDialog()
        val encryptPassword = RsaUtil.encrypt(mBinding.loginPwd.text.trim().toString(), RsaUtil.PUBLIC_KEY);
        val companyName:String = mBinding.editCompanyName.text.trim().toString()
        mViewModel.register(
            mBinding.loginPhoneNum.text.trim().toString(),
            encryptPassword,
            mBinding.registerSmsVerifyCode.text.trim().toString(),
            mBinding.editUserNick.text.trim().toString(),
            companyName
        ).observe(this) {
            if (it.success) {
                realLogin()
            } else {
                dismissLoadingDialog()
                Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun realLogin(){
        val encryptPassword = RsaUtil.encrypt(mBinding.loginPwd.text.trim().toString(), RsaUtil.PUBLIC_KEY);
        val phone = mBinding.loginPhoneNum.text.trim().toString();
        mViewModel.login(
            phone,
            encryptPassword,
            ""
        ).observe(this) {
            dismissLoadingDialog()
            Toast.makeText(applicationContext,"注册成功", Toast.LENGTH_LONG).show()
            if (it.success && it.data != null) {
                val data = it.data
                data?.let {
                    if (BuildConfig.DEBUG) {
                        Log.d("LoginActivity", "LoginActivity login success " + it.token)
                    }
                    SPUtils.getInstance().put(Constant.TOKEN_KEY, it.token)
                    SPUtils.getInstance().put(Constant.LOGIN_MOBILE_KEY, phone)
                    EventBus.getDefault().post(LoginEvent(true))
                    finish()
                }
            } else {
                EventBus.getDefault().post(LoginEvent(false))
                Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun goVerifyActivity() {
        val intent = Intent(this@RegisterActivity, PersonVerifyActivity::class.java)
        startActivity(intent)
    }

}