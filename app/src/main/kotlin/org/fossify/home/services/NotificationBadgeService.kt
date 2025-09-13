package org.fossify.home.services

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.fossify.home.core.ServiceLocator
import org.fossify.home.notifications.BadgeManager
import java.util.concurrent.ConcurrentHashMap

/**
 * NotificationListenerService to track active notifications for badge display.
 * This service requires explicit user permission and can be disabled.
 */
class NotificationBadgeService : NotificationListenerService() {

    companion object {
        private const val ACTION_NOTIFICATION_LISTENER_SERVICE = "android.service.notification.NotificationListenerService"
        
        // Track notification counts per package
        private val notificationCounts = ConcurrentHashMap<String, Int>()
        
        fun getNotificationCount(packageName: String): Int {
            return notificationCounts[packageName] ?: 0
        }
        
        fun hasNotifications(packageName: String): Boolean {
            return getNotificationCount(packageName) > 0
        }
    }

    private lateinit var badgeManager: BadgeManager

    override fun onCreate() {
        super.onCreate()
        badgeManager = BadgeManager(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let { notification ->
            val packageName = notification.packageName
            
            // Skip system notifications and our own notifications
            if (shouldIgnorePackage(packageName)) {
                return
            }

            updateNotificationCount(packageName)
            badgeManager.updateBadge(packageName, getNotificationCount(packageName))
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn?.let { notification ->
            val packageName = notification.packageName
            
            if (shouldIgnorePackage(packageName)) {
                return
            }

            updateNotificationCount(packageName)
            badgeManager.updateBadge(packageName, getNotificationCount(packageName))
        }
    }

    private fun updateNotificationCount(packageName: String) {
        val activeNotifications = activeNotifications ?: return
        val count = activeNotifications.count { 
            it.packageName == packageName && !shouldIgnoreNotification(it)
        }
        
        if (count > 0) {
            notificationCounts[packageName] = count
        } else {
            notificationCounts.remove(packageName)
        }
    }

    private fun shouldIgnorePackage(packageName: String): Boolean {
        return packageName == this.packageName || // Don't show badges for launcher itself
               packageName == "android" || // Skip system notifications
               packageName == "com.android.systemui" // Skip system UI notifications
    }

    private fun shouldIgnoreNotification(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification
        
        // Ignore ongoing notifications (like music players, downloads)
        if (notification.flags and android.app.Notification.FLAG_ONGOING_EVENT != 0) {
            return true
        }
        
        // Ignore foreground service notifications
        if (notification.flags and android.app.Notification.FLAG_FOREGROUND_SERVICE != 0) {
            return true
        }
        
        // Ignore low priority notifications
        if (notification.priority < android.app.Notification.PRIORITY_DEFAULT) {
            return true
        }
        
        return false
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        
        // Initialize notification counts when service connects
        refreshAllNotificationCounts()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        
        // Clear all badges when service disconnects
        notificationCounts.clear()
        badgeManager.clearAllBadges()
    }

    private fun refreshAllNotificationCounts() {
        val activeNotifications = activeNotifications ?: return
        notificationCounts.clear()
        
        activeNotifications
            .filterNot { shouldIgnorePackage(it.packageName) }
            .filterNot { shouldIgnoreNotification(it) }
            .groupBy { it.packageName }
            .forEach { (packageName, notifications) ->
                val count = notifications.size
                if (count > 0) {
                    notificationCounts[packageName] = count
                    badgeManager.updateBadge(packageName, count)
                }
            }
    }
}
