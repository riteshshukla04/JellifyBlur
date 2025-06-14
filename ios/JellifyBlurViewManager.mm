#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import "RCTBridge.h"
#import "JellifyBlurView.h"

@interface JellifyBlurViewManager : RCTViewManager
@end

@implementation JellifyBlurViewManager

RCT_EXPORT_MODULE(JellifyBlurView)

- (UIView *)view
{
  return [[JellifyBlurView alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(blurType, NSString)
RCT_EXPORT_VIEW_PROPERTY(blurAmount, CGFloat)
RCT_EXPORT_VIEW_PROPERTY(reducedTransparencyFallbackColor, NSString)

@end
