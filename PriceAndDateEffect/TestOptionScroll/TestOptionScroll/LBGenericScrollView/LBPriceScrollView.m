//
//  LBPriceScrollView.m
//  TestOptionScroll
//
//  Created on 2025-11-11.
//

#import "LBPriceScrollView.h"
#import "LBGenericScrollViewCellModel.h"

@interface LBPriceScrollView ()

/// 竖线
@property (nonatomic, strong) UIView *verticalLine;

/// 现价容器（可点击）
@property (nonatomic, strong) UIView *currentPriceContainer;

/// 现价标签
@property (nonatomic, strong) UILabel *currentPriceLabel;

/// 左箭头（显示在右边，表示现价在右边）
@property (nonatomic, strong) UIButton *leftArrowButton;

/// 右箭头（显示在左边，表示现价在左边）
@property (nonatomic, strong) UIButton *rightArrowButton;

@end

@implementation LBPriceScrollView

- (instancetype)initWithCurrentPrice:(CGFloat)currentPrice {
    if (self = [super initWithFrame:CGRectZero]) {
        _currentPrice = currentPrice;
        [self setupPriceUI];
    }
    return self;
}

- (void)setupPriceUI {
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
    _leftArrowButton.userInteractionEnabled = NO;
    UIImage *rightArrowImage = [self createArrowImageWithDirection:YES];
    [_leftArrowButton setImage:rightArrowImage forState:UIControlStateNormal];
    _leftArrowButton.hidden = YES;
    [_currentPriceContainer addSubview:_leftArrowButton];
    
    // 右箭头图片（显示在左边，表示现价在左边）
    _rightArrowButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _rightArrowButton.userInteractionEnabled = NO;
    UIImage *leftArrowImage = [self createArrowImageWithDirection:NO];
    [_rightArrowButton setImage:leftArrowImage forState:UIControlStateNormal];
    _rightArrowButton.hidden = YES;
    [_currentPriceContainer addSubview:_rightArrowButton];
    
    // 添加点击手势到整个容器
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(currentPriceContainerTapped)];
    [_currentPriceContainer addGestureRecognizer:tapGesture];
}

- (NSInteger)findNearestPriceIndex {
    if (self.dataSource.count == 0) return 0;
    
    CGFloat minDiff = CGFLOAT_MAX;
    NSInteger nearestIndex = 0;
    
    for (NSInteger i = 0; i < self.dataSource.count; i++) {
        LBGenericScrollViewCellModel *model = self.dataSource[i];
        CGFloat price = model.price;
        CGFloat diff = ABS(price - _currentPrice);
        if (diff < minDiff) {
            minDiff = diff;
            nearestIndex = i;
        }
    }
    
    return nearestIndex;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    // 延迟更新，确保 collectionView 布局完成
    dispatch_async(dispatch_get_main_queue(), ^{
        [self updateCurrentPricePosition];
    });
}

#pragma mark - Override Parent Methods

// 重写父类的 scrollViewDidScroll 方法
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    // 先调用父类的实现
    [super scrollViewDidScroll:scrollView];
    
    // 更新价格标签位置
    [self updateCurrentPricePosition];
}

- (void)updateCurrentPricePosition {
    // 找到最接近当前价格的两个价格
    NSInteger lowerIndex = -1;
    NSInteger upperIndex = -1;
    
    for (NSInteger i = 0; i < self.dataSource.count; i++) {
        LBGenericScrollViewCellModel *model = self.dataSource[i];
        CGFloat price = model.price;
        if (price <= _currentPrice) {
            lowerIndex = i;
        }
        if (price >= _currentPrice && upperIndex == -1) {
            upperIndex = i;
            break;
        }
    }
    
    // 如果找不到上下边界，使用边界值
    if (lowerIndex == -1) lowerIndex = 0;
    if (upperIndex == -1) upperIndex = self.dataSource.count - 1;
    
    // 检查这两个价格是否都可见
    BOOL lowerVisible = [self isIndexVisible:lowerIndex];
    BOOL upperVisible = [self isIndexVisible:upperIndex];
    
    if (lowerVisible && upperVisible) {
        // 两个价格都可见，显示悬浮标签
        [self showFloatingLabel];
    } else {
        // 至少有一个价格不可见，显示边界标签
        [self showBoundaryLabel];
    }
}

- (BOOL)isIndexVisible:(NSInteger)index {
    if (index < 0 || index >= self.dataSource.count) return NO;
    
    NSIndexPath *indexPath = [NSIndexPath indexPathForItem:index inSection:0];
    NSArray *visibleIndexPaths = [self.collectionView indexPathsForVisibleItems];
    
    return [visibleIndexPaths containsObject:indexPath];
}

