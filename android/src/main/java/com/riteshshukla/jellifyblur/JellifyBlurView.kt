package com.riteshshukla.jellifyblur

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class JellifyBlurView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // Properties matching iOS implementation
    private var blurType: String = "regular"
    private var blurAmount: Float = 100f
    private var reducedTransparencyFallbackColor: String = "#FFFFFF"
    
    // Blur parameters
    private var blurRadius: Float = 25f
    private var overlayColor: Int = Color.TRANSPARENT
    private var isBlurEnabled: Boolean = true
    
    // Background capture and blur
    private var backgroundBitmap: Bitmap? = null
    private var blurredBitmap: Bitmap? = null
    private var needsUpdate = true
    
    // Paint objects for rendering
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    
    // RenderScript components
    private var renderScript: RenderScript? = null
    private var blurScript: ScriptIntrinsicBlur? = null
    
    init {
        setWillNotDraw(false)
        initializeBlurComponents()
        updateBlurParameters()
        
        // Make the view transparent to see the blur effect
        setBackgroundColor(Color.TRANSPARENT)
    }

    private fun initializeBlurComponents() {
        // Initialize RenderScript for blur operations
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            try {
                renderScript = RenderScript.create(context)
                blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
            } catch (e: Exception) {
                // RenderScript failed, will use software blur
                renderScript = null
                blurScript = null
            }
        }
        
        // Configure paint objects
        overlayPaint.isAntiAlias = true
        blurPaint.isAntiAlias = true
        blurPaint.isFilterBitmap = true
    }

    fun setBlurType(type: String) {
        if (blurType != type) {
            blurType = type
            updateBlurParameters()
            needsUpdate = true
            invalidate()
        }
    }

    fun setBlurAmount(amount: Float) {
        val clampedAmount = max(0f, min(100f, amount))
        if (blurAmount != clampedAmount) {
            blurAmount = clampedAmount
            updateBlurParameters()
            needsUpdate = true
            invalidate()
        }
    }

    fun setReducedTransparencyFallbackColor(colorString: String) {
        if (reducedTransparencyFallbackColor != colorString) {
            reducedTransparencyFallbackColor = colorString
            if (isReducedTransparencyEnabled()) {
                setBackgroundColor(parseColor(colorString))
                isBlurEnabled = false
            } else {
                setBackgroundColor(Color.TRANSPARENT)
                isBlurEnabled = true
            }
            needsUpdate = true
            invalidate()
        }
    }

    private fun updateBlurParameters() {
        val intensity = blurAmount / 100f
        
        // Map blur types to visual parameters that match iOS
        when (blurType) {
            "light" -> {
                blurRadius = intensity * 8f
                overlayColor = Color.argb((0.7f * intensity * 255).toInt(), 255, 255, 255)
            }
            "extraLight" -> {
                blurRadius = intensity * 6f
                overlayColor = Color.argb((0.85f * intensity * 255).toInt(), 255, 255, 255)
            }
            "dark" -> {
                blurRadius = intensity * 10f
                overlayColor = Color.argb((0.7f * intensity * 255).toInt(), 20, 20, 20)
            }
            "regular" -> {
                blurRadius = intensity * 8f
                overlayColor = Color.argb((0.4f * intensity * 255).toInt(), 255, 255, 255)
            }
            "prominent" -> {
                blurRadius = intensity * 12f
                overlayColor = Color.argb((0.5f * intensity * 255).toInt(), 240, 240, 240)
            }
            "systemUltraThinMaterial" -> {
                blurRadius = intensity * 4f
                overlayColor = Color.argb((0.2f * intensity * 255).toInt(), 250, 250, 250)
            }
            "systemThinMaterial" -> {
                blurRadius = intensity * 6f
                overlayColor = Color.argb((0.35f * intensity * 255).toInt(), 245, 245, 245)
            }
            "systemMaterial" -> {
                blurRadius = intensity * 8f
                overlayColor = Color.argb((0.5f * intensity * 255).toInt(), 240, 240, 240)
            }
            "systemThickMaterial" -> {
                blurRadius = intensity * 12f
                overlayColor = Color.argb((0.65f * intensity * 255).toInt(), 235, 235, 235)
            }
            "systemChromeMaterial" -> {
                blurRadius = intensity * 10f
                overlayColor = Color.argb((0.8f * intensity * 255).toInt(), 248, 248, 248)
            }
            else -> {
                // Default to regular
                blurRadius = intensity * 8f
                overlayColor = Color.argb((0.4f * intensity * 255).toInt(), 255, 255, 255)
            }
        }
        
        // Ensure valid blur radius
        blurRadius = max(0.1f, min(blurRadius, 25f))
        overlayPaint.color = overlayColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        needsUpdate = true
    }

    override fun onDraw(canvas: Canvas) {
        if (!isBlurEnabled || blurAmount <= 0f) {
            super.onDraw(canvas)
            return
        }

        // Use modern RenderEffect for API 31+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            drawWithRenderEffect(canvas)
        } else {
            // Use background blur for older versions
            drawWithBackgroundBlur(canvas)
        }
        
        super.onDraw(canvas)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun drawWithRenderEffect(canvas: Canvas) {
        // Apply RenderEffect for hardware-accelerated blur
        if (blurRadius > 0f) {
            val renderEffect = android.graphics.RenderEffect.createBlurEffect(
                blurRadius, blurRadius, android.graphics.Shader.TileMode.CLAMP
            )
            setRenderEffect(renderEffect)
        }
        
        // Draw material overlay
        if (Color.alpha(overlayColor) > 0) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
        }
    }

    private fun drawWithBackgroundBlur(canvas: Canvas) {
        // Update blurred background if needed
        if (needsUpdate) {
            updateBlurredBackground()
            needsUpdate = false
        }
        
        // Draw the blurred background
        blurredBitmap?.let { bitmap ->
            // Scale the bitmap to fit the view
            val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
            val dstRect = Rect(0, 0, width, height)
            canvas.drawBitmap(bitmap, srcRect, dstRect, blurPaint)
        }
        
        // Draw material overlay for the blur type effect
        if (Color.alpha(overlayColor) > 0) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
        }
    }

    private fun updateBlurredBackground() {
        if (width <= 0 || height <= 0) return
        
        // Clean up old bitmaps
        backgroundBitmap?.recycle()
        blurredBitmap?.recycle()
        
        try {
            // Capture the background
            val background = captureViewBackground()
            if (background != null) {
                // Create blurred version
                val blurred = createBlurredBitmap(background)
                blurredBitmap = blurred
                
                // Clean up source if different
                if (background != backgroundBitmap) {
                    background.recycle()
                }
            }
        } catch (e: OutOfMemoryError) {
            // Handle memory issues gracefully
        }
    }

    private fun captureViewBackground(): Bitmap? {
        val rootView = rootView
        if (rootView.width <= 0 || rootView.height <= 0) return null
        
        try {
            // Create a bitmap of the root view
            val bitmap = Bitmap.createBitmap(
                rootView.width, 
                rootView.height, 
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            
            // Draw the root view
            rootView.draw(canvas)
            
            // Extract the portion that's behind this view
            val location = IntArray(2)
            getLocationInWindow(location)
            
            val left = location[0]
            val top = location[1]
            val right = left + width
            val bottom = top + height
            
            // Ensure bounds are within the bitmap
            val clampedLeft = max(0, left)
            val clampedTop = max(0, top)
            val clampedRight = min(bitmap.width, right)
            val clampedBottom = min(bitmap.height, bottom)
            
            if (clampedRight > clampedLeft && clampedBottom > clampedTop) {
                val extracted = Bitmap.createBitmap(
                    bitmap,
                    clampedLeft,
                    clampedTop,
                    clampedRight - clampedLeft,
                    clampedBottom - clampedTop
                )
                bitmap.recycle()
                return extracted
            } else {
                bitmap.recycle()
                return null
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun createBlurredBitmap(source: Bitmap): Bitmap? {
        if (blurRadius <= 0f) return source.copy(source.config ?: Bitmap.Config.ARGB_8888, false)
        
        return when {
            renderScript != null && blurScript != null -> {
                blurWithRenderScript(source)
            }
            else -> {
                blurWithSoftware(source)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun blurWithRenderScript(source: Bitmap): Bitmap? {
        val rs = renderScript ?: return null
        val script = blurScript ?: return null
        
        return try {
            val blurred = source.copy(source.config ?: Bitmap.Config.ARGB_8888, true)
            val input = Allocation.createFromBitmap(rs, blurred)
            val output = Allocation.createTyped(rs, input.type)
            
            script.setRadius(blurRadius.coerceIn(1f, 25f))
            script.setInput(input)
            script.forEach(output)
            
            output.copyTo(blurred)
            
            input.destroy()
            output.destroy()
            
            blurred
        } catch (e: Exception) {
            blurWithSoftware(source)
        }
    }

    private fun blurWithSoftware(source: Bitmap): Bitmap? {
        if (blurRadius < 1f) return source.copy(source.config ?: Bitmap.Config.ARGB_8888, false)
        
        return try {
            val radius = blurRadius.toInt().coerceIn(1, 10) // Limit for performance
            val blurred = source.copy(source.config ?: Bitmap.Config.ARGB_8888, true)
            
            // Apply a simple box blur
            val pixels = IntArray(blurred.width * blurred.height)
            blurred.getPixels(pixels, 0, blurred.width, 0, 0, blurred.width, blurred.height)
            
            // Horizontal pass
            for (y in 0 until blurred.height) {
                for (x in 0 until blurred.width) {
                    var r = 0
                    var g = 0
                    var b = 0
                    var a = 0
                    var count = 0
                    
                    for (i in -radius..radius) {
                        val px = (x + i).coerceIn(0, blurred.width - 1)
                        val pixel = pixels[y * blurred.width + px]
                        
                        a += (pixel shr 24) and 0xff
                        r += (pixel shr 16) and 0xff
                        g += (pixel shr 8) and 0xff
                        b += pixel and 0xff
                        count++
                    }
                    
                    pixels[y * blurred.width + x] = (a / count shl 24) or
                            (r / count shl 16) or
                            (g / count shl 8) or
                            (b / count)
                }
            }
            
            blurred.setPixels(pixels, 0, blurred.width, 0, 0, blurred.width, blurred.height)
            blurred
        } catch (e: Exception) {
            null
        }
    }

    private fun isReducedTransparencyEnabled(): Boolean {
        // Android doesn't have a direct equivalent to iOS reduce transparency
        return false
    }

    private fun parseColor(colorString: String): Int {
        return try {
            if (colorString.startsWith("#")) {
                Color.parseColor(colorString)
            } else {
                Color.parseColor("#$colorString")
            }
        } catch (e: IllegalArgumentException) {
            Color.WHITE
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        
        // Clean up resources
        backgroundBitmap?.recycle()
        backgroundBitmap = null
        blurredBitmap?.recycle()
        blurredBitmap = null
        
        // Clean up RenderScript
        blurScript?.destroy()
        renderScript?.destroy()
        renderScript = null
        blurScript = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        
        // Reinitialize if needed
        if (renderScript == null && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            initializeBlurComponents()
        }
        
        needsUpdate = true
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            needsUpdate = true
            invalidate()
        }
    }
}
