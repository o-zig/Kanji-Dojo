package ua.syt0r.kanji.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ua.syt0r.kanji.BuildConfig
import ua.syt0r.kanji.core.logger.LoggerConfiguration
import ua.syt0r.kanji.presentation.screen.AndroidGetCreditLibrariesUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.credits.GetCreditLibrariesUseCase

val appComponentsModule = module {

    factory { LoggerConfiguration(isEnabled = BuildConfig.DEBUG) }

    factory<GetCreditLibrariesUseCase> {
        AndroidGetCreditLibrariesUseCase(
            context = androidContext()
        )
    }

}