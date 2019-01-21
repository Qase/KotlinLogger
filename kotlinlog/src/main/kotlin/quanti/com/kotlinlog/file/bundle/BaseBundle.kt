package quanti.com.kotlinlog.file.bundle


/**
 * Just dummy abstract class to match against
 */
abstract class BaseBundle(
        val minimalLogLevel:Int,
        val maxDaysSavedThrowable: Int,
        val minimalOwnFileLogLevelThrowable: Int
)
