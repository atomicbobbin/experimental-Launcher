package org.fossify.home.touch

import android.view.MotionEvent
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class TouchEventManagerTest {

    private lateinit var touchEventManager: TouchEventManager

    @Mock
    private lateinit var motionEvent: MotionEvent

    @Before
    fun setUp() {
        touchEventManager = TouchEventManager()
        touchEventManager.initialize(screenHeight = 1000, moveGestureThreshold = 20)
    }

    @Test
    fun `initialize sets correct values`() {
        assertEquals(1000, touchEventManager.screenHeight)
        assertEquals(20, touchEventManager.moveGestureThreshold)
    }

    @Test
    fun `onTouchDown sets correct values`() {
        // Given
        val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 200f, 0)
        
        // When
        touchEventManager.onTouchDown(event)
        
        // Then
        assertEquals(100, touchEventManager.touchDownX)
        assertEquals(200, touchEventManager.touchDownY)
        assertFalse(touchEventManager.ignoreUpEvent)
        
        event.recycle()
    }

    @Test
    fun `onTouchUp resets values`() {
        // Given
        touchEventManager.touchDownX = 100
        touchEventManager.touchDownY = 200
        touchEventManager.ignoreMoveEvents = true
        touchEventManager.longPressedIcon = null
        
        // When
        touchEventManager.onTouchUp()
        
        // Then
        assertEquals(-1, touchEventManager.touchDownX)
        assertEquals(-1, touchEventManager.touchDownY)
        assertFalse(touchEventManager.ignoreMoveEvents)
        assertEquals(null, touchEventManager.longPressedIcon)
        assertEquals(Pair(-1f, -1f), touchEventManager.lastTouchCoords)
        assertFalse(touchEventManager.ignoreXMoveEvents)
        assertFalse(touchEventManager.ignoreYMoveEvents)
    }

    @Test
    fun `onTouchMove with no previous touch sets initial values`() {
        // Given
        val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 150f, 250f, 0)
        
        // When
        val result = touchEventManager.onTouchMove(event)
        
        // Then
        assertEquals(150, touchEventManager.touchDownX)
        assertEquals(250, touchEventManager.touchDownY)
        assertFalse(result.hasFingerMoved)
        assertEquals(Pair(150f, 250f), touchEventManager.lastTouchCoords)
        
        event.recycle()
    }

    @Test
    fun `onTouchMove with small movement returns false for hasFingerMoved`() {
        // Given
        val downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 200f, 0)
        val moveEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 110f, 210f, 0)
        touchEventManager.onTouchDown(downEvent)
        
        // When
        val result = touchEventManager.onTouchMove(moveEvent)
        
        // Then
        assertFalse(result.hasFingerMoved)
        assertEquals(-10f, result.diffX)
        assertEquals(-10f, result.diffY)
        
        downEvent.recycle()
        moveEvent.recycle()
    }

    @Test
    fun `onTouchMove with large movement returns true for hasFingerMoved`() {
        // Given
        val downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 200f, 0)
        val moveEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 150f, 250f, 0)
        touchEventManager.onTouchDown(downEvent)
        
        // When
        val result = touchEventManager.onTouchMove(moveEvent)
        
        // Then
        assertTrue(result.hasFingerMoved)
        assertEquals(-50f, result.diffX)
        assertEquals(-50f, result.diffY)
        
        downEvent.recycle()
        moveEvent.recycle()
    }
}
