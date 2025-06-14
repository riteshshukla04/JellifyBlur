import React, { useState } from 'react';
import {
  View,
  StyleSheet,
  Text,
  ScrollView,
  Image,
  TouchableOpacity,
  SafeAreaView,
  StatusBar,
} from 'react-native';
import { JellifyBlurView, type BlurType } from 'jellify-blur';

const blurTypes: BlurType[] = [
  'light',
  'extraLight',
  'dark',
  'regular',
  'prominent',
  'systemUltraThinMaterial',
  'systemThinMaterial',
  'systemMaterial',
  'systemThickMaterial',
  'systemChromeMaterial',
];

export default function App() {
  const [selectedBlurType, setSelectedBlurType] = useState<BlurType>('regular');
  const [blurAmount, setBlurAmount] = useState(100);

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" />

      {/* Background Image */}
      <Image
        source={{
          uri: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80',
        }}
        style={styles.backgroundImage}
        resizeMode="cover"
      />

      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Text style={styles.title}>JellifyBlur Demo</Text>

        {/* Main Blur Demo */}
        <View style={styles.demoContainer}>
          <JellifyBlurView
            blurType={selectedBlurType}
            blurAmount={blurAmount}
            style={styles.mainBlurView}
            reducedTransparencyFallbackColor="#FFFFFF"
          >
            <View style={styles.blurContent}>
              <Text style={styles.blurContentTitle}>
                {selectedBlurType.charAt(0).toUpperCase() +
                  selectedBlurType.slice(1)}
              </Text>
              <Text style={styles.blurContentSubtitle}>
                Blur Amount: {blurAmount}%
              </Text>
            </View>
          </JellifyBlurView>
        </View>

        {/* Blur Type Selection */}
        <View style={styles.controlsContainer}>
          <Text style={styles.controlsTitle}>Blur Types</Text>
          <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            <View style={styles.blurTypeContainer}>
              {blurTypes.map((type) => (
                <TouchableOpacity
                  key={type}
                  style={[
                    styles.blurTypeButton,
                    selectedBlurType === type && styles.selectedBlurType,
                  ]}
                  onPress={() => setSelectedBlurType(type)}
                >
                  <JellifyBlurView blurType={type} style={styles.smallBlurView}>
                    <View style={styles.smallBlurContent}>
                      <Text style={styles.smallBlurText} numberOfLines={2}>
                        {type}
                      </Text>
                    </View>
                  </JellifyBlurView>
                </TouchableOpacity>
              ))}
            </View>
          </ScrollView>
        </View>

        {/* Blur Amount Control */}
        <View style={styles.controlsContainer}>
          <Text style={styles.controlsTitle}>Blur Amount</Text>
          <View style={styles.amountContainer}>
            {[25, 50, 75, 100].map((amount) => (
              <TouchableOpacity
                key={amount}
                style={[
                  styles.amountButton,
                  blurAmount === amount && styles.selectedAmount,
                ]}
                onPress={() => setBlurAmount(amount)}
              >
                <JellifyBlurView
                  blurType="regular"
                  blurAmount={amount}
                  style={styles.amountBlurView}
                >
                  <Text style={styles.amountText}>{amount}%</Text>
                </JellifyBlurView>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Overlay Example */}
        <View style={styles.overlayExample}>
          <Text style={styles.overlayTitle}>Overlay Example</Text>
          <View style={styles.overlayContainer}>
            <Text style={styles.overlayBackgroundText}>
              Background Content Here
            </Text>
            <JellifyBlurView
              blurType="systemMaterial"
              style={styles.overlayBlur}
            >
              <Text style={styles.overlayText}>Blurred Overlay</Text>
            </JellifyBlurView>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  backgroundImage: {
    position: 'absolute',
    width: '100%',
    height: '100%',
  },
  scrollContent: {
    padding: 20,
    paddingBottom: 40,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: 'white',
    textAlign: 'center',
    marginBottom: 30,
    textShadowColor: 'rgba(0,0,0,0.3)',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 3,
  },
  demoContainer: {
    alignItems: 'center',
    marginBottom: 30,
  },
  mainBlurView: {
    width: 280,
    height: 200,
    borderRadius: 20,
    overflow: 'hidden',
  },
  blurContent: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  blurContentTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
    textAlign: 'center',
  },
  blurContentSubtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
  },
  controlsContainer: {
    marginBottom: 30,
  },
  controlsTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: 'white',
    marginBottom: 15,
    textShadowColor: 'rgba(0,0,0,0.3)',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 2,
  },
  blurTypeContainer: {
    flexDirection: 'row',
    gap: 12,
  },
  blurTypeButton: {
    borderRadius: 12,
    overflow: 'hidden',
  },
  selectedBlurType: {
    transform: [{ scale: 1.05 }],
  },
  smallBlurView: {
    width: 100,
    height: 80,
  },
  smallBlurContent: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 8,
  },
  smallBlurText: {
    fontSize: 12,
    fontWeight: '500',
    color: '#333',
    textAlign: 'center',
  },
  amountContainer: {
    flexDirection: 'row',
    gap: 15,
    justifyContent: 'center',
  },
  amountButton: {
    borderRadius: 12,
    overflow: 'hidden',
  },
  selectedAmount: {
    transform: [{ scale: 1.1 }],
  },
  amountBlurView: {
    width: 70,
    height: 70,
    justifyContent: 'center',
    alignItems: 'center',
  },
  amountText: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#333',
  },
  overlayExample: {
    marginTop: 20,
  },
  overlayTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: 'white',
    marginBottom: 15,
    textShadowColor: 'rgba(0,0,0,0.3)',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 2,
  },
  overlayContainer: {
    height: 120,
    backgroundColor: 'rgba(255,255,255,0.1)',
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
    position: 'relative',
  },
  overlayBackgroundText: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'white',
    textShadowColor: 'rgba(0,0,0,0.3)',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 3,
  },
  overlayBlur: {
    position: 'absolute',
    width: '80%',
    height: '60%',
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
  },
  overlayText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
  },
});
