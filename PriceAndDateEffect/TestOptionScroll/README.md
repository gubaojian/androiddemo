# LBTradeOption 滚动价格选择器

这是一个用 Objective-C 实现的可滚动价格选择器组件，具有流畅的动画效果和触觉反馈。

## 功能特性

### 1. 滚动价格选择（UICollectionView）
- 使用 `UICollectionView` 实现高性能滚动
- 横向滚动查看多个价格选项（支持1-100个价格）
- 自动对齐到最近的价格选项
- 选中的价格会高亮显示（80x80）
- 未选中的价格缩小显示（60x60）
- 平滑的尺寸过渡动画

### 2. 现价悬浮标签（实时跟随）
- 显示当前市场价格
- **实时跟随滚动**：滑动时立即响应，无延迟
- 竖线连接现价标签和选中的价格选项
- 选中价格始终居中展示

### 3. 边界处理（动态展示）
- **中间状态**：现价标签居中显示在选中价格上方，竖线垂直向下
- **左边界**：当滚动到左侧时，现价标签固定在左上角，显示左箭头（≪），竖线向右倾斜
- **右边界**：当滚动到右侧时，现价标签固定在右上角，显示右箭头（≫），竖线向左倾斜
- 竖线位置随滚动动态调整，平滑过渡

### 4. 触觉反馈
- 切换选中价格时震动
- 到达/离开边界状态时震动
- 使用 `UIImpactFeedbackGenerator` 提供轻量级触觉反馈

### 5. 点击跳转
- 点击现价标签可快速滚动到最接近现价的选项
- 平滑动画过渡

## 文件说明

### LBTradeOptionScrollView.h
头文件，定义了公共接口：
- `currentPrice`: 当前市场价格
- `initWithFrame:currentPrice:options:`: 初始化方法
- `scrollToNearestOption`: 滚动到最近选项的方法

### LBTradeOptionScrollView.m
实现文件，包含：
- `LBTradeOptionCell`: 单个价格选项 Cell（UICollectionViewCell）
- `LBTradeOptionFlowLayout`: 自定义 FlowLayout，实现尺寸渐变效果
- UICollectionView 管理
- 现价标签实时位置计算
- 边界检测逻辑
- 触觉反馈控制

### ViewController.m
示例使用，展示了两个滚动视图：
1. 跳转现价 - 演示基本功能
2. 边界展示 - 演示边界处理
- 使用1-100的价格数据

## 使用方法

```objc
#import "LBTradeOptionScrollView.h"

// 创建价格选项数组（1-100）
NSMutableArray *options = [NSMutableArray array];
for (NSInteger i = 1; i <= 100; i++) {
    [options addObject:@(i)];
}

// 初始化滚动视图
LBTradeOptionScrollView *scrollView = [[LBTradeOptionScrollView alloc] 
    initWithFrame:CGRectMake(0, 130, self.view.bounds.size.width, 200)
    currentPrice:49.95
    options:options];

// 添加到视图
[self.view addSubview:scrollView];

// 可选：滚动到最近的选项
[scrollView scrollToNearestOption];
```

## 实现细节

### UICollectionView 自定义布局
使用自定义 `LBTradeOptionFlowLayout` 实现：
- 选中项：80x80
- 未选中项：60x60
- 根据距离中心的位置动态计算尺寸
- 自动对齐到中心

```objc
- (NSArray<UICollectionViewLayoutAttributes *> *)layoutAttributesForElementsInRect:(CGRect)rect {
    NSArray *attributes = [super layoutAttributesForElementsInRect:rect];
    CGFloat centerX = self.collectionView.contentOffset.x + self.collectionView.bounds.size.width / 2;
    
    for (UICollectionViewLayoutAttributes *attr in attributes) {
        CGFloat distance = ABS(attr.center.x - centerX);
        CGFloat scale = MAX(1.0 - distance / (self.collectionView.bounds.size.width / 2), 0.75);
        CGFloat size = self.normalItemSize + (self.selectedItemSize - self.normalItemSize) * (scale - 0.75) / 0.25;
        attr.size = CGSizeMake(size, size);
    }
    
    return attributes;
}
```

### 实时位置更新
在 `scrollViewDidScroll:` 中实时更新现价标签位置：
```objc
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    [self updateCurrentPricePosition];  // 立即响应滚动
}
```

### 价格计算
每个价格选项会自动计算相对于现价的涨幅百分比：
```objc
CGFloat percent = ((price - currentPrice) / currentPrice) * 100;
```

### 边界检测
通过比较现价标签的目标位置和屏幕边界来判断状态：
- 左边界：`targetX < leftBoundary && lineX < 15`
- 右边界：`targetX > rightBoundary && lineX > containerWidth - 15`
- 中间状态：其他情况

### 竖线位置
竖线的 X 坐标根据现价标签的位置动态计算：
```objc
CGFloat lineX = cellCenterInView.x - finalX;
_verticalLine.frame = CGRectMake(finalX + lineX - 1, 20 + containerHeight, 2, lineHeight);
```

### 触觉反馈时机
- 选中价格改变时
- 边界状态改变时
- 点击现价标签时

## 自定义选项

可以通过修改以下参数来自定义外观：

```objc
// 在 LBTradeOptionFlowLayout init 方法中
_selectedItemSize = 80;       // 选中项尺寸
_normalItemSize = 60;         // 未选中项尺寸
self.minimumLineSpacing = 20; // 选项间距

// 在 updateCurrentPricePosition 方法中
CGFloat containerWidth = 120;  // 现价标签宽度
CGFloat containerHeight = 36;  // 现价标签高度
CGFloat leftBoundary = 20;     // 左边界距离
CGFloat rightBoundary = self.bounds.size.width - containerWidth - 20; // 右边界距离
```

## 设计亮点

1. **UICollectionView 架构**：使用 `UICollectionView` 替代 `UIScrollView`，支持大量数据，性能更优
2. **实时响应**：现价标签在 `scrollViewDidScroll:` 中实时更新，无延迟
3. **自动居中**：通过自定义 FlowLayout 的 `targetContentOffsetForProposedContentOffset:` 实现自动对齐
4. **流畅的尺寸动画**：选中/未选中状态平滑过渡（80x80 ↔ 60x60）
5. **智能边界处理**：三种状态无缝切换，竖线动态倾斜
6. **触觉反馈**：增强交互感，让用户清楚感知状态变化
7. **自适应布局**：竖线和标签位置实时计算，适应任何滚动位置
8. **性能优化**：使用 Cell 复用机制，支持1-100个价格无压力

## 技术改进

### v2.0 更新（当前版本）
✅ 使用 `UICollectionView` 替代 `UIScrollView`  
✅ 实时跟随滚动，无延迟  
✅ 选中价格自动居中  
✅ 支持1-100个价格选项  
✅ 选中项 80x80，未选中项 60x60  
✅ 边界状态动态展示（竖线倾斜）  

## 系统要求

- iOS 10.0+
- Xcode 12.0+
- Objective-C with ARC

## 许可证

MIT License

