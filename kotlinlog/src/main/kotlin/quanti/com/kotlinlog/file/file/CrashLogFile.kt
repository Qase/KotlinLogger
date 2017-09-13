package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.file.base.FileLoggerBundle
import quanti.com.kotlinlog.utils.getFormatedFileNameDayNowWithSeconds

/**
 * Created by Trnka Vladislav on 04.07.2017.
 *
 */

class CrashLogFile(
        ctx: Context,
        bun: FileLoggerBundle,
        crashReason: String,
        wasFatal: Boolean = false
) : BaseLogFile(
        ctx,
        getNewCrashFileName(crashReason, wasFatal),
        bun.maxDaysSaved
) {

    companion object {
        private fun getNewCrashFileName(crashReason: String, wasFatal: Boolean): String {
            if (wasFatal) {
                return getFormatedFileNameDayNowWithSeconds() + "_unhandled_$crashReason.log"
            }
            return getFormatedFileNameDayNowWithSeconds() + "_handled_$crashReason.log"
        }
    }


}