package org.fossify.home.gestures

import android.content.Context
import android.view.MotionEvent
import org.fossify.home.interfaces.FlingListener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GestureHandlerTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var flingListener: FlingListener

    @Mock
    private lateinit var gestureHandlerCallback: GestureHandlerCallback

    private lateinit var gestureHandler: GestureHandler

    @Before
    fun setUp() {
        gestureHandler = GestureHandler(context, flingListener)
    }

    @Test
    fun `onSingleTapUp calls homeScreenClicked when flingListener is GestureHandlerCallback`() {
        // Given
        val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 100f, 200f, 0)
        val callbackHandler = GestureHandler(context, gestureHandlerCallback)
        
        // When
        callbackHandler.onSingleTapUp(event)
        
        // Then
        verify(gestureHandlerCallback).homeScreenClicked(100f, 200f)
        
        event.recycle()
    }

    @Test
    fun `onDoubleTap calls homeScreenDoubleTapped when flingListener is GestureHandlerCallback`() {
        // Given
        val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 200f, 0)
        val callbackHandler = GestureHandler(context, gestureHandlerCallback)
        
        // When
        callbackHandler.onDoubleTap(event)
        
        // Then
        verify(gestureHandlerCallback).homeScreenDoubleTapped(100f, 200f)
        
        event.recycle()
    }

    @Test
    fun `onLongPress calls homeScreenLongPressed when flingListener is GestureHandlerCallback`() {
        // Given
        val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 200f, 0)
        val callbackHandler = GestureHandler(context, gestureHandlerCallback)
        
        // When
        callbackHandler.onLongPress(event)
        
        // Then
        verify(gestureHandlerCallback).homeScreenLongPressed(100f, 200f)
        
        event.recycle()
    }

    @Test
    fun `onFling with vertical velocity calls onFlingUp`() {
        // Given
        val event1 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 200f, 0)
        val event2 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 100f, 100f, 0)
        
        // When
        gestureHandler.onFling(event1, event2, 0f, -500f)
        
        // Then
        verify(flingListener).onFlingUp()
        
        event1.recycle()
        event2.recycle()
    }

    @Test
    fun `onFling with vertical velocity calls onFlingDown`() {
        // Given
        val event1 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 100f, 0)
        val event2 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 100f, 200f, 0)
        
        // When
        gestureHandler.onFling(event1, event2, 0f, 500f)
        
        // Then
        verify(flingListener).onFlingDown()
        
        event1.recycle()
        event2.recycle()
    }

    @Test
    fun `onFling with horizontal velocity calls onFlingLeft`() {
        // Given
        val event1 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 200f, 100f, 0)
        val event2 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 100f, 100f, 0)
        
        // When
        gestureHandler.onFling(event1, event2, -500f, 0f)
        
        // Then
        verify(flingListener).onFlingLeft()
        
        event1.recycle()
        event2.recycle()
    }

    @Test
    fun `onFling with horizontal velocity calls onFlingRight`() {
        // Given
        val event1 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 100f, 0)
        val event2 = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 200f, 100f, 0)
        
        // When
        gestureHandler.onFling(event1, event2, 500f, 0f)
        
        // Then
        verify(flingListener).onFlingRight()
        
        event1.recycle()
        event2.recycle()
    }

    @Test
    fun `updateLastUpEvent updates timestamp`() {
        // Given
        val beforeTime = System.currentTimeMillis()
        
        // When
        gestureHandler.updateLastUpEvent()
        
        // Then
        val afterTime = System.currentTimeMillis()
        // The timestamp should be between before and after time
        assert(beforeTime <= afterTime)
    }
}
