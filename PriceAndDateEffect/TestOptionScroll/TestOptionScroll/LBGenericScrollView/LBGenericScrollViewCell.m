//
//  LBGenericScrollViewCell.m
//  TestOptionScroll
//
//  Created on 2025-11-10.
//

#import "LBGenericScrollViewCell.h"
#import "LBGenericScrollViewCellModel.h"

@interface LBGenericScrollViewCell ()

@property (nonatomic, strong) UIView *bgView;
@property (nonatomic, strong) UILabel *contentLabel;
@property (nonatomic, assign) CGFloat currentScale;  // 当前缩放比例

@end

@implementation LBGenericScrollViewCell

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupUI];
        self.currentScale = 1.0;
    }
    return self;
}

- (void)setupUI {
    // bgView 距离左右各 2pt，实现 4pt 的间距效果
    _bgView = [[UIView alloc] init];
    _bgView.backgroundColor = [UIColor yellowColor];
    _bgView.layer.cornerRadius = 8;
    _bgView.clipsToBounds = YES;
    [self.contentView addSubview:_bgView];
    
    _contentLabel = [[UILabel alloc] init];
    _contentLabel.textAlignment = NSTextAlignmentCenter;
    _contentLabel.numberOfLines = 0;
    [_bgView addSubview:_contentLabel];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    // bgView 距离左右各 2pt
    CGFloat horizontalSpacing = 2.0f;
    _bgView.frame = CGRectMake(horizontalSpacing, 0, 
                               self.contentView.bounds.size.width - horizontalSpacing * 2, 
                               self.contentView.bounds.size.height);
    
    _contentLabel.frame = _bgView.bounds;
}

- (void)setModel:(LBGenericScrollViewCellModel *)model {
    _model = model;
    
    // 设置初始文本
    if (self.selected && model.selectedAttributedText) {
        _contentLabel.attributedText = model.selectedAttributedText;
    } else if (model.normalAttributedText) {
        _contentLabel.attributedText = model.normalAttributedText;
    }
    
    // 应用当前缩放比例
    if (self.currentScale > 0 && self.currentScale != 1.0) {
        [self updateScaleRatio:self.currentScale];
    }
}

- (void)setSelected:(BOOL)selected {
    [super setSelected:selected];
    
    if (!self.model) return;
    
    if (selected) {
        // 选中状态：只改变文字，不改变边框
        if (self.model.selectedAttributedText) {
            _contentLabel.attributedText = self.model.selectedAttributedText;
        }
    } else {
        // 未选中状态
        if (self.model.normalAttributedText) {
            _contentLabel.attributedText = self.model.normalAttributedText;
        }
    }
    
    // 重新应用缩放比例
    if (self.currentScale > 0 && self.currentScale != 1.0) {
        [self updateScaleRatio:self.currentScale];
    }
}

/// 根据 selected 状态更新字体（不再使用缩放）
- (void)updateScaleRatio:(CGFloat)scale {
    // 简化：只根据 selected 状态显示对应的富文本，不做任何缩放
    if (!self.model) return;
    
    self.currentScale = scale;
    
    NSAttributedString *text = self.selected ? self.model.selectedAttributedText : self.model.normalAttributedText;
    if (text) {
        self.contentLabel.attributedText = text;
    }
}

@end

