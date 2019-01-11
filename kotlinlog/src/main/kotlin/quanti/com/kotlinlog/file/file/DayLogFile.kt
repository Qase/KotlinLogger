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
        private val ctx: Context,
        private val maxDays: Int
) : AbstractLogFile(ctx) {
    override val logIdentifier: String = "daylog"

    override var fileName: String = createNewFileName()
    override var file: File = File(ctx.filesDir, fileName)
    override var fos: FileOutputStream = ctx.openFileOutput(fileName, Context.MODE_APPEND)


    override fun cleanFolder() {
        //switch to new file if needed
        loga("fileAge: " + file.fileAge())
        loga("fileName: " + file.name)
        if (file.fileAge() > 0 || !file.name!!.contains(getFormattedFileNameForDayTemp())) {
            createNewFile()
        }

        //remove all zips
        ctx.filesDir.listFiles().deleteAllZips()

        //remove all files older than x days
        ctx.filesDir.listFiles().deleteAllOldFiles(maxDays)
    }


    override fun createNewFileName(): String {
        val arr = arrayOf(getFormattedFileNameForDayTemp(), logIdentifier, LOG_FILE_EXTENSION)
        return arr.joinToString(separator = "_")
    }
}