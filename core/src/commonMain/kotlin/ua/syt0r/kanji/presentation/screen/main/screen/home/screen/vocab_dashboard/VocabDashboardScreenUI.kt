package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.vocab_dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.neutralTextButtonColors
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.dialog.AlternativeWordsDialog
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.vocab_dashboard.VocabDashboardScreenContract.ScreenState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabDashboardScreenUI(
    state: State<ScreenState>,
    select: (VocabPracticeDeck) -> Unit,
    navigateToPractice: (VocabPracticeDeck) -> Unit
) {

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(paddingValues)
                .padding(horizontal = 20.dp)
                .fillMaxSize()
                .wrapContentWidth()
                .widthIn(max = 400.dp)
        ) {

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Spacer(Modifier.height(20.dp))
            }

            items(vocabDecks) { vocabPracticeSet ->
                PracticeGridItem(
                    title = resolveString(vocabPracticeSet.titleResolver),
                    onClick = {
                        select(vocabPracticeSet)
                        showBottomSheet = true
                    }
                )
            }

        }

    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            tonalElevation = 0.dp
        ) {
            BottomSheetContent(state, navigateToPractice)
        }
    }

}

@Composable
private fun PracticeGridItem(
    title: String,
    onClick: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BottomSheetContent(
    state: State<ScreenState>,
    navigateToPractice: (VocabPracticeDeck) -> Unit
) {

    val currentState = state.value

    if (currentState == ScreenState.NothingSelected) {
        CircularProgressIndicator(Modifier.fillMaxWidth().wrapContentWidth())
        return
    }

    currentState as ScreenState.DeckSelected

    var selectedWord by remember { mutableStateOf<JapaneseWord?>(null) }
    selectedWord?.also {
        AlternativeWordsDialog(
            word = it,
            onDismissRequest = { selectedWord = null }
        )
    }

    val wordsState = currentState.words.collectAsState()
    val wordsVisible = rememberSaveable(currentState.deck.titleResolver) { mutableStateOf(false) }
    val wordsHidingOverlayAlpha = animateFloatAsState(
        targetValue = if (wordsVisible.value) 0f else 1f
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 20.dp)
            .heightIn(min = 400.dp),
    ) {

        item {
            ScreenBottomSheetHeader(
                screenState = currentState,
                showWords = wordsVisible,
                onPracticeClick = navigateToPractice
            )
        }

        when (val vocabPracticePreviewState = wordsState.value) {

            is VocabPracticePreviewState.Loaded -> {
                itemsIndexed(vocabPracticePreviewState.words) { index, word ->
                    VocabItem(
                        listIndex = index,
                        word = word,
                        overlayAlpha = wordsHidingOverlayAlpha,
                        onClick = { selectedWord = word })
                }
            }

            VocabPracticePreviewState.Loading -> {
                item { CircularProgressIndicator(Modifier.size(24.dp)) }
            }

        }

        item { Spacer(Modifier.height(20.dp)) }

    }

}

@Composable
private fun ScreenBottomSheetHeader(
    screenState: ScreenState.DeckSelected,
    showWords: MutableState<Boolean>,
    onPracticeClick: (VocabPracticeDeck) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier.padding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = resolveString(screenState.deck.titleResolver),
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = { showWords.value = !showWords.value }
            ) {
                Icon(
                    imageVector = when {
                        showWords.value -> Icons.Default.Visibility
                        else -> Icons.Default.VisibilityOff
                    },
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = { onPracticeClick(screenState.deck) },
                colors = ButtonDefaults.neutralTextButtonColors()
            ) {
                Text(text = resolveString { vocabDashboard.reviewButton })
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
            }
        }

        Text(
            text = resolveString { vocabDashboard.wordsCount(screenState.deck.expressionIds.size) },
            style = MaterialTheme.typography.bodySmall
        )

    }
}

@Composable
private fun VocabItem(
    listIndex: Int,
    word: JapaneseWord,
    overlayAlpha: State<Float>,
    onClick: () -> Unit
) {
    val hiddenColor = MaterialTheme.colorScheme.surfaceVariant
    FuriganaText(
        furiganaString = word.orderedPreview(listIndex),
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .drawWithContent {
                drawContent()
                drawRoundRect(
                    color = hiddenColor.copy(alpha = overlayAlpha.value),
                    size = size,
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
            .clickable(enabled = overlayAlpha.value == 0f, onClick = onClick)
            .padding(horizontal = 8.dp)
    )
}
