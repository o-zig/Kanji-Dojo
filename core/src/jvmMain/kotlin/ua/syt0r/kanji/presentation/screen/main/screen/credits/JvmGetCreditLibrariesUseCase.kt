package ua.syt0r.kanji.presentation.screen.main.screen.credits

import androidx.compose.ui.res.useResource
import com.mikepenz.aboutlibraries.Libs

object JvmGetCreditLibrariesUseCase : GetCreditLibrariesUseCase {
    override fun invoke(): Libs {
        val json = useResource("aboutlibraries.json") {
            it.bufferedReader().readText()
        }
        return Libs.Builder().withJson(json).build()
    }
}