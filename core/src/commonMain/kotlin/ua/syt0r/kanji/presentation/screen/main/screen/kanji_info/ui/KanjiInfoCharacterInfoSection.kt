package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.presentation.common.resolveString
import ua.syt0r.kanji.presentation.common.resources.icon.Copy
import ua.syt0r.kanji.presentation.common.resources.icon.ExtraIcons
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.kanji.AnimatedKanji
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiBackground
import ua.syt0r.kanji.presentation.common.ui.kanji.RadicalKanji
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract.ScreenState

@Composable
fun KanjiInfoCharacterInfoSection(
    screenState: ScreenState.Loaded,
    onCopyButtonClick: () -> Unit,
    onRadicalClick: (String) -> Unit
) {

    when (screenState) {
        is ScreenState.Loaded.Kana -> {
            KanaInfo(
                screenState = screenState,
                onCopyButtonClick = onCopyButtonClick,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }

        is ScreenState.Loaded.Kanji -> {
            KanjiInfo(
                screenState = screenState,
                onCopyButtonClick = onCopyButtonClick,
                onRadicalClick = onRadicalClick,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }
    }

}

@Composable
private fun KanaInfo(
    screenState: ScreenState.Loaded.Kana,
    onCopyButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AnimatableCharacter(screenState.strokes)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = screenState.kanaSystem.resolveString(),
                    style = MaterialTheme.typography.headlineSmall
                )

                val readings = screenState.reading.let {
                    if (it.alternative != null) listOf(it.nihonShiki) + it.alternative
                    else listOf(it.nihonShiki)
                }

                Text(
                    text = resolveString { kanjiInfo.romajiMessage(readings) },
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedIconButton(
                    onClick = onCopyButtonClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(ExtraIcons.Copy, null)
                }

            }

        }
    }

}

@Composable
private fun KanjiInfo(
    screenState: ScreenState.Loaded.Kanji,
    onCopyButtonClick: () -> Unit,
    onRadicalClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AnimatableCharacter(strokes = screenState.strokes)

            Column(
                modifier = Modifier.weight(1f)
            ) {

                screenState.grade?.let {
                    Text(
                        text = resolveString { kanjiInfo.gradeMessage(it) },
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                screenState.jlptLevel?.let {
                    Text(
                        text = resolveString { kanjiInfo.jlptMessage(it) },
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                screenState.frequency?.let {
                    Text(
                        text = resolveString { kanjiInfo.frequencyMessage(it) },
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                OutlinedIconButton(
                    onClick = onCopyButtonClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(ExtraIcons.Copy, null)
                }

            }

        }

        if (screenState.meanings.isNotEmpty()) {
            Text(
                text = screenState.meanings.joinToString(),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (screenState.kun.isNotEmpty())
            KanjiDataRow(
                title = resolveString { kunyomi },
                items = screenState.kun
            )

        if (screenState.on.isNotEmpty())
            KanjiDataRow(
                title = resolveString { onyomi },
                items = screenState.on
            )

        RadicalsSection(
            screenState = screenState,
            onRadicalClick = onRadicalClick
        )

    }

}

@Composable
private fun AnimatableCharacter(strokes: List<Path>) {

    Column {

        Card(
            modifier = Modifier.size(120.dp),
            elevation = CardDefaults.elevatedCardElevation()
        ) {

            Box(modifier = Modifier.fillMaxSize()) {

                KanjiBackground(Modifier.fillMaxSize())

                AnimatedKanji(
                    strokes = strokes,
                    modifier = Modifier.fillMaxSize()
                )

            }

        }

        Text(
            text = resolveString { kanjiInfo.strokesMessage(strokes.size) },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally)
        )

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KanjiDataRow(
    title: String,
    items: List<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        FlowRow(
            modifier = Modifier.weight(1f).align(Alignment.Bottom),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach {
                Text(
                    text = it,
                    modifier = Modifier.clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RadicalsSection(
    screenState: ScreenState.Loaded.Kanji,
    onRadicalClick: (String) -> Unit,
) {

    Text(
        text = resolveString { kanjiInfo.radicalsSectionTitle(screenState.radicals.size) },
        style = MaterialTheme.typography.titleLarge
    )

    Row(
        modifier = Modifier.padding(top = 16.dp)
    ) {

        Box(
            modifier = Modifier.size(120.dp)
        ) {

            RadicalKanji(
                strokes = screenState.strokes,
                radicals = screenState.radicals,
                modifier = Modifier.fillMaxSize()
            )

        }

        if (screenState.radicals.isEmpty()) {

            Text(
                text = resolveString { kanjiInfo.noRadicalsMessage },
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .height(120.dp)
                    .weight(1f)
                    .wrapContentSize()
            )

        } else {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                screenState.displayRadicals.forEach {
                    Text(
                        text = it,
                        fontSize = 32.sp,
                        modifier = Modifier.clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onRadicalClick(it) }
                            .padding(8.dp)
                            .width(IntrinsicSize.Min)
                            .aspectRatio(1f, true)
                            .wrapContentSize(unbounded = true)
                    )
                }
            }
        }

    }

}