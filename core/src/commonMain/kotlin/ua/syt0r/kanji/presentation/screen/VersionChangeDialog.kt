package ua.syt0r.kanji.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import ua.syt0r.kanji.presentation.common.MultiplatformDialog

@Composable
fun VersionChangeDialog(
    onDismissRequest: () -> Unit
) {

    MultiplatformDialog(onDismissRequest) {
        Column(
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 10.dp)
                .heightIn(max = 500.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text("Version Changes", style = MaterialTheme.typography.titleLarge)

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                version("1.5", LocalDate(2023, 10, 3)) {
                    append(
                        """
                        - Reminder notification. Enable in settings
                        - Daily goal and quick practice menu on home screen
                        - Version change dialog
                        - Additional menu for practice configuration and flexible practice saving
                        - Manual app theme toggle support for older versions of Android
                        """.trimIndent()
                    )
                }
                version("1.4", LocalDate(2023, 3, 29)) {
                    append(
                        """
                        - Fix migration crash when custom sorting was applied
                        """.trimIndent()
                    )
                }
                version("1.3", LocalDate(2023, 3, 28)) {
                    append(
                        """
                        - Loading time optimizations
                        - Left-handed mode
                        - Landscape mode for character info screen
                        - First desktop version
                        - Fix crash with unknown 々 character
                        - Fix stuck issue on writing practice screen when drawing strokes too fast
                        - Fix crash with in app review dialog for devices with faulty Play Services and Chromebooks
                        """.trimIndent()
                    )
                }
                version("1.2", LocalDate(2023, 3, 6)) {
                    append(
                        """
                        - Fixed bug with incorrect group selected on multiselect dialog when filter is used
                        - Fixed bug with low resolution input and characters on Android 8
                        - Added new reading practice mode and additional filter option
                        - Added select/deselect all buttons when selecting groups for multiselect mode
                        - Added Wanikani levels to Select screen
                        - Removed extra delays when using search
                        """.trimIndent()
                    )
                }
                version("1.1", LocalDate(2023, 2, 16)) {
                    append(
                        """
                        - Added option to search by kanji radicals
                        - Added option to see only characters that need review
                        - Added alternative expressions dialog to writing practice screen
                        - Improved review indicator algorithm
                        - Landscape mode improvements
                        """.trimIndent()
                    )
                }
                version("1.0", LocalDate(2023, 1, 30)) {
                    append(
                        """
                        - Added search screen
                        - Added highlighting to review groups based on last review date
                        - Added button to view alternative readings and translations of expression items
                        """.trimIndent()
                    )
                }
            }

            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Close")
            }

        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.version(
    versionNumber: String,
    releaseDate: LocalDate,
    changes: AnnotatedString.Builder.() -> Unit
) {
    stickyHeader {
        Row(
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(text = "Version: $versionNumber", modifier = Modifier.weight(1f))
            Text(text = releaseDate.toString(), style = MaterialTheme.typography.bodySmall)
        }
    }
    item {
        Text(
            text = AnnotatedString.Builder().apply(changes).toAnnotatedString(),
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}