
import android.content.Context
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLog
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.file.FileLogger
import quanti.com.kotlinlog.file.base.FileLoggerBundle
import quanti.com.kotlinlog.utils.ActualTime
import quanti.com.kotlinlog.utils.getFormatedFileNameDayNow


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class FileLoggerTest {

    lateinit var shadowApp: ShadowApplication
    lateinit var ctx: Context

    val bundle = FileLoggerBundle(LogLevel.VERBOSE, 4, 200)

    @Before
    fun init() {
        //redirect output to console
        ShadowLog.stream = System.out

        //make shadowed version of app
        shadowApp = Shadows.shadowOf(RuntimeEnvironment.application)
        ctx = shadowApp.applicationContext

        //give permission
        shadowApp.grantPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //add logger
        //Log.addLogger(AndroidLogger())
        Log.addLogger(FileLogger)
        FileLogger.init(ctx)

    }

    /**
     * Test that logger creates one temp file
     */
    //@Test
    fun testOneTempFile() {
        for (i in 0..bundle.maxLogsPerOneFile - 1) {
            Log.i(testText)
        }
        Assert.assertEquals(1, ctx.filesDir.listFiles().size)
    }

    /**
     * Test that logger create one temp file, then merges its content to dayTemp, creates new temp and deletes old
     */
    //@Test
    fun testTwoTempFile() {
        for (i in 0..bundle.maxLogsPerOneFile) {
            Log.i(i.toString())
        }
        Assert.assertEquals(2, ctx.filesDir.listFiles().size)
    }

    //@Test
    fun testElevenTempFiles() {
        for (i in 0..bundle.maxLogsPerOneFile * 11) {
            Log.i(testText)
        }
        Assert.assertEquals(2, ctx.filesDir.listFiles().size)
    }

    //@Test
    fun testCreatingDayTemps() {
        val days = 50
        for (i in 0..days) {
            println(getFormatedFileNameDayNow())
            for (j in 0..bundle.maxLogsPerOneFile*3) {
                Log.i(testText)
            }
            Log.i("2222222")
            ActualTime.shiftByOneDay()
        }
        Assert.assertEquals(days + 2, ctx.filesDir.listFiles().size)
    }


    //@After
    fun after() {

        println("\n\nAFTER")
        ctx.filesDir.listFiles().map { it.absolutePath }.forEach { println(it) }

        println("CLEAN")
        ctx.filesDir.listFiles().forEach {
            val wasDeleted = it.delete()
            println("File ${it.name} $wasDeleted")
        }
    }


    companion object {
        val testText = "testText"
    }

}