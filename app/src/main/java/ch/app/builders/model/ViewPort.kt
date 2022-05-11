package ch.app.builders.model

/**
 * A portion of a screen usually represented by a view.
 */
data class ViewPort(val height: Int, val width: Int)

val ViewPort.aspectRatio: Float get() = width.toFloat() / height.toFloat()