package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.file.base.FileLoggerBundle
import quanti.com.kotlinlog.utils.getFormatedFileNameDayNow
import java.io.File

/**
 * Created by Trnka Vladislav on 11.09.2017.
 *
 * Log file that can count number of written logs
 */

class CounterLogFile(
        ctx: Context,
        var bun: FileLoggerBundle
): BaseLogFile(ctx, getNewTempFileName(), bun.maxDaysSaved){

    private var logsWritten = 0
    fun isFull() = logsWritten == bun.maxLogsPerOneFile


    override fun write(string: String) {
        fos.write(string.toByteArray())
        logsWritten++
    }

    companion object {
        /**
         * Returns temp file name with higher number on the end
         */
        private fun getNewTempFileName() =
                File.createTempFile(getFormatedFileNameDayNow() + "_temp", ".log").name
    }

}







