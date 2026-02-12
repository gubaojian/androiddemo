package com.zhongpin.mvvm_android.ui.login

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.unit.Constraints
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.SPUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityLoginBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.LoginEvent
import com.zhongpin.mvvm_android.biz.utils.RsaUtil
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.common.utils.hideKeyboard
import com.zhongpin.mvvm_android.ui.common.callKeFuPhone
import com.zhongpin.mvvm_android.ui.find.FindPwdActivity
import com.zhongpin.mvvm_android.ui.register.RegisterActivity
import com.zhongpin.mvvm_android.ui.web.WebActivity


class LoginActivity : BaseVMActivity<LoginViewModel>() {


    private lateinit var mBinding: ActivityLoginBinding;
    private lateinit var countDownTimer: CountDownTimer

    private var mLoadingDialog: LoadingDialog? = null

    private val loginModeSms = 0;
    private val loginModePwd = 1;

    private var loginMode:Int = loginModeSms;
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().fullScreen(false).keyboardEnable(true).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityLoginBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    @SuppressLint("UseKtx")
    override fun initView() {
        super.initView()
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener {
            EventBusUtils.postEvent(LoginEvent(false))
            finish()
        }

        mBinding.scrollBody.setOnClickListener {
            hideKeyboard()
        }

        mBinding.btnLogin.setOnClickListener {
            loginAndCheck()
        }

        mBinding.cbProtocol.isChecked =  SPUtils.getInstance().getBoolean("auto_check_login_check", false)
        mBinding.cbProtocol.setOnCheckedChangeListener { _,_ ->
            SPUtils.getInstance().put("auto_check_login_check", mBinding.cbProtocol.isChecked)
        }

        val checkLoginButtonStatus = {
            if (loginMode == loginModePwd) {
                if (mBinding.loginPhoneNum.text.trim().isNotEmpty()
                    && mBinding.loginPwd.text.trim().isNotEmpty()) {
                    mBinding.btnLogin.isEnabled = true;
                } else {
                    mBinding.btnLogin.isEnabled = false;
                }
            } else {
                if (mBinding.loginPhoneNum.text.trim().isNotEmpty()
                    && mBinding.loginSmsVerifyCode.text.trim().isNotEmpty()) {
                    mBinding.btnLogin.isEnabled = true;
                } else {
                    mBinding.btnLogin.isEnabled = false;
                }
            }
        };

        mBinding.tabSmsContainer.setOnClickListener {
            if (mBinding.loginPwd.isFocused) {
                hideKeyboard()
            }
            loginMode = loginModeSms;
            mBinding.tabSmsText.setTextColor(Color.parseColor("#ff333333"))
            mBinding.tabPwdText.setTextColor(Color.parseColor("#88333333"))
            mBinding.tabSmsBg.visibility = View.VISIBLE
            mBinding.tabPwdBg.visibility = View.GONE
            mBinding.tabSmsIndicator.visibility = View.VISIBLE
            mBinding.tabPwdIndicator.visibility = View.GONE;
            mBinding.btnForget.visibility = View.INVISIBLE;
            mBinding.tabContentSms.visibility = View.VISIBLE;
            mBinding.tabContentPwd.visibility = View.GONE;
            checkLoginButtonStatus();
        }

        mBinding.tabPwdContainer.setOnClickListener {
            if (mBinding.loginSmsVerifyCode.isFocused) {
                hideKeyboard()
            }
            loginMode = loginModePwd;
            mBinding.tabPwdText.setTextColor(Color.parseColor("#ff333333"))
            mBinding.tabSmsText.setTextColor(Color.parseColor("#88333333"))
            mBinding.tabSmsBg.visibility = View.GONE
            mBinding.tabPwdBg.visibility = View.VISIBLE
            mBinding.tabSmsIndicator.visibility = View.GONE
            mBinding.tabPwdIndicator.visibility = View.VISIBLE
            mBinding.btnForget.visibility = View.VISIBLE;
            mBinding.tabContentSms.visibility = View.GONE;
            mBinding.tabContentPwd.visibility = View.VISIBLE;
            checkLoginButtonStatus();
        }

        mBinding.loginPhoneNum.addTextChangedListener {
            checkLoginButtonStatus();
        }

        if (mBinding.loginPhoneNum.text.isEmpty()) {
            val phone = SPUtils.getInstance().getString(Constant.LOGIN_MOBILE_KEY, "")
            if (phone.length == 11) {
                mBinding.loginPhoneNum.append(phone)
            }
        }

        mBinding.loginSmsVerifyCode.addTextChangedListener {
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
                        Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext,"请输入手机号", Toast.LENGTH_LONG).show()
            }
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

