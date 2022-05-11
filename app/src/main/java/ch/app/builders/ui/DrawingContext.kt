package ch.app.builders.ui

import android.graphics.Paint

/**
 * The context in which a [Skeleton] is drawn.
 */
internal data class DrawingContext(
    val paint: Paint,
    val errorPaint: Paint,
    val screenDensity: Float
)
