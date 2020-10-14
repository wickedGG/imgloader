package yb.com.imgloader

import android.app.Application
import android.graphics.Bitmap
import android.util.LruCache


class ImageApplication : Application() {

    val cache by lazy {
        object : LruCache<String, Bitmap>(((Runtime.getRuntime().maxMemory() / 1024) / 8).toInt()) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


    companion object {
        private lateinit var instance: ImageApplication
        @JvmStatic
        fun getGlobalContext(): ImageApplication {
            return instance
        }
    }
}