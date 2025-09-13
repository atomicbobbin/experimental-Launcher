package org.fossify.home.gestures

class GestureRouter {
    enum class GestureType { FlingUp, FlingDown, FlingLeft, FlingRight, DoubleTap }

    private val handlers = mutableMapOf<GestureType, MutableList<() -> Unit>>()

    fun register(type: GestureType, handler: () -> Unit) {
        handlers.getOrPut(type) { mutableListOf() }.add(handler)
    }

    fun dispatch(type: GestureType) {
        handlers[type]?.forEach { it.invoke() }
    }
}


