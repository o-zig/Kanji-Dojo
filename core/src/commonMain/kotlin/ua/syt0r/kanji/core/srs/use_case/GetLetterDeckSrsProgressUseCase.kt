package ua.syt0r.kanji.core.srs.use_case

import kotlinx.datetime.LocalDate
import ua.syt0r.kanji.core.srs.LetterDeckSrsProgress
import ua.syt0r.kanji.core.srs.LetterPracticeType
import ua.syt0r.kanji.core.srs.LetterSrsDeckInfo
import ua.syt0r.kanji.core.srs.SrsItemStatus
import ua.syt0r.kanji.core.user_data.practice.LetterPracticeRepository

interface GetLetterDeckSrsProgressUseCase {

    suspend operator fun invoke(
        deckId: Long,
        srsDate: LocalDate
    ): LetterSrsDeckInfo

}

class DefaultGetLetterDeckSrsProgressUseCase(
    private val repository: LetterPracticeRepository,
    private val getLetterSrsStatusUseCase: GetLetterSrsStatusUseCase,
) : GetLetterDeckSrsProgressUseCase {

    override suspend operator fun invoke(
        deckId: Long,
        srsDate: LocalDate
    ): LetterSrsDeckInfo {
        val deckInfo = repository.getDeck(deckId)
        val characters = repository.getDeckCharacters(deckId)

        return LetterSrsDeckInfo(
            id = deckId,
            title = deckInfo.name,
            position = deckInfo.position,
            characters = characters,
            writingDetails = getProgressForPracticeType(
                characters = characters,
                practiceType = LetterPracticeType.Writing,
                date = srsDate
            ),
            readingDetails = getProgressForPracticeType(
                characters = characters,
                practiceType = LetterPracticeType.Reading,
                date = srsDate
            )
        )
    }

    private suspend fun getProgressForPracticeType(
        characters: List<String>,
        practiceType: LetterPracticeType,
        date: LocalDate,
    ): LetterDeckSrsProgress {
        val charactersSrsData = characters.map { getLetterSrsStatusUseCase(it, practiceType, date) }

        val new = charactersSrsData.filter { it.status == SrsItemStatus.New }
            .map { it.character }
        val due = charactersSrsData.filter { it.status == SrsItemStatus.Review }
            .sortedByDescending { it.expectedReviewDate }
            .map { it.character }
        val done = charactersSrsData.filter { it.status == SrsItemStatus.Done }
            .map { it.character }

        return LetterDeckSrsProgress(
            charactersData = charactersSrsData.associateBy { it.character },
            all = charactersSrsData.map { it.character },
            done = done.toList(),
            due = due.toList(),
            new = new.toList()
        )
    }

}