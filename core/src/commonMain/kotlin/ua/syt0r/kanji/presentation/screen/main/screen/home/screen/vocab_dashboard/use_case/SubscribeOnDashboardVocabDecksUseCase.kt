package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.vocab_dashboard.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ua.syt0r.kanji.core.RefreshableData
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.refreshableDataFlow
import ua.syt0r.kanji.core.srs.SrsCardKey
import ua.syt0r.kanji.core.srs.SrsItemRepository
import ua.syt0r.kanji.core.srs.SrsItemStatus
import ua.syt0r.kanji.core.srs.use_case.GetSrsStatusUseCase
import ua.syt0r.kanji.core.user_data.practice.VocabPracticeRepository
import ua.syt0r.kanji.presentation.LifecycleState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.vocab_dashboard.DashboardVocabDeck
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.vocab_dashboard.VocabDeckSrsProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.vocab_dashboard.hardcodedVocabDecks
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.toSrsItemKey

interface SubscribeOnDashboardVocabDecksUseCase {
    operator fun invoke(
        lifecycleState: StateFlow<LifecycleState>
    ): Flow<RefreshableData<VocabDecks>>
}

data class VocabDecks(
    val userDecks: List<DashboardVocabDeck.User>,
    val defaultDecks: List<DashboardVocabDeck.Default>
)

class DefaultSubscribeOnDashboardVocabDecksUseCase(
    private val practiceRepository: VocabPracticeRepository,
    private val srsItemRepository: SrsItemRepository,
    private val getSrsStatusUseCase: GetSrsStatusUseCase
) : SubscribeOnDashboardVocabDecksUseCase {

    override fun invoke(
        lifecycleState: StateFlow<LifecycleState>
    ): Flow<RefreshableData<VocabDecks>> {
        return refreshableDataFlow(
            dataChangeFlow = practiceRepository.changesFlow,
            lifecycleState = lifecycleState,
            valueProvider = { getUpdatedDecks() }
        )
    }

    private suspend fun getUpdatedDecks(): VocabDecks {
        Logger.logMethod()

        val userDecks = practiceRepository.getDecks()

        val srsItemStatusMap: Map<SrsCardKey, SrsItemStatus> = srsItemRepository.getAll()
            .mapValues { (_, srsCard) ->
                getSrsStatusUseCase(srsCard.lastReview?.plus(srsCard.interval))
            }

        val hardcodedDecks = hardcodedVocabDecks.mapIndexed { index, it ->
            DashboardVocabDeck.Default(
                titleResolver = it.titleResolver,
                words = it.expressionIds,
                srsProgress = getVocabDeckSrsProgress(it.expressionIds, srsItemStatusMap),
                index = index
            )
        }

        return VocabDecks(
            userDecks = userDecks.map {
                val words = practiceRepository.getDeckWords(it.id)
                DashboardVocabDeck.User(
                    titleResolver = { it.title },
                    words = words,
                    srsProgress = getVocabDeckSrsProgress(words, srsItemStatusMap),
                    id = it.id
                )
            },
            defaultDecks = hardcodedDecks
        )
    }

    private fun getVocabDeckSrsProgress(
        words: List<Long>,
        srsItemStatusMap: Map<SrsCardKey, SrsItemStatus>,
    ): Map<VocabPracticeType, VocabDeckSrsProgress> {
        return VocabPracticeType.values().associateWith { vocabPracticeType ->
            val done = mutableListOf<Long>()
            val due = mutableListOf<Long>()
            val new = mutableListOf<Long>()
            words.forEach { wordId ->
                val key = vocabPracticeType.toSrsItemKey(wordId)
                val status = srsItemStatusMap[key] ?: SrsItemStatus.New
                val list = when (status) {
                    SrsItemStatus.New -> new
                    SrsItemStatus.Done -> done
                    SrsItemStatus.Review -> due
                }
                list.add(wordId)
            }
            VocabDeckSrsProgress(
                all = words,
                done = done,
                due = due,
                new = new
            )
        }
    }

}