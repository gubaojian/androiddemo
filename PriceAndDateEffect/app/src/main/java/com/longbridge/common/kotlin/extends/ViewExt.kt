import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.Group
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.lb.price.one.R
import com.longbridge.common.kotlin.extends.toSafeLong

import com.longbridge.core.comm.FApp

import java.lang.ref.WeakReference



/**
 * View相关的拓展函数
 */
/**
 * 从parent当中移除自身
 *
 * Boolean true:成功 false:失败
 */
fun View.removeSelf(): Boolean {
    if (parent != null && this.parent is ViewGroup) {
        (parent as ViewGroup).removeView(this)
        return true
    }
    return false
}

/**
 * 获取属于的activity
 */
fun View.parentActivity(): Activity? {
    var parentContent = this.context
    while (parentContent is ContextWrapper) {
        if (parentContent is Activity) {
            return parentContent
        }
        parentContent = parentContent.baseContext
    }
    return null
}

/**
 * 是否显示
 */
fun View.isVisible(): Boolean = visibility == View.VISIBLE

/**
 * Gone
 */
fun View.toGone() {
    visibility = View.GONE
}

/**
 * InVisible
 */
fun View.toInVisible() {
    visibility = View.INVISIBLE
}

/**
 * Visible
 */
fun View?.toVisible() {
    if (this == null) return
    visibility = View.VISIBLE
}

fun View?.visible(v: Boolean) {
    if (this == null) return
    if (v && visibility == View.VISIBLE) return
    if (!v && visibility == View.GONE) return
    visibility = if (v) View.VISIBLE else View.GONE
}

fun View?.visibleLite(v: Boolean) {
    if (this == null) return
    if (v && visibility == View.VISIBLE) return
    if (!v && visibility == View.INVISIBLE) return
    visibility = if (v) View.VISIBLE else View.INVISIBLE
}

fun <T : View> T?.weak(): WeakReference<T>? {
    if (this == null) return null
    return WeakReference(this)
}

/**
 * 设置View的宽度和高度
 * @param width 要设置的宽度
 * @param height 要设置的高度
 */
fun View.widthAndHeight(width: Int, height: Int): View {
    val params = layoutParams
    if (width != 0) {
        params.width = width
    }
    if (height != 0) {
        params.height = height
    }
    layoutParams = params
    return this
}

/**
 * 设置View的margin  默认保留原设置
 *
 * @param leftMargin    距离左的距离
 * @param topMargin     距离上的距离
 * @param rightMargin   距离右的距离
 * @param bottomMargin  距离下的距离
 */
fun View.margin(
    leftMargin: Int = Int.MAX_VALUE,
    topMargin: Int = Int.MAX_VALUE,
    rightMargin: Int = Int.MAX_VALUE,
    bottomMargin: Int = Int.MAX_VALUE
): View {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    if (leftMargin != Int.MAX_VALUE)
        params.leftMargin = leftMargin
    if (topMargin != Int.MAX_VALUE)
        params.topMargin = topMargin
    if (rightMargin != Int.MAX_VALUE)
        params.rightMargin = rightMargin
    if (bottomMargin != Int.MAX_VALUE)
        params.bottomMargin = bottomMargin
    layoutParams = params
    return this
}

fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this.context)
        .inflate(layout, this, attachToRoot)
}

inline fun EditText.beforeTextChanged(
    crossinline beforeTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable) {
        }

        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            beforeTextChanged(p0, p1, p2, p3)
        }

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }
    })
}

inline fun EditText.addTextWatcher(
    crossinline addTextWatcher: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable) {
        }

        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            addTextWatcher(p0, p1, p2, p3)
        }
    })
}

inline fun EditText.afterTextChanged(
    crossinline afterTextChanged: (Editable) -> Unit = {}
) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable) {
            afterTextChanged(p0)
        }

        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }
    })
}

inline fun ViewPager.onPageScrollStateChanged(
    crossinline onPageScrollStateChanged: (state: Int) -> Unit = { _ -> }
) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            onPageScrollStateChanged.invoke(state)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
        }
    })
}

inline fun ViewPager.onPageScrolled(
    crossinline onPageScrolled: (position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit = { _, _, _ -> }
) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            onPageScrolled.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
        }
    })
}

inline fun ViewPager.onPageSelected(
    crossinline onPageSelected: (position: Int) -> Unit = { _ -> }
) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }
    })
}

inline fun ViewPager2.onPageScrollStateChanged(
    crossinline onPageScrollStateChanged: (state: Int) -> Unit = { _ -> }
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            onPageScrollStateChanged.invoke(state)
        }
    })
}

inline fun ViewPager2.onPageScrolled(
    crossinline onPageScrolled: (position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit = { _, _, _ -> }
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            onPageScrolled.invoke(position, positionOffset, positionOffsetPixels)
        }
    })
}

inline fun ViewPager2.onPageSelected(
    crossinline onPageSelected: (position: Int) -> Unit = { _ -> }
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }
    })
}

