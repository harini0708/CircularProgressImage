package com.paraooo.circular_progress_image

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * State holder for circular progress animation.
 *
 * @property progress The current progress value (0.0f ~ 1.0f).
 * @constructor Creates a [CircularProgressState] with the given initial progress and coroutine scope.
 *
 * @param initialProgress Initial progress value (0.0f ~ 1.0f).
 * @param coroutineScope Coroutine scope for launching animations.
 */
@Stable
class CircularProgressState(
    initialProgress: Float,
    private val coroutineScope: CoroutineScope
) {
    private val _progress = Animatable(initialProgress.coerceIn(0f, 1f))
    val progress: Float
        get() = _progress.value

    /**
     * Animates the progress to the specified target value.
     *
     * @param targetProgress Target progress value (0.0f ~ 1.0f).
     * @param animationSpec Animation specification to use.
     */
    fun moveTo(
        targetProgress: Float,
        animationSpec: AnimationSpec<Float> = tween(durationMillis = 1000)
    ) {
        coroutineScope.launch {
            _progress.animateTo(
                targetValue = targetProgress.coerceIn(0f, 1f),
                animationSpec = animationSpec
            )
        }
    }
}

/**
 * Remembers and returns a [CircularProgressState] for use with [AnimatedCircularProgressImage].
 *
 * @param initialProgress Initial progress value.
 * @return Remembered [CircularProgressState] instance.
 */
@Composable
fun rememberCircularProgressState(
    initialProgress: Float = 0f
): CircularProgressState {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) {
        CircularProgressState(
            initialProgress = initialProgress,
            coroutineScope = coroutineScope
        )
    }
}

/**
 * Displays an animated circular progress image using the given [CircularProgressState].
 *
 * @param state The [CircularProgressState] controlling the progress.
 * @param painter The image painter to display.
 * @param modifier Modifier to be applied to the layout.
 * @param startAngle The starting angle in degrees.
 * @param maxSweepAngle The maximum sweep angle in degrees.
 * @param color Optional tint color for the progress image.
 * @param backgroundColor Optional background color for the image.
 * @param contentDescription Optional content description for accessibility.
 */
@Composable
fun AnimatedCircularProgressImage(
    state: CircularProgressState,
    painter: Painter,
    modifier: Modifier = Modifier,
    startAngle: Float = 0f,
    maxSweepAngle: Float = 360f,
    color : Color? = null,
    backgroundColor : Color? = null,
    contentDescription: String? = null
) {
    CircularProgressImage(
        painter = painter,
        progress = state.progress,
        modifier = modifier,
        startAngle = startAngle,
        maxSweepAngle = maxSweepAngle,
        color = color,
        backgroundColor = backgroundColor,
        contentDescription = contentDescription
    )
}

/**
 * Displays a circular progress image with the specified progress and appearance.
 *
 * @param painter The image painter to display.
 * @param progress The progress value (0.0f ~ 1.0f).
 * @param modifier Modifier to be applied to the layout.
 * @param startAngle The starting angle in degrees.
 * @param maxSweepAngle The maximum sweep angle in degrees.
 * @param color Optional tint color for the progress image.
 * @param backgroundColor Optional background color for the image.
 * @param contentDescription Optional content description for accessibility.
 */
@Composable
fun CircularProgressImage(
    modifier: Modifier = Modifier,
    painter: Painter,
    backgroundPainter : Painter? = null,
    progress: Float,
    startAngle: Float = 0f,
    maxSweepAngle: Float = 360f,
    color : Color? = null,
    backgroundColor : Color? = null,
    contentDescription : String? = null
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ){
        if (backgroundPainter != null || backgroundColor != null) {
            Image(
                modifier = Modifier.matchParentSize(),
                painter = backgroundPainter ?: painter,
                contentDescription = contentDescription,
                colorFilter = if (backgroundColor != null) ColorFilter.tint(backgroundColor, BlendMode.SrcIn) else null,
                contentScale = ContentScale.Crop
            )
        }

        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier.matchParentSize().clip(
                BorderSegmentShape(
                    startAngleDegrees = startAngle,
                    sweepAngleDegrees = maxSweepAngle * progress.coerceIn(0f, 1f)
                )
            ),
            colorFilter = if (color != null) ColorFilter.tint(color, BlendMode.SrcIn) else null,
            contentScale = ContentScale.Crop
        )
    }

}

