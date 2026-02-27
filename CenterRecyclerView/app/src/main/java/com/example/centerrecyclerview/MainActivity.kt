package com.example.centerrecyclerview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.centerrecyclerview.databinding.MainBinding
import com.view.jameson.library.CardScaleHelper
import github.hellocsl.gallerylayoutmanager.layout.impl.ScaleTransformer
import github.hellocsl.layoutmanager.gallery.GalleryLayoutManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = MainBinding.inflate(LayoutInflater.from(this))


        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.centerRecyclerView.layoutManager = layoutManager

        // 3. 设置适配器（这里用简单的字符串列表示例，你需替换为自己的适配器）
        val data = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 4", "Item 5", "Item 4", "Item 5")
        binding.centerRecyclerView.adapter = SimpleAdapter(data)
        //val padding = (resources.displayMetrics.widthPixels - 140*resources.displayMetrics.density)/2
        //binding.centerRecyclerView.setPadding(padding.toInt(),0,padding.toInt(), 0)
        // 4. 核心：添加 LinearSnapHelper 实现吸附效果
        //val snapHelper = LinearSnapHelper()
        //snapHelper.attachToRecyclerView(binding.centerRecyclerView)
        //binding.centerRecyclerView.smoothScrollToPosition(3)

        val mCardScaleHelper = CardScaleHelper()
        //图片间距(dp)
        val mPagePadding = 0

        //左右侧显示宽度(dp)
        val mShowLeftCardWidth = 0
        mCardScaleHelper.setPagePadding(mPagePadding)
        mCardScaleHelper.setShowLeftCardWidth(mShowLeftCardWidth)

        mCardScaleHelper.setScale(1.0f)
        mCardScaleHelper.setCurrentItemPos(0)
        mCardScaleHelper.attachToRecyclerView(binding.centerRecyclerView)


        val galleryLayoutManager = GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL)
        galleryLayoutManager.setOnItemSelectedListener { recyclerView, item, position ->
            Log.e("OnItemSelectedListener", "OnItemSelectedListener " + position)
        }
        galleryLayoutManager.attach(binding.recyclerView2, 0)
        galleryLayoutManager.setItemTransformer(ScaleTransformer())
        binding.recyclerView2.layoutManager = galleryLayoutManager

        // 3. 设置适配器（这里用简单的字符串列表示例，你需替换为自己的适配器）
        binding.recyclerView2.adapter = SimpleAdapter(data)



        setContentView(binding.root)


    }

    class CardSimpleAdapter(private val data: List<String>) :
        RecyclerView.Adapter<CardSimpleAdapter.ViewHolder>() {

        class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.date_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        override fun getItemCount() = data.size
    }

    class SimpleAdapter(private val data: List<String>) :
        RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {

        class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.date_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        override fun getItemCount() = data.size
    }
}


