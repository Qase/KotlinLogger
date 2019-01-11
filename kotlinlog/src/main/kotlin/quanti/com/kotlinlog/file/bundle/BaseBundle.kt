package quanti.com.kotlinlog.file.bundle

import quanti.com.kotlinlog.base.LogLevel


/**
 * Just dummy abstract class to match against
 */
abstract class BaseBundle(
        val minimalLogLevel:Int,
        val maxDaysSavedThrowable: Int
)
