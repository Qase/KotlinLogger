package quanti.com.kotlinlog.file

import android.content.Context
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.utils.mergeToFile


/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Implementation of file logger
 *
 * @param appCtx application (!) context
 * @param bun file logger settings
 */

class FileLogger @JvmOverloads constructor(
        appCtx: Context,
        bun: FileLoggerBundle = FileLoggerBundle()
)  : FileLoggerBase(appCtx, bun){


    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {

        if (androidLogLevel < bun.minimalLogLevel) {
            return
        }

        val formattedString = getFormatedString(androidLogLevel, tag, methodName, text)

        // if file is full - reached max lines -
        // copy contents to dayTemp
        // start a new new one
        // delete itself
        if (actualLogFile.isFull()) {
            actualLogFile.closeOutputStream()
            mergeToFile(actualLogFile.getName(), getDayTemp(), ctx)
            actualLogFile.delete()

            actualLogFile = LogFile(ctx, bun)
        }

        //append to file
        actualLogFile.write(formattedString)
    }

    override fun logThrowable(tag: String, methodName: String, text: String, t: Throwable) {
        log(LogLevel.ERROR, tag, methodName, text + "\n" + t.message)
    }
}
