package com.zhongpin.mvvm_android.view

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.zhongpin.lib_base.ktx.invisible
import com.zhongpin.lib_base.ktx.isInvisible
import com.zhongpin.lib_base.ktx.visible


fun interface onRemainTimeChange {
    fun onRemainTime(left: Long);
}
class CountDownTextView @JvmOverloads constructor (context: Context,
                                                   attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr)   {
    private var prefix:String = ""
    private var suffix:String = ""
    private  var countDownTimer: CountDownTimer? = null

    private var remainTime: Long = 0L;

    fun setPrefix(prefix:String) {
        this.prefix = prefix
    }

    fun setSuffix(suffix:String) {
        this.suffix = suffix
    }

    fun getRemainTime(): Long {
        return remainTime;
    }
    fun setRemainTime(time: Long, finishAction: (()->Unit)? = null) {
        if (time <= 0) {
            setCountDownText(time)
            remainTime = time;
            return
        }
        countDownTimer?.cancel()
        countDownTimer =  object: CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isAttachedToWindow) {
                    remainTime = millisUntilFinished;
                    setCountDownText(millisUntilFinished)
                }
            }

            override fun onFinish() {
                if (isAttachedToWindow) {
                    setCountDownText(0)
                    finishAction?.invoke()
                }
            }
        }
        countDownTimer?.start()
        setCountDownText(time)
        remainTime = time;
    }

    fun stop() {
        countDownTimer?.cancel()
    }

    private fun setCountDownText(left: Long) {
        val ss = (left/1000) % 60
        val mm = (left/(1000*60))  % 60
        val hh = (left/(1000*60*60))
        val date = String.format("%02d:%02d:%02d", hh, mm, ss)
        setText("${prefix}${date}${suffix}")
        onRemainTimeChangeAction?.onRemainTime(left)
    }

    fun setOnRemainTimeChange(onRemainTimeChange: onRemainTimeChange) {
        onRemainTimeChangeAction = onRemainTimeChange;
    }
    var onRemainTimeChangeAction: onRemainTimeChange? = null;
}
