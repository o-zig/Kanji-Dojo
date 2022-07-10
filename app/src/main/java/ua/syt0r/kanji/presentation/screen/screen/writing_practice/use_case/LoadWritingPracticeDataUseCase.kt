package ua.syt0r.kanji.presentation.screen.screen.writing_practice.use_case

import kotlinx.coroutines.delay
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji_dojo.shared.hiraganaToRomaji
import ua.syt0r.kanji_dojo.shared.katakanaToRomaji
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.ReviewCharacterData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration
import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable
import ua.syt0r.kanji_dojo.shared.isHiragana
import ua.syt0r.kanji_dojo.shared.isKana
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class LoadWritingPracticeDataUseCase @Inject constructor(
    private val kanjiRepository: KanjiDataRepository
) {

    companion object {
        private const val MINIMAL_LOADING_TIME = 600L //TODO replace with animation delay
    }

    suspend fun load(practiceConfiguration: WritingPracticeConfiguration): List<ReviewCharacterData> {

        val loadingStartTime = System.currentTimeMillis()

        val kanjiDataList = practiceConfiguration.characterList.map { character ->
            val strokes = parseKanjiStrokes(kanjiRepository.getStrokes(character))
            when {
                character.first().isKana() -> {
                    val isHiragana = character.first().isHiragana()
                    ReviewCharacterData.KanaReviewData(
                        character = character,
                        strokes = strokes,
                        kanaSystem = if (isHiragana) "Hiragana" else "Katakana",
                        romaji = if (isHiragana) hiraganaToRomaji(character.first())
                        else katakanaToRomaji(character.first())
                    )
                }
                else -> {
                    val readings = kanjiRepository.getReadings(character)
                    ReviewCharacterData.KanjiReviewData(
                        character = character,
                        on = readings.filter { it.value == KanjiReadingTable.ReadingType.ON }
                            .keys
                            .toList(),
                        kun = readings.filter { it.value == KanjiReadingTable.ReadingType.KUN }
                            .keys
                            .toList(),
                        meanings = kanjiRepository.getMeanings(character),
                        strokes = parseKanjiStrokes(
                            strokes = kanjiRepository.getStrokes(character)
                        )
                    )
                }
            }

        }

        val timeToMinimalLoadingLeft = MINIMAL_LOADING_TIME - System.currentTimeMillis() +
                loadingStartTime
        val delayTime = max(0, min(MINIMAL_LOADING_TIME, timeToMinimalLoadingLeft))

        delay(delayTime)

        return kanjiDataList
    }

}