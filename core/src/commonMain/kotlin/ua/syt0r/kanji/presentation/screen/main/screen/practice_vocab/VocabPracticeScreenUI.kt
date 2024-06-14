package ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.buildFuriganaString
import ua.syt0r.kanji.presentation.common.AutopaddedScrollableColumn
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.theme.neutralButtonColors
import ua.syt0r.kanji.presentation.common.theme.neutralTextButtonColors
import ua.syt0r.kanji.presentation.common.ui.FancyLoading
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.LocalOrientation
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.dialog.AlternativeWordsDialog
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationContainer
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationEnumSelector
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationOption
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSavedStateInfoLabel
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.VocabPracticeScreenContract.ScreenState

@Composable
fun VocabPracticeScreenUI(
    state: State<ScreenState>,
    onConfigured: (VocabPracticeConfiguration) -> Unit,
    onAnswerSelected: (String) -> Unit,
    onNext: () -> Unit,
    onFeedback: (JapaneseWord) -> Unit,
    navigateBack: () -> Unit
) {

    val tryNavigateBack = {
        // TODO leave confirmation
        navigateBack()
    }

    Scaffold(
        topBar = { ScreenTopBar(navigateUp = tryNavigateBack, state = state) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        AnimatedContent(
            targetState = state.value,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {

            when (it) {
                ScreenState.Loading -> {
                    FancyLoading(Modifier.fillMaxSize().wrapContentSize())
                }

                is ScreenState.Configuration -> {
                    ScreenConfiguration(
                        screenState = it,
                        onConfigured = onConfigured
                    )
                }

                is ScreenState.Review -> {
                    ScreenReview(
                        screenState = it,
                        onAnswerSelected = onAnswerSelected,
                        onNext = onNext,
                        feedbackClick = onFeedback
                    )
                }

                is ScreenState.Summary -> {
                    ScreenSummary(
                        screenState = it,
                        onFinishClick = navigateBack
                    )
                }
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenTopBar(
    navigateUp: () -> Unit,
    state: State<ScreenState>
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        actions = {
            val currentToTotal = remember {
                derivedStateOf {
                    when (val practiceState = state.value) {
                        is ScreenState.Review -> {
                            practiceState.practiceState.value
                                .run { currentPositionInQueue to totalItemsInQueue }
                        }

                        else -> null
                    }
                }
            }

            currentToTotal.value?.let { (current, total) ->
                Text(
                    text = resolveString { vocabPractice.practiceProgressCounter(current, total) },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    )
}

@Composable
private fun ScreenConfiguration(
    screenState: ScreenState.Configuration,
    onConfigured: (VocabPracticeConfiguration) -> Unit
) {

    var practiceType by rememberSaveable { mutableStateOf(screenState.practiceType) }
    var shuffle by rememberSaveable { mutableStateOf(screenState.shuffle) }
    var readingPriority by rememberSaveable { mutableStateOf(screenState.readingPriority) }
    var showMeaning by rememberSaveable { mutableStateOf(screenState.showMeaning) }

    PracticeConfigurationContainer(
        onClick = {
            onConfigured(
                VocabPracticeConfiguration(
                    practiceType = practiceType,
                    shuffle = shuffle,
                    readingPriority = readingPriority,
                    showMeaning = showMeaning
                )
            )
        }
    ) {

        PracticeConfigurationOption(
            title = resolveString { commonPractice.shuffleConfigurationTitle },
            subtitle = resolveString { commonPractice.shuffleConfigurationMessage },
            checked = shuffle,
            onChange = { shuffle = it }
        )

        PracticeConfigurationEnumSelector(
            title = resolveString { vocabPractice.readingPriorityConfigurationTitle },
            subtitle = resolveString { vocabPractice.readingPriorityConfigurationMessage },
            values = VocabPracticeReadingPriority.values(),
            selected = readingPriority,
            onSelected = { readingPriority = it }
        )

        PracticeConfigurationOption(
            title = resolveString { vocabPractice.readingMeaningConfigurationTitle },
            subtitle = resolveString { vocabPractice.readingMeaningConfigurationMessage },
            checked = showMeaning,
            onChange = { showMeaning = it }
        )

    }

}

@Composable
private fun ScreenReview(
    screenState: ScreenState.Review,
    onAnswerSelected: (String) -> Unit,
    onNext: () -> Unit,
    feedbackClick: (JapaneseWord) -> Unit
) {

    var alternativeWordsDialogWord by remember { mutableStateOf<JapaneseWord?>(null) }
    alternativeWordsDialogWord?.also {
        AlternativeWordsDialog(
            word = it,
            onDismissRequest = { alternativeWordsDialogWord = null }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (val currentState = screenState.practiceState.value.reviewState) {
            is VocabReviewState.Reading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    val selectedAnswer by currentState.selectedAnswer

                    FuriganaText(
                        furiganaString = currentState.displayReading.value,
                        textStyle = MaterialTheme.typography.displayLarge,
                        annotationTextStyle = MaterialTheme.typography.bodyLarge,
                    )

                    if (selectedAnswer != null || screenState.showMeaning)
                        TextButton(
                            onClick = { alternativeWordsDialogWord = currentState.word },
                            colors = ButtonDefaults.neutralTextButtonColors(),
                            modifier = Modifier.widthIn(max = 400.dp)
                        ) {
                            Text(currentState.word.meanings.first())
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                        }

                    Spacer(modifier = Modifier.weight(1f))

                    val maxItemsInEachRow = when (LocalOrientation.current) {
                        Orientation.Portrait -> 2
                        Orientation.Landscape -> 4
                    }
                    val answerRows = currentState.answers.chunked(maxItemsInEachRow)

                    Column(
                        modifier = Modifier.width(IntrinsicSize.Max)
                    ) {
                        answerRows.forEach { rowAnswers ->

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                rowAnswers.forEach { answer ->
                                    ReadingAnswerButton(
                                        answer = answer,
                                        selectedAnswer = selectedAnswer,
                                        enabled = selectedAnswer == null,
                                        onClick = { onAnswerSelected(answer) },
                                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                                    )

                                }

                                val lastLineItems = rowAnswers.size % maxItemsInEachRow
                                val missingSpots = maxItemsInEachRow - lastLineItems
                                if (lastLineItems != 0)
                                    Spacer(Modifier.weight(missingSpots.toFloat()))

                            }

                        }
                    }

                    val shouldShowNextButton = shouldShowNextButton(screenState.practiceState)

                    NextButton(
                        showNextButton = shouldShowNextButton,
                        onClick = onNext,
                        onFeedbackClick = { feedbackClick(currentState.word) }
                    )
                }

            }

        }


    }

}

@Composable
private fun shouldShowNextButton(state: State<VocabPracticeReviewState>): State<Boolean> {
    return remember {
        derivedStateOf {
            when (val currentState = state.value.reviewState) {
                is VocabReviewState.Reading -> currentState.selectedAnswer.value != null
            }
        }
    }
}

@Composable
private fun ReadingAnswerButton(
    answer: String,
    selectedAnswer: SelectedReadingAnswer?,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val (textColor, containerColor) = MaterialTheme.run {
        when {
            answer == selectedAnswer?.correct -> {
                colorScheme.surface to extraColorScheme.success
            }

            answer == selectedAnswer?.selected && !selectedAnswer.isCorrect -> {
                colorScheme.surface to colorScheme.error
            }

            else -> {
                colorScheme.onSurface to colorScheme.surface
            }
        }
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = textColor,
            disabledContentColor = textColor,
            containerColor = containerColor,
            disabledContainerColor = containerColor
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        Text(text = answer, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun NextButton(
    showNextButton: State<Boolean>,
    onClick: () -> Unit,
    onFeedbackClick: () -> Unit
) {

    val offset = animateFloatAsState(if (showNextButton.value) 0f else 1f)

    Row(
        modifier = Modifier.graphicsLayer { translationY = size.height * offset.value }
            .fillMaxWidth()
            .wrapContentWidth()
            .widthIn(max = 400.dp)
            .padding(horizontal = 20.dp)
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onFeedbackClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxHeight()
                .aspectRatio(1f)
        ) {
            Icon(Icons.Default.Flag, null)
        }

        Button(
            onClick = onClick,
            colors = ButtonDefaults.neutralButtonColors(),
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            Text(
                text = resolveString { vocabPractice.nextButton },
                style = MaterialTheme.typography.titleMedium
            )
            Icon(Icons.AutoMirrored.Filled.NavigateNext, null)
        }
    }

}

@Composable
private fun ScreenSummary(
    screenState: ScreenState.Summary,
    onFinishClick: () -> Unit
) {

    AutopaddedScrollableColumn(
        modifier = Modifier.fillMaxWidth()
            .wrapContentWidth()
            .widthIn(max = 400.dp)
            .padding(horizontal = 20.dp),
        bottomOverlayContent = {

            Button(
                onClick = onFinishClick,
                colors = ButtonDefaults.neutralButtonColors(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Text(text = resolveString { commonPractice.savedButton })
            }

        }
    ) {

        PracticeSavedStateInfoLabel(
            title = resolveString { commonPractice.savedTimeSpentLabel },
            data = resolveString {
                commonPractice.savedTimeSpentValue(screenState.practiceDuration)
            }
        )

        PracticeSavedStateInfoLabel(
            title = resolveString { vocabPractice.summaryItemsCountTitle },
            data = resolveString { screenState.results.size.toString() }
        )

        screenState.results.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FuriganaText(
                    furiganaString = buildFuriganaString {
                        append("${index + 1}. ")
                        append(item.reading)
                    },
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = item.character,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clip(MaterialTheme.shapes.small)
                        .clickable { }
                        .border(
                            width = 1.dp,
                            color = if (item.isCorrect) MaterialTheme.extraColorScheme.success else MaterialTheme.colorScheme.error,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                )
            }

        }

    }

}