        mBinding.registerContainer.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        mBinding.btnForget.setOnClickListener {
            val intent = Intent(this, FindPwdActivity::class.java)
            startActivity(intent)
            finish()
        }

        mBinding.platformProtocol.setOnClickListener {
            val intent = Intent(this@LoginActivity, WebActivity::class.java)
            intent.putExtra("title","用户协议")
            intent.putExtra("url", Constant.USER_TERM_URL)
            startActivity(intent)
        }

        mBinding.privateProtocol.setOnClickListener {
            val intent = Intent(this@LoginActivity, WebActivity::class.java)
            intent.putExtra("title","隐私政策")
            intent.putExtra("url", Constant.PRIVATE_TERM_URL)
            startActivity(intent)
        }

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
            val intent = Intent(this@LoginActivity, WebActivity::class.java)
            intent.putExtra("title","用户服务协议")
            intent.putExtra("url","https://www.baidu.com")
            startActivity(intent)
        }*/
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    fun loginAndCheck() {
        if(mBinding.cbProtocol.isChecked){
            realLogin()
        }else{
            val dialog = ConfirmDialog(
                mContext = this,
                title = "同意协议",
                message = HtmlCompat.fromHtml("我已阅读并同意《用户协议》和《隐私政策》<br/><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
                confirmText = "确认",
                onConfirm = {
                    mBinding.cbProtocol.isChecked = true
                    realLogin()
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
        mLoadingDialog?.showDialogV2(this)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissLoadingDialog() {
        mLoadingDialog?.dismissDialogV2()
        mLoadingDialog = null
    }

    fun realLogin() {
        UserInfoUtil.setUserAgreeProtocol()
        if (loginMode == loginModeSms) {
            realLoginBySms();
        } else {
            realLoginByPwd();
        }
    }

    fun realLoginBySms() {
        if (mBinding.loginPhoneNum.text.toString().isNotEmpty() and mBinding.loginSmsVerifyCode.text.toString().isNotEmpty()) {
            val code = mBinding.loginSmsVerifyCode.text.trim().toString()

            showLoadingDialog()
            val phone = mBinding.loginPhoneNum.text.trim().toString();
            mViewModel.login(
                phone,
                "",
                code
            ).observe(this) {
                dismissLoadingDialog()
                if (it.success && it.data != null) {
                    val data = it.data
                    data?.let {
                        if (BuildConfig.DEBUG) {
                            Log.d("LoginActivity", "LoginActivity login success " + it.token)
                        }
                        SPUtils.getInstance().put(Constant.TOKEN_KEY, data.token)
                        SPUtils.getInstance().put(Constant.LOGIN_MOBILE_KEY, phone)

                        EventBusUtils.postEvent(LoginEvent(true))

                        finish()
                    }
                } else {
                    EventBusUtils.postEvent(LoginEvent(false))
                    Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(applicationContext,"手机号或验证码为空", Toast.LENGTH_LONG).show()
        }
    }

    fun realLoginByPwd() {
        if (mBinding.loginPhoneNum.text.toString().isNotEmpty() and mBinding.loginPwd.text.toString().isNotEmpty()) {
            showLoadingDialog()
            val encryptPassword = RsaUtil.encrypt(mBinding.loginPwd.text.trim().toString(), RsaUtil.PUBLIC_KEY);
            val phone = mBinding.loginPhoneNum.text.trim().toString()
            mViewModel.login(
                phone,
                encryptPassword,
                ""
            ).observe(this) {
                dismissLoadingDialog()
                if (it.success && it.data != null) {
                    val data = it.data
                    data?.let {
                        if (BuildConfig.DEBUG) {
                            Log.d("LoginActivity", "LoginActivity login success " + it.token)
                        }
                        SPUtils.getInstance().put(Constant.TOKEN_KEY, data.token)
                        SPUtils.getInstance().put(Constant.LOGIN_MOBILE_KEY, phone)

                        EventBusUtils.postEvent(LoginEvent(true))
                        finish()
                    }
                } else {
                    EventBusUtils.postEvent(LoginEvent(false))
                    Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
                }
            }

        } else {
            Toast.makeText(applicationContext,"手机号或密码为空", Toast.LENGTH_LONG).show()
        }
    }

}