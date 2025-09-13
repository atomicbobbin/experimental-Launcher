package org.fossify.home.effects

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.*
import androidx.core.animation.doOnEnd
import org.fossify.home.core.ServiceLocator
import org.fossify.home.helpers.*

object TransitionEffects {

    private const val DEFAULT_DURATION = 300L
    private const val FAST_DURATION = 150L

    /**
     * Apply page transition effect based on user settings
     */
    fun applyPageTransition(
        view: View,
        fromPage: Int,
        toPage: Int,
        progress: Float,
        onComplete: (() -> Unit)? = null
    ) {
        val settings = ServiceLocator.settingsRepository
        val transitionMode = settings.transitionEffectMode

        when (transitionMode) {
            TRANSITION_FADE -> applyFadeTransition(view, progress, onComplete)
            TRANSITION_SLIDE -> applySlideTransition(view, fromPage, toPage, progress, onComplete)
            TRANSITION_ZOOM -> applyZoomTransition(view, progress, onComplete)
            TRANSITION_FLIP -> applyFlipTransition(view, progress, onComplete)
            else -> onComplete?.invoke() // No transition
        }
    }

    /**
     * Apply drawer open/close transition effect
     */
    fun applyDrawerTransition(
        view: View,
        isOpening: Boolean,
        onComplete: (() -> Unit)? = null
    ) {
        val settings = ServiceLocator.settingsRepository
        val transitionMode = settings.transitionEffectMode

        when (transitionMode) {
            TRANSITION_FADE -> applyDrawerFade(view, isOpening, onComplete)
            TRANSITION_SLIDE -> applyDrawerSlide(view, isOpening, onComplete)
            TRANSITION_ZOOM -> applyDrawerZoom(view, isOpening, onComplete)
            TRANSITION_FLIP -> applyDrawerFlip(view, isOpening, onComplete)
            else -> {
                // Default slide behavior
                applyDrawerSlide(view, isOpening, onComplete)
            }
        }
    }

    private fun applyFadeTransition(view: View, progress: Float, onComplete: (() -> Unit)?) {
        val alpha = if (progress < 0.5f) {
            1f - (progress * 2f)
        } else {
            (progress - 0.5f) * 2f
        }
        
        view.alpha = alpha
        if (progress >= 1f) {
            onComplete?.invoke()
        }
    }

    private fun applySlideTransition(
        view: View,
        fromPage: Int,
        toPage: Int,
        progress: Float,
        onComplete: (() -> Unit)?
    ) {
        val direction = if (toPage > fromPage) 1 else -1
        val translationX = view.width * progress * direction
        
        view.translationX = translationX
        if (progress >= 1f) {
            view.translationX = 0f
            onComplete?.invoke()
        }
    }

    private fun applyZoomTransition(view: View, progress: Float, onComplete: (() -> Unit)?) {
        val scale = if (progress < 0.5f) {
            1f - (progress * 0.4f) // Scale down to 0.8
        } else {
            0.8f + ((progress - 0.5f) * 0.4f) // Scale back up to 1.0
        }
        
        view.scaleX = scale
        view.scaleY = scale
        
        if (progress >= 1f) {
            view.scaleX = 1f
            view.scaleY = 1f
            onComplete?.invoke()
        }
    }

    private fun applyFlipTransition(view: View, progress: Float, onComplete: (() -> Unit)?) {
        val rotationY = progress * 180f
        view.rotationY = rotationY
        
        // Adjust alpha to create a fade effect during flip
        view.alpha = if (rotationY > 90f) 0.3f + ((rotationY - 90f) / 90f) * 0.7f else 1f - (rotationY / 90f) * 0.7f
        
        if (progress >= 1f) {
            view.rotationY = 0f
            view.alpha = 1f
            onComplete?.invoke()
        }
    }

    private fun applyDrawerFade(view: View, isOpening: Boolean, onComplete: (() -> Unit)?) {
        val startAlpha = if (isOpening) 0f else 1f
        val endAlpha = if (isOpening) 1f else 0f
        
        ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha).apply {
            duration = DEFAULT_DURATION
            interpolator = DecelerateInterpolator()
            doOnEnd { onComplete?.invoke() }
            start()
        }
    }

    private fun applyDrawerSlide(view: View, isOpening: Boolean, onComplete: (() -> Unit)?) {
        val startY = if (isOpening) view.height.toFloat() else 0f
        val endY = if (isOpening) 0f else view.height.toFloat()
        
        ObjectAnimator.ofFloat(view, "y", startY, endY).apply {
            duration = DEFAULT_DURATION
            interpolator = DecelerateInterpolator()
            doOnEnd { onComplete?.invoke() }
            start()
        }
    }

    private fun applyDrawerZoom(view: View, isOpening: Boolean, onComplete: (() -> Unit)?) {
        val startScale = if (isOpening) 0.8f else 1f
        val endScale = if (isOpening) 1f else 0.8f
        val startAlpha = if (isOpening) 0f else 1f
        val endAlpha = if (isOpening) 1f else 0f
        
        val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale)
        val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha)
        
        AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator)
            duration = DEFAULT_DURATION
            interpolator = DecelerateInterpolator()
            doOnEnd { onComplete?.invoke() }
            start()
        }
    }

    private fun applyDrawerFlip(view: View, isOpening: Boolean, onComplete: (() -> Unit)?) {
        val startRotation = if (isOpening) -90f else 0f
        val endRotation = if (isOpening) 0f else -90f
        
        ObjectAnimator.ofFloat(view, "rotationX", startRotation, endRotation).apply {
            duration = DEFAULT_DURATION
            interpolator = DecelerateInterpolator()
            doOnEnd { onComplete?.invoke() }
            start()
        }
    }

    /**
     * Apply folder open/close transition
     */
    fun applyFolderTransition(
        view: View,
        isOpening: Boolean,
        onComplete: (() -> Unit)? = null
    ) {
        val settings = ServiceLocator.settingsRepository
        val transitionMode = settings.transitionEffectMode

        when (transitionMode) {
            TRANSITION_ZOOM, TRANSITION_NONE -> {
                // Use default zoom behavior for folders (most appropriate)
                val startScale = if (isOpening) 0f else 1f
                val endScale = if (isOpening) 1f else 0.2f
                
                ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale).apply {
                    duration = FAST_DURATION
                    interpolator = DecelerateInterpolator()
                    doOnEnd { onComplete?.invoke() }
                    start()
                }
                ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale).apply {
                    duration = FAST_DURATION
                    interpolator = DecelerateInterpolator()
                    start()
                }
            }
            else -> {
                // Enhanced folder transitions for other modes
                applyDrawerZoom(view, isOpening, onComplete)
            }
        }
    }
}
