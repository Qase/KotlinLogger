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
    when (this) {
        android.util.Log.DEBUG -> return "D"
        android.util.Log.ERROR -> return "E"
        android.util.Log.VERBOSE -> return "V"
        android.util.Log.INFO -> return "I"
        android.util.Log.WARN -> return "W"
        android.util.Log.ASSERT-> return "A"

        else -> return "?"
    }
}


