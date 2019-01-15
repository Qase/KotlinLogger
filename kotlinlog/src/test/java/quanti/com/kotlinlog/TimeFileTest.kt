package quanti.com.kotlinlog

import android.content.Context
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import quanti.com.kotlinlog.utils.ActualTime
import quanti.com.kotlinlog.utils.fileAge
import quanti.com.kotlinlog.utils.getFormattedFileNameForDayTemp
import quanti.com.kotlinlog.utils.sortByAge
import java.io.File


@RunWith(RobolectricTestRunner::class)
class TimeFileTest {

    private lateinit var appCtx: Context

    @Suppress("DEPRECATION")
    @Before
    fun init() {
        RuntimeEnvironment.application.applicationInfo.nonLocalizedLabel = "FAKE APP NAME"
        appCtx = RuntimeEnvironment.application.applicationContext
        ActualTime.reset()
    }

    private fun createFile(filename: String = "temp.txt"): File {
        val file = File(appCtx.filesDir, filename)
        file.createNewFile()
        return file
    }

    @Test
    fun timeShift() {
        val nameBefore = getFormattedFileNameForDayTemp()
        ActualTime.shiftByOneDay()
        val nameAfter = getFormattedFileNameForDayTemp()
        Assert.assertEquals(false,nameAfter.contentEquals(nameBefore))
    }

    @Test
    fun fileAgeToday() {
        val file = createFile()
        Assert.assertEquals(0, file.fileAge())
    }

    @Test
    fun fileAgeYesterday() {
        val file = createFile()
        ActualTime.shiftByOneDay()
        Assert.assertEquals(1, file.fileAge())
    }

    @Test
    fun fileAgeTomorrowIsToday() {
        ActualTime.shiftByOneDay()
        val file = createFile()
        file.setLastModified(ActualTime.currentTimeMillis())
        Assert.assertEquals(0, file.fileAge())
    }

    @Test
    fun fileAgeTomorrow() {
        val file = createFile()
        ActualTime.shiftByOneDayBackward()
        Assert.assertEquals(-1, file.fileAge())
    }

    @Test
    fun sortFiles() {
        //since it was created sequentially, it must be sorted
        val test = "text".toByteArray()
        val list = (0..20).map {
            val file = createFile("temp$it.txt")
            file.writeBytes(test) //force file to be written to
            return@map file
        }.reversed()

        //create copy and shuffle and sort again
        val shuffle = list.toList().shuffled().sortByAge()

        Assert.assertEquals(list, shuffle)
    }

    @Test
    fun sortFilesOldest() {
        //since it was created sequentially, it must be sorted
        val test = "text".toByteArray()
        val list = (0..20).map {
            val file = createFile("temp$it.txt")
            file.writeBytes(test) //force file to be written to
            return@map file
        }

        //create copy and shuffle and sort again
        val shuffle = list.toList().shuffled().sortByAge(false)

        Assert.assertEquals(list, shuffle)
    }

}