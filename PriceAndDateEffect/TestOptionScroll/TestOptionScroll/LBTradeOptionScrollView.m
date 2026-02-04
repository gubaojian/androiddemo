//
//  LBTradeOptionScrollView.m
//  TestOptionScroll
//
//  Created on 2025-11-05.
//

#import "LBTradeOptionScrollView.h"

static NSString * const kCellIdentifier = @"LBTradeOptionCell";

// Cell
@interface LBTradeOptionCell : UICollectionViewCell
@property (nonatomic, strong) UILabel *priceLabel;
@property (nonatomic, strong) UILabel *percentLabel;
@property (nonatomic, assign) CGFloat price;
@property (nonatomic, assign) CGFloat percent;
@end

@implementation LBTradeOptionCell

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupUI];
    }
    return self;
}

- (void)setupUI {
    self.contentView.backgroundColor = [UIColor whiteColor];
    self.contentView.layer.cornerRadius = 12;
    self.contentView.layer.borderWidth = 2;
    self.contentView.layer.borderColor = [UIColor colorWithWhite:0.95 alpha:1.0].CGColor;
    
    _priceLabel = [[UILabel alloc] init];
    _priceLabel.font = [UIFont systemFontOfSize:24 weight:UIFontWeightBold];
    _priceLabel.textColor = [UIColor blackColor];
    _priceLabel.textAlignment = NSTextAlignmentCenter;
    [self.contentView addSubview:_priceLabel];
    
    _percentLabel = [[UILabel alloc] init];
    _percentLabel.font = [UIFont systemFontOfSize:14 weight:UIFontWeightMedium];
    _percentLabel.textColor = [UIColor colorWithRed:1.0 green:0.4 blue:0.2 alpha:1.0];
    _percentLabel.textAlignment = NSTextAlignmentCenter;
    [self.contentView addSubview:_percentLabel];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    CGFloat width = self.contentView.bounds.size.width;
    CGFloat height = self.contentView.bounds.size.height;
    
    _priceLabel.frame = CGRectMake(0, height * 0.3, width, 30);
    _percentLabel.frame = CGRectMake(0, height * 0.6, width, 20);
}

- (void)setPrice:(CGFloat)price {
    _price = price;
    _priceLabel.text = [NSString stringWithFormat:@"%.0f", price];
}

- (void)setPercent:(CGFloat)percent {
    _percent = percent;
    _percentLabel.text = [NSString stringWithFormat:@"+%.1f%%", percent];
}

- (void)setSelected:(BOOL)selected {
    [super setSelected:selected];
    
    if (selected) {
        // 选中状态：蓝色边框，黑色文字
        self.contentView.layer.borderColor = [UIColor systemBlueColor].CGColor;
        self.contentView.layer.borderWidth = 3;
        _priceLabel.textColor = [UIColor blackColor];
        _percentLabel.textColor = [UIColor colorWithRed:1.0 green:0.4 blue:0.2 alpha:1.0];
    } else {
        // 未选中状态：浅灰色边框，浅灰色文字
        self.contentView.layer.borderColor = [UIColor colorWithWhite:0.95 alpha:1.0].CGColor;
        self.contentView.layer.borderWidth = 2;
        _priceLabel.textColor = [UIColor colorWithWhite:0.6 alpha:1.0];
        _percentLabel.textColor = [UIColor colorWithWhite:0.7 alpha:1.0];
    }
}

@end

// Layout
@interface LBTradeOptionFlowLayout : UICollectionViewFlowLayout
@property (nonatomic, assign) CGFloat selectedItemSize;
@property (nonatomic, assign) CGFloat normalItemSize;
@end

@implementation LBTradeOptionFlowLayout

- (instancetype)init {
    if (self = [super init]) {
        _selectedItemSize = 80;
        _normalItemSize = 60;
        self.scrollDirection = UICollectionViewScrollDirectionHorizontal;
        self.minimumLineSpacing = 20;
    }
    return self;
}