- (void)showFloatingLabel {
    // 悬浮标签状态：禁用点击
    _currentPriceContainer.userInteractionEnabled = NO;
    
    // 找到最接近当前价格的两个价格的 cell
    NSInteger lowerIndex = -1;
    NSInteger upperIndex = -1;
    
    for (NSInteger i = 0; i < self.dataSource.count; i++) {
        LBGenericScrollViewCellModel *model = self.dataSource[i];
        CGFloat price = model.price;
        if (price <= _currentPrice) {
            lowerIndex = i;
        }
        if (price >= _currentPrice && upperIndex == -1) {
            upperIndex = i;
            break;
        }
    }
    
    if (lowerIndex == -1) lowerIndex = 0;
    if (upperIndex == -1) upperIndex = self.dataSource.count - 1;
    
    // 获取两个 cell
    UICollectionViewCell *lowerCell = [self.collectionView cellForItemAtIndexPath:[NSIndexPath indexPathForItem:lowerIndex inSection:0]];
    UICollectionViewCell *upperCell = [self.collectionView cellForItemAtIndexPath:[NSIndexPath indexPathForItem:upperIndex inSection:0]];
    
    if (!lowerCell || !upperCell) {
        _currentPriceContainer.hidden = YES;
        _verticalLine.hidden = YES;
        return;
    }
    
    // 计算两个 cell 的中心点
    CGPoint lowerCenter = [self.collectionView convertPoint:lowerCell.center toView:self];
    CGPoint upperCenter = [self.collectionView convertPoint:upperCell.center toView:self];
    
    // 悬浮标签的 X 位置在两个 cell 的正中间
    CGFloat floatingX = (lowerCenter.x + upperCenter.x) / 2;
    
    // 屏幕边界
    CGFloat screenLeft = 0;
    CGFloat screenRight = self.bounds.size.width;
    
    // 标签宽度和高度
    CGFloat labelWidth = 100;
    CGFloat labelHeight = 30;
    CGFloat labelY = self.collectionView.frame.origin.y - labelHeight - 10;  // 在 collectionView 上方 10pt
    
    // 计算标签的实际位置（考虑边界限制）
    CGFloat containerX = floatingX - labelWidth / 2;
    CGFloat minX = screenLeft;
    CGFloat maxX = screenRight - labelWidth;
    
    // 标签是否到达边界
    BOOL reachedLeftBoundary = (containerX < minX);
    BOOL reachedRightBoundary = (containerX > maxX);
    
    if (reachedLeftBoundary || reachedRightBoundary) {
        // 标签固定在边界
        if (reachedLeftBoundary) {
            containerX = minX;
        } else {
            containerX = maxX;
        }
        
        // 竖线在标签内移动
        CGFloat lineX = floatingX - containerX;
        
        // 检查竖线是否也到达标签边界
        if (lineX < 0 || lineX > labelWidth) {
            // 竖线也到达边界，切换为边界标签
            [self showBoundaryLabel];
            return;
        }
        
        // 显示悬浮标签
        _currentPriceContainer.hidden = NO;
        _verticalLine.hidden = NO;
        _leftArrowButton.hidden = YES;
        _rightArrowButton.hidden = YES;
        
        // 悬浮标签：恢复所有圆角
        _currentPriceContainer.layer.cornerRadius = 8;
        _currentPriceContainer.layer.maskedCorners = kCALayerMinXMinYCorner | kCALayerMaxXMinYCorner | kCALayerMinXMaxYCorner | kCALayerMaxXMaxYCorner;
        
        _currentPriceContainer.frame = CGRectMake(containerX, labelY, labelWidth, labelHeight);
        _currentPriceLabel.frame = _currentPriceContainer.bounds;
        
        // 竖线位置（从标签底部到 collectionView）
        CGFloat lineStartY = labelY + labelHeight;
        CGFloat lineHeight = self.collectionView.frame.origin.y - lineStartY;
        _verticalLine.frame = CGRectMake(containerX + lineX - 1, lineStartY, 2, lineHeight);
    } else {
        // 标签和竖线都正常显示
        _currentPriceContainer.hidden = NO;
        _verticalLine.hidden = NO;
        _leftArrowButton.hidden = YES;
        _rightArrowButton.hidden = YES;
        
        // 悬浮标签：恢复所有圆角
        _currentPriceContainer.layer.cornerRadius = 8;
        _currentPriceContainer.layer.maskedCorners = kCALayerMinXMinYCorner | kCALayerMaxXMinYCorner | kCALayerMinXMaxYCorner | kCALayerMaxXMaxYCorner;
        
        _currentPriceContainer.frame = CGRectMake(containerX, labelY, labelWidth, labelHeight);
        _currentPriceLabel.frame = _currentPriceContainer.bounds;
        
        // 竖线位置（从标签底部到 collectionView，居中）
        CGFloat lineStartY = labelY + labelHeight;
        CGFloat lineHeight = self.collectionView.frame.origin.y - lineStartY;
        _verticalLine.frame = CGRectMake(floatingX - 1, lineStartY, 2, lineHeight);
    }
}

