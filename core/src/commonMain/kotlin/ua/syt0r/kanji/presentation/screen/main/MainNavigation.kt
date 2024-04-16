package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ua.syt0r.kanji.core.japanese.CharacterClassification
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupScreen
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackScreen
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackTopic
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreen
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreen
import kotlin.reflect.KClass

interface MainNavigationState {
    fun navigateBack()
    fun popUpToHome()
    fun navigate(destination: MainDestination)
}

@Composable
expect fun rememberMainNavigationState(): MainNavigationState

@Composable
expect fun MainNavigation(state: MainNavigationState)

interface MainDestination {

    @Composable
    fun Draw(state: MainNavigationState)


    @Serializable
    object Home : MainDestination {

        @Composable
        override fun Draw(state: MainNavigationState) {
            HomeScreen(
                mainNavigationState = rememberUpdatedState(state)
            )
        }

    }

    @Serializable
    object About : MainDestination {

        @Composable
        override fun Draw(state: MainNavigationState) {
            AboutScreen(
                mainNavigationState = state
            )
        }

    }

    @Serializable
    object ImportPractice : MainDestination {

        @Composable
        override fun Draw(state: MainNavigationState) {
            PracticeImportScreen(
                mainNavigationState = state
            )
        }

    }

    @Serializable
    sealed interface CreatePractice : MainDestination {

        @Composable
        override fun Draw(state: MainNavigationState) {
            PracticeCreateScreen(
                configuration = this,
                mainNavigationState = state
            )
        }

        @Serializable
        object New : CreatePractice

        @Serializable
        data class EditExisting(
            val practiceId: Long
        ) : CreatePractice

        @Serializable
        data class Import(
            val title: String,
            val classification: CharacterClassification
        ) : CreatePractice

    }

    @Serializable
    data class PracticePreview(
        val id: Long
    ) : MainDestination {
        @Composable
        override fun Draw(state: MainNavigationState) {
            PracticePreviewScreen(
                practiceId = id,
                mainNavigationState = state
            )
        }
    }

    @Serializable
    sealed interface Practice : MainDestination {

        @Serializable
        data class Writing(
            val practiceId: Long,
            val characterList: List<String>
        ) : Practice {

            @Composable
            override fun Draw(state: MainNavigationState) {
                WritingPracticeScreen(
                    mainNavigationState = state,
                    configuration = this
                )
            }

        }

        @Serializable
        data class Reading(
            val practiceId: Long,
            val characterList: List<String>
        ) : Practice {

            @Composable
            override fun Draw(state: MainNavigationState) {
                ReadingPracticeScreen(
                    navigationState = state,
                    configuration = this
                )
            }

        }

    }

    @Serializable
    data class KanjiInfo(
        val character: String
    ) : MainDestination {

        @Composable
        override fun Draw(state: MainNavigationState) {
            KanjiInfoScreen(
                kanji = character,
                mainNavigationState = state
            )
        }

    }

    @Serializable
    object Backup : MainDestination {

        @Composable
        override fun Draw(state: MainNavigationState) {
            BackupScreen(state)
        }

    }

    @Serializable
    data class Feedback(
        val topic: FeedbackTopic
    ) : MainDestination {

        @Composable
        override fun Draw(state: MainNavigationState) {
            FeedbackScreen(
                feedbackTopic = topic,
                mainNavigationState = state
            )
        }

    }

}

data class MainDestinationConfiguration<T : MainDestination>(
    val clazz: KClass<T>,
    val subclassRegisterer: (PolymorphicModuleBuilder<MainDestination>) -> Unit
)

inline fun <reified T : MainDestination> KClass<T>.configuration(): MainDestinationConfiguration<T> {
    return MainDestinationConfiguration(
        clazz = this,
        subclassRegisterer = {
            it.subclass(
                subclass = this@configuration,
                serializer = kotlinx.serialization.serializer()
            )
        }
    )
}

val defaultMainDestinations: List<MainDestinationConfiguration<*>> = listOf(
    MainDestination.About::class.configuration(),
    MainDestination.Backup::class.configuration(),
    MainDestination.CreatePractice.EditExisting::class.configuration(),
    MainDestination.CreatePractice.Import::class.configuration(),
    MainDestination.CreatePractice.New::class.configuration(),
    MainDestination.Feedback::class.configuration(),
    MainDestination.Home::class.configuration(),
    MainDestination.ImportPractice::class.configuration(),
    MainDestination.KanjiInfo::class.configuration(),
    MainDestination.Practice.Reading::class.configuration(),
    MainDestination.Practice.Writing::class.configuration(),
    MainDestination.PracticePreview::class.configuration(),
)
