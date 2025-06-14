package com.jellifyblur

import android.graphics.Color
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.JellifyBlurViewManagerInterface
import com.facebook.react.viewmanagers.JellifyBlurViewManagerDelegate

@ReactModule(name = JellifyBlurViewManager.NAME)
class JellifyBlurViewManager : SimpleViewManager<JellifyBlurView>(),
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

  @ReactProp(name = "color")
  override fun setColor(view: JellifyBlurView?, color: String?) {
    view?.setBackgroundColor(Color.parseColor(color))
  }

  companion object {
    const val NAME = "JellifyBlurView"
  }
}
