package com.lb

/**
 * 通用滚动视图的 Cell 数据模型
 */
data class LBGenericScrollViewCellModel(
    /** 未选中状态的富文本 */
    var normalAttributedText: CharSequence? = null,
    /** 选中状态的富文本 */
    var selectedAttributedText: CharSequence? = null,
    /** 是否同年（影响 cell 宽度） */
    var isSameYear: Boolean = false,
    /** 价格（用于价格滚动视图） */
    var price: Float = 0.0f,

//
//    /** 关联对象（可选） */
//    var associatedObject: Any? = null,
//    /** 字体配置 */
//    var firstLineMinSize: Float = 14.0f,
//    var firstLineMaxSize: Float = 16.0f,
//    var secondLineMinSize: Float = 12.0f,
//    var secondLineMaxSize: Float = 14.0f
)


