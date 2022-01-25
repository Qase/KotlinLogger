package quanti.com.kotlinlog

import android.content.Context
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import quanti.com.kotlinlog.migration.KotlinLogMigrator
import quanti.com.kotlinlog.utils.logFilesDir
import java.io.File

@RunWith(RobolectricTestRunner::class)
class KotlinLogMigrationTest {

    private lateinit var appCtx: Context

    @Suppress("DEPRECATION")
    @Before
    fun init() {
        RuntimeEnvironment.application.applicationInfo.nonLocalizedLabel = "FAKE APP NAME"
        appCtx = RuntimeEnvironment.application.applicationContext
    }

    @Test
    fun testMigration() {
        setLastVersion(0)

        File(appCtx.filesDir, "test.log").createNewFile()
        File(appCtx.filesDir, "test2.log").createNewFile()
        File(appCtx.filesDir, "other-file.txt").createNewFile()

        KotlinLogMigrator.migrate(appCtx)

        Assert.assertEquals(2, appCtx.filesDir.listFiles()?.size) // text file + kotlinlog directory
        Assert.assertEquals(2, appCtx.logFilesDir.listFiles()?.size)
    }

    private fun setLastVersion(lastVersion: Int) {
        with(appCtx.getSharedPreferences("quanti.com.kotlinlog.library.preferences", Context.MODE_PRIVATE).edit()) {
            putInt("PREF_LAST_VERSION", lastVersion)
            commit()
        }

    }

}
