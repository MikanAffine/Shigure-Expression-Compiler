package org.owari.shigure.runtime

import org.owari.shigure.Shigure

object BuiltinFunctions {
    @JvmStatic
    val VERSION: ArithmeticFunction = { Shigure.version }

    @JvmStatic
    val table = hashMapOf<String, ArithmeticFunction>(
        "version" to VERSION,
    )
}