package com.zhongpin.mvvm_android.ui.buy.edit

import android.net.Uri
import java.io.File

data class EditPublishBuyImageItem(var isAdd:Boolean,
                                   var filePath:String?,
                                   var imageUrl:String?
) {
    fun getBuyImageUrl():String {
        if (!filePath.isNullOrEmpty()) {
            val imageUrl = Uri.fromFile(File(filePath!!)).toString();
            return imageUrl;
        }
        return  imageUrl ?: ""
    }
}
