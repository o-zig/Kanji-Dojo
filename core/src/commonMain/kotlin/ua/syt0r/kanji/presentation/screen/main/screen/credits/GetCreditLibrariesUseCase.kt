package ua.syt0r.kanji.presentation.screen.main.screen.credits

import com.mikepenz.aboutlibraries.Libs

interface GetCreditLibrariesUseCase {
    operator fun invoke(): Libs
}