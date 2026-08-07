package com.example.testflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainViewModel : ViewModel() {
    val flowTest = MutableSharedFlow<String>()
    val flow2 = MutableStateFlow<String>("init value")

    init {
        viewModelScope.launch {
            delay(1000.milliseconds)
            flowTest.emit("hello world")
            flow2.update {
                "hello update value"
            }
            delay(1000.milliseconds)
            flowTest.emit( "world hello")
            flow2.update {
                "2hello update value"
            }
            delay(1000.milliseconds)
            flow2.value = "update by value"
        }
    }
}