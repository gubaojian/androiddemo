package com.zhongpin.mvvm_android.ui.find

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityFindPwdBinding
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.biz.utils.RsaUtil
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.common.utils.hideKeyboard
import com.zhongpin.mvvm_android.ui.common.loginOut
import com.zhongpin.mvvm_android.ui.login.LoginActivity
import com.zhongpin.mvvm_android.ui.setpwd.FindSetPwdActivity

class FindPwdActivity : BaseVMActivity<FindPwdViewModel>() {


    private lateinit var mBinding: ActivityFindPwdBinding;
    private lateinit var countDownTimer: CountDownTimer

    private var mLoadingDialog: LoadingDialog? = null

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    private var from:String? = null;
    private var autoFillPhoneNum:String? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().fullScreen(false).keyboardEnable(true).init()
        if (intent != null) {
            from = intent.getStringExtra("from");
            autoFillPhoneNum = intent.getStringExtra("autoFillPhoneNum");
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityFindPwdBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener { finish() }
        mBinding.btnLogin.setOnClickListener {
            setAndCheck()
        }

        mBinding.scrollBody.setOnClickListener {
            hideKeyboard()
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

        val checkLoginButtonStatus = {
            var enabled = true;
            if (mBinding.loginPhoneNum.text.trim().isEmpty()) {
                enabled = false;
            }

            if (mBinding.loginPwd.text.trim().isEmpty()) {
                enabled = false;
            }
            if (mBinding.loginPwdConfirm.text.trim().isEmpty()) {
                enabled = false;
            }

            if (mBinding.registerSmsVerifyCode.text.trim().isEmpty()) {
                enabled = false;
            }
            mBinding.btnLogin.isEnabled = enabled;
        };


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

        if (!TextUtils.isEmpty(autoFillPhoneNum)) {
            mBinding.loginPhoneNum.append(autoFillPhoneNum ?: "")
            if (autoFillPhoneNum?.length == 11) {
                mBinding.loginPhoneNum.setFocusable(false);
                mBinding.loginPhoneNum.setFocusableInTouchMode(false);
                mBinding.loginPhoneNum.setEnabled(false);
            }
        }
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    fun setAndCheck(){
        realSetPassword()
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

    fun realSetPassword() {
        if (mBinding.loginPhoneNum.text.trim().toString().isEmpty()) {
            Toast.makeText(applicationContext,"请输入手机号", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.registerSmsVerifyCode.text.trim().toString().isEmpty()) {
            Toast.makeText(applicationContext,"请输入验证码", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.loginPwd.text.trim().toString().isEmpty()) {
            Toast.makeText(applicationContext,"请输入新密码", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.loginPwdConfirm.text.trim().toString().isEmpty()) {
            Toast.makeText(applicationContext,"请输入确认新密码", Toast.LENGTH_LONG).show()
            return
        }
        if (mBinding.loginPwd.text.trim().toString().length < 6) {
            Toast.makeText(applicationContext,"新密码长度最小6位", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.loginPwd.text.toString().contains(' ')) {
            Toast.makeText(applicationContext,"新密码不能包含空格", Toast.LENGTH_LONG).show()
            return
        }

        if (mBinding.loginPwdConfirm.text.toString().contains(' ')) {
            Toast.makeText(applicationContext,"确认新密码不能包含空格", Toast.LENGTH_LONG).show()
            return
        }

        if (!mBinding.loginPwd.text.trim().toString().equals(mBinding.loginPwdConfirm.text.trim().toString())) {
            Toast.makeText(applicationContext,"两次输入密码不一致，请重新输入", Toast.LENGTH_LONG).show()
            return
        }


        val mobile = mBinding.loginPhoneNum.text.trim().toString();
        val code = mBinding.registerSmsVerifyCode.text.trim().toString()
        val loginPwd = mBinding.loginPwd.text.trim().toString()

        val encryptPassword = RsaUtil.encrypt(loginPwd, RsaUtil.PUBLIC_KEY);
        mViewModel.setPassword(
            mobile,
            encryptPassword,
            code
        ).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                Toast.makeText(applicationContext,"重置成功，请登录", Toast.LENGTH_LONG).show()
                doneResetPwd()
            } else {
                Toast.makeText(applicationContext, it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun  goSetPwdActivity() {
        val intent = Intent(this, FindSetPwdActivity::class.java)
        intent.putExtra("code", mBinding.registerSmsVerifyCode.text.trim().toString())
        intent.putExtra("mobile", mBinding.loginPhoneNum.text.trim().toString())
        startActivity(intent)
        finish()
    }

    fun  doneResetPwd() {
        if ("fromUserProfile".equals(from)) {
            loginOut();
        } else { //登录界面重置密码，跳到登录
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}