- (NSArray<UICollectionViewLayoutAttributes *> *)layoutAttributesForElementsInRect:(CGRect)rect {
    NSArray *attributes = [super layoutAttributesForElementsInRect:rect];
    
    CGFloat centerX = self.collectionView.contentOffset.x + self.collectionView.bounds.size.width / 2;
    
    for (UICollectionViewLayoutAttributes *attr in attributes) {
        CGFloat distance = ABS(attr.center.x - centerX);
        CGFloat scale = MAX(1.0 - distance / (self.collectionView.bounds.size.width / 2), 0.75);
        
        // 使用 transform 缩放整个 cell（包括内容）
        attr.transform = CGAffineTransformMakeScale(scale, scale);
    }
    
    return attributes;
}

- (BOOL)shouldInvalidateLayoutForBoundsChange:(CGRect)newBounds {
    return YES;
}

- (CGPoint)targetContentOffsetForProposedContentOffset:(CGPoint)proposedContentOffset withScrollingVelocity:(CGPoint)velocity {
    CGFloat centerX = proposedContentOffset.x + self.collectionView.bounds.size.width / 2;
    
    CGRect targetRect = CGRectMake(proposedContentOffset.x, 0, self.collectionView.bounds.size.width, self.collectionView.bounds.size.height);
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

@interface LBTradeOptionScrollView () <UICollectionViewDelegate, UICollectionViewDataSource>

@property (nonatomic, strong) UICollectionView *collectionView;
@property (nonatomic, strong) LBTradeOptionFlowLayout *flowLayout;
@property (nonatomic, strong) NSArray<NSNumber *> *options;

// 现价标签相关
@property (nonatomic, strong) UIView *currentPriceContainer;
@property (nonatomic, strong) UILabel *currentPriceLabel;
@property (nonatomic, strong) UIView *verticalLine;
@property (nonatomic, strong) UIButton *leftArrowButton;
@property (nonatomic, strong) UIButton *rightArrowButton;

// 状态
@property (nonatomic, assign) NSInteger selectedIndex;
@property (nonatomic, assign) BOOL isAtLeftBoundary;
@property (nonatomic, assign) BOOL isAtRightBoundary;

// 现价附近的价格索引
@property (nonatomic, assign) NSInteger lowerPriceIndex;  // 小于等于现价的最大价格
@property (nonatomic, assign) NSInteger upperPriceIndex;  // 大于现价的最小价格

// 震动反馈
@property (nonatomic, strong) UIImpactFeedbackGenerator *feedbackGenerator;
@property (nonatomic, assign) NSInteger lastFeedbackIndex;

@end

@implementation LBTradeOptionScrollView

- (instancetype)initWithFrame:(CGRect)frame currentPrice:(CGFloat)currentPrice options:(NSArray<NSNumber *> *)options {
    if (self = [super initWithFrame:frame]) {
        _currentPrice = currentPrice;
        _options = options;
        _selectedIndex = -1;
        _lastFeedbackIndex = -1;
        
        [self setupUI];
        [self setupFeedbackGenerator];
        
        // 初始化完成后，滚动到最近的价格并更新UI
        dispatch_async(dispatch_get_main_queue(), ^{
            [self scrollToInitialPosition];
        });
    }
    return self;
}

// 初始滚动到指定索引位置
- (void)scrollToInitialPosition {
    NSInteger targetIndex = _initialScrollToIndex;
    
    // 确保索引在有效范围内
    if (targetIndex < 0) {
        targetIndex = 0;
    } else if (targetIndex >= _options.count) {
        targetIndex = _options.count - 1;
    }
    
    if (targetIndex >= 0 && targetIndex < _options.count) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForItem:targetIndex inSection:0];
        [_collectionView scrollToItemAtIndexPath:indexPath
                                atScrollPosition:UICollectionViewScrollPositionCenteredHorizontally
                                        animated:NO];  // 初始化不需要动画
        
        // 滚动完成后，更新选中状态和标签
        dispatch_async(dispatch_get_main_queue(), ^{
            [self updateCurrentPricePosition];
        });
    }
}

- (void)setupFeedbackGenerator {
    _feedbackGenerator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleLight];
    [_feedbackGenerator prepare];
}

