package ua.syt0r.kanji.core.srs

import kotlinx.datetime.LocalDate
import ua.syt0r.kanji.core.user_data.practice.CharacterStudyProgress

data class LetterSrsDecksData(
    val decks: List<LetterSrsDeckInfo>,
    val dailyLimitConfiguration: DailyLimitConfiguration,
    val dailyProgress: DailyProgress
)

data class LetterSrsDeckInfo(
    val id: Long,
    val title: String,
    val position: Int,
    val characters: List<String>,
    val writingDetails: LetterDeckSrsProgress,
    val readingDetails: LetterDeckSrsProgress
)

data class DailyProgress(
    val newReviewed: Int,
    val dueReviewed: Int,
    val newLeft: Int,
    val dueLeft: Int
)

data class LetterDeckSrsProgress(
    val charactersData: Map<String, CharacterSrsData>,
    val all: List<String>,
    val done: List<String>,
    val due: List<String>,
    val new: List<String>
)

data class CharacterSrsData(
    val character: String,
    val status: SrsItemStatus,
    val expectedReviewDate: LocalDate?,
    val studyProgress: CharacterStudyProgress?
)