- (void)showBoundaryLabel {
    // 边界标签状态：启用点击
    _currentPriceContainer.userInteractionEnabled = YES;
    
    // 判断当前价格在左边还是右边
    CGFloat screenCenterX = self.bounds.size.width / 2;
    UICollectionViewCell *centerCell = [self findCenterCell];
    
    if (!centerCell) {
        _currentPriceContainer.hidden = YES;
        _verticalLine.hidden = YES;
        return;
    }
    
    NSIndexPath *centerIndexPath = [self.collectionView indexPathForCell:centerCell];
    if (!centerIndexPath || centerIndexPath.item >= self.dataSource.count) {
        _currentPriceContainer.hidden = YES;
        _verticalLine.hidden = YES;
        return;
    }
    
    LBGenericScrollViewCellModel *centerModel = self.dataSource[centerIndexPath.item];
    CGFloat centerPrice = centerModel.price;
    
    // 隐藏竖线
    _verticalLine.hidden = YES;
    
    // 显示边界标签
    _currentPriceContainer.hidden = NO;
    
    CGFloat labelWidth = 120;
    CGFloat labelHeight = 30;
    CGFloat labelY = self.collectionView.frame.origin.y - labelHeight - 10;  // 在 collectionView 上方 10pt
    
    if (_currentPrice < centerPrice) {
        // 现价在左边，标签显示在左边界
        _currentPriceContainer.frame = CGRectMake(0, labelY, labelWidth, labelHeight);
        
        // 左边界：去掉左侧圆角，保留右侧圆角
        _currentPriceContainer.layer.cornerRadius = 8;
        _currentPriceContainer.layer.maskedCorners = kCALayerMaxXMinYCorner | kCALayerMaxXMaxYCorner;  // 右上角和右下角
        
        // 显示右箭头（在左边，表示现价在左边）
        _leftArrowButton.hidden = YES;
        _rightArrowButton.hidden = NO;
        
        // 布局：≪ 现价 XX.XX
        _rightArrowButton.frame = CGRectMake(5, (labelHeight - 20) / 2, 20, 20);
        _currentPriceLabel.frame = CGRectMake(30, 0, labelWidth - 35, labelHeight);
        _currentPriceLabel.textAlignment = NSTextAlignmentLeft;
    } else {
        // 现价在右边，标签显示在右边界
        _currentPriceContainer.frame = CGRectMake(self.bounds.size.width - labelWidth, labelY, labelWidth, labelHeight);
        
        // 右边界：去掉右侧圆角，保留左侧圆角
        _currentPriceContainer.layer.cornerRadius = 8;
        _currentPriceContainer.layer.maskedCorners = kCALayerMinXMinYCorner | kCALayerMinXMaxYCorner;  // 左上角和左下角
        
        // 显示左箭头（在右边，表示现价在右边）
        _leftArrowButton.hidden = NO;
        _rightArrowButton.hidden = YES;
        
        // 布局：现价 XX.XX ≫
        _currentPriceLabel.frame = CGRectMake(5, 0, labelWidth - 35, labelHeight);
        _currentPriceLabel.textAlignment = NSTextAlignmentRight;
        _leftArrowButton.frame = CGRectMake(labelWidth - 25, (labelHeight - 20) / 2, 20, 20);
    }
}

- (void)currentPriceContainerTapped {
    // 滚动到最接近当前价格的选项
    [self scrollToNearestPriceOption];
}

- (void)scrollToNearestPriceOption {
    NSInteger nearestIndex = [self findNearestPriceIndex];

    if (nearestIndex >= 0 && nearestIndex < self.dataSource.count) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForItem:nearestIndex inSection:0];
        [self.collectionView scrollToItemAtIndexPath:indexPath
                                    atScrollPosition:UICollectionViewScrollPositionCenteredHorizontally
                                            animated:YES];

        // 震动反馈
        [self.feedbackGenerator impactOccurred];
        [self.feedbackGenerator prepare];
    }
}

#pragma mark - Public Methods

/// 更新当前价格
- (void)updateCurrentPrice:(CGFloat)price {
    _currentPrice = price;

    // 更新标签文本
    _currentPriceLabel.text = [NSString stringWithFormat:@"现价 %.2f", _currentPrice];

    // 立即更新UI位置
    [self updateCurrentPricePosition];
}

// 创建箭头占位图片
- (UIImage *)createArrowImageWithDirection:(BOOL)isRight {
    CGSize size = CGSizeMake(20, 20);
    UIGraphicsBeginImageContextWithOptions(size, NO, 0);
    
    // 设置颜色为白色
    [[UIColor whiteColor] setFill];
    [[UIColor whiteColor] setStroke];
    
    // 绘制箭头
    UIBezierPath *path = [UIBezierPath bezierPath];
    if (isRight) {
        // 右箭头 ≫
        [path moveToPoint:CGPointMake(6, 4)];
        [path addLineToPoint:CGPointMake(14, 10)];
        [path addLineToPoint:CGPointMake(6, 16)];
    } else {
        // 左箭头 ≪
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

@end

