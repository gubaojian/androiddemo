package com.zhongpin.mvvm_android.network

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.base.viewstate.StateType
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.TokenExpiredEvent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


interface CheckResponseEmpty {
    fun isDataEmpty(): Boolean
}


fun <T> MutableLiveData<T>.setValueSafe(value: T) {
    if (Looper.getMainLooper().thread == Thread.currentThread()) {
        this.setValue(value)
    } else {
        this.postValue(value)
    }
}

/**
 * Created with Android Studio.
 * Description:数据解析扩展函数
 * @CreateDate: 2020/4/19 17:35
 */

fun <T> BaseResponse<T>.dataConvert(
    loadState: MutableLiveData<State>,
    urlKey:String = ""
): T? {
    if (data == null) {
        if (success) {
            loadState.setValueSafe(State(StateType.EMPTY, urlKey, message = msg))
        } else {
            loadState.setValueSafe(State(StateType.ERROR, urlKey, message = msg))
        }
        return data
    }
    return when (success) {
        true -> {
            if (data is List<*>) {
                if ((data as List<*>).isEmpty()) {
                    loadState.setValueSafe(State(StateType.EMPTY,urlKey))
                    return data
                }
            }
            if (data is CheckResponseEmpty) {
                if ((data as CheckResponseEmpty).isDataEmpty()) {
                    loadState.setValueSafe(State(StateType.EMPTY, urlKey))
                    return data
                }
            }
            loadState.setValueSafe(State(StateType.SUCCESS,urlKey))
            data
        }
        else -> {
            loadState.setValueSafe(State(StateType.ERROR,urlKey, message = msg))
            data
        }
    }
}


fun <T> BaseResponse<T>.showLoadingState(
    loadState: MutableLiveData<State>,
    urlKey:String = ""
): BaseResponse<T> {
    if (data == null) {
        if (success) {
            loadState.setValueSafe(State(StateType.EMPTY, urlKey, message = msg))
            return this
        } else {
            loadState.setValueSafe(State(StateType.ERROR, urlKey, message = msg))
            return this
        }
    }
    return when (success) {
        true -> {
            if (data is List<*>) {
                if ((data as List<*>).isEmpty()) {
                    loadState.setValueSafe(State(StateType.EMPTY, urlKey))
                    return this
                }
            }
            if (data is CheckResponseEmpty) {
                if ((data as CheckResponseEmpty).isDataEmpty()) {
                    loadState.setValueSafe(State(StateType.EMPTY, urlKey))
                    return this
                }
            }
            loadState.setValueSafe(State(StateType.SUCCESS, urlKey))
            this
        }
        else -> {
            loadState.setValueSafe(State(StateType.ERROR, urlKey, message = msg))
            this
        }
    }
}

fun <T> BaseResponse<T>.requireLogin(): BaseResponse<T> {
    if (code == 403) { //过滤登录过期错误码
        EventBusUtils.postEvent(TokenExpiredEvent(true))
    }
    return this;
}

fun <T> BaseResponse<T>.isTokenExpired(): Boolean {
    return code == 403
}


fun <T, R> BaseResponse<T>.mergeError(
    response:BaseResponse<R>?
): BaseResponse<T> {
    response?.let {
        if (!it.success) {
            success = false;
            code = it.code
            msg = it.msg
        }
    }
    return this;
}


fun <T> BaseResponse<T>.requireLoginWithShowLoading(
    loadState: MutableLiveData<State>,
    urlKey:String = ""
): BaseResponse<T> {
    if (code == 403) {
        EventBusUtils.postEvent(TokenExpiredEvent(true))
    }
    if (data == null) {
        if (success) {
            loadState.setValueSafe(State(StateType.EMPTY, urlKey, message = msg))
            return this
        } else {
            loadState.setValueSafe(State(StateType.ERROR, urlKey, message = msg))
            return this
        }
    }
    return when (success) {
        true -> {
            if (data is List<*>) {
                if ((data as List<*>).isEmpty()) {
                    loadState.setValueSafe(State(StateType.EMPTY,urlKey))
                    return this
                }
            }
            if (data is CheckResponseEmpty) {
                if ((data as CheckResponseEmpty).isDataEmpty()) {
                    loadState.setValueSafe(State(StateType.EMPTY, urlKey))
                    return this
                }
            }
            loadState.setValueSafe(State(StateType.SUCCESS,urlKey))
            this
        }
        else -> {
            loadState.setValueSafe(State(StateType.ERROR,urlKey, message = msg))
            this
        }
    }
}



