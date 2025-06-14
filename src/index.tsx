import React from 'react';
import type { ViewStyle } from 'react-native';
import JellifyBlurViewNativeComponent, { type BlurType } from './JellifyBlurViewNativeComponent';

export type { BlurType } from './JellifyBlurViewNativeComponent';

export interface JellifyBlurViewProps {
  /**
   * The blur effect type to apply
   * @default 'regular'
   */
  blurType?: BlurType;
  
  /**
   * The blur amount/intensity (0-100)
   * @default 100
   */
  blurAmount?: number;
  
  /**
   * Fallback color when reduced transparency is enabled
   * @default '#FFFFFF'
   */
  reducedTransparencyFallbackColor?: string;
  
  /**
   * Style applied to the blur view
   */
  style?: ViewStyle;
  
  /**
   * Child components to render on top of the blur
   */
  children?: React.ReactNode;
}

export const JellifyBlurView: React.FC<JellifyBlurViewProps> = ({
  blurType = 'regular',
  blurAmount = 100,
  reducedTransparencyFallbackColor = '#FFFFFF',
  style,
  children,
  ...props
}) => {
  return (
    <JellifyBlurViewNativeComponent
      blurType={blurType}
      blurAmount={blurAmount}
      reducedTransparencyFallbackColor={reducedTransparencyFallbackColor}
      style={style}
      {...props}
    >
      {children}
    </JellifyBlurViewNativeComponent>
  );
};

export default JellifyBlurView;
