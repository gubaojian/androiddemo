//
//  LBGenericScrollView.m
//  TestOptionScroll
//
//  Created on 2025-11-10.
//

#import "LBGenericScrollView.h"
#import "LBGenericScrollViewCell.h"
#import "LBGenericScrollViewCellModel.h"
#import "LBGenericMaskView.h"

static NSString * const kCellIdentifier = @"LBGenericScrollViewCell";
static CGFloat const kCellHeight = 80.0f;
static CGFloat const kCellWidthNormal = 100.0f;      // isSameYear=NO
static CGFloat const kCellWidthSameYear = 80.0f;     // isSameYear=YES

// 自定义 FlowLayout
@interface LBGenericFlowLayout : UICollectionViewFlowLayout

@property (nonatomic, weak) NSArray<LBGenericScrollViewCellModel *> *dataSource;

@end

@implementation LBGenericFlowLayout

- (instancetype)init {
    if (self = [super init]) {
        self.scrollDirection = UICollectionViewScrollDirectionHorizontal;
        self.minimumLineSpacing = 0;
        self.minimumInteritemSpacing = 0;
        // 不设置固定 itemSize，通过 delegate 方法动态返回
    }
    return self;
}

- (BOOL)shouldInvalidateLayoutForBoundsChange:(CGRect)newBounds {
    return YES;
}

- (CGPoint)targetContentOffsetForProposedContentOffset:(CGPoint)proposedContentOffset withScrollingVelocity:(CGPoint)velocity {
    CGFloat centerX = proposedContentOffset.x + self.collectionView.bounds.size.width / 2;
    
    CGRect targetRect = CGRectMake(proposedContentOffset.x, 0, 
                                    self.collectionView.bounds.size.width, 
                                    self.collectionView.bounds.size.height);
    NSArray *attributes = [super layoutAttributesForElementsInRect:targetRect];
    
    CGFloat minDistance = CGFLOAT_MAX;
    CGFloat targetOffsetX = proposedContentOffset.x;
    
    for (UICollectionViewLayoutAttributes *attr in attributes) {
        CGFloat distance = ABS(attr.center.x - centerX);
        if (distance < minDistance) {
            minDistance = distance;
            targetOffsetX = attr.center.x - self.collectionView.bounds.size.width / 2;
        }
    }
    
    return CGPointMake(targetOffsetX, proposedContentOffset.y);
}

@end

// 主视图
@interface LBGenericScrollView () <UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout>

@property (nonatomic, strong, readwrite) UICollectionView *collectionView;
@property (nonatomic, strong, readwrite) UIImpactFeedbackGenerator *feedbackGenerator;
@property (nonatomic, strong) LBGenericMaskView *overlayMaskView;
@property (nonatomic, strong) LBGenericFlowLayout *flowLayout;
@property (nonatomic, assign) NSInteger selectedIndex;
@property (nonatomic, assign) NSInteger lastFeedbackIndex;

@end

@implementation LBGenericScrollView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.selectedIndex = -1;
        self.lastFeedbackIndex = -1;
        self.initialScrollToIndex = 0;
        [self setupUI];
        [self setupFeedbackGenerator];
    }
    return self;
}

- (void)setupUI {
    self.backgroundColor = [UIColor colorWithWhite:0.98 alpha:1.0];
    
    // CollectionView
    [self addSubview:self.collectionView];
    
    // 蒙层（在 collectionView 之上）
    [self addSubview:self.overlayMaskView];
}

- (void)setupFeedbackGenerator {
    _feedbackGenerator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleLight];
    [_feedbackGenerator prepare];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    // CollectionView 垂直居中
    // 高度设置为 cell 高度，避免多行布局
    CGFloat collectionHeight = kCellHeight;  // 80
    CGFloat collectionY = (self.bounds.size.height - collectionHeight) / 2;
    _collectionView.frame = CGRectMake(0, collectionY, self.bounds.size.width, collectionHeight);
    
    // 设置左右内边距，使第一个和最后一个 item 可以居中
    // 使用最大宽度 100
    CGFloat maxWidth = 100.0f;
    CGFloat sideInset = (self.bounds.size.width - maxWidth) / 2;
    _collectionView.contentInset = UIEdgeInsetsMake(0, sideInset, 0, sideInset);
    
    // 蒙层覆盖整个视图
    _overlayMaskView.frame = self.bounds;
}

- (void)setDataSource:(NSArray<LBGenericScrollViewCellModel *> *)dataSource {
    _dataSource = [dataSource copy];
    _flowLayout.dataSource = dataSource;
    [_collectionView reloadData];
    
    // 数据加载完成后滚动到初始位置
    dispatch_async(dispatch_get_main_queue(), ^{
        [self scrollToInitialPosition];
    });
}

// 初始滚动到指定索引位置
- (void)scrollToInitialPosition {
    NSInteger targetIndex = self.initialScrollToIndex;
    
    // 确保索引在有效范围内
    if (targetIndex < 0) {
        targetIndex = 0;
    } else if (targetIndex >= self.dataSource.count) {
        targetIndex = self.dataSource.count - 1;
    }
    
    if (targetIndex >= 0 && targetIndex < self.dataSource.count) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForItem:targetIndex inSection:0];
        [_collectionView scrollToItemAtIndexPath:indexPath
                                atScrollPosition:UICollectionViewScrollPositionCenteredHorizontally
                                        animated:NO];
        
        // 滚动完成后更新
        dispatch_async(dispatch_get_main_queue(), ^{
            [self updateSelectedState];
            [self updateMaskSizeForCenterCell];  // 更新蒙层宽度
        });
    }
}