- (void)setupUI {
    self.backgroundColor = [UIColor colorWithWhite:0.98 alpha:1.0];
    
    // CollectionView Layout
    _flowLayout = [[LBTradeOptionFlowLayout alloc] init];
    _flowLayout.itemSize = CGSizeMake(80, 80);
    _flowLayout.minimumInteritemSpacing = 0;  // 垂直方向间距为0
    
    // CollectionView - 只设置一行的高度
    CGFloat collectionHeight = 100;  // 足够显示一行 80x80 的 cell
    CGFloat collectionY = (self.bounds.size.height - collectionHeight) / 2;  // 垂直居中
    _collectionView = [[UICollectionView alloc] initWithFrame:CGRectMake(0, collectionY, self.bounds.size.width, collectionHeight) collectionViewLayout:_flowLayout];
    _collectionView.backgroundColor = [UIColor clearColor];
    _collectionView.delegate = self;
    _collectionView.dataSource = self;
    _collectionView.showsHorizontalScrollIndicator = NO;
    _collectionView.showsVerticalScrollIndicator = NO;
    _collectionView.decelerationRate = UIScrollViewDecelerationRateFast;
    
    // 设置左右内边距，使第一个和最后一个item可以居中
    CGFloat sideInset = (self.bounds.size.width - 80) / 2;
    _collectionView.contentInset = UIEdgeInsetsMake(0, sideInset, 0, sideInset);
    
    [_collectionView registerClass:[LBTradeOptionCell class] forCellWithReuseIdentifier:kCellIdentifier];
    [self addSubview:_collectionView];
    
    // 竖线
    _verticalLine = [[UIView alloc] init];
    _verticalLine.backgroundColor = [UIColor blackColor];
    _verticalLine.layer.cornerRadius = 1;
    [self addSubview:_verticalLine];
    
    // 现价容器（整个容器可点击）
    _currentPriceContainer = [[UIView alloc] init];
    _currentPriceContainer.backgroundColor = [UIColor blackColor];
    _currentPriceContainer.layer.cornerRadius = 8;
    _currentPriceContainer.userInteractionEnabled = YES;
    [self addSubview:_currentPriceContainer];
    
    // 现价标签
    _currentPriceLabel = [[UILabel alloc] init];
    _currentPriceLabel.font = [UIFont systemFontOfSize:14 weight:UIFontWeightMedium];
    _currentPriceLabel.textColor = [UIColor whiteColor];
    _currentPriceLabel.textAlignment = NSTextAlignmentCenter;
    _currentPriceLabel.text = [NSString stringWithFormat:@"现价 %.2f", _currentPrice];
    _currentPriceLabel.userInteractionEnabled = NO;
    [_currentPriceContainer addSubview:_currentPriceLabel];
    
    // 左箭头图片（显示在右边，表示现价在右边）
    _leftArrowButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _leftArrowButton.userInteractionEnabled = NO;  // 禁用按钮交互，让容器处理点击
    // 创建占位图片（右箭头）
    UIImage *rightArrowImage = [self createArrowImageWithDirection:YES];
    [_leftArrowButton setImage:rightArrowImage forState:UIControlStateNormal];
    _leftArrowButton.hidden = YES;
    [_currentPriceContainer addSubview:_leftArrowButton];
    
    // 右箭头图片（显示在左边，表示现价在左边）
    _rightArrowButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _rightArrowButton.userInteractionEnabled = NO;  // 禁用按钮交互，让容器处理点击
    // 创建占位图片（左箭头）
    UIImage *leftArrowImage = [self createArrowImageWithDirection:NO];
    [_rightArrowButton setImage:leftArrowImage forState:UIControlStateNormal];
    _rightArrowButton.hidden = YES;
    [_currentPriceContainer addSubview:_rightArrowButton];
    
    // 添加点击手势到整个容器
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(currentPriceContainerTapped)];
    [_currentPriceContainer addGestureRecognizer:tapGesture];
}

