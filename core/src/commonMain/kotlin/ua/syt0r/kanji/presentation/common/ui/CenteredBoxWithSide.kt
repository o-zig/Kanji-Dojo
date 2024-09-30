package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.unit.Constraints
import kotlin.math.max

@Composable
fun CenteredBoxWithSide(
    modifier: Modifier = Modifier,
    placeSideContentAtStart: Boolean = true,
    sideContent: @Composable () -> Unit,
    centerContent: @Composable () -> Unit
) {

    Layout(
        content = {
            Box { sideContent() }
            Box { centerContent() }
        },
        modifier = modifier
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val sideContentPlaceable = measurables[0].measure(
            constraints = constraints.copy(minWidth = 0, minHeight = 0)
        )

        val centralContentWidthLimit = (constraints.maxWidth - sideContentPlaceable.width * 2)
            .coerceAtLeast(0)

        val centerContentPlaceable = measurables[1].measure(
            constraints = constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = centralContentWidthLimit
            )
        )

        layout(
            width = constraints.maxWidth,
            height = max(sideContentPlaceable.height, centerContentPlaceable.height)
        ) {

            val centerContentX = constraints.maxWidth / 2 - centerContentPlaceable.width / 2

            val sideContentX: Int = when {
                placeSideContentAtStart -> centerContentX - sideContentPlaceable.width
                else -> centerContentX + centerContentPlaceable.width
            }

            sideContentPlaceable.place(
                x = sideContentX,
                y = 0
            )

            centerContentPlaceable.place(
                x = centerContentX,
                y = 0
            )

        }

    }

}