#import <React/RCTViewComponentView.h>
#import <UIKit/UIKit.h>

#ifndef JellifyBlurViewNativeComponent_h
#define JellifyBlurViewNativeComponent_h

NS_ASSUME_NONNULL_BEGIN

@interface JellifyBlurView : RCTViewComponentView

@property (nonatomic, strong) UIVisualEffectView *blurEffectView;
@property (nonatomic, strong) NSString *blurType;
@property (nonatomic, assign) CGFloat blurAmount;
@property (nonatomic, strong) NSString *reducedTransparencyFallbackColor;

- (UIBlurEffect *)blurEffectForType:(NSString *)type;
- (UIColor *)colorFromHexString:(NSString *)hexString;

@end

NS_ASSUME_NONNULL_END

#endif /* JellifyBlurViewNativeComponent_h */
