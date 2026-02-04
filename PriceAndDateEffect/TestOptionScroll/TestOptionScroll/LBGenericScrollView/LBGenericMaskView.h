//
//  LBGenericMaskView.h
//  TestOptionScroll
//
//  Created on 2025-11-10.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/// 中间抠空的蒙层视图
@interface LBGenericMaskView : UIView

/// 抠空区域的大小（默认 80x80）
@property (nonatomic, assign) CGSize clearRectSize;

@end

NS_ASSUME_NONNULL_END

