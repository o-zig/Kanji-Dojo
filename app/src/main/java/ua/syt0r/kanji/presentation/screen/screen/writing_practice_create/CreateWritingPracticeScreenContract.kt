package ua.syt0r.kanji.presentation.screen.screen.writing_practice_create

import androidx.lifecycle.LiveData

interface CreateWritingPracticeScreenContract {

    interface ViewModel {
        val state: LiveData<State>
        fun submitUserInput(input: String)
        fun createSet(title: String)
    }

    enum class StateType {
        Loading,
        Loaded,
        Saving,
        Done
    }

    data class State(
        val data: Set<EnteredKanji>,
        val stateType: StateType
    )

    sealed class EnteredKanji(val kanji: String) {
        class Known(kanji: String) : EnteredKanji(kanji)
        class Unknown(kanji: String) : EnteredKanji(kanji)
    }

}
