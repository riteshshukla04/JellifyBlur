#import "JellifyBlurView.h"

#import <react/renderer/components/JellifyBlurViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/JellifyBlurViewSpec/EventEmitters.h>
#import <react/renderer/components/JellifyBlurViewSpec/Props.h>
#import <react/renderer/components/JellifyBlurViewSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface JellifyBlurView () <RCTJellifyBlurViewViewProtocol>

@end

@implementation JellifyBlurView {
    UIView * _view;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<JellifyBlurViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const JellifyBlurViewProps>();
    _props = defaultProps;

    _view = [[UIView alloc] init];
    _view.clipsToBounds = YES;
    
    // Initialize with default blur effect
    UIBlurEffect *blurEffect = [UIBlurEffect effectWithStyle:UIBlurEffectStyleRegular];
    _blurEffectView = [[UIVisualEffectView alloc] initWithEffect:blurEffect];
    _blurEffectView.frame = _view.bounds;
    _blurEffectView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    
    [_view addSubview:_blurEffectView];

    self.contentView = _view;
  }

  return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<JellifyBlurViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<JellifyBlurViewProps const>(props);

    // Update blur type
    if (oldViewProps.blurType != newViewProps.blurType) {
        NSString *blurTypeString = [[NSString alloc] initWithUTF8String: newViewProps.blurType.c_str()];
        [self updateBlurType:blurTypeString];
    }
    
    // Update blur amount
    if (oldViewProps.blurAmount != newViewProps.blurAmount) {
        [self updateBlurAmount:newViewProps.blurAmount];
    }
    
    // Update fallback color for reduced transparency
    if (oldViewProps.reducedTransparencyFallbackColor != newViewProps.reducedTransparencyFallbackColor) {
        NSString *colorString = [[NSString alloc] initWithUTF8String: newViewProps.reducedTransparencyFallbackColor.c_str()];
        [self updateReducedTransparencyFallbackColor:colorString];
    }

    [super updateProps:props oldProps:oldProps];
}

- (void)updateBlurType:(NSString *)blurType
{
    UIBlurEffect *blurEffect = [self blurEffectForType:blurType];
    if (blurEffect) {
        _blurEffectView.effect = blurEffect;
        _blurType = blurType;
    }
}

- (void)updateBlurAmount:(CGFloat)blurAmount
{
    _blurAmount = blurAmount;
    // Note: iOS doesn't have a direct API to control blur amount
    // We can simulate it by adjusting the alpha of the blur view
    _blurEffectView.alpha = MAX(0.0, MIN(1.0, blurAmount / 100.0));
}

- (void)updateReducedTransparencyFallbackColor:(NSString *)colorString
{
    if (UIAccessibilityIsReduceTransparencyEnabled()) {
        UIColor *fallbackColor = [self colorFromHexString:colorString];
        if (fallbackColor) {
            _view.backgroundColor = fallbackColor;
            _blurEffectView.hidden = YES;
        }
    } else {
        _view.backgroundColor = [UIColor clearColor];
        _blurEffectView.hidden = NO;
    }
    _reducedTransparencyFallbackColor = colorString;
}

- (UIBlurEffect *)blurEffectForType:(NSString *)type
{
    if ([type isEqualToString:@"light"]) {
        return [UIBlurEffect effectWithStyle:UIBlurEffectStyleLight];
    } else if ([type isEqualToString:@"extraLight"]) {
        return [UIBlurEffect effectWithStyle:UIBlurEffectStyleExtraLight];
    } else if ([type isEqualToString:@"dark"]) {
        return [UIBlurEffect effectWithStyle:UIBlurEffectStyleDark];
    } else if ([type isEqualToString:@"regular"]) {
        return [UIBlurEffect effectWithStyle:UIBlurEffectStyleRegular];
    } else if ([type isEqualToString:@"prominent"]) {
        return [UIBlurEffect effectWithStyle:UIBlurEffectStyleProminent];
    } else if ([type isEqualToString:@"systemUltraThinMaterial"]) {
        if (@available(iOS 13.0, *)) {
            return [UIBlurEffect effectWithStyle:UIBlurEffectStyleSystemUltraThinMaterial];
        }
    } else if ([type isEqualToString:@"systemThinMaterial"]) {
        if (@available(iOS 13.0, *)) {
            return [UIBlurEffect effectWithStyle:UIBlurEffectStyleSystemThinMaterial];
        }
    } else if ([type isEqualToString:@"systemMaterial"]) {
        if (@available(iOS 13.0, *)) {
            return [UIBlurEffect effectWithStyle:UIBlurEffectStyleSystemMaterial];
        }
    } else if ([type isEqualToString:@"systemThickMaterial"]) {
        if (@available(iOS 13.0, *)) {
            return [UIBlurEffect effectWithStyle:UIBlurEffectStyleSystemThickMaterial];
        }
    } else if ([type isEqualToString:@"systemChromeMaterial"]) {
        if (@available(iOS 13.0, *)) {
            return [UIBlurEffect effectWithStyle:UIBlurEffectStyleSystemChromeMaterial];
        }
    }
    
    // Default fallback
    return [UIBlurEffect effectWithStyle:UIBlurEffectStyleRegular];
}

- (UIColor *)colorFromHexString:(NSString *)hexString
{
    if (!hexString || hexString.length == 0) {
        return nil;
    }
    
    NSString *cleanString = [hexString stringByReplacingOccurrencesOfString:@"#" withString:@""];
    if (cleanString.length != 6) {
        return nil;
    }
    
    NSScanner *scanner = [NSScanner scannerWithString:cleanString];
    unsigned hexNumber;
    if (![scanner scanHexInt:&hexNumber]) {
        return nil;
    }
    
    int r = (hexNumber >> 16) & 0xFF;
    int g = (hexNumber >> 8) & 0xFF;
    int b = hexNumber & 0xFF;
    
    return [UIColor colorWithRed:r / 255.0f green:g / 255.0f blue:b / 255.0f alpha:1.0f];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    _blurEffectView.frame = _view.bounds;
}

Class<RCTComponentViewProtocol> JellifyBlurViewCls(void)
{
    return JellifyBlurView.class;
}

@end
