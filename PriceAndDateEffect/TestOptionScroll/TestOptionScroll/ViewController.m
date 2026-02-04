//
//  ViewController.m
//  TestOptionScroll
//
//  Created by 周鑫 on 2025/11/5.
//

#import "ViewController.h"
#import "LBTradeOptionScrollView.h"
#import "LBGenericScrollView.h"
#import "LBGenericScrollViewCellModel.h"
#import "LBPriceScrollView.h"

@interface ViewController ()

//@property (nonatomic, strong) LBTradeOptionScrollView *optionScrollView;
@property (nonatomic, strong) LBGenericScrollView *genericScrollView;
@property (nonatomic, strong) LBPriceScrollView *priceScrollView;
@property (nonatomic, strong) UIButton *randomPriceButton;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor colorWithWhite:0.95 alpha:1.0];

//    [self setupScrollView];
    [self setupGenericScrollView];
    [self setupPriceScrollView];
    [self setupRandomPriceButton];
}

//- (void)setupScrollView {
//    // 示例数据：价格从1到100
//    CGFloat currentPrice = 49.95;
//    NSMutableArray *options = [NSMutableArray array];
//    for (NSInteger i = 1; i <= 100; i++) {
//        [options addObject:@(i)];
//    }
//    
//    // 添加标题
//    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 60, self.view.bounds.size.width - 40, 40)];
//    titleLabel.text = @"跳转现价";
//    titleLabel.font = [UIFont systemFontOfSize:28 weight:UIFontWeightBold];
//    titleLabel.textColor = [UIColor blackColor];
//    [self.view addSubview:titleLabel];
//    
//    UILabel *subtitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 100, self.view.bounds.size.width - 40, 20)];
//    subtitleLabel.text = @"现价";
//    subtitleLabel.font = [UIFont systemFontOfSize:14 weight:UIFontWeightRegular];
//    subtitleLabel.textColor = [UIColor grayColor];
//    [self.view addSubview:subtitleLabel];
//    
//    // 滚动视图
//    _optionScrollView = [[LBTradeOptionScrollView alloc] initWithFrame:CGRectMake(0, 130, self.view.bounds.size.width, 200)
//                                                           currentPrice:currentPrice
//                                                                options:options];
//    
//    // 设置初始滚动到的索引位置（可选，如果不设置则默认为0）
//    // 例如：初始滚动到索引 53（即价格 54）
//    _optionScrollView.initialScrollToIndex = 53;
//    
//    [self.view addSubview:_optionScrollView];
//}

- (void)setupGenericScrollView {
    // 添加标题
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 360, self.view.bounds.size.width - 40, 40)];
    titleLabel.text = @"通用滚动视图";
    titleLabel.font = [UIFont systemFontOfSize:28 weight:UIFontWeightBold];
    titleLabel.textColor = [UIColor blackColor];
    [self.view addSubview:titleLabel];
    
    UILabel *subtitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 400, self.view.bounds.size.width - 40, 20)];
    subtitleLabel.text = @"支持富文本和字体缩放";
    subtitleLabel.font = [UIFont systemFontOfSize:14 weight:UIFontWeightRegular];
    subtitleLabel.textColor = [UIColor grayColor];
    [self.view addSubview:subtitleLabel];
    
    // 创建通用滚动视图
    _genericScrollView = [[LBGenericScrollView alloc] initWithFrame:CGRectMake(0, 430, self.view.bounds.size.width, 200)];
    [self.view addSubview:_genericScrollView];
    
    // 准备数据
    NSMutableArray *dataSource = [NSMutableArray array];
    for (NSInteger i = 1; i <= 100; i++) {
        LBGenericScrollViewCellModel *model = [[LBGenericScrollViewCellModel alloc] init];
        
        // 计算百分比
        CGFloat currentPrice = 49.95;
        CGFloat percent = ((i - currentPrice) / currentPrice) * 100;
        
        // 未选中状态的富文本（浅灰色）
        NSMutableAttributedString *normalText = [[NSMutableAttributedString alloc] init];
        
        // 第一行：价格 (14 regular)
        [normalText appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"%ld\n", (long)i]
                                                                            attributes:@{
            NSFontAttributeName: [UIFont systemFontOfSize:14 weight:UIFontWeightRegular],
            NSForegroundColorAttributeName: [UIColor colorWithWhite:0.6 alpha:1.0]
        }]];
        
        // 第二行：百分比 (12 regular)
        [normalText appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"+%.1f%%", percent]
                                                                            attributes:@{
            NSFontAttributeName: [UIFont systemFontOfSize:12 weight:UIFontWeightRegular],
            NSForegroundColorAttributeName: [UIColor colorWithWhite:0.7 alpha:1.0]
        }]];
        
        model.normalAttributedText = normalText;
        
        // 选中状态的富文本（黑色和橙色）
        NSMutableAttributedString *selectedText = [[NSMutableAttributedString alloc] init];
        
        // 第一行：价格 (16 medium)
        [selectedText appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"%ld\n", (long)i]
                                                                              attributes:@{
            NSFontAttributeName: [UIFont systemFontOfSize:16 weight:UIFontWeightMedium],
            NSForegroundColorAttributeName: [UIColor blackColor]
        }]];
        
        // 第二行：百分比 (14 medium)
        [selectedText appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"+%.1f%%", percent]
                                                                              attributes:@{
            NSFontAttributeName: [UIFont systemFontOfSize:14 weight:UIFontWeightMedium],
            NSForegroundColorAttributeName: [UIColor colorWithRed:1.0 green:0.4 blue:0.2 alpha:1.0]
        }]];
        
        model.selectedAttributedText = selectedText;
        
        // 设置 isSameYear（示例：随机设置一些为 YES，展示不同宽度效果）
        // 实际使用时应该根据业务逻辑设置，比如日期是否同年等
        model.isSameYear = (i % 7 == 0);  // 每隔7个设置为YES（80宽），其他为NO（100宽）
        
        // 关联数据
        model.associatedObject = @(i);
        
        [dataSource addObject:model];
    }
    
    // 设置数据源
    _genericScrollView.dataSource = dataSource;
    
    // 设置初始滚动位置
    _genericScrollView.initialScrollToIndex = 49;
    
    // 设置选中回调
    _genericScrollView.didSelectItemAtIndex = ^(NSInteger index) {
        NSLog(@"通用滚动视图 - 选中了索引：%ld", (long)index);
    };
}

