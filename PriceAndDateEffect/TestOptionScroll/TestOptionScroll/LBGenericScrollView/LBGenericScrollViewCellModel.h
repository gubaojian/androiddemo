//
//  LBGenericScrollViewCellModel.h
//  TestOptionScroll
//
//  Created on 2025-11-10.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 通用滚动视图的 Cell 数据模型
@interface LBGenericScrollViewCellModel : NSObject

/// 未选中状态的富文本
@property (nonatomic, copy) NSAttributedString *normalAttributedText;

/// 选中状态的富文本
@property (nonatomic, copy) NSAttributedString *selectedAttributedText;

/// 是否同年（影响 cell 大小）
@property (nonatomic, assign) BOOL isSameYear;

/// 价格（用于价格滚动视图）
@property (nonatomic, assign) CGFloat price;

/// 关联的数据对象（可选）
@property (nonatomic, strong, nullable) id associatedObject;

/// 字体大小配置（第一行非高亮）
@property (nonatomic, assign) CGFloat firstLineMinSize;

/// 字体大小配置（第一行高亮）
@property (nonatomic, assign) CGFloat firstLineMaxSize;

/// 字体大小配置（第二行非高亮）
@property (nonatomic, assign) CGFloat secondLineMinSize;

/// 字体大小配置（第二行高亮）
@property (nonatomic, assign) CGFloat secondLineMaxSize;

@end

NS_ASSUME_NONNULL_END

