import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ViewProps } from 'react-native';
import type { Double } from 'react-native/Libraries/Types/CodegenTypes';

export type BlurType = 
  | 'light' 
  | 'extraLight' 
  | 'dark' 
  | 'regular' 
  | 'prominent' 
  | 'systemUltraThinMaterial'
  | 'systemThinMaterial'
  | 'systemMaterial'
  | 'systemThickMaterial'
  | 'systemChromeMaterial';

export interface NativeProps extends ViewProps {
  blurType?: string;
  blurAmount?: Double;
  reducedTransparencyFallbackColor?: string;
}

export default codegenNativeComponent<NativeProps>('JellifyBlurView');
