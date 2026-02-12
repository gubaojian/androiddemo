package com.zhongpin.mvvm_android.network

import android.net.ParseException
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonParseException
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.base.viewstate.StateType
import com.zhongpin.mvvm_android.common.utils.Constant
import kotlinx.coroutines.CancellationException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created with Android Studio.
 * Description:
 * @CreateDate: 2020/5/5 11:32
 */



object NetExceptionHandle {
    fun handleException(e: Throwable?, loadState: MutableLiveData<State>) {
        if (BuildConfig.DEBUG) {
            e?.printStackTrace()
            val stackTraces = Thread.currentThread().stackTrace
            for(stackTrace in stackTraces) {
                LogUtils.e("NetExceptionHandle", stackTrace.toString());
            }
        }
        if (e is CancellationException) {
            return
        }
        e?.let {
            when (it) {
                is HttpException -> {
                    loadState.postValue(State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = it.message()))
                }
                is ConnectException -> {
                    loadState.postValue(State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络连接异常，请检查网络"))
                }
                is ConnectTimeoutException -> {
                    loadState.postValue(State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络连接超时，请稍后重试"))
                }
                is SocketTimeoutException -> {
                    loadState.postValue(State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络异常，请检查网络"))
                }
                is UnknownHostException -> {
                    loadState.postValue(State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络异常，请检查网络"))
                }
                is JSONException, is ParseException, is JsonParseException  -> {
                    loadState.postValue(State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "数据解析失败"))
                }
                is IOException -> {
                    loadState.postValue(State(StateType.NETWORK_ERROR, Constant.COMMON_KEY, message = "网络连接异常!"))
                }
                else -> {
                    loadState.postValue(State(StateType.NETWORK_ERROR, Constant.COMMON_KEY, message = it.message.toString()))
                }
            }
        }
    }


    fun <T> handleException(e: Throwable?, loadState: MutableLiveData<State>, responseState: MutableLiveData<BaseResponse<T>>) {
        if (BuildConfig.DEBUG) {
            e?.printStackTrace()
            val stackTraces = Thread.currentThread().stackTrace
            for(stackTrace in stackTraces) {
                LogUtils.e("NetExceptionHandle", stackTrace.toString());
            }
        }
        if (e is CancellationException) {
            return
        }

        var state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = e?.toString() ?: "");
        e?.let {
            when (it) {
                is HttpException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = it.message())
                }
                is ConnectException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络连接异常，请检查网络")
                }
                is ConnectTimeoutException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络连接超时，请稍后重试")
                }
                is SocketTimeoutException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络异常，请检查网络")
                }
                is UnknownHostException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络异常，请检查网络")
                }
                is JSONException, is ParseException, is JsonParseException  -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "数据解析失败")
                }
                is IOException -> {
                    state = State(StateType.NETWORK_ERROR, Constant.COMMON_KEY, message = "网络连接异常!")
                }
                else -> {
                    state = State(StateType.NETWORK_ERROR, Constant.COMMON_KEY, message = it.message.toString())
                }
            }
        }
        loadState.setValueSafe(state)
        responseState.setValueSafe(
            BaseResponse<T>(
                success = false,
                code = NETWORK_ERROR_CODE,
                msg = state.message
            )
        )
    }

    fun <T> exceptionToResponse(e: Throwable?): BaseResponse<T> {
        if (BuildConfig.DEBUG) {
            e?.printStackTrace()
            val stackTraces = Thread.currentThread().stackTrace
            for(stackTrace in stackTraces) {
                LogUtils.e("NetExceptionHandle", stackTrace.toString());
            }
        }
        var state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = e?.toString() ?: "");
        e?.let {
            when (it) {
                is HttpException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = it.message())
                }
                is ConnectException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络连接异常，请检查网络")
                }
                is ConnectTimeoutException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络连接超时，请稍后重试")
                }
                is SocketTimeoutException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络异常，请检查网络")
                }
                is UnknownHostException -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "网络异常，请检查网络")
                }
                is JSONException, is ParseException, is JsonParseException  -> {
                    state = State(StateType.NETWORK_ERROR,Constant.COMMON_KEY, message = "数据解析失败")
                }
                is IOException -> {
                    state = State(StateType.NETWORK_ERROR, Constant.COMMON_KEY, message = "网络连接异常!")
                }
                else -> {
                    state = State(StateType.NETWORK_ERROR, Constant.COMMON_KEY, message = it.message.toString())
                }
            }
        }
        return BaseResponse<T>(
            success = false,
            code = NETWORK_ERROR_CODE,
            msg = state.message
        );
    }
}

suspend fun <T> (suspend () ->  BaseResponse<T>).safeCall(): BaseResponse<T> {
    return try {
        return this.invoke()
    } catch (e: Exception) {
        NetExceptionHandle.exceptionToResponse(e);
    }
}