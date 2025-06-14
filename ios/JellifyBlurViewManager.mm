#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import "RCTBridge.h"

@interface JellifyBlurViewManager : RCTViewManager
@end

@implementation JellifyBlurViewManager

RCT_EXPORT_MODULE(JellifyBlurView)

- (UIView *)view
{
  return [[UIView alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(color, NSString)

@end
