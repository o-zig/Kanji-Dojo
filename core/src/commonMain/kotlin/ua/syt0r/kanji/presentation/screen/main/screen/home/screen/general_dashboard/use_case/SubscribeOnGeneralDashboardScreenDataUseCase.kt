package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.use_case

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import ua.syt0r.kanji.BuildConfig
import ua.syt0r.kanji.core.RefreshableData
import ua.syt0r.kanji.core.mergeSharedFlows
import ua.syt0r.kanji.core.refreshableDataFlow
import ua.syt0r.kanji.core.srs.LetterPracticeType
import ua.syt0r.kanji.core.srs.LetterSrsManager
import ua.syt0r.kanji.core.srs.VocabSrsManager
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.practice.ReviewHistoryRepository
import ua.syt0r.kanji.core.user_data.practice.StreakData
import ua.syt0r.kanji.core.user_data.preferences.UserPreferencesRepository
import ua.syt0r.kanji.presentation.LifecycleState
import ua.syt0r.kanji.presentation.common.ScreenLetterPracticeType
import ua.syt0r.kanji.presentation.common.ScreenVocabPracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.GeneralDashboardScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.LetterDecksData
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.LetterDecksStudyProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.StreakCalendarItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.VocabDecksData
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.VocabDecksStudyProgress

interface SubscribeOnGeneralDashboardScreenDataUseCase {

    operator fun invoke(
        coroutineScope: CoroutineScope,
        lifecycleState: StateFlow<LifecycleState>
    ): Flow<RefreshableData<ScreenState.Loaded>>

}

class DefaultSubscribeOnGeneralDashboardScreenDataUseCase(
    private val letterSrsManager: LetterSrsManager,
    private val vocabSrsManager: VocabSrsManager,
    private val preferencesRepository: UserPreferencesRepository,
    private val reviewHistoryRepository: ReviewHistoryRepository,
    private val timeUtils: TimeUtils
) : SubscribeOnGeneralDashboardScreenDataUseCase {

    override fun invoke(
        coroutineScope: CoroutineScope,
        lifecycleState: StateFlow<LifecycleState>
    ): Flow<RefreshableData<ScreenState.Loaded>> = refreshableDataFlow(
        dataChangeFlow = mergeSharedFlows(
            coroutineScope,
            letterSrsManager.dataChangeFlow,
            vocabSrsManager.dataChangeFlow
        ),
        lifecycleState = lifecycleState,
        valueProvider = { getLoadedState() }
    )

    private suspend fun getLoadedState(): ScreenState.Loaded = withContext(Dispatchers.IO) {

        fun StreakData.includesDate(date: LocalDate): Boolean {
            return date in start..end
        }

        val streaks = reviewHistoryRepository.getStreaks()
        val longestStreak = streaks.maxByOrNull { it.length }

        val currentDate = timeUtils.getCurrentDate()
        val streakCalendarStartDate = currentDate.minus(
            value = STREAK_CALENDAR_DAYS - 1,
            unit = DateTimeUnit.DAY
        )

        val displayDateRange = streakCalendarStartDate..currentDate
        val streaksForCalendar = streaks.filter { it.end in displayDateRange }
        val streakCalendarItems = mutableListOf<StreakCalendarItem>()

        var date = streakCalendarStartDate
        do {
            val hasReviews = streaksForCalendar.any { it.includesDate(date) }
            streakCalendarItems.add(StreakCalendarItem(date, hasReviews))
            date = date.plus(1, DateTimeUnit.DAY)
        } while (date <= currentDate)

        val currentStreakSearchDates = setOf(
            currentDate,
            currentDate.minus(1, DateTimeUnit.DAY)
        )
        val currentStreak = streaks.find { streak ->
            currentStreakSearchDates.any { streak.includesDate(it) }
        }

        ScreenState.Loaded(
            showAppVersionChangeHint = mutableStateOf(
                value = BuildConfig.versionName != preferencesRepository.lastAppVersionWhenChangesDialogShown.get()
            ),
            showTutorialHint = mutableStateOf(
                value = !preferencesRepository.tutorialSeen.get()
            ),
            letterDecksData = getLetterDecksData(),
            vocabDecksInfo = getVocabDecksData(),
            streakCalendarData = streakCalendarItems,
            currentStreak = currentStreak?.length ?: 0,
            longestStreak = longestStreak?.length ?: 0
        )
    }

    private suspend fun getLetterDecksData(): LetterDecksData {
        val data = letterSrsManager.getUpdatedDecksData()
        if (data.decks.isEmpty()) return LetterDecksData.NoDecks

        return LetterDecksData.Data(
            practiceType = mutableStateOf(
                value = ScreenLetterPracticeType.from(
                    preferencesRepository.generalDashboardLetterPracticeType.get()
                )
            ),
            studyProgressMap = LetterPracticeType.values().associate { practiceType ->
                val new = mutableMapOf<String, Long>()
                val due = mutableMapOf<String, Long>()

                data.decks
                    .map {
                        it.id to when (practiceType) {
                            LetterPracticeType.Writing -> it.writingDetails
                            LetterPracticeType.Reading -> it.readingDetails
                        }
                    }
                    .forEach { (deckId, srsProgress) ->
                        new.putAll(srsProgress.new.associateWith { deckId })
                        due.putAll(srsProgress.due.associateWith { deckId })
                    }

                ScreenLetterPracticeType.from(practiceType) to LetterDecksStudyProgress(
                    newToDeckIdMap = new.toList().take(data.dailyProgress.newLeft).toMap(),
                    dueToDeckIdMap = due.toList().take(data.dailyProgress.dueLeft).toMap()
                )
            }
        )
    }

    private suspend fun getVocabDecksData(): VocabDecksData {
        val data = vocabSrsManager.getUpdatedDecksData()
        if (data.decks.isEmpty()) return VocabDecksData.NoDecks

        val preferencesPracticeType = ScreenVocabPracticeType.from(
            preferencesRepository.generalDashboardVocabPracticeType.get()
        )

        return VocabDecksData.Data(
            practiceType = mutableStateOf(preferencesPracticeType),
            studyProgressMap = ScreenVocabPracticeType.values().associateWith { practiceType ->
                val due = mutableMapOf<Long, Long>()

                data.decks
                    .map { it.id to it.summaries.getValue(practiceType.dataType) }
                    .forEach { (deckId, srsProgress) ->
                        due.putAll(srsProgress.due.associateWith { deckId })
                    }

                VocabDecksStudyProgress(
                    dueToDeckIdMap = due
                )
            }
        )
    }

    companion object {
        private const val STREAK_CALENDAR_DAYS = 7
    }

}