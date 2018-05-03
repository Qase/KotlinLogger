package quanti.com.kotlinlog.android

import android.content.Context
import android.os.Build


/**
 * Extracts some build properties from system
 */
object MetadataLogger {

    private val logs = ArrayList<Pair<String, String>>()

    fun getLogStrings(context: Context): String {
        logs.clear()
        fillArrayBuild()
        logs.add(Pair("", ""))
        fillArrayBuildConfig(context)
        return createNiceString()
    }

    private fun fillArrayBuild() {
        logs.add(Pair("BRAND", Build.BRAND))
        if (Build.BRAND != Build.MANUFACTURER) {
            logs.add(Pair("MANUFACTURER", Build.MANUFACTURER))
        }

        logs.add(Pair("MODEL", Build.MODEL))
        if (Build.MODEL != Build.PRODUCT) {
            logs.add(Pair("PRODUCT", Build.PRODUCT))
        }
        if (Build.MODEL != Build.DEVICE) {
            logs.add(Pair("DEVICE", Build.DEVICE))
        }

        logs.add(Pair("BOARD", Build.BOARD))
        logs.add(Pair("FINGERPRINT", Build.FINGERPRINT))
        logs.add(Pair("HARDWARE", Build.HARDWARE))
        logs.add(Pair("ID", Build.ID))
        logs.add(Pair("TAGS", Build.TAGS))

    }

    private fun fillArrayBuildConfig(context: Context) {
        try {
            val clazz = Class.forName(context.packageName + ".BuildConfig")
            clazz.fields.forEach {
                logs.add(Pair(
                        it.toGenericString().extractFieldName(),
                        it.get(null).toString()
                ))
            }
        } catch (ex: Exception) {
        }


    }

    private fun createNiceString(): String {

        //first found max length and FIRST length
        var infoLength = 0
        val wholeLength = 99

        logs.forEach {
            if (it.first.length > infoLength) {
                infoLength = it.first.length
            }
        }

        //now build nice strings
        val sb = StringBuilder()
        sb.append("\n\n")
        sb.append("***************************************************************************************************\n")
        sb.append("***************************************************************************************************\n")
        sb.append("*******************************************METADATA INFO*******************************************\n")
        sb.append("***************************************************************************************************\n")

        logs.forEach {
            sb.append("**** ")
            sb.append(it.first)
            (0..infoLength - it.first.length).forEach { sb.append(' ') }
            sb.append("  ")
            sb.append(it.second)
            (0..(wholeLength - (13 + infoLength + it.second.length))).forEach { sb.append(" ") }
            sb.append("****")
            sb.append("\n")
        }
        sb.append("***************************************************************************************************\n")
        sb.append("***************************************************************************************************\n")

        return sb.toString()

    }


}

fun String.extractFieldName(): String {

    val indexOfLast = this.indexOfLast { it == '.' } + 1
    return this.subSequence(indexOfLast, this.length).toString()

}