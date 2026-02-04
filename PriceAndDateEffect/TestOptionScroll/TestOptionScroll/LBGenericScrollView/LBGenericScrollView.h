//
//  LBGenericScrollView.h
//  TestOptionScroll
//
//  Created on 2025-11-10.
//

#import <UIKit/UIKit.h>

@class LBGenericScrollViewCellModel;

NS_ASSUME_NONNULL_BEGIN

/// 通用滚动视图
@interface LBGenericScrollView : UIView

/// 数据源
@property (nonatomic, copy) NSArray<LBGenericScrollViewCellModel *> *dataSource;

/// 初始滚动到的索引位置（默认为0）
@property (nonatomic, assign) NSInteger initialScrollToIndex;

/// 选中回调
@property (nonatomic, copy, nullable) void (^didSelectItemAtIndex)(NSInteger index);

/// 只读属性
@property (nonatomic, strong, readonly) UICollectionView *collectionView;
@property (nonatomic, strong, readonly) UIImpactFeedbackGenerator *feedbackGenerator;

/// 滚动到最近的选项
- (void)scrollToNearestOption;

/// 找到最接近屏幕中心的 cell
- (nullable UICollectionViewCell *)findCenterCell;

- (void)scrollViewDidScroll:(UIScrollView *)scrollView;

@end

NS_ASSUME_NONNULL_END

