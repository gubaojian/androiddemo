package com.zhongpin.lib_base.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import com.blankj.utilcode.util.SizeUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.base.IPermission
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.DialogConfirmBinding
import com.zhilianshidai.pindan.app.databinding.DialogPermissionConfirmBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import java.security.Permissions

class PermissionConfirmDialog @JvmOverloads constructor(
    val mContext: Context,
    val title: String? = null,
    val message: CharSequence? = null,
    val confirmText: String? = null,
    val cancelText: String? = null,
    val onCancel: OnClickListener? = null,
    val onConfirm: OnClickListener? = null,
    var requestPermissions:List<IPermission> = emptyList<IPermission>(),
    val canceledOnTouchOutside: Boolean = true,
    val showCancelButton: Boolean = true,
) : Dialog(
    mContext,
    R.style.ConfirmDialog
) {

    private lateinit  var mBinding: DialogPermissionConfirmBinding;

    private var denyDoNotAskAgain: Boolean = false;
    var allDeny = false;
    var denyPermissions: MutableList<IPermission?> = mutableListOf()

    init {
        mBinding = DialogPermissionConfirmBinding.inflate(LayoutInflater.from(context))
        setContentView(mBinding.root)
        setCanceledOnTouchOutside(canceledOnTouchOutside)
        mBinding.title.text = title;
        mBinding.message.text = message;
        if (!TextUtils.isEmpty(confirmText)) {
            mBinding.confirmText.text = confirmText;
        }
        if (!TextUtils.isEmpty(cancelText)) {
            mBinding.cancelText.text = cancelText
        }

        if (TextUtils.isEmpty(title)) {
            mBinding.title.visibility = View.GONE
        }
        if (TextUtils.isEmpty(message)) {
            mBinding.message.visibility = View.GONE
        }
        mBinding.cancelButtonContainer.setOnClickListener {
            dismissDialog()
            onCancel?.onClick(it)
        }
        mBinding.confirmButtonContainer.setOnClickListener {
            dismissDialog()
            if (denyPermissions.isNotEmpty()) {
                XXPermissions.startPermissionActivity(context, denyPermissions);
            } else {
                onConfirm?.onClick(it)
            }
        }
        if (!showCancelButton) {
            mBinding.cancelButtonContainer.gone()
            mBinding.bottomButtonLine.gone()
        }

        val params = window?.attributes;
        if (params != null) {
            params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL;
            params.y = SizeUtils.dp2px(50.0f)
            window?.attributes = params
        }
        mBinding.buttonsContainer.gone()
        val permissionCallback = object : OnPermissionCallback {
            var allDeny = false;
            override fun onGranted(
                permissions: List<IPermission?>,
                allGranted: Boolean
            ) {
                if (!allDeny) {
                    dismissDialog()
                    onConfirm?.onClick(mBinding.root)
                } else {
                    failRequestPermission()
                }
            }

            override fun onDenied(
                permissions: List<IPermission?>,
                doNotAskAgain: Boolean) {
                denyDoNotAskAgain = doNotAskAgain
                if (permissions.size >= requestPermissions.size) { //全部deny才处理
                    allDeny = true;
                }
                if (allDeny) {
                    denyPermissions.addAll(permissions)
                    failRequestPermission()
                }
            }
        }
        mBinding.root.postDelayed( {
            XXPermissions
                .with(context)
                .permissions(requestPermissions).request(permissionCallback)
        }, 120)
    }

    fun failRequestPermission() {
        mBinding.root.post {
            mBinding.buttonsContainer.visible()
            mBinding.title.text = "权限申请失败，需去设置打开"
            mBinding.confirmText.text = "去设置打开"
        }
    }

    fun showDialog(context: Context) {
        if (context is Activity) {
            if (context.isFinishing) {
                return
            }
        }
        try {
            show()
        } catch (_:Exception) {}
    }

    fun dismissDialog() {
        try {
            dismiss()
        } catch (_:Exception) {}
    }

    override fun show() {
        try {
            super.show()
        } catch (_:Exception) {}
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (_:Exception) {}
    }
}