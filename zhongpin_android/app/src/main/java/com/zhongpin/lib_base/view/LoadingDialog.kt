package com.zhongpin.lib_base.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.zhilianshidai.pindan.app.R

class LoadingDialog(context: Context, val canNotCancel: Boolean) : Dialog(
    context,
    R.style.LoadingDialog
) {

    private var loadingDialog: LoadingDialog? = null


    init {
        setContentView(R.layout.layout_loading_view)
    }

    private fun  startAnimation() {
        val imageView: ImageView = findViewById(R.id.iv_image)
        val animation: Animation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.duration = 2000
        animation.repeatCount = Animation.INFINITE
        animation.fillAfter = true
        imageView.startAnimation(animation)
    }

    private fun cancelAnimation() {
        val imageView: ImageView = findViewById(R.id.iv_image)
        imageView.clearAnimation()
    }

    fun showDialog(context: Context, isCancel: Boolean) {
        if (context is Activity) {
            if (context.isFinishing) {
                return
            }
        }
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(context, isCancel)
        }
        loadingDialog?.startAnimation()
        loadingDialog?.show()
    }

    fun dismissDialog() {
        loadingDialog?.cancelAnimation()
        loadingDialog?.dismiss()
    }


    fun showDialogV2(context: Context) {
        if (context is Activity) {
            if (context.isFinishing) {
                return
            }
        }
        try {
            startAnimation()
            show()
        } catch (_:Exception) {}

    }

    fun dismissDialogV2() {
        try {
            cancelAnimation()
            dismiss()
        } catch (_:Exception) {}

    }


}