package quanti.com.kotlinlog.base

/**
 * Created by Trnka Vladislav on 06.06.2017.
 *
 */


class LogLevel {

    companion object {

        /**
         * Priority constant for the println method; use Log.v.
         */
        const val VERBOSE = 2

        /**
         * Priority constant for the println method; use Log.d.
         */
        const val DEBUG = 3

        /**
         * Priority constant for the println method; use Log.i.
         */
        const val INFO = 4

        /**
         * Priority constant for the println method; use Log.w.
         */
        const val WARN = 5

        /**
         * Priority constant for the println method; use Log.e.
         */
        const val ERROR = 6

        /**
         * Priority constant for the println method.
         */
        const val ASSERT = 7

    }


}

fun Int.getLogLevelString(): String {
    return when (this) {
        android.util.Log.DEBUG -> "D"
        android.util.Log.ERROR -> "E"
        android.util.Log.VERBOSE -> "V"
        android.util.Log.INFO -> "I"
        android.util.Log.WARN -> "W"
        android.util.Log.ASSERT-> "A"

        else -> "?"
    }
}

fun String.getLogLevel(): Int {
    return when (this[0]) {
        'V' -> 2
        'D' -> 3
        'I' -> 4
        'W' -> 5
        'E' -> 6
        'A' -> 7
        else -> return 0
    }
}


