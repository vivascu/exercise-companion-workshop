package ch.app.builders.model

import java.io.Serializable

/**
 * Represents a 3D point.
 */
data class Point(
    val x: Float,
    val y: Float,
    val z: Float,
) : Serializable {
    constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())
}
