package ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ua.syt0r.kanji.core.app_data.data.FuriganaString
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import kotlin.time.Duration

enum class VocabPracticeType {
    ReadingPicker
}

data class VocabPracticeConfiguration(
    val practiceType: VocabPracticeType
)

sealed interface VocabReviewState {
    interface Reading : VocabReviewState {
        val word: JapaneseWord
        val questionCharacter: String
        val displayReading: State<FuriganaString>
        val answers: List<String>
        val correctAnswer: String
        val selectedAnswer: State<SelectedReadingAnswer?>
    }
}

data class SelectedReadingAnswer(
    val selected: String,
    val correct: String
) {
    val isCorrect = selected == correct
}


sealed interface VocabReviewManagingState {

    object Loading : VocabReviewManagingState

    class Reading(
        override val word: JapaneseWord,
        override val questionCharacter: String,
        val revealedReading: FuriganaString,
        val hiddenReading: FuriganaString,
        override val answers: List<String>,
        override val correctAnswer: String,
    ) : VocabReviewManagingState, VocabReviewState.Reading {
        override val displayReading = mutableStateOf<FuriganaString>(hiddenReading)
        override val selectedAnswer = mutableStateOf<SelectedReadingAnswer?>(null)
    }

    data class Summary(
        val duration: Duration
    ) : VocabReviewManagingState

}

data class VocabQueueItemDescriptor(
    val id: Long,
    val practiceType: VocabPracticeType
)