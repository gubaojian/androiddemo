package com.zhongpin.mvvm_android.view.bottomsheet.choosemember

import android.app.Activity
import android.view.View
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.zhilianshidai.pindan.app.R
import com.zhongpin.mvvm_android.bean.RoleItem



fun Activity.showChooseMemberPermissionDialog(items:List<RoleItem>?,
                                              selectItems:List<RoleItem>?,
                                              action: OnChooseRoleAction,
                                              ) {
    val view = findViewById<View>(R.id.roleBottomView);
    if (view == null) {
        return
    }
    val close = findViewById<View?>(R.id.closePopup)
    if (close != null) {
        return
    }
    XPopup.Builder(this)
        .isDestroyOnDismiss(true)
        .atView(view)
        .isViewMode(true)
        .popupPosition(PopupPosition.Top)
        .asCustom( ChooseMemberPermissionBottomSheet(
            this, action,
            selectItems,
            items ?: emptyList()))
        .show()
}
