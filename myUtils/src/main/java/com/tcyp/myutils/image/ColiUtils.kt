package com.tcyp.myutils.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil3.ImageLoader
import coil3.asDrawable
import coil3.load
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ColiUtils {
    private lateinit var imageLoader: ImageLoader

    fun init(context: Context) {
        imageLoader = ImageLoader.Builder(context)
            .crossfade(true)
            .allowHardware(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    fun get(): ImageLoader = imageLoader

    /**
     * 加载图片
     * @param url 图片地址
     * @param placeholderRes 占位图
     * @param errorRes 错误图
     * @param crossfade 是否使用渐变效果
     */
    fun ImageView.loadImage(
        url: String?,
        @DrawableRes placeholderRes: Int? = null,
        @DrawableRes errorRes: Int? = null,
        crossfade: Boolean = true
    ) {
        load(url, imageLoader) {
            placeholderRes?.let {
                placeholder(it)
            }
            errorRes?.let {
                error(it)
            }
            if (crossfade) {
                crossfade(true)
            }
        }
    }

    /**
     * 为ImageView加载圆形头像
     *
     * @param url 要加载的图片URL，可为null
     * @param placeholderRes 占位图资源ID，可为null
     * @param errorRes 错误时显示的图片资源ID，可为null
     */
    fun ImageView.loadCircleIcon(
        url: String?,
        @DrawableRes placeholderRes: Int? = null,
        @DrawableRes errorRes: Int? = null,
    ) {
        load(url, imageLoader) {
            // 设置占位图
            placeholderRes?.let {
                placeholder(it)
            }
            // 设置错误图
            errorRes?.let {
                error(it)
            }
            // 应用圆形裁剪变换
            transformations(CircleCropTransformation())
            // 启用淡入淡出过渡效果
            crossfade(true)
        }
    }

    /**
     * 为ImageView加载圆形角图片
     *
     * @param url 要加载的图片URL，可为空
     * @param placeholderRes 占位图资源ID，可为空，默认为null
     * @param errorRes 加载错误时显示的图片资源ID，可为空，默认为null
     * @param cornerRadius 圆角半径，默认为10f
     */
    fun ImageView.loadRounded(
        url: String?,
        @DrawableRes placeholderRes: Int? = null,
        @DrawableRes errorRes: Int? = null,
        cornerRadius: Float = 10f
    ) {
        load(url, imageLoader) {
            placeholderRes?.let {
                placeholder(it)
            }
            errorRes?.let {
                error(it)
            }
            transformations(
                RoundedCornersTransformation(cornerRadius)
            )
            crossfade(true)
        }
    }


    /**
     * 预加载图片（提前缓存到内存/磁盘，常用于列表预加载下一页）
     */
    suspend fun preload(
        context: Context,
        urls: List<String>,
        size: Size = Size.ORIGINAL
    ) = withContext(Dispatchers.IO) {
        urls.forEach { url ->
            imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(url)
                    .size(size)
                    .build()
            )
        }
    }

    /**
     * 同步获取 Drawable（协程中常用）
     */
    suspend fun getDrawable(
        context: Context,
        url: String,
        errorDrawable: Drawable? = null
    ): Drawable? = withContext(Dispatchers.IO) {
        try {
            imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(url)
                    .build()
            ).image?.asDrawable(context.resources)
        } catch (e: Exception) {
            errorDrawable
        }
    }


}