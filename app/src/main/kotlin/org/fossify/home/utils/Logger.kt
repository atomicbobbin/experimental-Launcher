package org.fossify.home.utils

import android.util.Log
import org.fossify.home.BuildConfig

/**
 * Centralized logging utility for the launcher.
 * Provides consistent logging across the application with proper error handling.
 */
object Logger {
    
    private const val TAG = "FossifyLauncher"
    private const val MAX_LOG_LENGTH = 4000
    
    /**
     * Log debug messages.
     */
    fun d(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            logMessage(Log.DEBUG, message, throwable)
        }
    }
    
    /**
     * Log info messages.
     */
    fun i(message: String, throwable: Throwable? = null) {
        logMessage(Log.INFO, message, throwable)
    }
    
    /**
     * Log warning messages.
     */
    fun w(message: String, throwable: Throwable? = null) {
        logMessage(Log.WARN, message, throwable)
    }
    
    /**
     * Log error messages.
     */
    fun e(message: String, throwable: Throwable? = null) {
        logMessage(Log.ERROR, message, throwable)
    }
    
    /**
     * Log verbose messages.
     */
    fun v(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            logMessage(Log.VERBOSE, message, throwable)
        }
    }
    
    /**
     * Internal method to handle log message formatting and length limits.
     */
    private fun logMessage(priority: Int, message: String, throwable: Throwable?) {
        try {
            if (message.length > MAX_LOG_LENGTH) {
                // Split long messages
                val chunks = message.chunked(MAX_LOG_LENGTH)
                chunks.forEachIndexed { index, chunk ->
                    val chunkMessage = if (chunks.size > 1) "[$index/${chunks.size}] $chunk" else chunk
                    if (throwable != null && index == chunks.size - 1) {
                        Log.println(priority, TAG, chunkMessage)
                        Log.println(priority, TAG, Log.getStackTraceString(throwable))
                    } else {
                        Log.println(priority, TAG, chunkMessage)
                    }
                }
            } else {
                if (throwable != null) {
                    Log.println(priority, TAG, message)
                    Log.println(priority, TAG, Log.getStackTraceString(throwable))
                } else {
                    Log.println(priority, TAG, message)
                }
            }
        } catch (e: Exception) {
            // Fallback to system log if our logging fails
            System.err.println("Logger failed: $message")
            if (throwable != null) {
                throwable.printStackTrace()
            }
        }
    }
    
    /**
     * Log method entry for debugging.
     */
    fun methodEntry(methodName: String, vararg params: Any?) {
        if (BuildConfig.DEBUG) {
            val paramString = params.joinToString(", ") { it?.toString() ?: "null" }
            d("Entering $methodName($paramString)")
        }
    }
    
    /**
     * Log method exit for debugging.
     */
    fun methodExit(methodName: String, result: Any? = null) {
        if (BuildConfig.DEBUG) {
            val resultString = result?.toString() ?: "void"
            d("Exiting $methodName -> $resultString")
        }
    }
    
    /**
     * Log performance timing.
     */
    fun performance(operation: String, durationMs: Long) {
        if (BuildConfig.DEBUG) {
            d("Performance: $operation took ${durationMs}ms")
        }
    }
}
