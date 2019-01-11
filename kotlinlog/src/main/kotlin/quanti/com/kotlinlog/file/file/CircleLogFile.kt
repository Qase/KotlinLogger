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

const val FILE_IDENTIFIER = "circle"
const val LOG_FILE_EXTENSION = ".log"

/**
 * it also starts new 5MB every day
 */
class CircleLogFile(
        private val ctx: Context,
        private val bundle: CircleLogBundle
) : ILogFile {

    private val lock = ReentrantLock()

    private var fileName = createNewName()
    private var file = File(ctx.filesDir, fileName)

    private var fos: FileOutputStream = ctx.openFileOutput(fileName, Context.MODE_APPEND)


    override fun write(string: String) {
        lock.withLock {
            fos.write(string.toByteArray())
        }
    }

    override fun writeBatch(queue: LinkedBlockingQueue<String>) {
        lock.withLock {
            while (queue.isNotEmpty()) {
                fos.write(queue.poll().toByteArray())
            }
        }
    }

    override fun delete() {
        lock.withLock {
            val del = file.delete()
            loga("File ${file.absolutePath} was deleted: $del")
        }
    }

    override fun emptyFile() {
        lock.withLock {
            fos.close()
            val del = file.delete()
            android.util.Log.i(TAG, "Deleting file: $del")
            fos = ctx.openFileOutput(fileName, Context.MODE_APPEND)
        }
    }

    /**
     * Closes associated file output stream
     */
    override fun closeOutputStream() = fos.close()



    private fun createNewName(): String {
        val arr = arrayOf(getFormattedFileNameDayNow(), FILE_IDENTIFIER, "", LOG_FILE_EXTENSION)

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
        ctx.filesDir.listFiles().filter { it.name.contains(FILE_IDENTIFIER) }.deleteAllZips()

        //remove all files that exceeds specified limit
        ctx.filesDir.listFiles()
                .filter { it.name.contains(FILE_IDENTIFIER) }
                .sortByAge()
                .drop(bundle.numOfFiles)
                .deleteAll()
    }


    //todo move to abstract class
    private fun createNewFile() {
        fos.close()
        fileName = createNewName()
        loga("fileAge: $fileName")
        file = File(ctx.filesDir, fileName)
        fos = ctx.openFileOutput(fileName, Context.MODE_APPEND)
    }



}