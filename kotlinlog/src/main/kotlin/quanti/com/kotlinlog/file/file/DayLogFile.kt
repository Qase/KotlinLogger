package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.file.base.FileLoggerBundle
import quanti.com.kotlinlog.utils.getFormattedFileNameForDayTemp

/**
 * Created by Trnka Vladislav on 11.09.2017.
 *
 * Another version of LogFile with changed naming
 */
class DayLogFile(
        ctx: Context,
        bun: FileLoggerBundle
) : BaseLogFile(
        ctx,
        DayLogFile.getNewFileName(),
        bun.maxDaysSaved
) {

    companion object {
        private fun getNewFileName() =
                "${getFormattedFileNameForDayTemp()}_day.log"
    }

}