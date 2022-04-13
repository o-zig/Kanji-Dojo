package ua.syt0r.kanji.core.user_data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.syt0r.kanji.core.user_data.db.converter.LocalDateTimeConverter
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntity
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntryEntity
import ua.syt0r.kanji.core.user_data.db.entity.WritingReviewEntity

@Database(
    entities = [
        PracticeSetEntity::class,
        PracticeSetEntryEntity::class,
        WritingReviewEntity::class
    ],
    version = 1
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class UserDataDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "user_data"

        fun create(context: Context): UserDataDatabase {
            return Room.databaseBuilder(context, UserDataDatabase::class.java, DB_NAME).build()
        }

    }

    abstract val dao: UserDataDao

}