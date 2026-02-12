package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
fun main() {

    runBlocking {
        val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

        val flow = MutableStateFlow<String>("");
        val stateFlow = flow.asStateFlow()
        coroutineScope.launch {
            flow.collect { value ->
                println(value)
            }
        }
        flow.value = "hello world"
        delay(100)
        coroutineScope.cancel()

    }


}