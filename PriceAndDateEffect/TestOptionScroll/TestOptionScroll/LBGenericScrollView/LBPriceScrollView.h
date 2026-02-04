//
//  LBPriceScrollView.h
//  TestOptionScroll
//
//  Created on 2025-11-11.
//

#import "LBGenericScrollView.h"

NS_ASSUME_NONNULL_BEGIN

/// 价格滚动视图（继承自通用滚动视图，添加价格标签和边界处理）
@interface LBPriceScrollView : LBGenericScrollView

/// 当前价格
@property (nonatomic, assign) CGFloat currentPrice;

/// 初始化方法
/// @param currentPrice 当前价格
- (instancetype)initWithCurrentPrice:(CGFloat)currentPrice;

/// 更新当前价格
/// @param price 新的价格
- (void)updateCurrentPrice:(CGFloat)price;

@end

NS_ASSUME_NONNULL_END

