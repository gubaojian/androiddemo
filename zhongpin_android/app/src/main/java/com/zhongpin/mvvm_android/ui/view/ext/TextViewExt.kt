package com.zhongpin.mvvm_android.ui.view.ext

import android.graphics.Typeface
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.zhilianshidai.pindan.app.R
import com.zhongpin.lib_base.ktx.extSetDrawableColor
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.OrderDetailItem
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.ui.common.BoxConfigData
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils


fun TextView.setOrderStatusText(item: OrderItem) {
    val status = item.orderStatusName ?: ""
    text = status
    if (status.contains("部分送达") || status.contains("待收货")) {
        setTextColor("#FFA826".toColorInt())
    } else if (status.contains("申诉中")) {
        setTextColor("#D83333".toColorInt())
    } else if (status.contains("取消")) {
        setTextColor("#999999".toColorInt())
    } else {
        setTextColor("#57C248".toColorInt())
    }
}

fun View.setConfirmOrderButtonShow(item: OrderItem) {
    if (item.signStatus == 1 || item.signStatus == 3) {
        visible()
    } else {
        gone()
    }
    visible()
}

fun View.setConfirmOrderButtonShow(item: OrderDetailItem) {
    if (item.signStatus == 1 || item.signStatus == 3) {
        visible()
    } else {
        gone()
    }
}

fun View.setFeedbackButtonShow(item: OrderItem) {
    if (item.orderAppeal == true || item.orderStatus == 50) {
        visible()
    } else {
        gone()
    }
}


fun View.setFeedbackButtonShow(item: OrderDetailItem) {
    if (item.orderAppeal == true || item.orderStatus == 50) {
        visible()
    } else {
        gone()
    }
}

fun View.setCancelButtonShow(item: OrderDetailItem) {
    if (item.cancelOrder == true) {
        visible()
    } else {
        gone()
    }
}

fun TextView.setPaperSizeText(item: OrderItem) {
    text = "${item.length}*${item.width}"
}

fun TextView.setPaperSizeText(item: OrderDetailItem) {
    text = "${item.length}*${item.width}"
}


fun TextView.setLineTypeText(item: OrderItem) {
    if (BoxConfigData.noneLineDesc.equals(item.lineType)) {
        text = BoxConfigData.noneLineDesc
    } else {
        text = "${item.touch}/${item.lineType}"
    }
}

fun TextView.setLineTypeText(item: OrderDetailItem) {
    if (BoxConfigData.noneLineDesc.equals(item.lineType)) {
        text = BoxConfigData.noneLineDesc
    } else {
        text = "${item.touch}/${item.lineType}"
    }
}

fun EditText.setMoneyInputFormat() {
    //region TextWatcher
    val mTextWatcher: TextWatcher = object : TextWatcher {
        private var validateLock = false

        private var mDefaultText: String? = null
        private var mPreviousText = ""

        override fun afterTextChanged(s: Editable) {
            val text = s.toString()
            if (validateLock) {
                return
            }
            if (!TextUtils.isEmpty(text)) {
                if (!PingDanAppUtils.priceValid(text)) {
                    validateLock = true
                    setText(mPreviousText) // cancel change and revert to previous input
                    setSelection(mPreviousText.length)
                    validateLock = false
                    return
                }
            }
            mPreviousText = text.trim()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // do nothing
        }
    }
    addTextChangedListener(mTextWatcher)
}

fun ImageView.setPlatformPriceArrow(priceStr:String?, prePriceStr:String?) {
    if (!TextUtils.isEmpty(prePriceStr)) {
        val prePrice:Double = prePriceStr?.toDoubleOrNull() ?: 0.0
        val price: Double = priceStr?.toDoubleOrNull() ?: 0.0
        if (prePrice == price) {
            gone()
        } else {
            if (price > prePrice) {
                visible()
                setImageResource(R.mipmap.home_platform_price_up)
            } else {
                visible()
                setImageResource(R.mipmap.home_platform_price_down)
            }
        }
    } else {
        gone()
    }
}

fun ImageView.setMinePriceArrow(priceStr:String?, prePriceStr:String?) {
    if (!TextUtils.isEmpty(prePriceStr)) {
        val prePrice:Double = prePriceStr?.toDoubleOrNull() ?: 0.0
        val price: Double = priceStr?.toDoubleOrNull() ?: 0.0
        if (prePrice == price) {
            gone()
        } else {
            if (price > prePrice) {
                visible()
                setImageResource(R.mipmap.home_platform_price_up)
            } else {
                visible()
                setImageResource(R.mipmap.home_platform_price_down)
            }
        }
    } else {
        gone()
    }
}


fun EditText.setAutoUpperCase() {
    val textWatcher = object : TextWatcher {
        var isChangeUpperCaseFlag = false;
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (text != null && !isChangeUpperCaseFlag) {
                val upperCase = text.toString().uppercase()
                if (!TextUtils.equals(text.toString(), upperCase)) {
                    isChangeUpperCaseFlag = true;
                    if (text is Editable) {
                        text.clear()
                        text.append(upperCase)
                    }
                    isChangeUpperCaseFlag = false;
                }
            }
        }
    }
    addTextChangedListener(textWatcher)
}


fun EditText.setAutoInputTips(inputTipView: TextView) {
    val textWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            val length = s?.toString()?.length ?: 0
            val format  = inputTipView.text;
            val numbers = format.split(Regex("[/]")).toMutableList();
            if (numbers.size == 2) {
                numbers[0] = length.toString()
                inputTipView.text = numbers.joinToString(separator = "/")
            }
        }

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
    addTextChangedListener(textWatcher)
}


fun TextView.setWaText(platCode:String?) {
    setCustomFontDin()
    val length = platCode?.length ?: 0
    if (length <= 6) {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f)
    } else {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
    }
    text = platCode
}


fun View.setWaBg(platCode:String?) {
    val length = platCode?.length ?: 0
    val bg = background;
    if (length <= 3) {
       bg?.extSetDrawableColor("#FFEACF")
    } else if (length <= 5) {
        bg?.extSetDrawableColor("#D8E2FF")
    } else {
        bg?.extSetDrawableColor("#E5D6FF")
    }
}

fun TextView.setWaTextColor(platCode:String?) {
    val length = platCode?.length ?: 0
    if (length <= 3) {
       setTextColor("#FFA42C".toColorInt())
    } else if (length <= 5) {
        setTextColor("#557EF7".toColorInt())
    } else {
        setTextColor("#A56EFF".toColorInt())
    }
}

fun TextView.setCustomFontDin() {
    val fontName = "font/DIN Alternate Bold.ttf"
    setCustomAssetFontBold(fontName);
}

fun TextView.setCustomAssetFontBold(assetFilePath: String) {
    try {
        var typeface =  globalFontCache[assetFilePath];
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.assets, assetFilePath)
            globalFontCache.put(assetFilePath, typeface)
        }
        setTypeface(typeface, Typeface.BOLD)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val globalFontCache = mutableMapOf<String, Typeface>();
