package com.zhongpin.lib_base.utils

import android.content.res.Resources
import android.graphics.Outline
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi

object BannerUtils {
    /**
     * 获取真正的位置
     *
     * @param isIncrease 首尾是否有增加
     * @param position  当前位置
     * @param realCount 真实数量
     * @return
     */
    fun getRealPosition(isIncrease: Boolean, position: Int, realCount: Int): Int {
        if (!isIncrease) {
            return position
        }
        val realPosition = if (position == 0) {
            realCount - 1
        } else if (position == realCount + 1) {
            0
        } else {
            position - 1
        }
        return realPosition
    }

    /**
     * 将布局文件转成view，这里为了适配viewpager2中高宽必须为match_parent
     *
     * @param parent
     * @param layoutId
     * @return
     */
    fun getView(parent: ViewGroup, @LayoutRes layoutId: Int): View {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        val params = view.layoutParams
        //这里判断高度和宽带是否都是match_parent
        if (params.height != -1 || params.width != -1) {
            params.height = -1
            params.width = -1
            view.layoutParams = params
        }
        return view
    }

    fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        )
    }

    /**
     * 设置view圆角
     *
     * @param radius
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setBannerRound(view: View, radius: Float) {
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        view.clipToOutline = true
    }
}