var TextView.leftDrawable: Drawable?
    get() = compoundDrawables[0]
    set(icon) {
        setCompoundDrawablesWithIntrinsicBounds(icon, topDrawable, rightDrawable, bottomDrawable)
    }
var TextView.topDrawable: Drawable?
    get() = compoundDrawables[1]
    set(icon) {
        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, icon, rightDrawable, bottomDrawable)
    }
var TextView.rightDrawable: Drawable?
    get() = compoundDrawables[2]
    set(icon) {
        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, icon, bottomDrawable)
    }
var TextView.bottomDrawable: Drawable?
    get() = compoundDrawables[3]
    set(icon) {
        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, rightDrawable, icon)
    }

/**
 * EditText限制输入值，其中中文为2个字符，英文为一个字符
 */
fun EditText.limitNum(textSize: Int): Int {
    var textMaxLength = 0
    var editable = text
    val str = editable.toString()
    var selEndIndex = Selection.getSelectionEnd(editable)
    for (i in str.indices) {
        val charAt = str[i]
        if (charAt.toInt() <= 128) {
            textMaxLength++
        } else {
            textMaxLength += 2
        }
        if (textMaxLength > textSize) {
            val newStr = str.substring(0, i)
            setText(newStr)
            editable = text
            val newLen = editable.length
            if (selEndIndex > newLen) {
                selEndIndex = editable.length
            }
        }
    }
    if (textMaxLength > textSize) {
        textMaxLength = textSize
    }
    Selection.setSelection(editable, selEndIndex)
    return textMaxLength
}

fun visible(vararg views: View?) {
    if (views.isNotEmpty()) {
        views.forEach {
            it?.toVisible()
        }
    }

}

fun invisible(vararg views: View?) {
    if (views.isNotEmpty()) {
        views.forEach {
            it?.toInVisible()
        }
    }
}

fun gone(vararg views: View?) {
    if (views.isNotEmpty()) {
        views.forEach {
            it?.toGone()
        }
    }
}

fun alpha(alpha: Float = 1f, vararg views: View) {
    if (views.isNotEmpty()) {
        views.forEach {
            it.alpha = alpha
        }
    }
}

fun visibleOrGone(
    isVisible: Boolean = true,
    vararg views: View,
    onGone: (() -> Unit)? = null,
    onVisible: (() -> Unit)? = null
) {
    if (views.isNotEmpty()) {
        views.forEach {
            if (isVisible) {
                it.toVisible()
            } else {
                it.toGone()
            }
        }
    }
    if (isVisible) {
        onVisible?.invoke()
    } else {
        onGone?.invoke()
    }
}

fun visibleOrInvisible(
    isVisible: Boolean = true, vararg views: View, onInvisible: (() -> Unit)? = null,
    onVisible: (() -> Unit)? = null
) {
    if (views.isNotEmpty()) {
        views.forEach {
            if (isVisible) {
                it.toVisible()
            } else {
                it.toInVisible()
            }
        }
    }
    if (isVisible) {
        onVisible?.invoke()
    } else {
        onInvisible?.invoke()
    }
}

/**
 * 判断 View 是否在屏幕上
 */
val View.inScreen: Boolean
    get() {
        // 获取屏幕宽度
        val screenWidth = context?.resources?.displayMetrics?.widthPixels ?: 0
        // 获取屏幕高度
        val screenHeight = context?.resources?.displayMetrics?.heightPixels ?: 0
        // 构建屏幕矩形
        val screenRect = Rect(0, 0, screenWidth, screenHeight)
        val array = IntArray(2)
        // 获取视图矩形
        getLocationOnScreen(array)
        val viewRect = Rect(array[0], array[1], array[0] + width, array[1] + height)
        // 判断屏幕和视图矩形是否有交集
        return screenRect.intersect(viewRect)
    }

fun View.onThrottledClick(
    throttleDelay: Long = 500L,
    onClick: (View) -> Unit
) {
    setOnClickListener {
        onClick(this)
        isClickable = false
        postDelayed({ isClickable = true }, throttleDelay)
    }
}

fun View?.click(ms: Long = 500L, block: (View) -> Unit) {
    this?.setOnClickListener {
        val tag = it.getTag(R.id.view_time_ms)
        if (tag == null) {
            block.invoke(it)
            it.setTag(R.id.view_time_ms, System.currentTimeMillis())
            return@setOnClickListener
        }
        val timeTag = tag.toString().toSafeLong()
        if (timeTag == 0L)
            block.invoke(it)
        else {
            val current = System.currentTimeMillis()
            if (current - timeTag >= ms) {
                block.invoke(it)
                it.setTag(R.id.view_time_ms, current)
            }
        }
    }
}

/**
 * 给group设置多组件点击事件
 */
fun Group.addOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}

fun View.locationToString(): String {
    return "left:${top},top:${top},right:${right},bottom:${bottom}"
}
