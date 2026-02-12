package com.zhongpin.mvvm_android.view.upload

import android.net.Uri
import java.io.File

data class GridUploadImageItem(var isAdd:Boolean = false,
                               var filePath:String? = null,
                               var imageUrl:String? = null
) {
    fun getHttpOrFileImageUrl():String {
        if (!filePath.isNullOrEmpty()) {
            val imageUrl = Uri.fromFile(File(filePath!!)).toString();
            return imageUrl;
        }
        return  imageUrl ?: ""
    }

    fun getHttpImageUrl():String  {
        return  imageUrl ?: ""
    }
}
