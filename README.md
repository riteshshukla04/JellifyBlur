# blur-react-native

A powerful React Native library that provides native blur effects for both iOS and Android. Create beautiful, performant blur overlays with various blur types and customization options.

**⚠️ New Architecture Only**: This library requires React Native 0.76+ with the New Architecture (Fabric) enabled.

## Features

- 🎨 **Multiple Blur Types**: Support for 10+ different blur effects on both platforms
- ⚡ **Native Performance**: Uses UIVisualEffectView on iOS and hardware-accelerated blur on Android
- 🔧 **Customizable**: Adjustable blur amount and fallback colors
- ♿ **Accessibility**: Respects reduced transparency settings
- 📱 **Cross-Platform**: Works on both iOS and Android with platform-specific optimizations
- 🚀 **New Architecture**: Built exclusively for React Native's New Architecture (Fabric)

## Requirements

- **React Native 0.76 or higher**
- **New Architecture (Fabric) enabled**
- iOS 13.0 or higher
- Android API 21 (Android 5.0) or higher

## Installation

```sh
npm install blur-react-native
```

Or with yarn:

```sh
yarn add blur-react-native
```

### iOS Setup

The library will be automatically linked with React Native 0.76+. You just need to install the pods:

```sh
cd ios && pod install
```

### Android Setup

The library will be automatically linked with React Native 0.76+. No additional setup is required.

#### Android Technical Stack

The library automatically chooses the best blur implementation based on the Android API level:

- **Android 12+ (API 31+)**: Uses `RenderEffect` for hardware-accelerated blur
- **Android 5.0+ (API 21+)**: Uses `RenderScript` with `ScriptIntrinsicBlur` for GPU-accelerated blur
- **Fallback**: Uses optimized software blur with multi-pass box blur algorithm
- **Performance Optimizations**: Background threading, bitmap caching, and memory management

## Usage

### Basic Usage

```tsx
import React from 'react';
import { View, Text } from 'react-native';
import { BlurView } from 'blur-react-native';

export default function App() {
  return (
    <View style={{ flex: 1 }}>
      <BlurView
        blurType="regular"
        style={{
          position: 'absolute',
          top: 100,
          left: 20,
          right: 20,
          height: 200,
          borderRadius: 20,
        }}
      >
        <Text style={{ textAlign: 'center', marginTop: 50 }}>
          Content on blur background
        </Text>
      </BlurView>
    </View>
  );
}
```

### Advanced Usage with Different Blur Types

```tsx
import React from 'react';
import { BlurView, BlurType } from 'blur-react-native';

const blurTypes: BlurType[] = [
  'light',
  'dark',
  'regular',
  'systemMaterial',
  'systemThickMaterial',
];

export function BlurDemo() {
  return (
    <View>
      {blurTypes.map((type) => (
        <BlurView
          key={type}
          blurType={type}
          blurAmount={80}
          reducedTransparencyFallbackColor="#FFFFFF"
          style={{
            height: 100,
            margin: 10,
            borderRadius: 15,
          }}
        >
          <Text>{type} blur effect</Text>
        </BlurView>
      ))}
    </View>
  );
}
```

## API Reference

### Props

| Prop                               | Type        | Default     | Description                                         |
| ---------------------------------- | ----------- | ----------- | --------------------------------------------------- |
| `blurType`                         | `BlurType`  | `'regular'` | The type of blur effect to apply                    |
| `blurAmount`                       | `number`    | `100`       | Blur intensity from 0-100                           |
| `reducedTransparencyFallbackColor` | `string`    | `'#FFFFFF'` | Fallback color when reduced transparency is enabled |
| `style`                            | `ViewStyle` | `undefined` | Style object for the blur view                      |
| `children`                         | `ReactNode` | `undefined` | Child components to render on top of blur           |

### Blur Types

The following blur types are available on both platforms:

#### Standard Blur Effects

- `'light'` - Light blur effect with white tint
- `'extraLight'` - Extra light blur effect with minimal tint
- `'dark'` - Dark blur effect with black tint
- `'regular'` - Standard blur effect with neutral tint
- `'prominent'` - Enhanced blur effect with stronger material appearance

#### System Material Effects (iOS 13+, Android equivalent)

