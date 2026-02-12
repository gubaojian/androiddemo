package com.zhongpin.lib_base.ktx

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * EditText相关扩展方法
 *
 * @author Qu Yunshuo
 * @since 2020/9/17
 */

/**
 * 过滤掉空格和回车
 */
fun EditText.filterBlankAndCarriageReturn() {
    val filterList = mutableListOf<InputFilter>()
    filterList.addAll(filters)
    filterList.add(InputFilter { source, _, _, _, _, _ -> if (source == " " || source == "\n") "" else null })
    filters = filterList.toTypedArray()
}

fun EditText.focusAndShowKeyboard() {
    // 1. 获取焦点
    requestFocus()
    // 2. 弹出软键盘
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
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



fun EditText.setAutoHintNormalAndBoldText() {
    val editText = this;
    val textWatcher = object : TextWatcher {
        var isChangeUpperCaseFlag = false;
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (TextUtils.isEmpty(text)) {
                editText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            } else {
                editText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
        }
    }
    addTextChangedListener(textWatcher)
}



fun EditText.limit1Decimal() {
    val textWatcher = object : TextWatcher {
        var isChangeValueFlag = false;
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (text != null && !isChangeValueFlag) {
                val input =  text.toString() ?: ""
                if (!TextUtils.isEmpty(input)) {
                    var parts = input.split(Regex("[.]"))
                    if (parts.size == 2) {
                        if (parts[1].length > 1){
                            val limitText =  "${parts[0]}.${parts[1].substring(0, 1)}";
                            isChangeValueFlag = true;
                            if (text is Editable) {
                                text.clear()
                                text.append(limitText)
                            }
                            isChangeValueFlag = false;
                        }
                    }
                }
            }
        }
    }
    addTextChangedListener(textWatcher)
}

fun EditText.limit2Decimal() {
    val textWatcher = object : TextWatcher {
        var isChangeValueFlag = false;
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (text != null && !isChangeValueFlag) {
                val input =  text.toString() ?: ""
                if (!TextUtils.isEmpty(input)) {
                    var parts = input.split(Regex("[.]"))
                    if (parts.size == 2) {
                        if (parts[1].length > 2){
                            val limitText =  "${parts[0]}.${parts[1].substring(0, 2)}";
                            isChangeValueFlag = true;
                            if (text is Editable) {
                                text.clear()
                                text.append(limitText)
                            }
                            isChangeValueFlag = false;
                        }
                    }
                }
            }
        }
    }
    addTextChangedListener(textWatcher)
}


fun EditText.limit3Decimal() {
    val textWatcher = object : TextWatcher {
        var isChangeValueFlag = false;
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (text != null && !isChangeValueFlag) {
                val input =  text.toString() ?: ""
                if (!TextUtils.isEmpty(input)) {
                    var parts = input.split(Regex("[.]"))
                    if (parts.size == 2) {
                        if (parts[1].length > 3){
                            val limitText =  "${parts[0]}.${parts[1].substring(0, 3)}";
                            isChangeValueFlag = true;
                            if (text is Editable) {
                                text.clear()
                                text.append(limitText)
                            }
                            isChangeValueFlag = false;
                        }
                    }
                }
            }
        }
    }
    addTextChangedListener(textWatcher)
}