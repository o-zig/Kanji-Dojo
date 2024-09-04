import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import ua.syt0r.kanji.core.srs.fsrs.DefaultFsrsScheduler
import ua.syt0r.kanji.core.srs.fsrs.Fsrs5
import ua.syt0r.kanji.core.srs.fsrs.FsrsAlgorithm
import ua.syt0r.kanji.core.srs.fsrs.FsrsAnswers
import ua.syt0r.kanji.core.srs.fsrs.FsrsCard
import ua.syt0r.kanji.core.srs.fsrs.FsrsCardParams
import ua.syt0r.kanji.core.srs.fsrs.FsrsCardStatus.Learning
import ua.syt0r.kanji.core.srs.fsrs.FsrsCardStatus.Relearning
import ua.syt0r.kanji.core.srs.fsrs.FsrsCardStatus.Review
import ua.syt0r.kanji.core.srs.fsrs.FsrsReviewRating
import ua.syt0r.kanji.core.srs.fsrs.FsrsReviewRating.Again
import ua.syt0r.kanji.core.srs.fsrs.FsrsReviewRating.Easy
import ua.syt0r.kanji.core.srs.fsrs.FsrsReviewRating.Good
import ua.syt0r.kanji.core.srs.fsrs.FsrsReviewRating.Hard
import ua.syt0r.kanji.core.srs.fsrs.FsrsScheduler
import ua.syt0r.kanji.core.srs.fsrs.fsrs5Configuration
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

/***
 * Tests according to https://github.com/open-spaced-repetition/go-fsrs/blob/main/fsrs_test.go
 * Almost matching :)
 */
class FsrsSchedulerTest {

    private val testWeights = listOf(
        0.4197, 1.1869, 3.0412, 15.2441, 7.1434, 0.6477, 1.0007, 0.0674, 1.6597, 0.1712, 1.1178,
        2.0225, 0.0904, 0.3025, 2.1214, 0.2498, 2.9466, 0.4891, 0.6468,
    )

    private lateinit var fsrsAlgorithm: FsrsAlgorithm
    private lateinit var scheduler: FsrsScheduler
    private lateinit var now: Instant

    @BeforeTest
    fun setup() {
        fsrsAlgorithm = Fsrs5(
            configuration = fsrs5Configuration.copy(w = testWeights)
        )
        scheduler = DefaultFsrsScheduler(fsrsAlgorithm)
        now = LocalDateTime(2022, 11, 29, 12, 30)
            .toInstant(TimeZone.UTC)
    }

    @Test
    fun intervalsTest() {
        val ratings = listOf(
            Good, Good, Good, Good, Good, Good, Again, Again, Good, Good, Good, Good, Good
        )

        val srsCards = mutableListOf<FsrsCard>()

        var card = scheduler.newCard()
        var answers = scheduler.schedule(card, now)

        ratings.forEach { rating ->
            card = answers.get(rating)
            val params = card.params as FsrsCardParams.Existing
            val due = now + card.interval
            println("%s %f %f %s".format(rating.name, params.difficulty, params.stability, due))
            srsCards.add(card)
            now = due
            answers = scheduler.schedule(card, now)
        }

        // TODO find the difference    0, 4, 17, 62, 198, 563, 0, 0, 9, 27, 74, 190, 457
        val expectedIntervals = listOf(0, 4, 18, 68, 215, 356, 0, 0, 8, 26, 73, 188, 356)
        val resultIntervals = srsCards.map { it.interval.toDouble(DurationUnit.DAYS).toInt() }

        val expectedStatuses = listOf(
            Learning, Review, Review, Review, Review, Review, Relearning, Relearning,
            Review, Review, Review, Review, Review
        )
        val resultStatuses = srsCards.map { it.status }

        assertEquals(expectedIntervals, resultIntervals)
        assertEquals(expectedStatuses, resultStatuses)
    }

    @Test
    fun memoryStateTest() {
        val ratings = listOf(Again, Good, Good, Good, Good, Good, Good)
        val reviewIntervals = listOf(0, 0, 1, 3, 8, 21, 0)

        var time = Clock.System.now()
        val lastCard = ratings.zip(reviewIntervals)
            .fold(scheduler.newCard()) { card, (rating, interval) ->
                val answers = scheduler.schedule(card, time)
                val newCard = answers.get(rating)
                time = time.plus(interval.days)
                newCard
            }

        val params = lastCard.params as FsrsCardParams.Existing

        val decimalFormat = DecimalFormat("#.####")
        decimalFormat.roundingMode = RoundingMode.FLOOR

        val expectStability = 71.4554
        val resultStability = decimalFormat.format(params.stability).toDouble()

        val expectDifficulty = 5.0976
        val resultDifficulty = decimalFormat.format(params.difficulty).toDouble()

        assertEquals(expectStability, resultStability)
        assertEquals(expectDifficulty, resultDifficulty)
    }

    private fun FsrsAnswers.get(rating: FsrsReviewRating) = when (rating) {
        Again -> again
        Hard -> hard
        Good -> good
        Easy -> easy
    }

}