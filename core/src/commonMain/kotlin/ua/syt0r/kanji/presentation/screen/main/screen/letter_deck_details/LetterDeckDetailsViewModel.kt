package ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.RefreshableData
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.launchUnit
import ua.syt0r.kanji.presentation.LifecycleAwareViewModel
import ua.syt0r.kanji.presentation.LifecycleState
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.LetterDeckDetailsContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.DeckDetailsListItem
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.DeckDetailsListItemKey
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.DeckDetailsVisibleData
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.LetterDeckDetailsConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.MutableDeckDetailsVisibleData
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.MutableLetterDeckDetailsLoadedState
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.PracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.use_case.GetLetterDeckDetailsConfigurationUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.use_case.GetLetterDeckDetailsVisibleDataUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.use_case.LetterDeckDetailsData
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.use_case.SubscribeOnLetterDeckDetailsDataUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.use_case.UpdateLetterDeckDetailsConfigurationUseCase

class LetterDeckDetailsViewModel(
    private val viewModelScope: CoroutineScope,
    private val subscribeOnDataUseCase: SubscribeOnLetterDeckDetailsDataUseCase,
    private val getConfigurationUseCase: GetLetterDeckDetailsConfigurationUseCase,
    private val updateConfigurationUseCase: UpdateLetterDeckDetailsConfigurationUseCase,
    private val getVisibleDataUseCase: GetLetterDeckDetailsVisibleDataUseCase,
    private val analyticsManager: AnalyticsManager,
) : LetterDeckDetailsContract.ViewModel, LifecycleAwareViewModel {

    private var practiceId: Long = -1L
    private val screenShownEvents = Channel<Unit>()

    private lateinit var loadedState: MutableLetterDeckDetailsLoadedState
    private lateinit var selectionStates: Map<DeckDetailsListItemKey, MutableState<Boolean>>

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    override val state: StateFlow<ScreenState> = _state

    override val lifecycleState: MutableStateFlow<LifecycleState> =
        MutableStateFlow(LifecycleState.Hidden)

    override fun notifyScreenShown(practiceId: Long) = viewModelScope.launchUnit {
        if (this@LetterDeckDetailsViewModel.practiceId != -1L) {
            screenShownEvents.send(Unit)
            return@launchUnit
        }

        this@LetterDeckDetailsViewModel.practiceId = practiceId
        subscribeOnDataUseCase(practiceId, lifecycleState)
            .onEach { it.applyToState() }
            .launchIn(viewModelScope)

        screenShownEvents.send(Unit)
    }

    override fun updateConfiguration(configuration: LetterDeckDetailsConfiguration) {
        if (loadedState.visibleDataState.value.configuration == configuration) {
            return
        }

        viewModelScope.launch {
            updateConfigurationUseCase(configuration)
            val result = getVisibleDataUseCase(
                items = loadedState.allItems,
                configuration = configuration,
                currentVisibleData = loadedState.mutableVisibleDataState.value.asImmutable,
                currentSelectionStates = selectionStates
            )
            selectionStates = result.selectionStates
            loadedState.mutableVisibleDataState.value = result.data
        }
    }

    override fun showGroupDetails(group: DeckDetailsListItem.Group) {
        val visibleData = loadedState.mutableVisibleDataState
            .value as MutableDeckDetailsVisibleData.Groups
        visibleData.selectedItem.value = group
    }

    override fun toggleSelectionMode() {
        var selectionModeEnabled by loadedState.mutableVisibleDataState.value.isSelectionModeEnabled
        selectionModeEnabled = !selectionModeEnabled
    }

    override fun toggleSelection(item: DeckDetailsListItem) {
        var mutableState by selectionStates.getValue(item.key)
        mutableState = !mutableState
    }

    override fun selectAll() {
        selectionStates.forEach { (_, visible) -> visible.value = true }
    }

    override fun deselectAll() {
        selectionStates.forEach { (_, visible) -> visible.value = false }
    }

    override fun getPracticeConfiguration(group: DeckDetailsListItem.Group): MainDestination.Practice {
        val characters = group.items.map { it.character }

        return when (loadedState.visibleDataState.value.configuration.practiceType) {
            PracticeType.Writing -> MainDestination.Practice.Writing(
                practiceId = practiceId,
                characterList = characters
            )

            PracticeType.Reading -> MainDestination.Practice.Reading(
                practiceId = practiceId,
                characterList = characters
            )
        }
    }

    override fun getMultiselectPracticeConfiguration(): MainDestination.Practice {
        val characters: List<String> = when (
            val currentState = loadedState.visibleDataState.value
        ) {
            is DeckDetailsVisibleData.Items -> currentState.items.asSequence()
                .filter { it.selected.value }
                .map { it.item.character }
                .toList()

            is DeckDetailsVisibleData.Groups -> currentState.items.asSequence()
                .filter { it.selected.value }
                .flatMap { it.items }
                .map { it.character }
                .toList()
        }

        return when (loadedState.visibleDataState.value.configuration.practiceType) {
            PracticeType.Writing -> MainDestination.Practice.Writing(
                practiceId = practiceId,
                characterList = characters
            )

            PracticeType.Reading -> MainDestination.Practice.Reading(
                practiceId = practiceId,
                characterList = characters
            )
        }
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("practice_preview")
    }

    private suspend fun RefreshableData<LetterDeckDetailsData>.applyToState() {
        when (this) {
            is RefreshableData.Loading -> {
                _state.value = ScreenState.Loading
            }

            is RefreshableData.Loaded -> {
                val configuration = getConfigurationUseCase()
                val currentVisibleData = when (::loadedState.isInitialized) {
                    true -> loadedState.mutableVisibleDataState.value.asImmutable
                    false -> null
                }
                val currentSelectionStates = when (::selectionStates.isInitialized) {
                    true -> selectionStates
                    false -> null
                }

                val visibleState = getVisibleDataUseCase(
                    items = value.items,
                    configuration = configuration,
                    currentVisibleData = currentVisibleData,
                    currentSelectionStates = currentSelectionStates
                )

                loadedState = MutableLetterDeckDetailsLoadedState(
                    title = value.deckTitle,
                    allItems = value.items,
                    sharePractice = value.sharePractice,
                    mutableVisibleDataState = mutableStateOf(visibleState.data)
                )
                selectionStates = visibleState.selectionStates

                _state.value = loadedState
            }
        }
    }

}