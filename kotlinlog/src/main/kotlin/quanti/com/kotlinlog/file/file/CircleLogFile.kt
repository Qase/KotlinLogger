package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.TAG
import quanti.com.kotlinlog.file.bundle.CircleLogBundle
import quanti.com.kotlinlog.utils.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Created by Vladislav Trnka on 11.1.2019
 *
 *
 */



/**
 * it also starts new 5MB every day
 */
class CircleLogFile(
        private val ctx: Context,
        private val bundle: CircleLogBundle
) : AbstractLogFile(ctx) {


    override val logIdentfier: String = "circle"

    override var fileName: String = createNewFileName()
    override var file: File = File(ctx.filesDir, fileName)
    override var fos: FileOutputStream = ctx.openFileOutput(fileName, Context.MODE_APPEND)


    override fun createNewFileName(): String {
        val arr = arrayOf(getFormattedFileNameDayNow(), logIdentfier, "", LOG_FILE_EXTENSION)

        val fileName = arr.joinToString(separator = "_")

        //now check if file exists
        val file = File(ctx.filesDir, fileName)

        val ret = if (file.exists()) {
            //add random string to the end
            arr[2] = getRandomString(4)
            arr.joinToString(separator = "_")
        } else {
            fileName
        }

        loga("New filename $ret")
        return ret
    }

    override fun cleanFolder() {
        //find if my current file has more the fileSize MB
        if (file.length() > bundle.maxFileSize) {
            //create new file
            createNewFile()
        }

        //remove all zips
        listOfLoggerFiles().deleteAllZips()

        //remove all files that exceeds specified limit
        listOfLoggerFiles()
                .sortByAge()
                .drop(bundle.numOfFiles)
                .deleteAll()
    }

}