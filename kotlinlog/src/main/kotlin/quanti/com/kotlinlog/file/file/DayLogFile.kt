package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.utils.*
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Trnka Vladislav on 13.09.2017.
 *
 * Implementation of ILogFile that works like this:
 * --every day creates new file for all logs
 * --deletes old files when their age exceeds 'maxDays' parameter
 *
 * Writing is thread safe using ReentrantLock
 *
 */

class DayLogFile(
        ctx: Context,
        private val maxDays: Int
) : AbstractLogFile(ctx) {
    override val logIdentifier: String = "daylog"

    override var fileName: String = createNewFileName()
    override var file: File = File(ctx.logFilesDir, fileName)
    override var fos: FileOutputStream = ctx.openLogFileOutput(fileName, true)

    override fun cleanFolder() {
        //switch to new file if needed
        loga("fileAge: " + file.fileAge())
        loga("fileName: " + file.name)
        if (file.fileAge() > 0 || !file.name!!.contains(getFormattedFileNameForDayTemp())) {
            createNewFile()
        }

        //remove all zips
        listOfLoggerFiles().deleteAllZips()

        //remove all files older than x days
        loga("max days saved: $maxDays")
        listOfLoggerFiles().deleteAllOldFiles(maxDays)
    }


    override fun createNewFileName(): String {
        return "${getFormattedFileNameForDayTemp()}_$logIdentifier.log"
    }
}