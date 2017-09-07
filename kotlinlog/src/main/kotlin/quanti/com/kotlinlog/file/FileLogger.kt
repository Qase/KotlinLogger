package quanti.com.kotlinlog.file

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider.getUriForFile
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.getLogLevelString
import quanti.com.kotlinlog.utils.*
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException


/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Implementation of file logger
 *
 */
class FileLogger {

    companion object : ILogger {

        private var wasNotSetUp = true
        private lateinit var ctx: Context
        private lateinit var bun: FileLoggerBundle
        private lateinit var actualLogFile: LogFile

        fun setUp(ctx: Context, bun: FileLoggerBundle = FileLoggerBundle()) {
            this.ctx = ctx
            this.bun = bun

            //check permission
            if (!Companion.ctx.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                android.util.Log.e("FileLogger", "Give me write external storage permission to use this logger")
                throw SecurityException("Give me ${Manifest.permission.WRITE_EXTERNAL_STORAGE}")
            }

            LogFile.removeAllOldTemps(Companion.ctx, Companion.bun.maxDaysSaved)

            actualLogFile = LogFile(Companion.ctx, Companion.bun)
            wasNotSetUp = false
        }


        private fun getDayTemp() = "${getFormattedFileNameForDayTemp()}_daytemp.log"

        override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {

            if (wasNotSetUp) {
                throw Exception("Logger was not initialized - call method setUp first")
            }

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
                mergeToFile(actualLogFile.getName(), getDayTemp(), ctx) //todo make reactive
                actualLogFile.delete()

                actualLogFile = LogFile(ctx, bun)
            }

            //append to file
            actualLogFile.write(formattedString)
        }

        override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {
            log(androidLogLevel, tag, methodName, text + "\n" + t.message)
        }

        fun getZipOfLogsUri(fileAge: Int = bun.maxDaysSaved): Observable<Uri> {
            return getZipOfLogs(fileAge)
                    .map {
                        getUriForFile(ctx, "com.quanti.kotlinlog", it)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
        }

        fun copyLogsToSDCard(fileAge: Int = bun.maxDaysSaved): Observable<File> {
            return getZipOfLogs(fileAge)
                    .map {
                        val dir = File(Environment.getExternalStorageDirectory(), bun.sdCardFolderName)
                        dir.mkdirs()
                        val output = File(dir, it.name)
                        it.copyTo(output, true)
                        return@map output
                    }
                    .map {
                        it.scanFile(ctx)
                        return@map it
                    }
                    .observeOn(AndroidSchedulers.mainThread())
        }

        private fun getZipOfLogs(fileAge: Int): Observable<File> {
            val listFiles = ctx.filesDir.listFiles(FileFilter {
                it.name.endsWith(".log") and (it.fileAge() <= fileAge)
            })

            if (listFiles.isEmpty()) {
                return Observable.error {FileNotFoundException("No files were found")}
            }

            val zipFile = File(ctx.filesDir, "${getFormattedFileNameForDayTemp()}.zip")
            zipFile.createNewFile()  //create file if not exists

            return Observable.just(zipFile).map { listFiles.zip(it) }.observeOn(AndroidSchedulers.mainThread())
        }

        /**
         * Returns android-log like formatted string
         */
        private fun getFormatedString(logLevel: Int, tag: String, methodName: String, text: String): String {
            val s = "%s/%s %s/%s:\t%s\n" //date/class  LOG_TYPE/method name: text
            return String.format(
                    s,
                    getFormatedNow(),
                    tag,
                    logLevel.getLogLevelString(),
                    methodName,
                    text
            )
        }
    }
}
