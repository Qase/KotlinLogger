package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.file.bundle.StrictCircleLogBundle
import quanti.com.kotlinlog.utils.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.withLock


/**
 * Created by Vladislav Trnka on 11.1.2019
 *
 * Works the same as @see CircleLogBundle but maximum file size is always guaranteed
 *
 */
class StrictCircleLogFile(
        ctx: Context,
        private val bundle: StrictCircleLogBundle
) : AbstractLogFile(ctx) {


    override val logIdentifier: String = "strictcircle"

    override var fileName: String = createNewFileName()
    override var file: File = File(ctx.logFilesDir, fileName)
    override var fos: FileOutputStream = ctx.openLogFileOutput(fileName, false)

    private fun writeWithCheck(string: String) {
        val str = string.toByteArray()
        //if finalSize is bigger than allowed size, than create new file
        if (file.length() + str.size > bundle.maxFileSize) {
            createNewFile()
        }

        fos.write(str)
    }

    override fun write(string: String) {
        lock.withLock { writeWithCheck(string) }
    }

    override fun writeBatch(queue: LinkedBlockingQueue<String>) {
        lock.withLock {
            while (queue.isNotEmpty()) {
                queue.poll()?.let{ writeWithCheck(it) }
            }
        }
    }

    override fun createNewFileName(): String {
        var fileName = "${getFormattedFileNameDayNow()}_$logIdentifier.log"

        //now check if file exists
        if (File(ctx.logFilesDir, fileName).exists()) {
            fileName = "${getFormattedFileNameDayNow()}_${getRandomString(4)}_$logIdentifier.log"
        }

        loga("New filename $fileName")
        return fileName
    }

    override fun cleanFolder() {

        //remove all zips
        listOfLoggerFiles()?.deleteAllZips()

        //remove all files that exceeds specified limit
        listOfLoggerFiles()
                ?.sortByAge()
                ?.drop(bundle.numOfFiles)
                ?.deleteAll()
    }

}