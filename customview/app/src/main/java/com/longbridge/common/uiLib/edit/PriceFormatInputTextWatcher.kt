package com.longbridge.common.uiLib.edit

import android.text.Editable
import android.text.TextWatcher
import kotlin.math.abs

class PriceFormatInputTextWatcher : TextWatcher {
    var isEditIng = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val input = s?.toString() ?: ""
        if (!isEditIng) {
            val numbers = input.split(Regex("\\."))
            if (numbers.size >= 2) {
                val firstPart = numbers[0].toIntOrNull()
                firstPart?.let {
                    //大于1保留2位小数，小于1保留4位小数
                    var formatInput = ""
                    formatInput = if (abs(firstPart) <= 0) {
                        if (numbers[1].length <= 4) {
                            "${abs(firstPart)}.${numbers[1]}"
                        } else {
                            "${abs(firstPart)}.${numbers[1].substring(0, 4)}"
                        }
                    } else {
                        if (numbers[1].length <= 2) {
                            "${abs(firstPart)}.${numbers[1]}"
                        } else {
                            "${abs(firstPart)}.${numbers[1].substring(0, 2)}"
                        }
                    }
                    if (input != formatInput) {
                        isEditIng = true
                        s?.replace(0, s.length, formatInput)
                        isEditIng = false
                    }
                }
            } else {
                val firstPart = input.toIntOrNull()
                firstPart?.let {
                    val formatInput = "${abs(firstPart)}"
                    if (input != formatInput) {
                        isEditIng = true
                        s?.replace(0, s.length, formatInput)
                        isEditIng = false
                    }
                }
            }
        }
    }
}