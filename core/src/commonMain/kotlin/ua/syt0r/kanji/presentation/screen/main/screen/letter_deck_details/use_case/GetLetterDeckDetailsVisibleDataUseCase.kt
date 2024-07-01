package ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.use_case

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.DeckDetailsLayout
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.DeckDetailsListItem
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.DeckDetailsListItemKey
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.DeckDetailsVisibleData
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.LetterDeckDetailsConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.LetterDeckDetailsItemData
import ua.syt0r.kanji.presentation.screen.main.screen.letter_deck_details.data.MutableDeckDetailsVisibleData

interface GetLetterDeckDetailsVisibleDataUseCase {

    operator fun invoke(
        items: List<LetterDeckDetailsItemData>,
        configuration: LetterDeckDetailsConfiguration,
        currentVisibleData: DeckDetailsVisibleData?,
        currentSelectionStates: Map<DeckDetailsListItemKey, MutableState<Boolean>>?
    ): LetterDeckDetailsStateCreationResult

}

data class LetterDeckDetailsStateCreationResult(
    val data: MutableDeckDetailsVisibleData,
    val selectionStates: Map<DeckDetailsListItemKey, MutableState<Boolean>>,
)

class DefaultGetLetterDeckDetailsVisibleDataUseCase(
    private val applyFilterUseCase: LetterDeckDetailsApplyFilterUseCase,
    private val applySortUseCase: LetterDeckDetailsApplySortUseCase,
    private val createGroupsUseCase: LetterDeckDetailsCreatePracticeGroupsUseCase,
) : GetLetterDeckDetailsVisibleDataUseCase {

    override fun invoke(
        items: List<LetterDeckDetailsItemData>,
        configuration: LetterDeckDetailsConfiguration,
        currentVisibleData: DeckDetailsVisibleData?,
        currentSelectionStates: Map<DeckDetailsListItemKey, MutableState<Boolean>>?,
    ): LetterDeckDetailsStateCreationResult {

        val isSelectionModeEnabled = mutableStateOf(
            value = currentVisibleData?.isSelectionModeEnabled?.value ?: false
        )

        val visibleItems = applyFilterUseCase(
            items = items,
            practiceType = configuration.practiceType,
            filterConfiguration = configuration.filterConfiguration
        )
            .let {
                applySortUseCase(
                    items = it,
                    sortOption = configuration.sortOption,
                    isDescending = configuration.isDescending
                )
            }

        return when (configuration.layout) {
            DeckDetailsLayout.SingleCharacter -> {
                val selectionStates = mutableMapOf<DeckDetailsListItemKey, MutableState<Boolean>>()
                val data = MutableDeckDetailsVisibleData.Items(
                    items = visibleItems.map {
                        val selectionState = mutableStateOf(false)
                        val item = DeckDetailsListItem.Letter(
                            item = it,
                            selected = selectionState
                        )

                        currentSelectionStates?.get(item.key)?.value?.also {
                            selectionState.value = it
                        }

                        selectionStates[item.key] = selectionState
                        item
                    },
                    isSelectionModeEnabled = isSelectionModeEnabled,
                    configuration = configuration
                )
                LetterDeckDetailsStateCreationResult(
                    data = data,
                    selectionStates = selectionStates
                )
            }

            DeckDetailsLayout.Groups -> {
                val groupsCreationResult = createGroupsUseCase(
                    items = items,
                    visibleItems = visibleItems,
                    type = configuration.practiceType,
                    probeKanaGroups = configuration.kanaGroups,
                    previousSelectionStates = currentSelectionStates
                )

                val currentSelectedGroup = currentVisibleData
                    ?.let { it as? DeckDetailsVisibleData.Groups }
                    ?.selectedItem
                    ?.value

                val updatedSelectedGroup = currentSelectedGroup?.index?.let { groupIndex ->
                    groupsCreationResult.groups.find { it.index == groupIndex }
                }

                val data = MutableDeckDetailsVisibleData.Groups(
                    kanaGroupsMode = groupsCreationResult.kanaGroups,
                    items = groupsCreationResult.groups,
                    selectedItem = mutableStateOf(updatedSelectedGroup),
                    isSelectionModeEnabled = isSelectionModeEnabled,
                    configuration = configuration
                )

                LetterDeckDetailsStateCreationResult(
                    data = data,
                    selectionStates = groupsCreationResult.selectionStates
                )
            }
        }
    }

}