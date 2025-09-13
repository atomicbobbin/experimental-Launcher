package org.fossify.home.menu

import android.content.Context
import android.graphics.Color
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.Menu
import android.widget.PopupMenu
import org.fossify.commons.extensions.getPopupMenuTheme
import org.fossify.home.R
import org.fossify.home.extensions.handleGridItemPopupMenu
import org.fossify.home.extensions.isDefaultLauncher
import org.fossify.home.interfaces.ItemMenuListener
import org.fossify.home.models.HomeScreenGridItem

/**
 * Manages popup menus for the launcher.
 * Extracted from MainActivity to improve separation of concerns.
 */
class MenuManager(private val context: Context) {
    
    private var openPopupMenu: PopupMenu? = null
    
    /**
     * Show home icon context menu.
     */
    fun showHomeIconMenu(
        x: Float,
        y: Float,
        gridItem: HomeScreenGridItem,
        isOnAllAppsFragment: Boolean,
        anchorView: android.view.View,
        menuListener: ItemMenuListener,
        getClickableRect: (HomeScreenGridItem) -> android.graphics.Rect,
        getCurrentIconSize: () -> Float,
        getIconSize: () -> Int
    ) {
        openPopupMenu?.dismiss()
        openPopupMenu = null
        
        val anchorY = if (isOnAllAppsFragment || gridItem.type == org.fossify.home.helpers.ITEM_TYPE_WIDGET) {
            val iconSize = getIconSize()
            y - iconSize / 2f
        } else {
            val clickableRect = getClickableRect(gridItem)
            clickableRect.top.toFloat() - getCurrentIconSize() / 2f
        }

        anchorView.x = x
        anchorView.y = anchorY

        openPopupMenu = handleGridItemPopupMenu(
            anchorView = anchorView,
            gridItem = gridItem,
            isOnAllAppsFragment = isOnAllAppsFragment,
            listener = menuListener
        )
    }
    
    /**
     * Show main long press menu.
     */
    fun showMainLongPressMenu(
        x: Float,
        y: Float,
        anchorView: android.view.View,
        onMenuItemClick: (Int) -> Unit
    ) {
        anchorView.x = x
        anchorView.y = y - context.resources.getDimension(R.dimen.long_press_anchor_button_offset_y) * 2
        
        val contextTheme = ContextThemeWrapper(context, getPopupMenuTheme())
        PopupMenu(
            contextTheme,
            anchorView,
            Gravity.TOP or Gravity.END
        ).apply {
            inflate(R.menu.menu_home_screen)
            menu.findItem(R.id.set_as_default).isVisible = !isDefaultLauncher()
            setOnMenuItemClickListener { item ->
                onMenuItemClick(item.itemId)
                true
            }
            show()
        }
    }
    
    /**
     * Dismiss any open popup menu.
     */
    fun dismissPopupMenu() {
        openPopupMenu?.dismiss()
        openPopupMenu = null
    }
    
    /**
     * Check if popup menu is open.
     */
    fun isPopupMenuOpen(): Boolean = openPopupMenu != null
    
    /**
     * Handle menu item clicks.
     */
    fun handleMenuItemClick(itemId: Int, onAction: (Int) -> Unit) {
        onAction(itemId)
    }
}
