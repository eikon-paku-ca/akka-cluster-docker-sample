package com.mlh.clustering

import java.io.{PrintWriter, StringWriter}

object ExceptionUtil {

  def stackTraceString(t: Throwable): String = {
    val sw = new StringWriter
    t.printStackTrace(new PrintWriter(sw))
    sw.toString
  }

}
