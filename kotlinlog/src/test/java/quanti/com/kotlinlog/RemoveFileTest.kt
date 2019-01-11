package quanti.com.kotlinlog

import android.content.Context
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import quanti.com.kotlinlog.utils.ActualTime
import quanti.com.kotlinlog.utils.getRandomString
import quanti.com.kotlinlog.utils.deleteAllOldFiles
import quanti.com.kotlinlog.utils.deleteAllZips
import java.io.File

@RunWith(RobolectricTestRunner::class)
class RemoveFileTest {

    lateinit var appCtx: Context

    @Before
    fun init() {
        RuntimeEnvironment.application.applicationInfo.nonLocalizedLabel = "FAKE APP NAME"
        appCtx = RuntimeEnvironment.application.applicationContext

    }

    /**
     * Creates new file with overwritten lastmodified paramter to match ActualTime
     */
    private fun createFile(end: String = ".txt") {
        val name = getRandomString() + end
        val file = File(appCtx.filesDir, name)
        file.createNewFile()
        file.setLastModified(ActualTime.currentTimeMillis())
    }

    @Test
    fun createFileDoNotShift() {
        createFile()

        appCtx.filesDir.listFiles().deleteAllOldFiles(3)
        assert(appCtx.filesDir.listFiles().size == 1)
    }

    @Test
    fun createFileDoShift() {
        createFile()

        ActualTime.shiftByOneDay()

        appCtx.filesDir.listFiles().deleteAllOldFiles(0)
        assert(appCtx.filesDir.listFiles().isEmpty())
    }

    @Test
    fun createZip() {
        createFile(".zip")
        appCtx.filesDir.listFiles().deleteAllZips()
        assert(appCtx.filesDir.listFiles().isEmpty())
    }

    @Test
    fun moreFilesCombinedTypes() {
        val zipCount = 5
        val txtCount = 9
        (1..zipCount).forEach { createFile(".zip") }
        (1..txtCount).forEach { createFile(".txt") }

        appCtx.filesDir.listFiles().deleteAllZips()
        assert(appCtx.filesDir.listFiles().count() == txtCount)
    }

    @Test
    fun moreFilesCombinedTime() {

        val beforeCount = 5
        val afterCount = 9
        (1..beforeCount).forEach { createFile() }
        ActualTime.shiftByOneDay()
        (1..afterCount).forEach { createFile() }
        appCtx.filesDir.listFiles().deleteAllOldFiles(0)
        Assert.assertEquals(afterCount, appCtx.filesDir.listFiles().count())
    }


}