- `'systemUltraThinMaterial'` - Ultra-thin system material
- `'systemThinMaterial'` - Thin system material
- `'systemMaterial'` - Standard system material
- `'systemThickMaterial'` - Thick system material
- `'systemChromeMaterial'` - Chrome system material

## Examples

### Overlay Modal

```tsx
<BlurView blurType="systemMaterial" style={StyleSheet.absoluteFill}>
  <View style={styles.modalContent}>
    <Text>Modal Content</Text>
  </View>
</BlurView>
```

### Card with Blur Background

```tsx
<BlurView
  blurType="systemThickMaterial"
  blurAmount={90}
  style={{
    padding: 20,
    borderRadius: 16,
    margin: 16,
  }}
>
  <Text style={styles.cardTitle}>Card Title</Text>
  <Text style={styles.cardContent}>Content here</Text>
</BlurView>
```

### Navigation Bar Blur

```tsx
<BlurView
  blurType="systemChromeMaterial"
  style={{
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 100,
    paddingTop: 50,
  }}
>
  <Text style={styles.navTitle}>Navigation Title</Text>
</BlurView>
```

## Performance

### iOS Performance

- **Native UIVisualEffectView**: Hardware-accelerated blur with optimal battery usage
- **System-level Optimization**: Leverages iOS's built-in blur implementation for optimal performance
- **Automatic Optimization**: Adapts to device capabilities and power state

### Android Performance

- **Modern Hardware Acceleration**: Uses `RenderEffect` on Android 12+ (API 31+)
- **RenderScript GPU Acceleration**: Uses `ScriptIntrinsicBlur` on Android 5.0+ (API 21-30)
- **Multi-threaded Processing**: Background blur calculation to prevent UI blocking
- **Memory Management**: Automatic bitmap recycling and memory optimization
- **Caching**: Intelligent blur result caching for repeated operations
- **Fallback Support**: Optimized software blur for older/unsupported devices

## Accessibility

The library automatically respects the system's "Reduce Transparency" accessibility setting:

- **iOS**: Automatically detects `UIAccessibilityIsReduceTransparencyEnabled()`
- **Android**: Provides fallback color support for accessibility preferences
- **Customizable**: Use `reducedTransparencyFallbackColor` to set your preferred fallback

## Platform Support

- ✅ **iOS 13.0+**: Native UIVisualEffectView blur effects
- ✅ **Android 5.0+ (API 21+)**: RenderScript hardware-accelerated blur
- ✅ **Android 12+ (API 31+)**: Modern RenderEffect blur
- ✅ **React Native 0.76+**: New Architecture (Fabric) support only

### Platform-Specific Implementation Details

**iOS:**

- Uses native `UIVisualEffectView` for optimal performance and battery life
- All blur types supported with system-native appearance
- Automatic dark/light mode adaptation
- Leverages iOS's built-in hardware acceleration through system APIs

**Android:**

- **API 31+**: `android.graphics.RenderEffect` for hardware-accelerated blur
- **API 21-30**: `RenderScript` with `ScriptIntrinsicBlur` for GPU acceleration
- **Fallback**: Optimized multi-pass box blur algorithm for older devices
- Background processing with `HandlerThread` for smooth UI
- LRU bitmap caching for performance optimization
- Automatic memory management and cleanup

## Troubleshooting

### Common Issues

**New Architecture not enabled:**

This library requires React Native's New Architecture (Fabric). Ensure you have:
- React Native 0.76 or higher
- New Architecture enabled in your app

**Pod install fails:**

```sh
cd ios && rm -rf Pods Podfile.lock && pod install --repo-update
```

**Build errors:**

- Ensure iOS deployment target is 13.0 or higher
- Ensure Android `minSdkVersion` is 21 or higher
- Ensure `compileSdkVersion` is 31 or higher for modern blur effects
- Clean build folder: Product → Clean Build Folder in Xcode

**Performance issues:**

- The library automatically optimizes for device capabilities
- On older devices, reduce `blurAmount` for better performance

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

> **Disclaimer**: Most of this README and code was written by vibe coding™ 🎵✨ - if you find a bug, please raise a PR! We promise it's more fun than debugging alone 😄

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
