package quanti.com.kotlinlog.utils

import quanti.com.kotlinlog.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Array section
 *
 * All array methods are just wrapper of toList and calling appropriate list method
 */

/**
 * Delete all files that are older than specified number of days inclusive
 */
fun Array<File>.deleteAllOldFiles(maxDaysOld: Int) = toList().deleteAllOldFiles(maxDaysOld)

/**
 * Delete all zip files
 */
fun Array<File>.deleteAllZips() = toList().deleteAllZips()

/**
 * Delete all files that matches given predicate
 */
fun Array<File>.deleteAll(filter: (File) -> Boolean) = toList().deleteAll(filter)

/**
 * Delete all files in current list
 */
fun Array<File>.deleteAll() = toList().deleteAll()

/**
 * Sort by age
 * Youngest file is on the beginning
 * Oldest file is on the end
 *
 * @param youngestFirst default behavior as explained in method documentation, reverses the list when false
 */
fun Array<File>.sortByAge(youngestFirst: Boolean = true) = toList().sortByAge(youngestFirst)


/**
 * List section
 */

/**
 * Delete all files that are older than specified number of days inclusive
 */
fun List<File>.deleteAllOldFiles(maxDaysOld: Int) = deleteAll { it.fileAge() > maxDaysOld }

/**
 * Delete all zip files
 */
fun List<File>.deleteAllZips() = deleteAll { it.name.contains(".zip") }

/**
 * Delete all files that matches given filter predicate
 */
fun List<File>.deleteAll(filter: (File) -> Boolean) {
    this.filter(filter)
            .forEach {
                val del = it.delete()
                loga("File deleted: " + del + "/t" + it.absolutePath)
            }
}

/**
 * Delete all files in current list
 */
fun List<File>.deleteAll() = deleteAll { true }


/**
 * Sort by age
 * Youngest file is on the beginning
 * Oldest file is on the end
 *
 * @param youngestFirst default behavior as explained in method documentation, reverses the list when false
 */
fun List<File>.sortByAge(youngestFirst: Boolean = true): List<File> {
    return if (youngestFirst){
        sortedWith(comparator)
    } else {
        sortedWith(comparator).reversed()
    }
}


/**
 * Create .zip from all specified files
 */
fun List<File>.zip(zipFile: File): File {
    if (isEmpty()) {
        return zipFile
    }

    try {
        val dest = FileOutputStream(zipFile)
        val out = ZipOutputStream(BufferedOutputStream(dest))

        for (i in this.indices) {
            //Log.v("Adding: " + files[i])
            try {
                val fi = FileInputStream(this[i])
                val entry = ZipEntry(this[i].name)
                out.putNextEntry(entry)

                fi.copyTo(out)
                fi.close()
            } catch (fnf: FileNotFoundException) {
                Log.w("the logfile no longer exists: $fnf" +
                    "\nLogfile deleted during zipping. Probably caused by lot of logs or circle " +
                    "logfile size is too small.")
            }
        }
        out.close()
    } catch (ex: Exception) {
        Log.e("Zipping error", ex)
    }

    return zipFile
}

private val comparator = Comparator<File> { o1, o2 ->
    val firstTime = o1.lastModified()
    val secondTime = o2.lastModified()

    return@Comparator (secondTime - firstTime).toInt()
}