/**
 * A [Shape] implementation that clips content to a circular segment (arc) defined by a start angle and sweep angle.
 *
 * @property startAngleDegrees The starting angle of the segment in degrees.
 * @property sweepAngleDegrees The sweep angle of the segment in degrees.
 * @constructor Creates a [BorderSegmentShape] with the given angles.
 */
class BorderSegmentShape(
    private val startAngleDegrees: Float,
    private val sweepAngleDegrees: Float
) : Shape {

    /**
     * Creates an [Outline] for the shape based on the given size and angles.
     *
     * @param size The size of the shape.
     * @param layoutDirection The layout direction.
     * @param density The current density.
     * @return The outline representing the circular segment.
     */
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path: Path

        if (sweepAngleDegrees <= 180f) {
            path = createConvexSegmentPath(size, startAngleDegrees, sweepAngleDegrees)
        } else {
            val fullRectPath = Path().apply { addRect(Rect(Offset.Zero, size)) }

            val emptyStartAngle = startAngleDegrees + sweepAngleDegrees
            val emptySweepAngle = 360f - sweepAngleDegrees
            val emptyPath = createConvexSegmentPath(size, emptyStartAngle, emptySweepAngle)

            path = Path().apply {
                op(fullRectPath, emptyPath, PathOperation.Difference)
            }
        }
        return Outline.Generic(path)
    }

    /**
     * Creates a convex path for a circular segment with a sweep angle less than or equal to 180 degrees.
     *
     * @param size The size of the shape.
     * @param startAngle The starting angle in degrees.
     * @param sweepAngle The sweep angle in degrees.
     * @return The path representing the segment.
     */
    private fun createConvexSegmentPath(size: Size, startAngle: Float, sweepAngle: Float): Path {
        return Path().apply {
            if (abs(sweepAngle) < 0.01f) return@apply

            val center = Offset(size.width / 2f, size.height / 2f)
            val endAngle = startAngle + sweepAngle

            val startPoint = calculateIntersection(size, startAngle)
            val endPoint = calculateIntersection(size, endAngle)

            moveTo(center.x, center.y)
            lineTo(startPoint.x, startPoint.y)

            val corners = listOf(
                Offset(size.width, 0f),     // 0: Top-Right
                Offset(size.width, size.height), // 1: Bottom-Right
                Offset(0f, size.height),    // 2: Bottom-Left
                Offset(0f, 0f)         // 3: Top-Left
            )

            val startSide = getSide(size, startPoint)
            val endSide = getSide(size, endPoint)

            var currentSide = startSide
            while(currentSide != endSide) {
                lineTo(corners[currentSide].x, corners[currentSide].y)
                currentSide = (currentSide + 1) % 4
            }

            lineTo(endPoint.x, endPoint.y)
            close()
        }
    }

    /**
     * Determines which side of the rectangle the given point lies on.
     *
     * @param size The size of the rectangle.
     * @param point The point to check.
     * @return The index of the side (0: top, 1: right, 2: bottom, 3: left).
     */
    private fun getSide(size: Size, point: Offset): Int {
        val tolerance = 0.001f
        return when {
            abs(point.y) < tolerance -> 0 // Top
            abs(point.x - size.width) < tolerance -> 1 // Right
            abs(point.y - size.height) < tolerance -> 2 // Bottom
            else -> 3 // Left
        }
    }

    /**
     * Calculates the intersection point of the arc at the given angle with the rectangle.
     *
     * @param size The size of the rectangle.
     * @param angleFromTop The angle from the top in degrees.
     * @return The intersection [Offset].
     */
    private fun calculateIntersection(size: Size, angleFromTop: Float): Offset {
        val normalizedAngle = (angleFromTop % 360 + 360) % 360
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        when (normalizedAngle) {
            0f, 360f -> return Offset(centerX, 0f)
            90f -> return Offset(size.width, centerY)
            180f -> return Offset(centerX, size.height)
            270f -> return Offset(0f, centerY)
        }
        val angleRad = Math.toRadians(-normalizedAngle + 90.0).toFloat()
        val tanAngle = tan(angleRad)
        val x = size.width / 2f
        val y = x * tanAngle
        return if (abs(y) <= size.height / 2f) {
            if (cos(angleRad) > 0) Offset(size.width, centerY - y) else Offset(0f, centerY + y)
        } else {
            val invTanAngle = 1 / tanAngle
            val y2 = size.height / 2f
            val x2 = y2 * invTanAngle
            if (sin(angleRad) > 0) Offset(centerX + x2, 0f) else Offset(centerX - x2, size.height)
        }
    }
}