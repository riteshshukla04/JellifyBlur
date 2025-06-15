package com.riteshshukla.jellifyblur

import android.graphics.Color
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.JellifyBlurViewManagerInterface
import com.facebook.react.viewmanagers.JellifyBlurViewManagerDelegate

@ReactModule(name = JellifyBlurViewManager.NAME)
class JellifyBlurViewManager : ViewGroupManager<JellifyBlurView>(),
  JellifyBlurViewManagerInterface<JellifyBlurView> {
  private val mDelegate: ViewManagerDelegate<JellifyBlurView>

  init {
    mDelegate = JellifyBlurViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<JellifyBlurView>? {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): JellifyBlurView {
    return JellifyBlurView(context)
  }

  @ReactProp(name = "blurType")
  override fun setBlurType(view: JellifyBlurView?, blurType: String?) {
    view?.setBlurType(blurType ?: "regular")
  }

  @ReactProp(name = "blurAmount")
  override fun setBlurAmount(view: JellifyBlurView?, blurAmount: Double) {
    view?.setBlurAmount(blurAmount.toFloat())
  }

  @ReactProp(name = "reducedTransparencyFallbackColor")
  override fun setReducedTransparencyFallbackColor(view: JellifyBlurView?, color: String?) {
    view?.setReducedTransparencyFallbackColor(color ?: "#FFFFFF")
  }

  // Legacy color prop support for backward compatibility
  @ReactProp(name = "color")
  fun setColorProp(view: JellifyBlurView?, color: String?) {
    // This is kept for backward compatibility but blur properties take precedence
    color?.let {
      try {
        view?.setBackgroundColor(Color.parseColor(it))
      } catch (e: IllegalArgumentException) {
        // Ignore invalid colors
      }
    }
  }

  companion object {
    const val NAME = "JellifyBlurView"
  }
}