- (void)setupPriceScrollView {
    // 添加标题
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 160, self.view.bounds.size.width - 40, 40)];
    titleLabel.text = @"价格滚动视图";
    titleLabel.font = [UIFont systemFontOfSize:28 weight:UIFontWeightBold];
    titleLabel.textColor = [UIColor blackColor];
    [self.view addSubview:titleLabel];
    
    UILabel *subtitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 200, self.view.bounds.size.width - 40, 20)];
    subtitleLabel.text = @"带价格标签和边界处理";
    subtitleLabel.font = [UIFont systemFontOfSize:14 weight:UIFontWeightRegular];
    subtitleLabel.textColor = [UIColor grayColor];
    [self.view addSubview:subtitleLabel];
    
    // 准备价格数据
    CGFloat currentPrice = 49.95;
    NSMutableArray *dataSource = [NSMutableArray array];
    
    for (NSInteger i = 1; i <= 100; i++) {
        LBGenericScrollViewCellModel *model = [[LBGenericScrollViewCellModel alloc] init];
        
        CGFloat price = (CGFloat)i;
        CGFloat percent = ((price - currentPrice) / currentPrice) * 100;
        
        // 未选中状态的富文本（浅灰色）
        NSMutableAttributedString *normalText = [[NSMutableAttributedString alloc] init];
        
        // 第一行：价格 (14 regular)
        [normalText appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"%.0f\n", price]
                                                                            attributes:@{
            NSFontAttributeName: [UIFont systemFontOfSize:14 weight:UIFontWeightRegular],
            NSForegroundColorAttributeName: [UIColor colorWithWhite:0.6 alpha:1.0]
        }]];
        
        // 第二行：百分比 (12 regular)
        [normalText appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"+%.1f%%", percent]
                                                                            attributes:@{
            NSFontAttributeName: [UIFont systemFontOfSize:12 weight:UIFontWeightRegular],
            NSForegroundColorAttributeName: [UIColor colorWithWhite:0.7 alpha:1.0]
        }]];
        
        model.normalAttributedText = normalText;
        
        // 选中状态的富文本（黑色和橙色）
        NSMutableAttributedString *selectedText = [[NSMutableAttributedString alloc] init];
        
        // 第一行：价格 (16 medium)
        [selectedText appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"%.0f\n", price]
                                                                              attributes:@{
            NSFontAttributeName: [UIFont systemFontOfSize:16 weight:UIFontWeightMedium],
            NSForegroundColorAttributeName: [UIColor blackColor]
        }]];
        
        // 第二行：百分比 (14 medium)
        [selectedText appendAttributedString:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:@"+%.1f%%", percent]
                                                                              attributes:@{
            NSFontAttributeName: [UIFont systemFontOfSize:14 weight:UIFontWeightMedium],
            NSForegroundColorAttributeName: [UIColor colorWithRed:1.0 green:0.4 blue:0.2 alpha:1.0]
        }]];
        
        model.selectedAttributedText = selectedText;
        
        // 设置价格
        model.price = price;
        
        [dataSource addObject:model];
    }
    
    // 创建价格滚动视图
    _priceScrollView = [[LBPriceScrollView alloc] initWithCurrentPrice:currentPrice];
    _priceScrollView.frame = CGRectMake(0, 230, self.view.bounds.size.width, 200);
    _priceScrollView.dataSource = dataSource;  // 使用父类的 dataSource 属性
    [self.view addSubview:_priceScrollView];
}

- (void)setupRandomPriceButton {
    // 创建按钮
    _randomPriceButton = [UIButton buttonWithType:UIButtonTypeSystem];
    _randomPriceButton.frame = CGRectMake(20, 80, self.view.bounds.size.width - 40, 50);
    _randomPriceButton.backgroundColor = [UIColor colorWithRed:0.0 green:0.48 blue:1.0 alpha:1.0];
    _randomPriceButton.layer.cornerRadius = 10;

    [_randomPriceButton setTitle:@"随机切换价格 (1-100)" forState:UIControlStateNormal];
    [_randomPriceButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    _randomPriceButton.titleLabel.font = [UIFont systemFontOfSize:18 weight:UIFontWeightSemibold];

    [_randomPriceButton addTarget:self action:@selector(randomPriceButtonTapped) forControlEvents:UIControlEventTouchUpInside];

    [self.view addSubview:_randomPriceButton];
}

- (void)randomPriceButtonTapped {
    // 生成 1-100 之间的随机数（保留两位小数）
    CGFloat randomPrice = (arc4random_uniform(10000) / 100.0) + 1.0;  // 1.00 - 100.99

    // 也可以使用整数价格：
    // CGFloat randomPrice = (CGFloat)(arc4random_uniform(100) + 1);  // 1 - 100

    NSLog(@"随机生成新价格: %.2f", randomPrice);

    // 更新价格
    [_priceScrollView updateCurrentPrice:randomPrice];
}

@end