fun <T : BaseRepository> BaseViewModel<T>.initiateRequest(
    block: suspend () -> Unit,
    loadState: MutableLiveData<State>
) {
    viewModelScope.launch {
        runCatching {
            block()
        }.onSuccess {
        }.onFailure {
            NetExceptionHandle.handleException(it, loadState)
        }
    }
}

fun <T : BaseRepository, Data> BaseViewModel<T>.initiateRequest(
    block: suspend () -> Unit,
    loadState: MutableLiveData<State>,
    responseState: MutableLiveData<BaseResponse<Data>>
) {
    viewModelScope.launch {
        runCatching {
            block()
        }.onSuccess {
        }.onFailure {
            NetExceptionHandle.handleException(it, loadState, responseState)
        }
    }
}

fun <T : BaseRepository> BaseViewModel<T>.initiate2Request(
    block1: suspend () -> Unit,
    block2: suspend () -> Unit,
    done: suspend () -> Unit,
    loadState: MutableLiveData<State>
) {
    viewModelScope.launch {
        runCatching {
            var error: Throwable? = null;
            val tasks = mutableListOf<Deferred<Any>>();
            tasks.add(
                async {
                    runCatching {
                        block1()
                    }.onFailure {
                        error = it
                    }
                }
            )
            tasks.add(
                async {
                    runCatching {
                        block2()
                    }.onFailure {
                        error = it
                    }
                }
            )
            tasks.awaitAll()
            if (error == null) {
                done()
            } else {
                NetExceptionHandle.handleException(error, loadState)
            }
        }.onSuccess {
        }.onFailure {
            NetExceptionHandle.handleException(it, loadState)
        }
    }
}


fun <T : BaseRepository> BaseViewModel<T>.initiate3Request(
    block1: suspend () -> Unit,
    block2: suspend () -> Unit,
    block3: suspend () -> Unit,
    done: suspend () -> Unit,
    loadState: MutableLiveData<State>
) {
    viewModelScope.launch {
        runCatching {
            var error: Throwable? = null;
            val tasks = mutableListOf<Deferred<Any>>();
            tasks.add(
                async {
                    runCatching {
                        block1()
                    }.onFailure {
                        error = it
                    }
                }
            )
            tasks.add(
                async {
                    runCatching {
                        block2()
                    }.onFailure {
                        error = it
                    }
                }
            )
            tasks.add(
                async {
                    runCatching {
                        block3()
                    }.onFailure {
                        error = it
                    }
                }
            )
            tasks.awaitAll()
            if (error == null) {
                done()
            } else {
                NetExceptionHandle.handleException(error, loadState)
            }
        }.onSuccess {
        }.onFailure {
            NetExceptionHandle.handleException(it, loadState)
        }
    }
}

fun <T : BaseRepository> BaseViewModel<T>.initiate4Request(
    block1: suspend () -> Unit,
    block2: suspend () -> Unit,
    block3: suspend () -> Unit,
    block4: suspend () -> Unit,
    done: suspend () -> Unit,
    loadState: MutableLiveData<State>
) {
    viewModelScope.launch {
        runCatching {
            var error: Throwable? = null;
            val tasks = mutableListOf<Deferred<Any>>();
            tasks.add(
                async {
                    runCatching {
                        block1()
                    }.onFailure {
                        error = it
                    }
                }
            )
            tasks.add(
                async {
                    runCatching {
                        block2()
                    }.onFailure {
                        error = it
                    }
                }
            )
            tasks.add(
                async {
                    runCatching {
                        block3()
                    }.onFailure {
                        error = it
                    }
                }
            )
            tasks.add(
                async {
                    runCatching {
                        block4()
                    }.onFailure {
                        error = it
                    }
                }
            )
            tasks.awaitAll()
            if (error == null) {
                done()
            } else {
                NetExceptionHandle.handleException(error, loadState)
            }
        }.onSuccess {
        }.onFailure {
            NetExceptionHandle.handleException(it, loadState)
        }
    }
}

fun <T : BaseRepository> BaseViewModel<T>.initiateNRequest(
    vararg blocks: suspend () -> Unit,
    done: suspend () -> Unit,
    loadState: MutableLiveData<State>
) {
    viewModelScope.launch {
        runCatching {
            var error: Throwable? = null;
            val tasks = mutableListOf<Deferred<Any>>();
            blocks.forEach {
                block ->
                tasks.add(
                    async {
                        runCatching {
                            block()
                        }.onFailure {
                            error = it
                        }
                    }
                )
            }
            tasks.awaitAll()
            if (error == null) {
                done()
            } else {
                NetExceptionHandle.handleException(error, loadState)
            }
        }.onSuccess {
        }.onFailure {
            NetExceptionHandle.handleException(it, loadState)
        }
    }
}