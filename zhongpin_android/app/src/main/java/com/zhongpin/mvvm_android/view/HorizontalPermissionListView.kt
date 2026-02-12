package com.zhongpin.mvvm_android.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ViewHorizontalPermissionListItemBinding
import com.zhilianshidai.pindan.app.databinding.ViewHorizontalPermissionListPlaceholderBinding
import com.zhilianshidai.pindan.app.databinding.ViewHorizontalPermissionListViewBinding

class HorizontalPermissionListView @JvmOverloads constructor (context: Context,
                                                              attrs: AttributeSet? = null,
                                                              defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr)  {
    private lateinit var binding: ViewHorizontalPermissionListViewBinding;
    private val mData = mutableListOf<String>();

    private var placeholder:String? = null


    init {
        binding = ViewHorizontalPermissionListViewBinding.inflate(
            LayoutInflater.from(context), this, true)
    }

    fun setPlaceholder(placeholder:String?) {
        this.placeholder = placeholder;
        setPermissionList(mData.toList())
    }

    fun setPermissionList(permission:List<String>) {
        if (permission.isEmpty()) {
            mData.clear()
            binding.permissionsContainer.removeAllViews()
            if (TextUtils.isEmpty(placeholder)) {
                visibility = GONE;
            } else {
                placeholder?.let {
                    val placeholderBinding = ViewHorizontalPermissionListPlaceholderBinding.inflate(LayoutInflater.from(context), binding.permissionsContainer, false)
                    placeholderBinding.permissionPlaceholder.text = placeholder
                    binding.permissionsContainer.addView(placeholderBinding.root)
                }
                visibility = VISIBLE;
            }
            return
        }
        visibility = VISIBLE;
        mData.clear();
        mData.addAll(permission);
        binding.permissionsContainer.removeAllViews()

        mData.forEach {
            val itemBinding = ViewHorizontalPermissionListItemBinding.inflate(LayoutInflater.from(context), binding.permissionsContainer, false)
            itemBinding.permissionName.text = it
            if ("管理员".equals(it)) {
                itemBinding.permissionName.setBackgroundResource(R.drawable.bg_permission_blue_item)
            } else if ("采购员".equals(it)) {
                itemBinding.permissionName.setBackgroundResource(R.drawable.bg_permission_yellow_item)
            } else {
                itemBinding.permissionName.setBackgroundResource(R.drawable.bg_permission_green_item)
            }
            binding.permissionsContainer.addView(itemBinding.root)
        }
    }

    fun setContentClickListener(action: View.OnClickListener) {
        binding.permissionsContainer.setOnClickListener(action)
    }

}