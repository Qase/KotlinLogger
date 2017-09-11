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
        crashReason: String
) : BaseLogFile(
        ctx,
        getNewCrashFileName(crashReason),
        bun.maxDaysSaved
) {

    companion object {
        private fun getNewCrashFileName(crashReason: String) =
                getFormatedFileNameDayNowWithSeconds() + "_crash_$crashReason.log"
    }


}