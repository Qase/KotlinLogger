package quanti.com.kotlinlog.utils

import quanti.com.kotlinlog.DEBUG_LIBRARY
import quanti.com.kotlinlog.TAG

fun loga(string: String){
    if(DEBUG_LIBRARY){
        android.util.Log.i(TAG, string)
    }
}