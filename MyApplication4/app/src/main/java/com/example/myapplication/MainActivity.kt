package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.example.myapplication.databinding.MainLayoutBinding
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = MainLayoutBinding.inflate(layoutInflater);
        binding.hello.setOnClickListener {
            Toast.makeText(baseContext, "hello world", Toast.LENGTH_SHORT).show()
        }
        binding.input.addTextChangedListener(PriceFormatInputTextWatcher())
        val controller = ListController()
        binding.recycleView.layoutManager = LinearLayoutManager(this@MainActivity,
            LinearLayoutManager.VERTICAL, false)
        binding.recycleView.setController(controller)

        //controller.requestModelBuild()
        val data1: MutableList<String> = mutableListOf();
        for( i in 1.. 100) {
            data1.add("eproxy demo index ${i}");
        }
        binding.demo2.setOnClickListener {
            startActivity(Intent(this@MainActivity, MainActivity2::class.java))
        }
        binding.demo3.setOnClickListener {
            startActivity(Intent(this@MainActivity, MainActivity3::class.java))
        }
        binding.change.setOnClickListener {
            val data2: MutableList<String> = mutableListOf();
            for( i in 1.. 100) {
                data2.add("change demo index ${i}");
            }
            controller.setData(data2)
        }
        controller.setData(data1)

        setContentView(binding.root)
    }
    class ListController : TypedEpoxyController<List<String>>() {
        override fun buildModels(data: List<String>?) {
            if (data.isNullOrEmpty()) return
            headerView {
                id("header_view_fixed")
                title2("fixed header view")
            }
            data?.forEachIndexed { index, it->
                headerView {
                    id("header_view_hot_${index}")
                    title2("header title ${it}")
                }
                footerView {
                    id("footer_view_${index}")
                    title("${it}")
                    backgroundColor("red background")
                }
            }
        }
    }

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
                        var formatInput = "";
                        formatInput = if (abs(firstPart) <= 0) {
                            if (numbers[1].length <= 3) {
                                "${abs(firstPart)}.${numbers[1]}"
                            } else {
                                "${abs(firstPart)}.${numbers[1].substring(0, 3)}"
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
                            s?.clear()
                            s?.append(formatInput)
                            isEditIng = false
                        }
                    }
                } else {
                    val firstPart = input.toIntOrNull()
                    firstPart?.let {
                        val formatInput = "${abs(firstPart)}";
                        if (input != formatInput) {
                            isEditIng = true
                            s?.clear()
                            s?.append(formatInput)
                            isEditIng = false
                        }
                    }
                }
            }
        }
    }
}



