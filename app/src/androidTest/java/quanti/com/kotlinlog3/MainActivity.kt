package quanti.com.kotlinlog3

import android.content.Context
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import quanti.com.kotlinlog.file.file.DayLogFile
import quanti.com.kotlinlog.utils.logFilesDir
import java.io.File


@RunWith(AndroidJUnit4::class)
@LargeTest
class HelloWorldEspressoTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)
    private lateinit var appCtx: Context
    private lateinit var file: File

    @Before
    fun init() {
        appCtx = activityRule.activity.applicationContext
        onView(withId(R.id.delete)).perform(click())
        val dayFile = DayLogFile(appCtx, 7)
        file = File(appCtx.filesDir, dayFile.fileName)
    }

    @Test
    fun button_hitme() {
        onView(withId(R.id.hitme)).perform(click())
        //sleep to force write
        Thread.sleep(6000L)

        val any = file
                .readLines()
                .map { it.contains(RANDOM_TEXT) }
                .any()

        Assert.assertEquals(true, any)
    }

    @Test
    fun button_test1() {
        onView(withId(R.id.test1)).perform(click())

        //sleep to force write
        Thread.sleep(6000L)
        val count = file
                .readLines()
                .count()

        Assert.assertEquals(50, count)
    }

    @Test
    fun button_test2() {
        onView(withId(R.id.test2)).perform(click())

        //sleep to force write
        Thread.sleep(6000L)
        val count = file
                .readLines()
                .count()

        Assert.assertEquals(5000, count)
    }

    @Test
    fun button_test3() {
        onView(withId(R.id.test3)).perform(click())

        //wait some time for everything to happen
        Thread.sleep(6000L)


        val count = appCtx
                .logFilesDir
                .listFiles()
                .filter { it.name.contains("ArrayIndexOutOf") }
                .count()

        Assert.assertTrue("At least one handled exception file should be present.", count >= 1)
    }

    @Test
    fun button_test4() {
        onView(withId(R.id.throwu)).perform(click())

        //wait some time for everything to happen
        Thread.sleep(6000L)


        val count = appCtx
                .logFilesDir
                .listFiles()
                .filter { it.name.contains("unhandled") }
                .count()

        Assert.assertTrue("At least one handled exception file should be present.", count >= 1)
    }

    @Test
    fun button_testStrictCircle() {
        onView(withId(R.id.switchButton))
                .perform(click())
                .perform(click())

        onView(withId(R.id.test2))
                .perform(click())
                .perform(click())
                .perform(click())
                .perform(click())
                .perform(click())

        //wait some time for everything to happen
        Thread.sleep(12000L)

        val filtered = appCtx
                .logFilesDir
                .listFiles()
                .filter { it.name.contains("strictcircle") }

        val countFull = filtered.count { it.length() > 1022 * 1024 }
        val countEmpty = filtered.count() - countFull

        Assert.assertEquals("There should be two full files.", 2, countFull)
        Assert.assertEquals("There should be one opened file.", 1, countEmpty)
    }

    @Test
    fun button_testStrictCircleDeletesOldFiles() {
        onView(withId(R.id.switchButton))
                .perform(click())
                .perform(click())

        //write huge amount of data
        onView(withId(R.id.test2))
                .perform(click()).perform(click()).perform(click())
                .perform(click()).perform(click()).perform(click())
                .perform(click()).perform(click()).perform(click())
                .perform(click()).perform(click()).perform(click())
                .perform(click()).perform(click()).perform(click())
                .perform(click()).perform(click()).perform(click())
                .perform(click()).perform(click()).perform(click())

        //wait some time for everything to happen
        Thread.sleep(12000L)

        val count = appCtx
                .logFilesDir
                .listFiles()
                .filter { it.name.contains("strictcircle") }
                .count()

        Assert.assertEquals("There should be 4 files.", 4, count)
    }

}







