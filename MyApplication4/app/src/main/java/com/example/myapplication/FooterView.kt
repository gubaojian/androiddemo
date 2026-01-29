package com.example.myapplication

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.example.myapplication.databinding.FooterViewContentBinding

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class FooterView @JvmOverloads constructor(context: Context,
                             attrs: AttributeSet? = null,
                             defStyleAttr:Int = 0,

                             defStyleRes:Int = 0): FrameLayout(context, attrs, defStyleAttr,defStyleRes) {
 lateinit var binding: FooterViewContentBinding;
 init {
     binding = FooterViewContentBinding.inflate(LayoutInflater.from(context), this, true)
 }

    @set:ModelProp
    var title: String = "";

    @set:ModelProp
    var backgroundColor:String = ""

    @AfterPropsSet
    fun setData() {
        binding.headerViewContent.text = "${backgroundColor}${title}"
    }

}