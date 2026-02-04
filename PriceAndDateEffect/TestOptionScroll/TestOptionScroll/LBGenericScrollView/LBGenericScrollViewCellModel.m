//
//  LBGenericScrollViewCellModel.m
//  TestOptionScroll
//
//  Created on 2025-11-10.
//

#import "LBGenericScrollViewCellModel.h"

@implementation LBGenericScrollViewCellModel

- (instancetype)init {
    if (self = [super init]) {
        // 设置默认字体大小
        _firstLineMinSize = 14.0f;   // 第一行非高亮
        _firstLineMaxSize = 16.0f;   // 第一行高亮
        _secondLineMinSize = 12.0f;  // 第二行非高亮
        _secondLineMaxSize = 14.0f;  // 第二行高亮
    }
    return self;
}

@end

