# JellifyBlur

A powerful React Native library that provides native blur effects for iOS using UIVisualEffectView. Create beautiful, performant blur overlays with various blur types and customization options.

## Features

- 🎨 **Multiple Blur Types**: Support for 10+ different iOS blur effects
- ⚡ **Native Performance**: Uses UIVisualEffectView for optimal performance
- 🔧 **Customizable**: Adjustable blur amount and fallback colors
- ♿ **Accessibility**: Respects reduced transparency settings
- 📱 **iOS Focused**: Designed specifically for iOS with native blur effects

## Installation

```sh
npm install jellify-blur
```

Or with yarn:

```sh
yarn add jellify-blur
```

### iOS Setup

For iOS, no additional setup is required as the library uses native UIVisualEffectView.

## Usage

### Basic Usage

```tsx
import React from 'react';
import { View, Text } from 'react-native';
import { JellifyBlurView } from 'jellify-blur';

export default function App() {
  return (
    <View style={{ flex: 1 }}>
      <JellifyBlurView 
        blurType="regular"
        style={{ 
          position: 'absolute',
          top: 100,
          left: 20,
          right: 20,
          height: 200,
          borderRadius: 20 
        }}
      >
        <Text style={{ textAlign: 'center', marginTop: 50 }}>
          Content on blur background
        </Text>
      </JellifyBlurView>
    </View>
  );
}
```

### Advanced Usage with Different Blur Types

```tsx
import React from 'react';
import { JellifyBlurView, BlurType } from 'jellify-blur';

const blurTypes: BlurType[] = [
  'light',
  'dark', 
  'regular',
  'systemMaterial',
  'systemThickMaterial'
];

export function BlurDemo() {
  return (
    <View>
      {blurTypes.map((type) => (
        <JellifyBlurView
          key={type}
          blurType={type}
          blurAmount={80}
          reducedTransparencyFallbackColor="#FFFFFF"
          style={{ 
            height: 100, 
            margin: 10, 
            borderRadius: 15 
          }}
        >
          <Text>{type} blur effect</Text>
        </JellifyBlurView>
      ))}
    </View>
  );
}
```

## API Reference

### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `blurType` | `BlurType` | `'regular'` | The type of blur effect to apply |
| `blurAmount` | `number` | `100` | Blur intensity from 0-100 (controls opacity) |
| `reducedTransparencyFallbackColor` | `string` | `'#FFFFFF'` | Fallback color when reduced transparency is enabled |
| `style` | `ViewStyle` | `undefined` | Style object for the blur view |
| `children` | `ReactNode` | `undefined` | Child components to render on top of blur |

### Blur Types

The following blur types are available:

#### Standard Blur Effects
- `'light'` - Light blur effect
- `'extraLight'` - Extra light blur effect  
- `'dark'` - Dark blur effect
- `'regular'` - Regular blur effect
- `'prominent'` - Prominent blur effect

#### System Material Effects (iOS 13+)
- `'systemUltraThinMaterial'` - Ultra-thin system material
- `'systemThinMaterial'` - Thin system material
- `'systemMaterial'` - Standard system material
- `'systemThickMaterial'` - Thick system material
- `'systemChromeMaterial'` - Chrome system material

## Examples

### Overlay Modal

```tsx
<JellifyBlurView 
  blurType="systemMaterial"
  style={StyleSheet.absoluteFill}
>
  <View style={styles.modalContent}>
    <Text>Modal Content</Text>
  </View>
</JellifyBlurView>
```

### Card with Blur Background

```tsx
<JellifyBlurView 
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
</JellifyBlurView>
```

### Navigation Bar Blur

```tsx
<JellifyBlurView 
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
</JellifyBlurView>
```

## Accessibility

The library automatically respects the system's "Reduce Transparency" accessibility setting. When enabled, the blur effect is replaced with the specified `reducedTransparencyFallbackColor`.

## Platform Support

- ✅ iOS 10.0+
- ❌ Android (not supported - iOS specific blur effects)

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
