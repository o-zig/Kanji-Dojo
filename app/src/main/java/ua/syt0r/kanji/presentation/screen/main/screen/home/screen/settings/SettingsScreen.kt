package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainContract

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenContract.ViewModel = hiltViewModel<SettingsViewModel>(),
    navigation: MainContract.Navigation
) {

    SettingsScreenUI(
        viewModel.state.value,
        onAnalyticsToggled = { viewModel.updateAnalyticsEnabled(it) },
        onAboutButtonClick = { navigation.navigateToAbout() }
    )

}