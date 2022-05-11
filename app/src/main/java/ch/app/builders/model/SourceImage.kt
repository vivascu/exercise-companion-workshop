package ch.app.builders.model

data class SourceImage(
    val height: Int,
    val width: Int,
    val rotation: Int,
    val isFlipped: Boolean,
) {
    val rotatedHeight
        get() = if (rotation == 0 || rotation == 180) {
            height
        } else width

    val rotatedWidth
        get() = if (rotation == 0 || rotation == 180) {
            width
        } else height
}
