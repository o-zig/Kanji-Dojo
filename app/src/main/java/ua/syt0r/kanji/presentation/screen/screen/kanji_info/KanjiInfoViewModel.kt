package ua.syt0r.kanji.presentation.screen.screen.kanji_info

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreenContract.ScreenState
import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable
import ua.syt0r.kanji_dojo.shared.hiraganaToRomaji
import ua.syt0r.kanji_dojo.shared.isHiragana
import ua.syt0r.kanji_dojo.shared.isKana
import ua.syt0r.kanji_dojo.shared.katakanaToHiragana
import javax.inject.Inject

@HiltViewModel
class KanjiInfoViewModel @Inject constructor(
    private val kanjiDataRepository: KanjiDataRepository
) : ViewModel(), KanjiInfoScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun loadCharacterInfo(character: String) {
        Logger.d(">>")
        viewModelScope.launch {
            Logger.d("coroutine >>")
            state.value = ScreenState.Loading
            val loadedState = getData(character.first())
            state.value = loadedState
            Logger.d("coroutine <<")
        }
        Logger.d("<<")
    }

    override fun onCleared() {
        Logger.logMethod()
        super.onCleared()
    }

    private suspend fun getData(character: Char): ScreenState.Loaded = withContext(Dispatchers.IO) {

        val strokes = parseKanjiStrokes(kanjiDataRepository.getStrokes(character.toString()))

        when {

            character.isKana() -> {
                val isHiragana = character.isHiragana()
                ScreenState.Loaded.Kana(
                    character = character.toString(),
                    kanaSystem = if (isHiragana) "Hiragana" else "Katakana",
                    reading = if (isHiragana) {
                        hiraganaToRomaji(character)
                    } else {
                        hiraganaToRomaji(katakanaToHiragana(character))
                    },
                    strokes = strokes
                )
            }

            else -> {
                val readings = kanjiDataRepository.getReadings(character.toString())
                val kanjiData = kanjiDataRepository.getData(character.toString())
                ScreenState.Loaded.Kanji(
                    kanji = character.toString(),
                    strokes = strokes,
                    meanings = kanjiDataRepository.getMeanings(character.toString()),
                    on = readings.filter { it.value == KanjiReadingTable.ReadingType.ON }
                        .map { it.key },
                    kun = readings.filter { it.value == KanjiReadingTable.ReadingType.KUN }
                        .map { it.key },
                    grade = kanjiData?.grade,
                    jlpt = kanjiData?.jlpt,
                    frequency = kanjiData?.frequency
                )

            }

        }
    }

}