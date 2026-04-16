package com.example.myapplication

import android.os.Bundle
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
import com.airbnb.epoxy.Typed2EpoxyController
import com.airbnb.epoxy.TypedEpoxyController
import com.example.myapplication.databinding.MainLayout2Binding
import com.example.myapplication.databinding.MainLayoutBinding

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = MainLayout2Binding.inflate(layoutInflater);
        binding.hello.setOnClickListener {
            Toast.makeText(baseContext, "hello world", Toast.LENGTH_SHORT).show()
        }
        val controller = ListController()
        binding.recycleView.layoutManager = LinearLayoutManager(this@MainActivity2,
            LinearLayoutManager.VERTICAL, false)
        binding.recycleView.setController(controller)

        val data1: MutableList<String> = mutableListOf();
        for( i in 1.. 100) {
            data1.add("eproxy demo index ${i}");
        }
        controller.setData(data1, false)
        binding.changeData.setOnClickListener {
            val data1: MutableList<String> = mutableListOf();
            for( i in 1.. 100) {
                data1.add("change demo index ${i}");
            }
            controller.setData(data1, true)
        }
        setContentView(binding.root)
    }
    class ListController : Typed2EpoxyController<List<String>, Boolean>() {

        override fun buildModels(
            data: List<String>?,
            data2: Boolean?
        ) {
            data2?.let {
                if (data2) {
                    headerView {
                        id("load more")
                        title2("load more header")
                    }
                }
            }
            data?.forEachIndexed { index, it ->
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
}



