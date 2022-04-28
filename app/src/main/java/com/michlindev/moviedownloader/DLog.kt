package com.michlindev.moviedownloader

import android.util.Log

object DLog {

    private var DEFAULT_TAG = "Movie-Tag"

    private fun tag(tag: String): String {
        return Thread.currentThread().stackTrace[4].let {
            "$tag: (${it.fileName}:${it.lineNumber})#${it.methodName}"
        }
    }

    //--d--
    fun d(msg: String) {
        dPrint(tag(DEFAULT_TAG), msg)
    }

    fun d(tag: String, msg: String) {
        dPrint(tag(tag), msg)
    }

    private fun dPrint(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    //--i--

    fun i(msg: String) {
        iPrint(tag(DEFAULT_TAG), msg)
    }


    fun i(tag: String, msg: String) {
        iPrint(tag(tag), msg)
    }

    private fun iPrint(tag: String, msg: String) {
        Log.i(tag, msg)

    }

    //--e--
    fun e(msg: String) {
        ePrint(tag(DEFAULT_TAG), msg)
    }

    fun e(tag: String, msg: String) {
        ePrint(tag(tag), msg)
    }

    private fun ePrint(tag: String, msg: String) {
        Log.e(tag, msg)
    }

    //--w--

    fun w(msg: String) {
        wPrint(tag(DEFAULT_TAG), msg)
    }

    fun w(tag: String, msg: String) {
        wPrint(tag(tag), msg)
    }

    private fun wPrint(tag: String, msg: String) {
        Log.w(tag, msg)
    }
}