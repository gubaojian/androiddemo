package com.zhongpin.mvvm_android.ui.mine.company.sign

import android.net.Uri
import java.io.File

data class CompanySignImageItem(var isAdd:Boolean,
                                var filePath:String?,
                                var imageUrl:String?
) {
    fun getSignImageUrl():String {
        if (!filePath.isNullOrEmpty()) {
            val imageUrl = Uri.fromFile(File(filePath!!)).toString();
            return imageUrl;
        }
        return  imageUrl ?: ""
    }

    fun getHttpSignImageUrl():String  {
        return  imageUrl ?: ""
    }
}