- (UICollectionViewCell *)findCenterCell {
    CGFloat screenCenterX = self.bounds.size.width / 2;
    NSArray *visibleCells = [_collectionView visibleCells];
    
    if (visibleCells.count == 0) return nil;
    
    UICollectionViewCell *centerCell = nil;
    CGFloat minDistance = CGFLOAT_MAX;
    
    for (UICollectionViewCell *cell in visibleCells) {
        CGPoint cellCenter = [_collectionView convertPoint:cell.center toView:self];
        CGFloat distance = ABS(cellCenter.x - screenCenterX);
        
        if (distance < minDistance) {
            minDistance = distance;
            centerCell = cell;
        }
    }
    
    return centerCell;
}

- (void)scrollToNearestOption {
    UICollectionViewCell *centerCell = [self findCenterCell];
    if (!centerCell) return;
    
    NSIndexPath *centerIndexPath = [_collectionView indexPathForCell:centerCell];
    if (centerIndexPath) {
        [_collectionView scrollToItemAtIndexPath:centerIndexPath
                                atScrollPosition:UICollectionViewScrollPositionCenteredHorizontally
                                        animated:YES];
        
        // 震动反馈
        [_feedbackGenerator impactOccurred];
        [_feedbackGenerator prepare];
    }
}

- (void)updateSelectedState {
    UICollectionViewCell *centerCell = [self findCenterCell];
    if (centerCell) {
        NSIndexPath *centerIndexPath = [_collectionView indexPathForCell:centerCell];
        if (centerIndexPath && _selectedIndex != centerIndexPath.item) {
            _selectedIndex = centerIndexPath.item;
            
            // 选中居中的 cell
            [_collectionView selectItemAtIndexPath:centerIndexPath 
                                           animated:NO 
                                     scrollPosition:UICollectionViewScrollPositionNone];
            
            // 震动反馈
            if (_lastFeedbackIndex != centerIndexPath.item && _lastFeedbackIndex != -1) {
                [_feedbackGenerator impactOccurred];
                [_feedbackGenerator prepare];
            }
            _lastFeedbackIndex = centerIndexPath.item;
        }
    }
}

- (void)updateMaskSizeForCenterCell {
    UICollectionViewCell *centerCell = [self findCenterCell];
    if (centerCell) {
        NSIndexPath *centerIndexPath = [_collectionView indexPathForCell:centerCell];
        if (centerIndexPath && centerIndexPath.item < self.dataSource.count) {
            LBGenericScrollViewCellModel *model = self.dataSource[centerIndexPath.item];
            CGFloat targetWidth = model.isSameYear ? kCellWidthSameYear : kCellWidthNormal;
            
            // 只在宽度改变时才更新
            if (self.overlayMaskView.clearRectSize.width != targetWidth) {
                // 更新蒙层的抠空大小（只改变宽度，高度固定为 80）
                // 动画在 LBGenericMaskView 内部处理
                self.overlayMaskView.clearRectSize = CGSizeMake(targetWidth, kCellHeight);
            }
        }
    }
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.dataSource.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    LBGenericScrollViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:kCellIdentifier 
                                                                               forIndexPath:indexPath];
    
    if (indexPath.item < self.dataSource.count) {
        cell.model = self.dataSource[indexPath.item];
    }
    
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    // 滚动到选中的 cell，居中显示
    [collectionView scrollToItemAtIndexPath:indexPath
                           atScrollPosition:UICollectionViewScrollPositionCenteredHorizontally
                                   animated:YES];
    
    // 回调
    if (self.didSelectItemAtIndex) {
        self.didSelectItemAtIndex(indexPath.item);
    }
}

#pragma mark - UICollectionViewDelegateFlowLayout

- (CGSize)collectionView:(UICollectionView *)collectionView 
                  layout:(UICollectionViewLayout *)collectionViewLayout 
  sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.item < self.dataSource.count) {
        LBGenericScrollViewCellModel *model = self.dataSource[indexPath.item];
        CGFloat width = model.isSameYear ? kCellWidthSameYear : kCellWidthNormal;
        return CGSizeMake(width, kCellHeight);
    }
    return CGSizeMake(kCellWidthNormal, kCellHeight);
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    // 只更新选中状态
    [self updateSelectedState];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    if (!decelerate) {
        [self scrollToNearestOption];
        [self updateMaskSizeForCenterCell];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    [self scrollToNearestOption];
    [self updateMaskSizeForCenterCell];
}

#pragma mark - Lazy Init

- (UICollectionView *)collectionView {
    if (!_collectionView) {
        _flowLayout = [[LBGenericFlowLayout alloc] init];
        
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero
                                             collectionViewLayout:_flowLayout];
        _collectionView.backgroundColor = [UIColor clearColor];
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        _collectionView.showsHorizontalScrollIndicator = NO;
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.decelerationRate = UIScrollViewDecelerationRateFast;
        
        [_collectionView registerClass:[LBGenericScrollViewCell class] 
             forCellWithReuseIdentifier:kCellIdentifier];
    }
    return _collectionView;
}

- (LBGenericMaskView *)overlayMaskView {
    if (!_overlayMaskView) {
        _overlayMaskView = [[LBGenericMaskView alloc] init];
        // 使用最大宽度 100 作为初始值，后续会根据实际居中 cell 的 isSameYear 动态调整
        _overlayMaskView.clearRectSize = CGSizeMake(100.0f, kCellHeight);
    }
    return _overlayMaskView;
}

@end

