package quanti.com.kotlinlog

import android.content.Context
import junit.framework.Assert
import junit.framework.AssertionFailedError
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import quanti.com.kotlinlog.utils.ActualTime
import quanti.com.kotlinlog.utils.fileAge
import quanti.com.kotlinlog.utils.getFormattedFileNameForDayTemp
import java.io.File


@RunWith(RobolectricTestRunner::class)
class TimeFileTest {

    private lateinit var appCtx: Context

    @Before
    fun init() {
        RuntimeEnvironment.application.applicationInfo.nonLocalizedLabel = "FAKE APP NAME"
        appCtx = RuntimeEnvironment.application.applicationContext
        ActualTime.reset()
    }


    @Test
    fun timeShift() {
        val nameBefore = getFormattedFileNameForDayTemp()
        ActualTime.shiftByOneDay()
        val nameAfter = getFormattedFileNameForDayTemp()
        assert(!nameAfter.contentEquals(nameBefore))
    }

    @Test
    fun fileAgeToday() {
        val file = File(appCtx.filesDir, "temp.txt")
        file.createNewFile()
        Assert.assertEquals(0, file.fileAge())
    }

    @Test
    fun fileAgeYesterday() {
        val file = File(appCtx.filesDir, "temp.txt")
        file.createNewFile()
        ActualTime.shiftByOneDay()
        Assert.assertEquals(1, file.fileAge())
    }

    @Test
    fun fileAgeTomorrowIsToday() {
        ActualTime.shiftByOneDay()
        val file = File(appCtx.filesDir, "temp.txt")
        file.createNewFile()
        file.setLastModified(ActualTime.currentTimeMillis())
        Assert.assertEquals(0, file.fileAge())
    }

    @Test
    fun fileAgeTomorrow() {
        val file = File(appCtx.filesDir, "temp.txt")
        file.createNewFile()
        ActualTime.shiftByOneDayBackward()
        Assert.assertEquals(-1, file.fileAge())
    }

}