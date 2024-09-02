package ua.syt0r.kanji.core.feedback

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import ua.syt0r.kanji.BuildConfig

class DefaultFeedbackUserDataProvider : FeedbackUserDataProvider {

    override suspend fun provide(): JsonObject {
        return JsonObject(
            mapOf("versionName" to JsonPrimitive(BuildConfig.versionName))
        )
    }

}