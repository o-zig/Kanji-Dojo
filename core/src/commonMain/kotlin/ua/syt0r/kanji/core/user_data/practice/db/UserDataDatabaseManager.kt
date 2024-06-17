package ua.syt0r.kanji.core.user_data.practice.db

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.readUserVersion
import ua.syt0r.kanji.core.transferToCompat
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.userdata.db.PracticeQueries
import java.io.File
import java.io.InputStream

interface UserDataDatabaseManager {

    val databaseChangeFlow: SharedFlow<Unit>

    suspend fun <T> runTransaction(block: PracticeQueries.() -> T): T

    suspend fun doWithSuspendedConnection(
        scope: suspend (info: UserDatabaseInfo) -> Unit
    )

    suspend fun replaceDatabase(inputStream: InputStream)

}

class UserDatabaseInfo(
    val version: Long,
    val file: File
)

abstract class BaseUserDataDatabaseManager(
    private val coroutineScope: CoroutineScope
) : UserDataDatabaseManager {

    protected data class DatabaseConnection(
        val sqlDriver: SqlDriver,
        val database: UserDataDatabase
    )

    private val currentDatabaseConnection = MutableStateFlow<Deferred<DatabaseConnection>?>(
        value = createDeferredDatabaseConnection()
    )

    private val _databaseChangeFlow = MutableSharedFlow<Unit>()
    override val databaseChangeFlow: SharedFlow<Unit> = _databaseChangeFlow

    protected fun getMigrationCallbacks(): Array<AfterVersion> = arrayOf(
        AfterVersion(3) { UserDataDatabaseMigrationAfter3.handleMigrations(it) },
        AfterVersion(4) { UserDataDatabaseMigrationAfter4.handleMigrations(it) }
    )

    protected abstract suspend fun createDatabaseConnection(): DatabaseConnection

    protected abstract fun getDatabaseFile(): File

    override suspend fun <T> runTransaction(
        block: PracticeQueries.() -> T
    ): T {
        return withContext(coroutineScope.coroutineContext) {
            val queries = waitDatabaseConnection().database.practiceQueries
            queries.transactionWithResult { queries.block() }
        }
    }

    override suspend fun doWithSuspendedConnection(
        scope: suspend (info: UserDatabaseInfo) -> Unit
    ) {
        val info = getActiveDatabaseInfo()
        closeCurrentConnection()
        val result = runCatching { scope(info) }
        currentDatabaseConnection.value = createDeferredDatabaseConnection()
        result.exceptionOrNull()?.let { throw it }
    }

    override suspend fun replaceDatabase(inputStream: InputStream) {
        doWithSuspendedConnection {
            val databaseFile = getDatabaseFile()
            databaseFile.delete()
            inputStream.use { it.transferToCompat(databaseFile.outputStream()) }
        }
        _databaseChangeFlow.emit(Unit)
    }

    private suspend fun closeCurrentConnection() = coroutineScope.launch {
        coroutineScope {
            currentDatabaseConnection.value?.await()?.sqlDriver?.close()
            currentDatabaseConnection.value = null
        }
    }

    private suspend fun getActiveDatabaseInfo(): UserDatabaseInfo {
        return UserDatabaseInfo(
            version = waitDatabaseConnection().sqlDriver.readUserVersion(),
            file = getDatabaseFile()
        )
    }

    private fun createDeferredDatabaseConnection(): Deferred<DatabaseConnection> {
        return coroutineScope.async { createDatabaseConnection() }
    }

    private suspend fun waitDatabaseConnection(): DatabaseConnection {
        return currentDatabaseConnection.filterNotNull().first().await()
    }

}