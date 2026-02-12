package com.zhongpin.mvvm_android.base.ext

import android.text.TextUtils

object ToastExt {
   var lastToastMsg:String? = null;
   var lastToastTime:Long = 0;


   fun throttleToast(msg:String, block: () -> Unit)  {
       if (!TextUtils.isEmpty(msg)) {
           //调用多次，同一个错误信息只弹出一次
           if (!TextUtils.equals(lastToastMsg, msg)) {
               lastToastMsg = msg
               lastToastTime = System.currentTimeMillis()
               block();
           } else {
               if (System.currentTimeMillis() - lastToastTime > 500) {
                   lastToastMsg = msg
                   lastToastTime = System.currentTimeMillis()
                   block();
               }
           }
       }
   }

}