// 创建箭头占位图片
- (UIImage *)createArrowImageWithDirection:(BOOL)isRight {
    CGSize size = CGSizeMake(20, 20);
    UIGraphicsBeginImageContextWithOptions(size, NO, 0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    // 设置颜色为白色
    [[UIColor whiteColor] setFill];
    [[UIColor whiteColor] setStroke];
    
    // 绘制箭头
    UIBezierPath *path = [UIBezierPath bezierPath];
    if (isRight) {
        // 右箭头 >
        [path moveToPoint:CGPointMake(6, 4)];
        [path addLineToPoint:CGPointMake(14, 10)];
        [path addLineToPoint:CGPointMake(6, 16)];
    } else {
        // 左箭头 <
        [path moveToPoint:CGPointMake(14, 4)];
        [path addLineToPoint:CGPointMake(6, 10)];
        [path addLineToPoint:CGPointMake(14, 16)];
    }
    
    path.lineWidth = 2;
    path.lineCapStyle = kCGLineCapRound;
    path.lineJoinStyle = kCGLineJoinRound;
    [path stroke];
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return image;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    // CollectionView 只占一行的高度，垂直居中
    CGFloat collectionHeight = 100;
    CGFloat collectionY = (self.bounds.size.height - collectionHeight) / 2;
    _collectionView.frame = CGRectMake(0, collectionY, self.bounds.size.width, collectionHeight);
    
    // 更新内边距
    CGFloat sideInset = (self.bounds.size.width - 80) / 2;
    _collectionView.contentInset = UIEdgeInsetsMake(0, sideInset, 0, sideInset);
    
    // 延迟更新，确保 collectionView 布局完成
    dispatch_async(dispatch_get_main_queue(), ^{
        [self updateCurrentPricePosition];
    });
}

- (void)updateCurrentPricePosition {
    CGFloat containerWidth = 120;
    CGFloat containerHeight = 36;
    
    // 找到最接近现价的两个价格
    _lowerPriceIndex = -1;  // 小于等于现价的最大价格（49）
    _upperPriceIndex = -1;  // 大于现价的最小价格（50）
    
    for (NSInteger i = 0; i < _options.count; i++) {
        CGFloat price = [_options[i] floatValue];
        if (price <= _currentPrice) {
            _lowerPriceIndex = i;
        } else if (_upperPriceIndex == -1) {
            _upperPriceIndex = i;
            break;
        }
    }
    
    // 检查这两个价格是否在可见范围内
    BOOL lowerPriceVisible = NO;
    BOOL upperPriceVisible = NO;
    
    NSArray *visibleIndexPaths = [_collectionView indexPathsForVisibleItems];
    for (NSIndexPath *indexPath in visibleIndexPaths) {
        if (indexPath.item == _lowerPriceIndex) {
            lowerPriceVisible = YES;
        }
        if (indexPath.item == _upperPriceIndex) {
            upperPriceVisible = YES;
        }
    }
    
    // 判断边界状态
    BOOL wasAtLeftBoundary = _isAtLeftBoundary;
    BOOL wasAtRightBoundary = _isAtRightBoundary;
    
    _isAtLeftBoundary = NO;
    _isAtRightBoundary = NO;
    _leftArrowButton.hidden = YES;
    _rightArrowButton.hidden = YES;
    _currentPriceContainer.hidden = YES;
    _verticalLine.hidden = YES;
    
    // 判断显示悬浮标签还是边界标签
    if (lowerPriceVisible || upperPriceVisible) {
        // 至少一个价格可见 → 显示悬浮标签
        [self showFloatingLabel:lowerPriceVisible upperVisible:upperPriceVisible containerWidth:containerWidth containerHeight:containerHeight];
    } else {
        // 两个价格都不可见 → 显示边界标签
        [self showBoundaryLabel:visibleIndexPaths containerWidth:containerWidth containerHeight:containerHeight];
    }
    
    // 触发震动反馈
    if ((wasAtLeftBoundary != _isAtLeftBoundary) || (wasAtRightBoundary != _isAtRightBoundary)) {
        [_feedbackGenerator impactOccurred];
        [_feedbackGenerator prepare];
    }
    
    // 更新选中状态（找到最接近屏幕中心的cell）
    UICollectionViewCell *centerCell = [self findCenterCell];
    if (centerCell) {
        NSIndexPath *centerIndexPath = [_collectionView indexPathForCell:centerCell];
        if (centerIndexPath && _selectedIndex != centerIndexPath.item) {
            _selectedIndex = centerIndexPath.item;
            
            // 选中居中的cell，触发边框显示
            [_collectionView selectItemAtIndexPath:centerIndexPath animated:NO scrollPosition:UICollectionViewScrollPositionNone];
            
            // 震动反馈
            if (_lastFeedbackIndex != centerIndexPath.item && _lastFeedbackIndex != -1) {
                [_feedbackGenerator impactOccurred];
                [_feedbackGenerator prepare];
            }
            _lastFeedbackIndex = centerIndexPath.item;
        }
    }
}

// 显示悬浮标签（在49和50中间）
- (void)showFloatingLabel:(BOOL)lowerVisible upperVisible:(BOOL)upperVisible containerWidth:(CGFloat)containerWidth containerHeight:(CGFloat)containerHeight {
    // 获取 cell 位置
    UICollectionViewCell *lowerCell = nil;
    UICollectionViewCell *upperCell = nil;
    
    if (_lowerPriceIndex >= 0) {
        lowerCell = [_collectionView cellForItemAtIndexPath:[NSIndexPath indexPathForItem:_lowerPriceIndex inSection:0]];
    }
    if (_upperPriceIndex >= 0) {
        upperCell = [_collectionView cellForItemAtIndexPath:[NSIndexPath indexPathForItem:_upperPriceIndex inSection:0]];
    }
    
    // 计算中间位置（竖线应该在的位置）
    CGFloat middleX = 0;
    CGFloat middleY = 0;
    
    if (lowerCell && upperCell) {
        // 两个都可见，取中间位置
        CGPoint lowerCenter = [_collectionView convertPoint:lowerCell.center toView:self];
        CGPoint upperCenter = [_collectionView convertPoint:upperCell.center toView:self];
        middleX = (lowerCenter.x + upperCenter.x) / 2;
        middleY = (lowerCenter.y + upperCenter.y) / 2;
    } else if (lowerCell) {
        // 只有 lower 可见
        CGPoint lowerCenter = [_collectionView convertPoint:lowerCell.center toView:self];
        middleX = lowerCenter.x;
        middleY = lowerCenter.y;
    } else if (upperCell) {
        // 只有 upper 可见
        CGPoint upperCenter = [_collectionView convertPoint:upperCell.center toView:self];
        middleX = upperCenter.x;
        middleY = upperCenter.y;
    } else {
        return;  // 都不可见，不显示
    }
    
    // 计算标签位置（理想位置）
    CGFloat containerX = middleX - containerWidth / 2;
    CGFloat leftBoundary = 0;
    CGFloat rightBoundary = self.bounds.size.width - containerWidth;
    
    // 判断是否到达边界
    BOOL reachedLeftBoundary = containerX < leftBoundary;
    BOOL reachedRightBoundary = containerX > rightBoundary;
    
    if (reachedLeftBoundary) {
        // 标签固定在左边界
        containerX = leftBoundary;
        
        // 判断竖线是否也到达边界（竖线在标签左边缘或更左）
        if (middleX <= leftBoundary) {
            // 切换为边界标签
            NSArray *visibleIndexPaths = [_collectionView indexPathsForVisibleItems];
            [self showBoundaryLabel:visibleIndexPaths containerWidth:containerWidth containerHeight:containerHeight];
            return;
        }
    } else if (reachedRightBoundary) {
        // 标签固定在右边界
        containerX = rightBoundary;
        
        // 判断竖线是否也到达边界（竖线在标签右边缘或更右）
        if (middleX >= rightBoundary + containerWidth) {
            // 切换为边界标签
            NSArray *visibleIndexPaths = [_collectionView indexPathsForVisibleItems];
            [self showBoundaryLabel:visibleIndexPaths containerWidth:containerWidth containerHeight:containerHeight];
            return;
        }
    }
    
    // 显示悬浮标签
    _currentPriceContainer.hidden = NO;
    _verticalLine.hidden = NO;
    
    // 设置悬浮标签位置
    _currentPriceContainer.frame = CGRectMake(containerX, 20, containerWidth, containerHeight);
    
    // 悬浮标签只显示文字，不显示箭头
    _currentPriceLabel.frame = CGRectMake(0, 0, containerWidth, containerHeight);
    
    // 设置竖线位置（从标签底部到中间位置，竖线X坐标始终跟随middleX）
    CGFloat lineHeight = middleY - (20 + containerHeight);
    _verticalLine.frame = CGRectMake(middleX - 1, 20 + containerHeight, 2, MAX(0, lineHeight));
}

// 显示边界标签
- (void)showBoundaryLabel:(NSArray *)visibleIndexPaths containerWidth:(CGFloat)containerWidth containerHeight:(CGFloat)containerHeight {
    if (visibleIndexPaths.count == 0) {
        return;
    }
    
    NSIndexPath *firstVisibleIndexPath = [[visibleIndexPaths sortedArrayUsingSelector:@selector(compare:)] firstObject];
    CGFloat firstVisiblePrice = [_options[firstVisibleIndexPath.item] floatValue];
    
    CGFloat leftBoundary = 0;
    CGFloat rightBoundary = self.bounds.size.width - containerWidth;
    
    _currentPriceContainer.hidden = NO;
    _verticalLine.hidden = YES;  // 边界标签不显示竖线
    
    if (firstVisiblePrice < _currentPrice) {
        // 所有可见价格都小于现价 → 现价在右边 → 显示右边界标签（现价 >）
        _isAtLeftBoundary = YES;
        _leftArrowButton.hidden = NO;
        _currentPriceContainer.frame = CGRectMake(rightBoundary, 20, containerWidth, containerHeight);
        _currentPriceLabel.frame = CGRectMake(8, 0, containerWidth - 38, containerHeight);
        _leftArrowButton.frame = CGRectMake(containerWidth - 30, (containerHeight - 20) / 2, 20, 20);
    } else {
        // 所有可见价格都大于现价 → 现价在左边 → 显示左边界标签（< 现价）
        _isAtRightBoundary = YES;
        _rightArrowButton.hidden = NO;
        _currentPriceContainer.frame = CGRectMake(leftBoundary, 20, containerWidth, containerHeight);
        _rightArrowButton.frame = CGRectMake(10, (containerHeight - 20) / 2, 20, 20);
        _currentPriceLabel.frame = CGRectMake(30, 0, containerWidth - 38, containerHeight);
    }
}

// 找到最接近屏幕中心的cell
- (UICollectionViewCell *)findCenterCell {
    CGFloat screenCenterX = self.bounds.size.width / 2;
    
    NSArray *visibleCells = [_collectionView visibleCells];
    if (visibleCells.count == 0) {
        return nil;
    }
    
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

// 整个现价容器点击
- (void)currentPriceContainerTapped {
    if (_isAtLeftBoundary || _isAtRightBoundary) {
        // 在边界状态，滚动到现价附近
        [self scrollToCurrentPriceArea];
    } else {
        // 非边界状态，滚动到最近的选项
        [self scrollToNearestOption];
    }
}

// 滚动到现价附近的两个价格
- (void)scrollToCurrentPriceArea {
    NSInteger targetIndex = -1;
    
    // 优先滚动到 lowerPriceIndex（小于等于现价的最大价格）
    if (_lowerPriceIndex >= 0 && _lowerPriceIndex < _options.count) {
        targetIndex = _lowerPriceIndex;
    } else if (_upperPriceIndex >= 0 && _upperPriceIndex < _options.count) {
        targetIndex = _upperPriceIndex;
    }
    
    if (targetIndex >= 0) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForItem:targetIndex inSection:0];
        [_collectionView scrollToItemAtIndexPath:indexPath
                                atScrollPosition:UICollectionViewScrollPositionCenteredHorizontally
                                        animated:YES];
        
        // 震动反馈
        [_feedbackGenerator impactOccurred];
        [_feedbackGenerator prepare];
    }
}

- (void)scrollToNearestOption {
    UICollectionViewCell *centerCell = [self findCenterCell];
    if (centerCell) {
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
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return _options.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    LBTradeOptionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:kCellIdentifier forIndexPath:indexPath];
    
    CGFloat price = [_options[indexPath.item] floatValue];
    CGFloat percent = ((price - _currentPrice) / _currentPrice) * 100;
    
    cell.price = price;
    cell.percent = percent;
    
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    // 滚动到选中的cell，居中显示
    [collectionView scrollToItemAtIndexPath:indexPath 
                           atScrollPosition:UICollectionViewScrollPositionCenteredHorizontally 
                                   animated:YES];
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    [self updateCurrentPricePosition];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    if (!decelerate) {
        [self scrollToNearestOption];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    [self scrollToNearestOption];
}

@end

