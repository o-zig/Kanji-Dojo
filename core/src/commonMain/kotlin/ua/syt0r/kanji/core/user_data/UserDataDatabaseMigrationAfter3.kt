package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.user_data.model.CharacterStudyProgress
import ua.syt0r.kanji.core.user_data.model.PracticeType

object UserDataDatabaseMigrationAfter3 {

    suspend fun handleMigrations(driver: SqlDriver) {
        migrateCharacterProgress(
            sqlDriver = driver,
            readTable = "writing_review",
            practiceType = 0
        )
        migrateCharacterProgress(
            sqlDriver = driver,
            readTable = "reading_review",
            practiceType = 1
        )
    }

    private suspend fun migrateCharacterProgress(
        sqlDriver: SqlDriver,
        readTable: String,
        practiceType: Int
    ) {
        val reviews = sqlDriver.executeQuery(
            identifier = null,
            sql = "SELECT * FROM $readTable",
            mapper = {
                val list = mutableListOf<Triple<String, Long, Long>>()
                while (it.next()) {
                    val character = it.getString(0)!!
                    val timestamp = it.getLong(2)!!
                    val mistakes = it.getLong(3)!!
                    list.add(Triple(character, timestamp, mistakes))
                }
                list
            },
            parameters = 0
        ).await()

        val characterProgresses = reviews.groupBy { it.first }
            .mapNotNull { (character, data) ->
                val lastReviewTime = data.maxOfOrNull { it.second }
                    ?: return@mapNotNull null

                val failedReviews = data.filter { it.third > 2 }
                val lastFailedReviewTime = failedReviews.maxOfOrNull { it.second } ?: 0

                val successfulReviewsAfterLastFailed =
                    data.filter { it.second >= lastFailedReviewTime }

                CharacterStudyProgress(
                    character = character,
                    practiceType = PracticeType.Writing,
                    lastReviewTime = Instant.fromEpochMilliseconds(lastReviewTime),
                    repeats = successfulReviewsAfterLastFailed.size,
                    lapses = failedReviews.size
                )
            }

        characterProgresses.forEach {
            it.apply {
                sqlDriver.execute(
                    identifier = null,
                    sql = "INSERT INTO character_progress(character, mode, last_review_time, repeats, lapses) VALUES('$character', $practiceType, ${lastReviewTime.toEpochMilliseconds()}, $repeats, $lapses)",
                    parameters = 0
                ).await()
            }
        }
    }

}