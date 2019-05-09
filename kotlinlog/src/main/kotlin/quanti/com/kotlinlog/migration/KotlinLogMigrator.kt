package quanti.com.kotlinlog.migration

import android.content.Context
import quanti.com.kotlinlog.BuildConfig
import quanti.com.kotlinlog.utils.logFilesDir
import java.io.File

internal class KotlinLogMigrator(val context: Context) {
    companion object {
        private const val PREF_FILE_NAME = "quanti.com.kotlinlog.library.preferences"
        private const val PREF_LAST_VERSION = "PREF_LAST_VERSION"

        private val map: Map<Int, (Context) -> Unit> = mapOf(
                Pair(1, { context ->
                    val filesToCopy = context.filesDir.listFiles()?.filter { it.name.endsWith(".log") }
                    filesToCopy?.forEach {file ->
                        val newDestination = File(context.logFilesDir.absolutePath, file.name)
                        file.renameTo(newDestination)
                    }
                })
        )

        fun migrate(context: Context) {
            KotlinLogMigrator(context).migrate()
        }
    }

    private val preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    private fun migrate() {
        val startWith = lastVersion() + 1
        for (i in startWith..currentVersion()) {
            migrateTo(i)
            setLastVersion(i)
        }
    }

    private fun migrateTo(versioncode: Int) {
        map[versioncode]?.invoke(context)
    }

    private fun lastVersion(): Int {
        return preferences.getInt(PREF_LAST_VERSION, 0)
    }

    private fun setLastVersion(versioncode: Int) {
        with(preferences.edit()) {
            putInt(PREF_LAST_VERSION, versioncode)
            commit()
        }
    }

    private fun currentVersion(): Int {
        return BuildConfig.VERSION_CODE
    }

}