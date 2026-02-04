//
//  LBTradeOptionScrollView.h
//  TestOptionScroll
//
//  Created on 2025-11-05.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface LBTradeOptionScrollView : UIView

/// 当前价格
@property (nonatomic, assign) CGFloat currentPrice;

/// 初始滚动到的索引位置（默认为0，即第一个选项）
@property (nonatomic, assign) NSInteger initialScrollToIndex;

/// 初始化方法
/// @param frame 框架
/// @param currentPrice 当前价格
/// @param options 价格选项数组（NSNumber）
- (instancetype)initWithFrame:(CGRect)frame currentPrice:(CGFloat)currentPrice options:(NSArray<NSNumber *> *)options;

/// 滚动到最接近当前价格的选项
- (void)scrollToNearestOption;

@end

NS_ASSUME_NONNULL_END

