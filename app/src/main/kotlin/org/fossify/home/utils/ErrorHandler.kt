package org.fossify.home.utils

import android.content.Context
import android.widget.Toast
import org.fossify.commons.extensions.showErrorToast
import org.fossify.home.R

/**
 * Centralized error handling utility for the launcher.
 * Provides consistent error handling and user feedback across the application.
 */
object ErrorHandler {
    
    /**
     * Handle and log errors with user-friendly messages.
     */
    fun handleError(
        context: Context,
        error: Throwable,
        userMessage: String? = null,
        showToast: Boolean = true
    ) {
        // Log the error
        Logger.e("Error occurred: ${error.message}", error)
        
        // Show user-friendly message
        if (showToast) {
            val message = userMessage ?: getDefaultErrorMessage(error)
            try {
                context.showErrorToast(message)
            } catch (e: Exception) {
                // Fallback to system toast if custom toast fails
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                Logger.e("Failed to show error toast", e)
            }
        }
    }
    
    /**
     * Handle errors silently (log only, no user feedback).
     */
    fun handleErrorSilently(error: Throwable, context: String = "") {
        Logger.e("Silent error in $context: ${error.message}", error)
    }
    
    /**
     * Handle errors with custom logging level.
     */
    fun handleError(
        error: Throwable,
        level: LogLevel = LogLevel.ERROR,
        context: String = "",
        userMessage: String? = null,
        showToast: Boolean = false,
        appContext: Context? = null
    ) {
        when (level) {
            LogLevel.DEBUG -> Logger.d("Error in $context: ${error.message}", error)
            LogLevel.INFO -> Logger.i("Error in $context: ${error.message}", error)
            LogLevel.WARNING -> Logger.w("Error in $context: ${error.message}", error)
            LogLevel.ERROR -> Logger.e("Error in $context: ${error.message}", error)
        }
        
        if (showToast && appContext != null) {
            val message = userMessage ?: getDefaultErrorMessage(error)
            try {
                appContext.showErrorToast(message)
            } catch (e: Exception) {
                Logger.e("Failed to show error toast", e)
            }
        }
    }
    
    /**
     * Get user-friendly error message based on exception type.
     */
    private fun getDefaultErrorMessage(error: Throwable): String {
        return when (error) {
            is SecurityException -> "Permission denied. Please check app permissions."
            is IllegalStateException -> "App is in an invalid state. Please restart the app."
            is IllegalArgumentException -> "Invalid input provided."
            is OutOfMemoryError -> "Not enough memory available. Please close other apps."
            is NullPointerException -> "An unexpected error occurred. Please try again."
            else -> "An unexpected error occurred. Please try again."
        }
    }
    
    /**
     * Handle database errors specifically.
     */
    fun handleDatabaseError(
        context: Context,
        error: Throwable,
        operation: String = "database operation"
    ) {
        Logger.e("Database error during $operation: ${error.message}", error)
        
        val userMessage = when (error) {
            is android.database.sqlite.SQLiteException -> 
                "Database error. Your data may be corrupted. Please restart the app."
            is java.sql.SQLException -> 
                "Database connection error. Please try again."
            else -> 
                "Database error occurred. Please restart the app."
        }
        
        try {
            context.showErrorToast(userMessage)
        } catch (e: Exception) {
            Logger.e("Failed to show database error toast", e)
        }
    }
    
    /**
     * Handle network errors specifically.
     */
    fun handleNetworkError(
        context: Context,
        error: Throwable,
        operation: String = "network operation"
    ) {
        Logger.e("Network error during $operation: ${error.message}", error)
        
        val userMessage = when (error) {
            is java.net.UnknownHostException -> 
                "Cannot connect to server. Please check your internet connection."
            is java.net.SocketTimeoutException -> 
                "Connection timed out. Please try again."
            is java.io.IOException -> 
                "Network error. Please check your connection and try again."
            else -> 
                "Network error occurred. Please try again."
        }
        
        try {
            context.showErrorToast(userMessage)
        } catch (e: Exception) {
            Logger.e("Failed to show network error toast", e)
        }
    }
    
    /**
     * Handle file system errors specifically.
     */
    fun handleFileSystemError(
        context: Context,
        error: Throwable,
        operation: String = "file operation"
    ) {
        Logger.e("File system error during $operation: ${error.message}", error)
        
        val userMessage = when (error) {
            is java.io.FileNotFoundException -> 
                "File not found. The file may have been moved or deleted."
            is java.io.IOException -> 
                "Cannot access file. Please check storage permissions."
            is SecurityException -> 
                "Permission denied. Please check storage permissions."
            else -> 
                "File system error occurred. Please try again."
        }
        
        try {
            context.showErrorToast(userMessage)
        } catch (e: Exception) {
            Logger.e("Failed to show file system error toast", e)
        }
    }
    
    /**
     * Log levels for error handling.
     */
    enum class LogLevel {
        DEBUG, INFO, WARNING, ERROR
    }
}
