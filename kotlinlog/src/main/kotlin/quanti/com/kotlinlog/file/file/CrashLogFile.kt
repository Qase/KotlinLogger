package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.utils.*
import java.io.FileOutputStream

/**
 * Created by Trnka Vladislav on 04.07.2017.
 *
 */

class CrashLogFile(
        appCtx: Context,
        crashReason: String,
        wasFatal: Boolean = false) {

    private val fileName = if (wasFatal) {
        getFormattedFileNameDayNowWithSeconds() + "_unhandled_$crashReason.log"
    } else {
        getFormattedFileNameDayNowWithSeconds() + "_handled_$crashReason.log"
    }

    private var fos: FileOutputStream = appCtx.openLogFileOutput(fileName, true)

    fun write(string: String) {
        fos.write(string.toByteArray())
    }

    fun closeOutputStream() = fos.close()


    companion object {
        fun clean(appCtx: Context, maxDays: Int){
            //remove all files that exceeds specified limit
            appCtx.logFilesDir
                    .listFiles()
                    ?.filter { it.name.contains("handled") }
                    ?.deleteAllOldFiles(maxDays)
        }
    }

}