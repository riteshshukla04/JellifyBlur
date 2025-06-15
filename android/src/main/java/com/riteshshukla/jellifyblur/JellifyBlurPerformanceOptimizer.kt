package com.riteshshukla.jellifyblur

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.View
// Removed toolkit import - using built-in methods instead
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

/**
 * Performance optimizer for blur operations
 * Handles threading, caching, and memory management
 */
class JellifyBlurPerformanceOptimizer {
    
    companion object {
        private const val BLUR_THREAD_NAME = "JellifyBlurThread"
        private const val MAX_CACHE_SIZE = 10
        private const val DOWNSCALE_FACTOR = 0.5f // Downscale for better performance
        private const val MAX_BITMAP_SIZE = 1024 * 1024 // 1MB max bitmap size
        
        @Volatile
        private var instance: JellifyBlurPerformanceOptimizer? = null
        
        fun getInstance(): JellifyBlurPerformanceOptimizer {
            return instance ?: synchronized(this) {
                instance ?: JellifyBlurPerformanceOptimizer().also { instance = it }
            }
        }
    }
    
    private val blurThread: HandlerThread = HandlerThread(BLUR_THREAD_NAME).apply { start() }
    private val blurHandler: Handler = Handler(blurThread.looper)
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    
    private val bitmapCache = ConcurrentHashMap<String, CachedBitmap>()
    
    private data class CachedBitmap(
        val bitmap: Bitmap,
        val timestamp: Long,
        val accessCount: Int = 0
    )
    
    data class BlurRequest(
        val sourceBitmap: Bitmap,
        val blurRadius: Float,
        val blurType: String,
        val callback: (Bitmap?) -> Unit
    )
    
    /**
     * Performs blur operation asynchronously with caching and optimization
     */
    fun performBlurAsync(request: BlurRequest) {
        blurHandler.post {
            val result = performBlurSync(request)
            
            // Return result on main thread
            mainHandler.post {
                request.callback(result)
            }
        }
    }
    
    /**
     * Performs blur operation synchronously with optimization
     */
    fun performBlurSync(request: BlurRequest): Bitmap? {
        val cacheKey = generateCacheKey(request)
        
        // Check cache first
        val cached = bitmapCache[cacheKey]
        if (cached != null && !cached.bitmap.isRecycled) {
            // Update access count
            bitmapCache[cacheKey] = cached.copy(
                accessCount = cached.accessCount + 1,
                timestamp = System.currentTimeMillis()
            )
                         return cached.bitmap.copy(cached.bitmap.config ?: Bitmap.Config.ARGB_8888, false)
        }
        
        // Optimize bitmap size for performance
        val optimizedBitmap = optimizeBitmapForBlur(request.sourceBitmap)
        
        // Perform blur operation
        val blurred = when {
            request.blurRadius <= 0f -> {
                                 optimizedBitmap.copy(optimizedBitmap.config ?: Bitmap.Config.ARGB_8888, false)
            }
            else -> {
                performActualBlur(optimizedBitmap, request.blurRadius)
            }
        }
        
        // Cache the result
        blurred?.let { bitmap ->
            cacheBlurResult(cacheKey, bitmap)
        }
        
        // Clean up optimized bitmap if different from source
        if (optimizedBitmap != request.sourceBitmap) {
            optimizedBitmap.recycle()
        }
        
        return blurred
    }
    
    /**
     * Optimizes bitmap size for blur operations
     */
    private fun optimizeBitmapForBlur(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val totalPixels = width * height
        
        // If bitmap is too large, downscale it
        if (totalPixels > MAX_BITMAP_SIZE) {
            val scaleFactor = kotlin.math.sqrt(MAX_BITMAP_SIZE.toDouble() / totalPixels).toFloat()
            val newWidth = (width * scaleFactor).toInt()
            val newHeight = (height * scaleFactor).toInt()
            
            return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
        }
        
        // For smaller bitmaps, optionally downscale for performance
        if (width > 512 || height > 512) {
            val newWidth = (width * DOWNSCALE_FACTOR).toInt()
            val newHeight = (height * DOWNSCALE_FACTOR).toInt()
            
            return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
        }
        
        return source
    }
    
