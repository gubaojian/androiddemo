//
//  LBGenericMaskView.m
//  TestOptionScroll
//
//  Created on 2025-11-10.
//

#import "LBGenericMaskView.h"

@interface LBGenericMaskView ()

@property (nonatomic, strong) CAShapeLayer *maskLayer;
@property (nonatomic, strong) CAShapeLayer *borderLayer;

@end

@implementation LBGenericMaskView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupDefaultValues];
        [self setupLayers];
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    if (self = [super initWithCoder:coder]) {
        [self setupDefaultValues];
        [self setupLayers];
    }
    return self;
}

- (void)setupDefaultValues {
    self.backgroundColor = [UIColor clearColor];
    self.userInteractionEnabled = NO;  // 让点击穿透
    self.clearRectSize = CGSizeMake(80, 80);
}

- (void)setupLayers {
    // 蒙层
    _maskLayer = [CAShapeLayer layer];
    _maskLayer.fillColor = [UIColor colorWithWhite:1.0 alpha:0.4].CGColor;
    _maskLayer.fillRule = kCAFillRuleEvenOdd;
    [self.layer addSublayer:_maskLayer];
    
    // 边框层
    _borderLayer = [CAShapeLayer layer];
    _borderLayer.fillColor = [UIColor clearColor].CGColor;
    _borderLayer.strokeColor = [UIColor colorWithWhite:0.8 alpha:1.0].CGColor;
    _borderLayer.lineWidth = 2.0;
    [self.layer addSublayer:_borderLayer];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self updateMaskPath];
}

- (void)updateMaskPath {
    CGRect bounds = self.bounds;
    
    // 计算中间透明区域
    CGFloat centerX = CGRectGetMidX(bounds);
    CGFloat centerY = CGRectGetMidY(bounds);
    CGRect clearRect = CGRectMake(centerX - self.clearRectSize.width / 2,
                                   centerY - self.clearRectSize.height / 2,
                                   self.clearRectSize.width,
                                   self.clearRectSize.height);
    
    // 创建路径：整个视图减去中间区域
    UIBezierPath *path = [UIBezierPath bezierPathWithRect:bounds];
    UIBezierPath *clearPath = [UIBezierPath bezierPathWithRoundedRect:clearRect
                                                          cornerRadius:8];
    [path appendPath:clearPath];
    
    // 更新蒙层路径
    _maskLayer.path = path.CGPath;
    _maskLayer.frame = bounds;
    
    // 更新边框路径
    _borderLayer.path = clearPath.CGPath;
    _borderLayer.frame = bounds;
}

- (void)setClearRectSize:(CGSize)clearRectSize {
    _clearRectSize = clearRectSize;
    
    // 获取当前路径作为动画起点
    CGPathRef oldMaskPath = _maskLayer.path;
    CGPathRef oldBorderPath = _borderLayer.path;
    
    // 更新路径
    [self updateMaskPath];
    
    // 添加动画
    if (oldMaskPath) {
        CABasicAnimation *maskAnimation = [CABasicAnimation animationWithKeyPath:@"path"];
        maskAnimation.fromValue = (__bridge id)oldMaskPath;
        maskAnimation.toValue = (__bridge id)_maskLayer.path;
        maskAnimation.duration = 0.3;
        maskAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
        [_maskLayer addAnimation:maskAnimation forKey:@"pathAnimation"];
    }
    
    if (oldBorderPath) {
        CABasicAnimation *borderAnimation = [CABasicAnimation animationWithKeyPath:@"path"];
        borderAnimation.fromValue = (__bridge id)oldBorderPath;
        borderAnimation.toValue = (__bridge id)_borderLayer.path;
        borderAnimation.duration = 0.3;
        borderAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
        [_borderLayer addAnimation:borderAnimation forKey:@"pathAnimation"];
    }
}

@end

