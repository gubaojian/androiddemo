//
//  LBGenericScrollViewCell.h
//  TestOptionScroll
//
//  Created on 2025-11-10.
//

#import <UIKit/UIKit.h>

@class LBGenericScrollViewCellModel;

NS_ASSUME_NONNULL_BEGIN

/// 通用滚动视图的 Cell
@interface LBGenericScrollViewCell : UICollectionViewCell

/// 数据模型
@property (nonatomic, strong) LBGenericScrollViewCellModel *model;

/// 根据缩放比例更新字体大小（保持颜色不变）
/// @param scale 缩放比例 (0.75 ~ 1.0)
- (void)updateScaleRatio:(CGFloat)scale;

@end

NS_ASSUME_NONNULL_END