    /**
     * Performs the actual blur operation using the most efficient method available
     */
    private fun performActualBlur(bitmap: Bitmap, radius: Float): Bitmap? {
        return try {
            // Use fallback blur implementation (optimized software blur)
            performFallbackBlur(bitmap, radius)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Fallback blur implementation for when RenderScript is not available
     */
    private fun performFallbackBlur(bitmap: Bitmap, radius: Float): Bitmap? {
        if (radius < 1f) return bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, false)
        
        return try {
                         val blurred = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
            val pixels = IntArray(bitmap.width * bitmap.height)
            blurred.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            
            // Simplified box blur for fallback
            fastBoxBlur(pixels, bitmap.width, bitmap.height, radius.toInt().coerceIn(1, 10))
            
            blurred.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            blurred
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Fast box blur implementation
     */
    private fun fastBoxBlur(pixels: IntArray, width: Int, height: Int, radius: Int) {
        // Single pass approximation for speed
        val temp = IntArray(pixels.size)
        
        // Horizontal pass
        for (y in 0 until height) {
            val lineStart = y * width
            for (x in 0 until width) {
                var a = 0
                var r = 0
                var g = 0
                var b = 0
                var count = 0
                
                val start = max(0, x - radius)
                val end = min(width - 1, x + radius)
                
                for (i in start..end) {
                    val pixel = pixels[lineStart + i]
                    a += (pixel shr 24) and 0xff
                    r += (pixel shr 16) and 0xff
                    g += (pixel shr 8) and 0xff
                    b += pixel and 0xff
                    count++
                }
                
                temp[lineStart + x] = (a / count shl 24) or
                        (r / count shl 16) or
                        (g / count shl 8) or
                        (b / count)
            }
        }
        
        // Vertical pass
        for (x in 0 until width) {
            for (y in 0 until height) {
                var a = 0
                var r = 0
                var g = 0
                var b = 0
                var count = 0
                
                val start = max(0, y - radius)
                val end = min(height - 1, y + radius)
                
                for (i in start..end) {
                    val pixel = temp[i * width + x]
                    a += (pixel shr 24) and 0xff
                    r += (pixel shr 16) and 0xff
                    g += (pixel shr 8) and 0xff
                    b += pixel and 0xff
                    count++
                }
                
                pixels[y * width + x] = (a / count shl 24) or
                        (r / count shl 16) or
                        (g / count shl 8) or
                        (b / count)
            }
        }
    }
    
    /**
     * Generates cache key for blur operation
     */
    private fun generateCacheKey(request: BlurRequest): String {
        return "${request.sourceBitmap.width}x${request.sourceBitmap.height}_${request.blurRadius}_${request.blurType}"
    }
    
    /**
     * Caches blur result with LRU eviction
     */
    private fun cacheBlurResult(key: String, bitmap: Bitmap) {
        // Check cache size and evict if necessary
        if (bitmapCache.size >= MAX_CACHE_SIZE) {
            evictLeastRecentlyUsed()
        }
        
        bitmapCache[key] = CachedBitmap(
             bitmap = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, false),
            timestamp = System.currentTimeMillis(),
            accessCount = 1
        )
    }
    
    /**
     * Evicts least recently used cache entries
     */
    private fun evictLeastRecentlyUsed() {
        val oldestEntry = bitmapCache.minByOrNull { it.value.timestamp }
        oldestEntry?.let { (key, cachedBitmap) ->
            cachedBitmap.bitmap.recycle()
            bitmapCache.remove(key)
        }
    }
    
    /**
     * Clears all cached bitmaps
     */
    fun clearCache() {
        bitmapCache.values.forEach { cached ->
            if (!cached.bitmap.isRecycled) {
                cached.bitmap.recycle()
            }
        }
        bitmapCache.clear()
    }
    
    /**
     * Captures view content efficiently
     */
    fun captureViewContent(view: View): Bitmap? {
        if (view.width <= 0 || view.height <= 0) return null
        
        return try {
            val bitmap = Bitmap.createBitmap(
                view.width, 
                view.height, 
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            null
        }
    }
    
    /**
     * Destroys the optimizer and cleans up resources
     */
    fun destroy() {
        clearCache()
        blurThread.quitSafely()
    }
    
    /**
     * Gets cache statistics for debugging
     */
    fun getCacheStats(): Map<String, Any> {
        return mapOf(
            "cacheSize" to bitmapCache.size,
            "maxCacheSize" to MAX_CACHE_SIZE,
            "totalMemoryUsage" to bitmapCache.values.sumOf { 
                it.bitmap.allocationByteCount 
            }
        )
    }
} 