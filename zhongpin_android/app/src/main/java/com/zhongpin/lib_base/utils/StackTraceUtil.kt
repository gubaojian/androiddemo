package com.zhongpin.lib_base.utils

import java.io.PrintWriter
import java.io.StringWriter

object StackTraceUtil {


    fun getCallStackTrace(): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        try {
            val stackTraces = Thread.currentThread().stackTrace
            pw.println();
            stackTraces.forEach {
                pw.println(it)
            }
            pw.flush()
            return sw.toString()
        } finally {
            pw.close()
        }
    }
}