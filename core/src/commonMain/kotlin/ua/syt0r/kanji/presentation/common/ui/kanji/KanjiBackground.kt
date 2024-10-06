package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.ceil


private val LineSegmentLength = 6.dp
private val LineSegmentWidth = 2.dp
private val SmallerLineSegmentWidth = LineSegmentWidth / 4

@Composable
fun KanjiBackground(
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.outline
) {

    Canvas(modifier) {

        val segmentLengthPx = LineSegmentLength.toPx()
        val segmentWidthPx = LineSegmentWidth.toPx()
        val smallSegmentWidth = SmallerLineSegmentWidth.toPx()

        val verticalSegmentSize = Size(segmentWidthPx, segmentLengthPx)
        val smallVerticalSegmentSize = Size(smallSegmentWidth, segmentLengthPx)

        val horizontalSegmentSize = Size(segmentLengthPx, segmentWidthPx)
        val smallHorizontalSegmentSize = Size(segmentLengthPx, smallSegmentWidth)

        val segmentsCount = ceil(size.maxDimension / segmentLengthPx / 2).toInt()

        /***
         * Drawing rectangles instead of line with path effect for backward compatibility
         * Issue: https://github.com/syt0r/Kanji-Dojo/issues/12
         */

        /***
         * Drawing rectangles instead of line with path effect for backward compatibility
         * Issue: https://github.com/syt0r/Kanji-Dojo/issues/12
         */
        drawDottedLineWithRectangles(
            Orientation.Vertical,
            segmentsCount,
            verticalSegmentSize,
            Offset(size.width / 2 - verticalSegmentSize.width / 2, 0f),
            lineColor
        )

        drawDottedLineWithRectangles(
            Orientation.Horizontal,
            segmentsCount,
            horizontalSegmentSize,
            Offset(0f, size.height / 2 - horizontalSegmentSize.height / 2),
            lineColor
        )

        drawDottedLineWithRectangles(
            Orientation.Vertical,
            segmentsCount,
            smallVerticalSegmentSize,
            Offset(size.width / 4 - smallVerticalSegmentSize.width / 2, 0f),
            lineColor
        )

        drawDottedLineWithRectangles(
            Orientation.Vertical,
            segmentsCount,
            smallVerticalSegmentSize,
            Offset(size.width * 3 / 4 - smallVerticalSegmentSize.width / 2, 0f),
            lineColor
        )

        drawDottedLineWithRectangles(
            Orientation.Horizontal,
            segmentsCount,
            smallHorizontalSegmentSize,
            Offset(0f, size.height / 4 - smallHorizontalSegmentSize.height / 2),
            lineColor
        )

        drawDottedLineWithRectangles(
            Orientation.Horizontal,
            segmentsCount,
            smallHorizontalSegmentSize,
            Offset(0f, size.height * 3 / 4 - smallHorizontalSegmentSize.height / 2),
            lineColor
        )

    }

}

private fun DrawScope.drawDottedLineWithRectangles(
    orientation: Orientation,
    segmentsCount: Int,
    lineSegmentSize: Size,
    startOffset: Offset,
    color: Color,
) {
    for (i in 0 until segmentsCount) {
        val offset = when (orientation) {
            Orientation.Vertical -> startOffset + Offset(0f, lineSegmentSize.height * 2 * i)
            Orientation.Horizontal -> startOffset + Offset(lineSegmentSize.width * 2 * i, 0f)
        }
        drawRect(color, offset, lineSegmentSize)
    }
}

