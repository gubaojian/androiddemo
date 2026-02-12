package com.zhongpin.mvvm_android.ui.order.add.item.add.view

import android.app.Activity
import android.view.View
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.zhilianshidai.pindan.app.R
import com.zhongpin.mvvm_android.bean.BoxTypeConfigItem
import com.zhongpin.mvvm_android.bean.WaLenTypeItem
import com.zhongpin.mvvm_android.ui.common.throttleToast


fun Activity.showChooseBoxTypeConfigDialog(
    selectItemCode: String? = null,
    items: List<BoxTypeConfigItem> = emptyList<BoxTypeConfigItem>(),
    chooseAction: OnChooseBoxTypeAction? = null
) {
    val view = findViewById<View>(R.id.addPurchaseBottomView);
    if (view == null) {
        return
    }
    val close = findViewById<View?>(R.id.closePopup)
    if (close != null) {
        return
    }

    if (items.isEmpty()) {
        throttleToast("无箱型可以选择")
        return
    }
    XPopup.Builder(this)
        .isDestroyOnDismiss(true)
        .atView(view)
        .isViewMode(true)
        .popupPosition(PopupPosition.Top)
        .asCustom(ChooseBoxTypeBottomSheet(
            this,
            selectItemCode = selectItemCode,
            items = items,
            chooseAction = chooseAction
        ))
        .show()
}


fun Activity.showChooseWaLenTypeConfigDialog(
    selectCode: String? = null,
    items: List<WaLenTypeItem> = emptyList<WaLenTypeItem>(),
    chooseAction: OnChooseWaLenTypeAction? = null
) {
    val view = findViewById<View>(R.id.addPurchaseBottomView);
    if (view == null) {
        return
    }
    val close = findViewById<View?>(R.id.closePopup)
    if (close != null) {
        return
    }

    if (items.isEmpty()) {
        throttleToast("无瓦型可以选择")
        return
    }
    XPopup.Builder(this)
        .isDestroyOnDismiss(true)
        .atView(view)
        .isViewMode(true)
        .popupPosition(PopupPosition.Top)
        .asCustom(ChooseWaTypeBottomSheet(
            this,
            selectWaCode = selectCode,
            items = items,
            chooseAction = chooseAction
        ))
        .show()
}