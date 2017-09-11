package quanti.com.kotlinlog.file

import android.content.Context
import quanti.com.kotlinlog.utils.fileAge
import quanti.com.kotlinlog.utils.getFormatedFileNameDayNow
import quanti.com.kotlinlog.utils.getFormatedFileNameDayNowWithSeconds
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Trnka Vladislav on 04.07.2017.
 *
 */

class LogFile(
        ctx: Context,
        val bundle: FileLoggerBundle,
        crash: Boolean = false,
        crashReason: String = ""
) {

    private var logsWritten = 0
    private val file: File
    private val name: String
    private val fos: FileOutputStream

    init {
        name = if (crash) getNewCrashFile(crashReason) else getNewTempFile()

        file = File(ctx.filesDir, name)
        fos = ctx.openFileOutput(name, Context.MODE_PRIVATE)

        android.util.Log.i(TAG, "Creating new text file: " + file.absolutePath)
    }


    fun getName(): String = name

    fun isFull() = logsWritten == bundle.maxLogsPerOneFile

    fun closeOutputStream() = fos.close()

    fun write(string: String) {
        fos.write(string.toByteArray())
        logsWritten++
    }

    fun delete() {
        val del = file.delete()
        android.util.Log.i(TAG, "File ${file.absolutePath} was deleted: $del")
    }


    companion object {

        val TAG = "LogFile"

        /**
         * Returns temp file name with higher number on the end
         */
        private fun getNewTempFile() =
                File.createTempFile(getFormatedFileNameDayNow() + "_temp", ".log").name

        private fun getNewCrashFile(crashReason: String) =
                getFormatedFileNameDayNowWithSeconds() + "_crash_$crashReason.log"

        fun removeAllOldTemps(ctx: Context, maxDaysSaved: Int) {
            //check old files and remove them
            ctx.filesDir.listFiles().filter {
                it.name.contains("_temp") || it.fileAge() > maxDaysSaved
            }.forEach {
                android.util.Log.i("FileLogger", "Deleting old temp file" + it.absolutePath + "\tSuccess: " + it.delete())
            }
        }
    }


}