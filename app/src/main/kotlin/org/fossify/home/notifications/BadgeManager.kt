package org.fossify.home.notifications

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import org.fossify.home.core.ServiceLocator
import org.fossify.home.helpers.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages notification badge rendering and caching for app icons
 */
class BadgeManager(private val context: Context) {

    companion object {
        private const val BADGE_BROADCAST = "org.fossify.home.BADGE_UPDATE"
        private const val EXTRA_PACKAGE_NAME = "package_name"
        private const val EXTRA_BADGE_COUNT = "badge_count"
    }

    // Cache for badge-enhanced drawables
    private val badgeCache = ConcurrentHashMap<String, Drawable>()
    private val originalDrawables = ConcurrentHashMap<String, Drawable>()

    private val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val badgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    fun updateBadge(packageName: String, count: Int) {
        val settings = ServiceLocator.settingsRepository
        
        if (!settings.enableNotificationBadges) {
            return
        }

        // Broadcast badge update to UI components
        val intent = Intent(BADGE_BROADCAST).apply {
            putExtra(EXTRA_PACKAGE_NAME, packageName)
            putExtra(EXTRA_BADGE_COUNT, count)
        }
        context.sendBroadcast(intent)
    }

    fun createBadgedDrawable(originalDrawable: Drawable, packageName: String, count: Int): Drawable {
        val settings = ServiceLocator.settingsRepository
        
        if (!settings.enableNotificationBadges || count <= 0) {
            return originalDrawable
        }

        val cacheKey = "${packageName}_${count}_${settings.notificationBadgeStyle}"
        badgeCache[cacheKey]?.let { return it }

        // Store original drawable for future reference
        originalDrawables[packageName] = originalDrawable

        val iconSize = originalDrawable.intrinsicWidth.coerceAtLeast(originalDrawable.intrinsicHeight)
        val badgedBitmap = createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(badgedBitmap)

        // Draw original icon
        originalDrawable.setBounds(0, 0, iconSize, iconSize)
        originalDrawable.draw(canvas)

        // Draw badge based on style
        when (settings.notificationBadgeStyle) {
            BADGE_STYLE_DOT -> drawDotBadge(canvas, iconSize)
            BADGE_STYLE_COUNT -> drawCountBadge(canvas, iconSize, count)
            BADGE_STYLE_LARGE_DOT -> drawLargeDotBadge(canvas, iconSize)
        }

        val badgedDrawable = badgedBitmap.toDrawable(context.resources)
        badgeCache[cacheKey] = badgedDrawable
        
        return badgedDrawable
    }

    private fun drawDotBadge(canvas: Canvas, iconSize: Int) {
        val badgeRadius = (iconSize * 0.08f).coerceAtLeast(6f)
        val badgeX = iconSize * 0.75f
        val badgeY = iconSize * 0.25f

        // Draw badge background (red dot)
        badgePaint.color = Color.RED
        canvas.drawCircle(badgeX, badgeY, badgeRadius, badgePaint)

        // Draw white border
        badgePaint.color = Color.WHITE
        badgePaint.style = Paint.Style.STROKE
        badgePaint.strokeWidth = badgeRadius * 0.2f
        canvas.drawCircle(badgeX, badgeY, badgeRadius, badgePaint)
        badgePaint.style = Paint.Style.FILL
    }

    private fun drawCountBadge(canvas: Canvas, iconSize: Int, count: Int) {
        val countText = if (count > 99) "99+" else count.toString()
        val badgeRadius = (iconSize * 0.12f).coerceAtLeast(10f)
        val badgeX = iconSize * 0.75f
        val badgeY = iconSize * 0.25f

        // Adjust badge size based on text length
        val textBounds = Rect()
        badgeTextPaint.textSize = badgeRadius * 0.8f
        badgeTextPaint.getTextBounds(countText, 0, countText.length, textBounds)
        
        val badgeWidth = (textBounds.width() + badgeRadius).coerceAtLeast(badgeRadius * 2)
        val badgeHeight = badgeRadius * 2

        // Draw badge background (rounded rectangle)
        badgePaint.color = Color.RED
        val badgeRect = RectF(
            badgeX - badgeWidth / 2,
            badgeY - badgeHeight / 2,
            badgeX + badgeWidth / 2,
            badgeY + badgeHeight / 2
        )
        canvas.drawRoundRect(badgeRect, badgeRadius, badgeRadius, badgePaint)

        // Draw white border
        badgePaint.color = Color.WHITE
        badgePaint.style = Paint.Style.STROKE
        badgePaint.strokeWidth = badgeRadius * 0.1f
        canvas.drawRoundRect(badgeRect, badgeRadius, badgeRadius, badgePaint)
        badgePaint.style = Paint.Style.FILL

        // Draw count text
        canvas.drawText(
            countText,
            badgeX,
            badgeY + textBounds.height() / 2f,
            badgeTextPaint
        )
    }

    private fun drawLargeDotBadge(canvas: Canvas, iconSize: Int) {
        val badgeRadius = (iconSize * 0.12f).coerceAtLeast(8f)
        val badgeX = iconSize * 0.75f
        val badgeY = iconSize * 0.25f

        // Draw badge background (larger red dot)
        badgePaint.color = Color.RED
        canvas.drawCircle(badgeX, badgeY, badgeRadius, badgePaint)

        // Draw white border
        badgePaint.color = Color.WHITE
        badgePaint.style = Paint.Style.STROKE
        badgePaint.strokeWidth = badgeRadius * 0.15f
        canvas.drawCircle(badgeX, badgeY, badgeRadius, badgePaint)
        badgePaint.style = Paint.Style.FILL
    }

    fun getOriginalDrawable(packageName: String): Drawable? {
        return originalDrawables[packageName]
    }

    fun clearBadge(packageName: String) {
        // Remove all cached badges for this package
        badgeCache.keys.removeAll { it.startsWith("${packageName}_") }
        updateBadge(packageName, 0)
    }

    fun clearAllBadges() {
        badgeCache.clear()
        
        // Broadcast clear all badges
        val intent = Intent(BADGE_BROADCAST).apply {
            putExtra(EXTRA_PACKAGE_NAME, "")
            putExtra(EXTRA_BADGE_COUNT, 0)
        }
        context.sendBroadcast(intent)
    }

    fun hasBadge(packageName: String): Boolean {
        return org.fossify.home.services.NotificationBadgeService.hasNotifications(packageName)
    }

    fun getBadgeCount(packageName: String): Int {
        return org.fossify.home.services.NotificationBadgeService.getNotificationCount(packageName)
    }
}
