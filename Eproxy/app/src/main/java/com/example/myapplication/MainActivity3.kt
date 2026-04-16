package com.example.myapplication


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.myapplication.databinding.HeaderViewContentBinding
import com.example.myapplication.databinding.MainLayout3Binding


class MainActivity3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = MainLayout3Binding.inflate(layoutInflater);
        binding.hello.setOnClickListener {
            Toast.makeText(baseContext, "hello world", Toast.LENGTH_SHORT).show()
        }

        //controller.requestModelBuild()
        val data1: MutableList<String> = mutableListOf();
        for( i in 1.. 100) {
            data1.add("base quick adapter demo index ${i}");
        }
        val adapter = QuickAdapter(data1);
        
        binding.recycleView.layoutManager = LinearLayoutManager(this@MainActivity3,
            LinearLayoutManager.VERTICAL, false)
        binding.recycleView.adapter = adapter;

        val multiItems = mutableListOf<MultiItem>()
        multiItems.add(MultiItem(type = 0, label = "type 0 "))
        multiItems.add(MultiItem(type = 0, label = "type 0 "))
        multiItems.add(MultiItem(type = 0, label = "type 0 "))
        multiItems.add(MultiItem(type = 1, label = "type 1 "))
        multiItems.add(MultiItem(type = 1, label = "type 1 "))
        multiItems.add(MultiItem(type = 1, label = "type 1 "))

        val multiAdapter = MultiTypeAdapter(multiItems)
        binding.recycleView2.layoutManager = LinearLayoutManager(this@MainActivity3,
        LinearLayoutManager.VERTICAL, false)
        binding.recycleView2.adapter = multiAdapter ;



        setContentView(binding.root)
    }



    class QuickAdapter(
        private val mAdapterDatas: MutableList<String> // 私有化数据源，仅内部访问
    ) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.header_view_content, mAdapterDatas) {

        /**
         * 绑定数据到列表项
         * @param helper ViewHolder 辅助类（简化 findViewById）
         * @param item 当前项的数据
         */
        override fun convert(helper: BaseViewHolder, item: String) {
            // 方式1：BRVAH 封装的快捷方法（推荐，更简洁）
            helper.setText(R.id.header_view_content, item)

            // 方式2：手动获取 View 再赋值（和你原代码一致）
            // val textView = helper.getView<TextView>(R.id.header_view_content)
            // textView.text = item

            // 可选：设置其他属性（比如文字颜色、点击事件等）
            // helper.setTextColor(R.id.header_view_content, ContextCompat.getColor(context, R.color.black))
        }
    }

    data class MultiItem(
        val type: Int,
        val label:String
    ): MultiItemEntity {
        override fun getItemType(): Int {
            return type
        }

    };

    class MultiTypeAdapter(mMultiDatas: MutableList<MultiItem>)
        : BaseMultiItemQuickAdapter<MultiItem, BaseViewHolder>(mMultiDatas) {
        init {
            addItemType(0, R.layout.header_view_content)
            addItemType(1, R.layout.footer_view_content)
        }
        override fun convert(
            helper: BaseViewHolder,
            item: MultiItem
        ) {
            if (item.itemType == 0) {
                val binding: HeaderViewContentBinding = HeaderViewContentBinding.bind(helper.itemView)
                binding.headerViewContent.text = item.label

            }else if (item.itemType == 1) {
                helper.setText(R.id.header_view_content, item.label)

            }
        }

    }

}



