package quanti.com.kotlinlog

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.file.FileLogger
import quanti.com.kotlinlog.file.bundle.DayLogBundle
import quanti.com.kotlinlog.utils.ActualTime
import quanti.com.kotlinlog.utils.getFormattedFileNameForDayTemp
import quanti.com.kotlinlog.utils.getRandomString
import java.io.File


@RunWith(RobolectricTestRunner::class)
class FileLoggerTest {

    private lateinit var appCtx: Context
    private val flb = DayLogBundle(LogLevel.VERBOSE, maxDaysSaved = 2)
    private lateinit var logger: FileLogger
    private lateinit var file: File

    @Before
    fun init() {
        RuntimeEnvironment.application.applicationInfo.nonLocalizedLabel = "FAKE APP NAME"

        appCtx = RuntimeEnvironment.application.applicationContext
        logger = FileLogger(appCtx, flb, true)
        Log.addLogger(logger)

        val fileName = getFormattedFileNameForDayTemp() + "_day.log"
        file = File(appCtx.filesDir, fileName)
    }

    /**
     * Checks if daylog is created
     * Sync version
     */
    @Test
    fun daylogIsCreated() {
        //force some logs
        Log.iSync("Ahoj")

        //check if file exists
        assert(file.exists())
    }

    /**
     * Checks if given information is written into daylog
     * Sync version
     */
    @Test
    fun daylogIsCreatedAndProperlyWrittenOneLine() {
        val str = "Hello world xd"
        Log.iSync(str)

        //check if file contains
        val lines = file.readLines()

        assert(lines.size == 1)
        assert(lines[0].contains(str))
    }

    /**
     * Checks if more strings are written into daylog
     * Sync version
     */
    @Test
    fun daylogIsCreatedAndProperlyWrittenHundredLines() {
        //create random string
        val list = (1..100)
                .map { getRandomString() }
                .onEach { Log.iSync(it) }
                .toList()

        //check if file contains
        val lines = file.readLines()

        assert(lines.size == list.size)

        val result = lines
                .zip(list)
                .all {
                    val strFromFile = it.first
                    val strFromList = it.second
                    strFromFile.contains(strFromList)
                }
        assert(result)
    }

    /**
     * Checks if daylog is created
     * Async version
     */
    @Test
    fun daylogIsCreatedAsync() {
        //force some logs
        Log.i("Ahoj")

        //wait 6 seconds, beacuse thread executor writes every 5 seconds
        Thread.sleep(6000)
        //check if file exists
        assert(file.exists())
    }

    /**
     * Checks if more strings are written into daylog
     * Async version
     */
    @Test
    fun daylogIsCreatedAndProperlyWrittenHundredLinesAsync() {
        //create random string
        val list = (1..100)
                .map { getRandomString() }
                .onEach { Log.i(it) }
                .toList()

        Thread.sleep(6000)

        //check if file contains
        val lines = file.readLines()
        assert(lines.size == list.size)

        val result = lines
                .zip(list)
                .all {
                    val strFromFile = it.first
                    val strFromList = it.second
                    strFromFile.contains(strFromList)
                }
        assert(result)
    }

    /**
     * Checks if daylog is created if lot of threads are writting at once
     * Check if everything is properly written
     * Async version
     */
    @Test
    fun daylogIsCreatedAndLotOfThreadAreWrittingAsync() {
        //force some logs
        val threads = 85
        val strings = 999
        runBlocking {
            (1..threads).forEach { threadNum ->
                launch {
                    (1..strings)
                            .map { getRandomString() }
                            .onEach { Log.i("$threadNum $it") }
                            .toList()
                }
            }
        }

        //wait 6 seconds, beacuse thread executor writes every 5 seconds
        Thread.sleep(6000L)
        //check if file contains
        val count = file.readLines().count()
        println(count)

        assert(count == threads * strings)
    }

    /**
     * Checks if daylog is created if two threads are writting sync at once
     * Check if everything is properly written
     * Async version
     */
    @Test
    fun daylogIsCreatedAnTwoThreadsAreWrittingSync() {
        //force some logs
        val threads = 2
        val strings = 100000


        runBlocking {
            repeat(threads) { thread ->
                launch(Dispatchers.Default) {
                    repeat(strings) {
                        Log.iSync("$thread TAG", getRandomString());
                    }
                }
            }
        }

        //wait 6 seconds, beacuse thread executor writes every 5 seconds
        Thread.sleep(6000L)
        //check if file contains
        val count = file.readLines().onEach { println(it) }.count()
        println(count)

        assert(count == threads * strings)
    }

    @Test
    fun dayLogIsCreatedAndThanDeletedWhenNewDay() {
        Log.iSync("Random text")

        ActualTime.shiftByOneDay()
        ActualTime.shiftByOneDay()
        ActualTime.shiftByOneDay() //aka three days as in bundle
        val afterName = getFormattedFileNameForDayTemp() + "_day.log"

        //wait 5 seconds for clean to happen
        Thread.sleep(6000)
        Log.iSync("Random text new day")


        val count = appCtx.filesDir.listFiles().count()
        Assert.assertEquals(1, count)
        Assert.assertEquals(appCtx.filesDir.listFiles()[0].name, afterName)

    }

    @Test
    fun dayLogIsCreatedAndThanDeletedWhenNewDayMore() {
        val firstName = getFormattedFileNameForDayTemp() + "_day.log"
        Log.iSync("Random text 0")
        ActualTime.shiftByOneDay()
        Thread.sleep(6000)

        Log.iSync("Random text 1")
        ActualTime.shiftByOneDay()
        Thread.sleep(6000)

        Log.iSync("Random text 2")
        ActualTime.shiftByOneDay()
        Thread.sleep(6000)

        Log.iSync("Random text 3")


        val count = appCtx.filesDir.listFiles().count()
        Assert.assertEquals(3, count)
//        Assert.assertEquals(appCtx.filesDir.listFiles()[0].name, afterName)

    }


}